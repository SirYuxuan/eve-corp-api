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
package com.yuxuan66.modules.auth.rest;

import com.yuxuan66.modules.auth.service.AuthService;
import com.yuxuan66.support.esi.EsiApi;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 授权相关控制器
 *
 * @author Sir丶雨轩
 * @since 2021/7/27
 */
@Controller
@RequestMapping(path = "/auth")
public class AuthController {

    private final EsiApi esiApi;
    private final AuthService authService;

    public AuthController(EsiApi esiApi, AuthService authService) {
        this.esiApi = esiApi;
        this.authService = authService;
    }

    /**
     * 前往授权
     *
     * @param userId 用户id
     * @return 授权地址
     */
    @GetMapping
    public String auth(Long userId) {
        return "redirect:" + esiApi.buildAuthPath(userId);
    }

    /**
     * 授权回调
     *
     * @param code  esi code
     * @param state esi state
     * @return 前端地址
     */
    @GetMapping(path = "/callback")
    public String callback(String code, String state) {
        return "redirect:" + authService.callback(code, state);
    }

}
