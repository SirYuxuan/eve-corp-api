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
package com.yuxuan66.modules.lp.rest;

import com.yuxuan66.modules.lp.entity.LpLog;
import com.yuxuan66.modules.lp.entity.dto.SendLPDto;
import com.yuxuan66.modules.lp.service.LpLogService;
import com.yuxuan66.support.basic.BasicQuery;
import com.yuxuan66.support.basic.http.PageEntity;
import com.yuxuan66.support.basic.http.RespEntity;
import org.springframework.web.bind.annotation.*;

/**
 * LP日志控制器
 * @author Sir丶雨轩
 * @since 2021/7/27
 */
@RestController
@RequestMapping(path = "/lpLog")
public class LpLogController {

    private final LpLogService lpLogService;

    public LpLogController(LpLogService lpLogService) {
        this.lpLogService = lpLogService;
    }

    /**
     * 根据LP发放记录获取LP获取排行
     * @return LP获取排行
     */
    @GetMapping(path = "/top10")
    public RespEntity top10(){
        return lpLogService.top10();
    }

    /**
     * 分页查询lp获取日志
     * @param basicQuery 查询参数
     * @return 标准分页
     */
    @GetMapping
    public PageEntity list(BasicQuery<LpLog> basicQuery){
        return lpLogService.list(basicQuery);
    }

    /**
     * 批量发放LP
     * @param sendLPDto LP发放信息
     * @return 标准返回
     */
    @PostMapping(path = "/sendLP")
    public RespEntity sendLP(@RequestBody SendLPDto sendLPDto){
        return lpLogService.sendLP(sendLPDto);
    }


}
