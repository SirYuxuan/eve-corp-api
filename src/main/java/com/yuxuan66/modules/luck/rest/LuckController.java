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
package com.yuxuan66.modules.luck.rest;

import com.yuxuan66.modules.luck.entity.LuckDraw;
import com.yuxuan66.modules.luck.service.LuckService;
import com.yuxuan66.support.basic.BasicQuery;
import com.yuxuan66.support.basic.http.PageEntity;
import com.yuxuan66.support.basic.http.RespEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * @author Sir丶雨轩
 * @since 2021/8/24
 */
@RestController
@RequestMapping(path = "/luck")
public class LuckController {

    private final LuckService luckService;

    public LuckController(LuckService luckService) {
        this.luckService = luckService;
    }

    /**
     * 分页查询活动列表
     * @param basicQuery 查询参数
     * @return 标准分页返回
     */
    @GetMapping
    public PageEntity list(BasicQuery<LuckDraw> basicQuery){
        return luckService.list(basicQuery);
    }

    @GetMapping(path = "/{id}")
    public RespEntity get(@PathVariable("id") Long id) {
        return luckService.get(id);
    }

    @DeleteMapping
    public RespEntity del(@RequestBody Set<Long> id) {
        return luckService.del(id);
    }

    @PostMapping
    public RespEntity add(@RequestBody LuckDraw luckDraw) {
        return luckService.add(luckDraw);
    }

    @PutMapping(path = "/buyNode")
    public RespEntity buyNode(Long id,String node) {
        return luckService.buyNode(id,node);
    }

}
