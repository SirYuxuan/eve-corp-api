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

import com.alibaba.fastjson.JSONObject;
import com.yuxuan66.modules.corp.entity.query.SrpQuery;
import com.yuxuan66.modules.corp.service.SrpService;
import com.yuxuan66.support.basic.http.PageEntity;
import com.yuxuan66.support.basic.http.RespEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Sir丶雨轩
 * @since 2021/8/23
 */
@RestController
@RequestMapping(path = "/srp")
public class SrpController {

    private final SrpService srpService;

    public SrpController(SrpService srpService) {
        this.srpService = srpService;
    }

    /**
     * 查询自己的补损提交记录
     * @param basicQuery 基础查询
     * @return 标准分页返回
     */
    @GetMapping
    public PageEntity list(SrpQuery basicQuery){
        return srpService.list(basicQuery);
    }

    @GetMapping(path = "/oldData")
    public RespEntity oldData(Long id){
        return srpService.oldData(id);
    }

    @GetMapping(path = "/getKillInfo")
    public RespEntity getKillInfo(String url){
        return srpService.getKillInfo(url);
    }

    @PostMapping(path = "/newSrp")
    public RespEntity newSrp(@RequestBody JSONObject data){
        return srpService.newSrp(data);
    }

    @PostMapping(path = "/exchangeApproval")
    public RespEntity exchangeApproval(@RequestBody JSONObject data){
        return srpService.exchangeApproval(data.getLong("id"),data.getInteger("status"),data.getString("spRemark"));
    }
}
