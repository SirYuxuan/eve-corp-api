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
package com.yuxuan66.modules.auth.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yuxuan66.common.utils.Lang;
import com.yuxuan66.common.utils.StpEx;
import com.yuxuan66.common.utils.WebUtil;
import com.yuxuan66.modules.user.entity.User;
import com.yuxuan66.modules.user.entity.UserAccount;
import com.yuxuan66.modules.user.mapper.UserAccountMapper;
import com.yuxuan66.modules.user.mapper.UserMapper;
import com.yuxuan66.support.config.SystemConfig;
import com.yuxuan66.support.esi.EsiApi;
import com.yuxuan66.support.esi.entity.EsiAccountInfo;
import com.yuxuan66.support.esi.entity.EsiTokenInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 处理授权相关操作
 *
 * @author Sir丶雨轩
 * @since 2021/7/27
 */
@Service
public class AuthService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserAccountMapper userAccountMapper;

    private final EsiApi esiApi;

    private final SystemConfig systemConfig;

    public AuthService(EsiApi esiApi, SystemConfig systemConfig) {
        this.esiApi = esiApi;
        this.systemConfig = systemConfig;
    }

    /**
     * ESI授权回调
     *
     * @param code  ESI返回Code
     * @param state 授权传递State
     * @return 授权完成的地址
     */
    public String callback(String code, String state) {


        EsiTokenInfo tokenInfo = esiApi.codeToInfo(code);

        UserAccount userAccount = userAccountMapper.selectOne(new QueryWrapper<UserAccount>().eq("character_id", tokenInfo.getCharacterId()));

        String DEFAULT_STATE = "NONE";

        if (userAccount != null) {
            // 此角色已经在系统中存在，更新一下Token信息即可
            userAccount.setAccessToken(tokenInfo.getAccessToken());
            userAccount.setRefreshToken(tokenInfo.getRefreshToken());
            userAccountMapper.updateById(userAccount);
            User user = userMapper.selectById(userAccount.getUserId());

            // 没有UserID 说明是直接点击的授权登录。这个时候应该进行常规的登录操作，由于角色已经存在与系统中，如果账号不存在则说明此数据异常
            if (user == null) {
                return getErrorPath("角色已存在，但是用户不存在");
            }
            // 没有传递UserID,说明是正常授权进行登录。
            if (DEFAULT_STATE.equals(state)) {

                StpEx.loginSaveUser(user);
                return getLoginSuccess(user,state);
            }
            // 这里说明传递了UserID，意味着想要绑定到某个账号的，但是自身肯定已经存在了UserID，这里是将不支持绑定，如果是绑定自己则通过
            if (Convert.toLong(state).equals(userAccount.getUserId())) {
                // 这里是绑定自身；放行且保存当前登录信息
                StpEx.loginSaveUser(user);
                return getLoginSuccess(user,state);
            }
            // 这个角色属于别的用户 禁止再次绑定
            return getErrorPath("此角色已被别的账号绑定");
        }
        // 角色不存在
        userAccount = new UserAccount();
        userAccount.setCharacterId(tokenInfo.getCharacterId());
        userAccount.setName(tokenInfo.getName());
        userAccount.setIsMain(DEFAULT_STATE.equals(state));
        userAccount.setAccessToken(tokenInfo.getAccessToken());
        userAccount.setRefreshToken(tokenInfo.getRefreshToken());
        userAccount.setCreateTime(Lang.getTime());
        EsiAccountInfo accountInfo = esiApi.getAccountInfo(userAccount.getCharacterId(), userAccount.getAccessToken());
        userAccount.setCorpId(accountInfo.getCorpId());
        userAccount.setCorpName(accountInfo.getCorpName());
        userAccount.setAllianceId(accountInfo.getAllianceId());
        userAccount.setAllianceName(accountInfo.getAllianceName());
        userAccount.setJoinTime(accountInfo.getJoinTime());
        User user;
        if (userAccount.getIsMain()) {
            // 这个是主号，说明当前没有账号，开始创建
            user = new User();
            user.setCreateTime(Lang.getTime());
            // 如果用户不属于主军团，则无法通过总监权限获得系统管理权限
            if (!accountInfo.getCorpId().equals(systemConfig.getEveMainCorp())) {
                user.setIsAdmin(false);
                user.setCorp(false);
            } else {
                List<String> roles = EsiApi.getUserRoles(userAccount.getCharacterId(), userAccount.getAccessToken());
                user.setIsAdmin(roles.contains("Accountant"));
                user.setCorp(true);
            }
            user.setLastTime(Lang.getTime());
            user.setLastIp(ServletUtil.getClientIP(WebUtil.getRequest()));
            user.setLastCity(WebUtil.getIPCity(user.getLastIp()));

            userMapper.insert(user);
            userAccount.setUserId(user.getId());
        } else {
            userAccount.setUserId(Convert.toLong(state));
            user = userMapper.selectById(userAccount.getUserId());
        }
        userAccountMapper.insert(userAccount);
        StpEx.loginSaveUser(user);
        return getLoginSuccess(user,state);
    }

    /**
     * 获取登录成功的地址 并更新用户最后登录时间
     *
     * @return 地址
     */
    private String getLoginSuccess(User user,String state) {
        user.setLastTime(Lang.getTime());
        user.setLastIp(ServletUtil.getClientIP(WebUtil.getRequest()));
        user.setLastCity(WebUtil.getIPCity(user.getLastIp()));
        userMapper.updateById(user);
        if("NONE".equals(state)){
            return systemConfig.getWebPath() + "loginHD?token=" + StpEx.getTokenValue();
        }else{
            return systemConfig.getWebPath() + "loginHD?token=close";
        }

    }

    /**
     * 获取异常的地址
     *
     * @param msg 异常信息
     * @return 地址
     */
    private String getErrorPath(String msg) {
        return systemConfig.getWebPath() + "error?msg=" + URLUtil.encode(msg);
    }
}
