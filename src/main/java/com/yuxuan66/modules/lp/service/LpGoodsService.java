/*
 * Copyright 2013-2021 Sir丶雨轩
 *
 * This file is part of Sir丶雨轩/eve-corp-api.

 * Sir丶雨轩/eve-corp-api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.

 * Sir丶雨轩/eve-corp-api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with Sir丶雨轩/eve-corp-api.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.yuxuan66.modules.lp.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yuxuan66.common.async.AsyncMail;
import com.yuxuan66.common.utils.Lang;
import com.yuxuan66.common.utils.StpEx;
import com.yuxuan66.modules.lp.entity.GoodsBuyLog;
import com.yuxuan66.modules.lp.entity.LpGoods;
import com.yuxuan66.modules.lp.entity.LpLog;
import com.yuxuan66.modules.lp.entity.dto.BuyGoodsDto;
import com.yuxuan66.modules.lp.entity.dto.ExchangeApprovalDto;
import com.yuxuan66.modules.lp.entity.query.BuyLogQuery;
import com.yuxuan66.modules.lp.mapper.GoodsBuyLogMapper;
import com.yuxuan66.modules.lp.mapper.LpGoodsMapper;
import com.yuxuan66.modules.lp.mapper.LpLogMapper;
import com.yuxuan66.modules.user.entity.User;
import com.yuxuan66.modules.user.entity.UserAccount;
import com.yuxuan66.modules.user.mapper.UserAccountMapper;
import com.yuxuan66.modules.user.service.UserService;
import com.yuxuan66.support.basic.BasicQuery;
import com.yuxuan66.support.basic.http.PageEntity;
import com.yuxuan66.support.basic.http.RespEntity;
import com.yuxuan66.support.config.SystemConfig;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * LP商品服务
 *
 * @author Sir丶雨轩
 * @since 2021/7/27
 */
@Service
public class LpGoodsService {

    @Resource
    private LpGoodsMapper lpGoodsMapper;
    @Resource
    private UserAccountMapper userAccountMapper;
    @Resource
    private LpLogMapper lpLogMapper;
    @Resource
    private GoodsBuyLogMapper goodsBuyLogMapper;


    private final Object lock = new Object();

    private final AsyncMail asyncMail;
    private final UserService userService;
    private final SystemConfig systemConfig;

    public LpGoodsService(AsyncMail asyncMail, UserService userService, SystemConfig systemConfig) {
        this.asyncMail = asyncMail;
        this.userService = userService;
        this.systemConfig = systemConfig;
    }


    /**
     * 分页查询LP商品列表
     *
     * @param basicQuery 查询兼分页参数
     * @return 标准分页返回
     */
    public PageEntity list(BasicQuery<LpGoods> basicQuery) {
        basicQuery.processingBlurry("title", "type");
        QueryWrapper<LpGoods> queryWrapper = basicQuery.getQueryWrapper();

        queryWrapper.orderByDesc("create_time");

        return PageEntity.success(lpGoodsMapper.selectPage(basicQuery.getPage(), queryWrapper));
    }


    /**
     * 查询商品购买日志
     *
     * @param buyLogQuery 购买日志查询对象
     * @return 标准分页
     */
    public PageEntity listBuyLog(BuyLogQuery buyLogQuery) {

        buyLogQuery.processingBlurry("title", "content", "examine_content");

        QueryWrapper<GoodsBuyLog> queryWrapper = buyLogQuery.getQueryWrapper();

        queryWrapper.eq(!Objects.isNull(buyLogQuery.getStatus()), "status", buyLogQuery.getStatus());
        queryWrapper.orderByDesc("create_time");
        queryWrapper.orderByAsc("status");

        // 如果不是管理员，只能看到自己的申请记录
        User loginUser = StpEx.getLoginUser();
        if (!loginUser.getIsAdmin()) {
            queryWrapper.eq("user_id", loginUser.getId());
        }

        return PageEntity.success(goodsBuyLogMapper.selectPage(buyLogQuery.getPage(), queryWrapper));
    }


    /**
     * 批量删除LP商品
     * @param ids id列表
     * @return 标准返回
     */
    public RespEntity del(Set<Long> ids) {
        lpGoodsMapper.deleteBatchIds(ids);
        return RespEntity.SUCCESS;
    }

    /**
     * LP商品新增或修改
     * @param lpGoods lp商品信息
     * @return 标准返回
     */
    public RespEntity addOrEdit(LpGoods lpGoods){

        if(lpGoods.getId()!=null){
            lpGoodsMapper.updateById(lpGoods);
        }else{
            lpGoods.setShopNum(0);
            User loginUser = StpEx.getLoginUser();
            lpGoods.setCreateBy(loginUser.getNickName());
            lpGoods.setCreateId(loginUser.getId());
            lpGoods.setCreateTime(Lang.getTime());
            lpGoodsMapper.insert(lpGoods);
        }

        return RespEntity.success();

    }

