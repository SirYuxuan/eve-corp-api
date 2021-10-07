/*
 * Copyright 2013-2021 Sir丶雨轩
 *
 * This file is part of Sir丶雨轩/ehi-blog.

 * Sir丶雨轩/ehi-blog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.

 * Sir丶雨轩/ehi-blog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with Sir丶雨轩/ehi-blog.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.yuxuan66.modules.luck.service;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yuxuan66.cache.RedisUtil;
import com.yuxuan66.common.utils.StpEx;
import com.yuxuan66.modules.lp.mapper.LpLogMapper;
import com.yuxuan66.modules.luck.entity.LuckDraw;
import com.yuxuan66.modules.luck.entity.LuckDrawNode;
import com.yuxuan66.modules.luck.mapper.LuckDrawMapper;
import com.yuxuan66.modules.luck.mapper.LuckDrawNodeMapper;
import com.yuxuan66.modules.user.entity.User;
import com.yuxuan66.modules.user.entity.UserAccount;
import com.yuxuan66.modules.user.mapper.UserAccountMapper;
import com.yuxuan66.modules.user.mapper.UserMapper;
import com.yuxuan66.support.basic.BasicQuery;
import com.yuxuan66.support.basic.http.PageEntity;
import com.yuxuan66.support.basic.http.RespEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author Sir丶雨轩
 * @since 2021/8/21
 */
