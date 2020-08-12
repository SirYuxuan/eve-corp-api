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
package com.yuxuan66.modules.corp.rest;

import com.yuxuan66.modules.corp.service.CorpService;
import com.yuxuan66.support.basic.http.RespEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sir丶雨轩
 * @since 2021/8/20
 */
@RestController
@RequestMapping(path = "/corp")
public class CorpController {

    private final CorpService corpService;

    public CorpController(CorpService corpService) {
        this.corpService = corpService;
    }

    /**
     * 判断指定QQ是否注册了军团系统
     * @param qq qq
     * @return 标准返回
     */
    @GetMapping(path = "/checkQQDoesItExist/{qq}")
    public RespEntity checkQQDoesItExist(@PathVariable("qq") String qq){
        return corpService.checkQQDoesItExist(qq);
    }


    /**
     * 获取当前系统内所有QQ，用于供机器人判断
     * @return 标准返回
     */
    @GetMapping(path = "/getAllQQ")
    public RespEntity getAllQQ(){
        return corpService.getAllQQ();
    }
}
