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
package com.yuxuan66.common.utils;

import cn.dev33.satoken.stp.StpUtil;
import com.yuxuan66.modules.user.entity.User;

/**
 * 扩展StpUtil的功能
 * @author Sir丶雨轩
 * @since 2021/7/27
 */
public class StpEx extends StpUtil {

    /**
     * 登录的session key
     */
    private static final String LOGIN_KEY = "loginUser";

    /**
     * 登录并保存当前用户
     * @param user 用户
     */
    public static void loginSaveUser(User user){
        login(user.getId());
        getSession().set(LOGIN_KEY,user);
    }

    /**
     * 获取当前登录的用户
     * @return 用户信息
     */
    public static User getLoginUser(){
        return getSession().getModel(LOGIN_KEY,User.class);
    }
}
