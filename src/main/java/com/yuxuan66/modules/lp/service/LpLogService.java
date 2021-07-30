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

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yuxuan66.common.utils.Lang;
import com.yuxuan66.common.utils.StpEx;
import com.yuxuan66.modules.lp.entity.LpLog;
import com.yuxuan66.modules.lp.entity.dto.SendLPDto;
import com.yuxuan66.modules.lp.mapper.LpLogMapper;
import com.yuxuan66.modules.user.entity.User;
import com.yuxuan66.modules.user.entity.UserAccount;
import com.yuxuan66.modules.user.mapper.UserAccountMapper;
import com.yuxuan66.modules.user.service.UserService;
import com.yuxuan66.support.basic.BasicQuery;
import com.yuxuan66.support.basic.http.PageEntity;
import com.yuxuan66.support.basic.http.RespEntity;
import com.yuxuan66.support.esi.EsiApi;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * LP日志服务
 *
 * @author Sir丶雨轩
 * @since 2021/7/27
 */
@Service
public class LpLogService {

    @Resource
    private LpLogMapper lpLogMapper;
    @Resource
    private UserAccountMapper userAccountMapper;

    private final UserService userService;

    private final EsiApi esiApi;

    public LpLogService(UserService userService, EsiApi esiApi) {
        this.userService = userService;
        this.esiApi = esiApi;
    }

    public PageEntity list(BasicQuery<LpLog> basicQuery){
        basicQuery.processingBlurry("character_name","content");
        QueryWrapper<LpLog> queryWrapper = basicQuery.getQueryWrapper();
        User loginUser = StpEx.getLoginUser();
        if (!loginUser.getIsAdmin()) {
            queryWrapper.eq("user_id", loginUser.getId());
        }

        return PageEntity.success(lpLogMapper.selectPage(basicQuery.getPage(),queryWrapper));
    }

    /**
     * 根据LP发放记录获取LP获取排行
     *
     * @return LP获取排行
     */
    public RespEntity top10() {
        Map<String, Object> result = new HashMap<>();
        result.put("all", lpLogMapper.top10());
        result.put("time", lpLogMapper.top10ByMonth());
        return RespEntity.success(result);
    }

    /**
     * 批量发放LP
     *
     * @param sendLPDto LP发放信息
     * @return 标准返回
     */
    public RespEntity sendLP(SendLPDto sendLPDto) {

        User user = StpEx.getLoginUser();

        UserAccount formAccount = userService.getMailAccount(user.getId());

        for (Long id : sendLPDto.getUserList()) {
            UserAccount userAccount = userAccountMapper.selectById(id);

            if (userAccount == null) {
                continue;
            }

            userAccount.setLpNow(userAccount.getLpNow() + sendLPDto.getNum());
            userAccount.setLpTotal(userAccount.getLpTotal() + sendLPDto.getNum());
            userAccountMapper.updateById(userAccount);

            LpLog lpLog = new LpLog();
            lpLog.setCreateTime(Lang.getTime());
            lpLog.setContent(sendLPDto.getWhere());
            lpLog.setSource(2);
            lpLog.setType(2);
            lpLog.setCharacterName(userAccount.getName());
            lpLog.setLp(sendLPDto.getNum());
            lpLog.setCreateBy(user.getNickName());
            lpLog.setCreateId(user.getId());
            lpLog.setAccountId(userAccount.getId());
            lpLog.setUserId(userAccount.getUserId());
            lpLogMapper.insert(lpLog);

            esiApi.sendMail(formAccount, userAccount, DateUtil.today() + " LP发放完成.", "<font size=‘15’ color='#b3ffffff'>" + userAccount.getName() + ",您好</font>\r\n   您的LP已经发放，本次发放数量" + sendLPDto.getNum() + "LP，原因：" + sendLPDto.getWhere() + "\r\n您当前剩余LP：" + userAccount.getLpNow() + "LP");


        }


        return RespEntity.success();
    }


}