@Service
public class LuckService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserAccountMapper userAccountMapper;
    @Resource
    private LuckDrawMapper luckDrawMapper;
    @Resource
    private LuckDrawNodeMapper luckDrawNodeMapper;
    private final RedisUtil redisUtil;
    @Resource
    private LpLogMapper lpLogMapper;

    public LuckService(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }


    /**
     * 查询活动列表
     * @param basicQuery 查询参数
     * @return 标准分页返回
     */
    public PageEntity list(BasicQuery<LuckDraw> basicQuery) {
        basicQuery.processingBlurry("name","type");
        QueryWrapper<LuckDraw> queryWrapper = basicQuery.getQueryWrapper();
        queryWrapper.orderByDesc("create_time");
        return PageEntity.success(luckDrawMapper.selectPage(basicQuery.getPage(),queryWrapper));
    }


    public RespEntity get(Long id){
        return RespEntity.success(luckDrawMapper.findLuckById(id));
    }

    public RespEntity add(LuckDraw luckDraw) {


        // 保持抽奖信息
        luckDraw.setStatus(1);


        if(luckDraw.getStartTime().getTime() < System.currentTimeMillis()){
            luckDraw.setStatus(2);
        }
        if(luckDraw.getEndTime().getTime() < System.currentTimeMillis()){
            luckDraw.setStatus(3);
        }


        luckDraw.setCreateTime(new Timestamp(System.currentTimeMillis()));
        luckDraw.setCreateBy(StpEx.getLoginUser().getNickName());

        List<String> nodeList = new ArrayList<>(luckDraw.getNodeNum());

        // 生成随机数 用于给雨轩购买节点
        int num = RandomUtil.randomInt(0,luckDraw.getNodeNum() - 1);

        List<UserAccount> userAccountList = userAccountMapper.selectList(new QueryWrapper<UserAccount>().eq("user_id",StpEx.getLoginUser().getId()));
        // 生成节点并保存
        Set<LuckDrawNode> luckDrawNodeSet = new HashSet<>();

        luckDrawMapper.insert(luckDraw);
        for (int i = 0; i < luckDraw.getNodeNum(); i++) {
            String nodeNo = RandomUtil.randomString(10).toUpperCase();
            while (nodeList.contains(nodeNo)){
                nodeNo = RandomUtil.randomString(10).toUpperCase();
            }
            nodeList.add(nodeNo);

            LuckDrawNode luckDrawNode = new LuckDrawNode();
            luckDrawNode.setNo(nodeNo);
            // 随机购买一个节点

            if(i == num){

               /* if(luckDraw.getLp() != null & luckDraw.getLp() > 0){
                    // 扣下雨轩的LP
                    long needLP = luckDraw.getLp();

                    long nowLP = userAccountList.stream().mapToLong(UserAccount::getLpNow).sum();

                    if (needLP <= nowLP) {
                        luckDrawNode.setName("Yuxuan");
                        luckDrawNode.setUid(104L);
                        luckDrawNode.setAccountId(626L);
                        luckDrawNode.setPayTime(new Timestamp(System.currentTimeMillis()));
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
                            lpLog.setContent("购买超网节点【"+luckDraw.getName()+"】");
                            lpLog.setType(1);
                            lpLog.setSource(7);
                            lpLog.setCharacterName(account.getName());
                            lpLog.setBuyLogId(luckDraw.getId());
                            lpLog.setLp(useLP);
                            lpLog.setCreateBy(account.getName());
                            lpLog.setCreateId(account.getId());
                            lpLog.setAccountId(account.getId());
                            lpLog.setUserId(account.getUserId());
                            lpLogMapper.insert(lpLog);
                            needLP -= useLP;

                        }
                    }


                }*/
            }


            luckDrawNodeSet.add(luckDrawNode);
        }


        for (LuckDrawNode luckDrawNode : luckDrawNodeSet) {
            luckDrawNode.setLuckDrawId(luckDraw.getId());
            luckDrawNodeMapper.insert(luckDrawNode);

        }
        if(luckDraw.getStatus() == 1){
            // 设置自动开始活动
            redisUtil.set("LUCK:S:"+luckDraw.getId(),"ok", Math.abs(DateUtil.between(new Date(),luckDraw.getStartTime(), DateUnit.SECOND)));
            // 设置自动结束活动
            redisUtil.set("LUCK:E:"+luckDraw.getId(),"ok", Math.abs(DateUtil.between(new Date(),luckDraw.getEndTime(), DateUnit.SECOND)));
        }
        if(luckDraw.getStatus() == 2){
            // 设置自动结束活动
            redisUtil.set("LUCK:E:"+luckDraw.getId(), DateUtil.between(new Date(),luckDraw.getEndTime(), DateUnit.SECOND));
        }

        return RespEntity.SUCCESS;
    }





    public RespEntity del( Set<Long> id) {
        luckDrawMapper.deleteBatchIds(id);
        return RespEntity.success();
    }

    public synchronized RespEntity buyNode(Long id,String node){

        LuckDraw luckDraw = luckDrawMapper.findLuckById(id);


        if(luckDraw.getStatus() != 2){
            return RespEntity.fail("当前抽奖未在进行中，无法购买节点");
        }

       List<UserAccount> userAccountList = userAccountMapper.selectList(new QueryWrapper<UserAccount>().eq("user_id",StpEx.getLoginUser().getId()));
       /*  long needLP = luckDraw.getLp();

        long nowLP = userAccountList.stream().mapToLong(UserAccount::getLpNow).sum();

        if (needLP > nowLP) {
            return RespEntity.fail("您的LP余额不足,无法购买");
        }
*/

        for (LuckDrawNode luckDrawNode : luckDraw.getLuckDrawNodes()) {
            if(luckDrawNode.getUid() != null && Objects.equals(StpEx.getLoginUser().getId(), luckDrawNode.getUid())){
                return RespEntity.fail("您已经购买过了，一个用户仅允许购买一个节点");
            }
        }

        List<LuckDrawNode> luckDrawNodes = luckDrawNodeMapper.selectList(new QueryWrapper<LuckDrawNode>().eq("no",node));
        for (LuckDrawNode luckDrawNode : luckDrawNodes) {
            if(luckDrawNode.getLuckDrawId().equals(id) && luckDrawNode.getUid() != null){
                return RespEntity.fail("当前节点已被他人抢购");
            }
        }

        luckDraw.getLuckDrawNodes().forEach(item->{
            if(node.equals(item.getNo())){
                UserAccount userAccount = null;
                if(userAccountList.isEmpty()){
                    return;
                }
                for (UserAccount account : userAccountList) {
                    if(account.getIsMain()){
                        userAccount = account;
                        break;
                    }
                }
                if(userAccount == null){
                    userAccount = userAccountList.get(0);
                }
                item.setName(userAccount.getName());
                item.setUid(userAccount.getUserId());
                item.setAccountId(userAccount.getId());
                item.setPayTime(new Timestamp(System.currentTimeMillis()));
                luckDrawNodeMapper.updateById(item);
            }
        });


      /*  for (UserAccount account : userAccountList) {
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
            lpLog.setContent("购买超网节点【"+luckDraw.getName()+"】");
            lpLog.setType(1);
            lpLog.setSource(7);
            lpLog.setCharacterName(account.getName());
            lpLog.setBuyLogId(luckDraw.getId());
            lpLog.setLp(useLP);
            lpLog.setCreateBy(account.getName());
            lpLog.setCreateId(account.getId());
            lpLog.setAccountId(account.getId());
            lpLog.setUserId(account.getUserId());
            lpLogMapper.insert(lpLog);
            needLP -= useLP;

        }*/

        openLuckDraw(id);
        return RespEntity.success(node);
    }

    public synchronized void openLuckDraw(Long id){

        LuckDraw luckDraw = luckDrawMapper.findLuckById(id);
        // 如果不是进行中，则不考虑开奖
        if(luckDraw.getStatus() != 2){
            return;
        }

        boolean isOpen = true;

        for (LuckDrawNode luckDrawNode : luckDraw.getLuckDrawNodes()) {
            if (luckDrawNode.getUserAccount() == null) {
                isOpen = false;
                break;
            }
        }

        if(isOpen){
            // 开奖拉
            List<LuckDrawNode> luckDrawNodes = new ArrayList<>(luckDraw.getLuckDrawNodes());
            int num = RandomUtil.randomInt(0,luckDrawNodes.size() - 1);

            LuckDrawNode luckDrawNode = luckDrawNodes.get(num);
            luckDrawNode.setWin(true);
            luckDrawNodeMapper.updateById(luckDrawNode);
            luckDraw.setWinNo(luckDrawNode.getNo());
            UserAccount account = userAccountMapper.selectById(luckDrawNode.getAccountId());
            luckDraw.setWinAccountName(account.getName());
            luckDraw.setWinAccountId(account.getId());
            luckDraw.setWinUserId(luckDrawNode.getUid());
            User user = userMapper.selectById(luckDrawNode.getUid());
            luckDraw.setWinUserName(user.getNickName());
            luckDraw.setStatus(3);
            luckDrawMapper.updateById(luckDraw);
        }

    }

}
