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
package com.yuxuan66.modules.user.rest;

import com.yuxuan66.modules.user.entity.User;
import com.yuxuan66.modules.user.service.UserService;
import com.yuxuan66.support.basic.http.RespEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 * @author Sir丶雨轩
 * @since 2021/7/27
 */
@RestController
@RequestMapping(path = "/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取当前登录用户的信息
     * @return 用户信息
     */
    @GetMapping(path = "/info")
    public RespEntity info() {
        return userService.info();
    }


    /**
     * 个人中心信息查询
     *
     * @return 个人信息
     */
    @GetMapping(path = "/myInfo")
    public RespEntity myInfo() {
        return userService.myInfo();
    }



    /**
     * 查询当前登录账号的所有角色
     * @return 角色列表
     */
    @GetMapping(path = "/listAccount")
    public RespEntity listAccount() {
        return userService.listAccount();
    }

    /**
     * 修改当前账户的信息
     * @param resources 账户信息
     * @return 标准返回
     */
    @PostMapping(path = "/saveInfo")
    public RespEntity saveInfo(@RequestBody User resources) {
        return userService.saveInfo(resources);
    }
}