    /**
     * 购买lp商品
     *
     * @param buyGoodsDto 购买参数
     * @return 标准返回
     */
    public RespEntity buyGoods(BuyGoodsDto buyGoodsDto) {

        UserAccount userAccount = userAccountMapper.selectById(buyGoodsDto.getUid());
        if (userAccount == null) {
            return RespEntity.fail("没有找到您要兑换的角色");
        }

        LpGoods lpGoods = lpGoodsMapper.selectById(buyGoodsDto.getShopId());
        if (lpGoods == null) {
            return RespEntity.fail("没有找到您要兑换的商品");
        }

        if (buyGoodsDto.getNum() > lpGoods.getNum() - lpGoods.getShopNum()) {
            return RespEntity.fail("商品库存不足");
        }

        // 购买此商品所需要的LP
        long needLP = lpGoods.getLp() * buyGoodsDto.getNum();

        List<UserAccount> userAccountList = userService.getLoginAccount();

        // 当前账号下所有角色合计的LP数量
        long nowLP = userAccountList.stream().mapToLong(UserAccount::getLpNow).sum();

        if (needLP > nowLP) {
            return RespEntity.fail("您的LP余额不足");
        }

        // 同步线程锁，避免超售（真的会有吗？）
        synchronized (lock) {
            // 减少LP商品库存,当前已售出数量增加本次购买的数量
            lpGoods.setShopNum(lpGoods.getShopNum() + buyGoodsDto.getNum());
            lpGoodsMapper.updateById(lpGoods);

            // 插入兑换日志
            GoodsBuyLog goodsBuyLog = new GoodsBuyLog();
            goodsBuyLog.setCreateTime(Lang.getTime());
            goodsBuyLog.setContent(buyGoodsDto.getContent());
            goodsBuyLog.setNum(buyGoodsDto.getNum());
            goodsBuyLog.setTitle(lpGoods.getTitle());
            goodsBuyLog.setAccountId(userAccount.getId());
            goodsBuyLog.setUserId(StpEx.getLoginUser().getId());
            goodsBuyLog.setStatus(1);
            goodsBuyLog.setAccountName(userAccount.getName());
            goodsBuyLog.setUserName(StpEx.getLoginUser().getNickName());
            goodsBuyLogMapper.insert(goodsBuyLog);

            // 修改各个角色的LP数量
            for (UserAccount account : userAccountList) {
                // 如果所需的LP小于等于0，说明兑换商品需要的LP均已扣除
                if (needLP <= 0) {
                    break;
                }
                if (account.getLpNow() <= 0) {
                    continue;
                }

                // 判断当前所需的LP 当前角色是否充足
                long useLP = needLP;

                if (useLP > account.getLpNow()) {
                    useLP = account.getLpNow();
                }

                account.setLpNow(account.getLpNow() - useLP);
                account.setLpUse(account.getLpUse() + useLP);
                userAccountMapper.updateById(account);

                // 添加LP消费记录
                LpLog lpLog = new LpLog();
                lpLog.setCreateTime(Lang.getTime());
                lpLog.setContent("兑换[" + lpGoods.getTitle() + "] *" + buyGoodsDto.getNum());
                lpLog.setType(1);
                lpLog.setSource(4);
                lpLog.setCharacterName(account.getName());
                lpLog.setBuyLogId(goodsBuyLog.getId());
                lpLog.setLp(useLP);
                lpLog.setCreateBy(account.getName());
                lpLog.setCreateId(account.getId());
                lpLog.setAccountId(account.getId());
                lpLog.setUserId(account.getUserId());
                lpLogMapper.insert(lpLog);
                needLP -= useLP;

            }

        }

        // 给系统管理员推送信息告知有人兑换商品
        String subject = "有人兑换DKP了,快滚去处理";
        String sendHtml = "<h1 style='color:red'>兑换角色:" + userAccount.getName() + "物品名称:" + lpGoods.getTitle() + ",DKP数量:" + (lpGoods.getLp() * buyGoodsDto.getNum()) + "</h1>";
        for (String to : systemConfig.getEveManagerMail()) {
            asyncMail.send(to, subject, sendHtml);
        }


        return RespEntity.success();
    }

    /**
     * LP商品兑换申请审批
     *
     * @param exchangeApprovalDto 审批信息
     * @return 标准返回
     */
    public RespEntity exchangeApproval(ExchangeApprovalDto exchangeApprovalDto) {

        User loginUser = StpEx.getLoginUser();

        for (Long id : exchangeApprovalDto.getIds()) {
            GoodsBuyLog goodsBuyLog = goodsBuyLogMapper.selectById(id);

            if (goodsBuyLog == null) {
                continue;
            }
            // 更新商品兑换申请记录
            goodsBuyLog.setStatus(exchangeApprovalDto.getStatus());
            goodsBuyLog.setExamineContent(exchangeApprovalDto.getExamineContent());
            goodsBuyLog.setExamineTime(Lang.getTime());
            goodsBuyLog.setExamineBy(loginUser.getNickName());
            goodsBuyLog.setExamineId(loginUser.getId());
            goodsBuyLogMapper.updateById(goodsBuyLog);

            if (exchangeApprovalDto.getStatus() == 3) {
                // 审批失败,退回LP
                List<LpLog> lpLogList = lpLogMapper.selectList(new QueryWrapper<LpLog>().eq("buy_log_id", goodsBuyLog.getId()).ne("source",7).eq("type",1));

                for (LpLog lpLog : lpLogList) {
                    LpLog newLpLog = new LpLog();
                    newLpLog.setCreateTime(new Timestamp(System.currentTimeMillis()));
                    newLpLog.setContent(lpLog.getContent() + " 审批拒绝自动退还");
                    newLpLog.setType(2);
                    newLpLog.setSource(5);
                    newLpLog.setCharacterName(lpLog.getCharacterName());
                    newLpLog.setLp(lpLog.getLp());
                    newLpLog.setCreateBy(goodsBuyLog.getAccountName());
                    newLpLog.setCreateId(goodsBuyLog.getAccountId());
                    newLpLog.setAccountId(lpLog.getAccountId());
                    newLpLog.setUserId(lpLog.getUserId());

                    lpLogMapper.insert(newLpLog);
                    // 给用户加回LP
                    UserAccount userAccount = userAccountMapper.selectById(lpLog.getCreateId());
                    userAccount.setLpUse(userAccount.getLpUse() - lpLog.getLp());
                    userAccount.setLpNow(userAccount.getLpNow() + lpLog.getLp());

                    userAccountMapper.updateById(userAccount);
                }

            }


        }

        return RespEntity.success();
    }

}
