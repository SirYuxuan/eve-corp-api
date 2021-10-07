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
package com.yuxuan66.modules.tool.rest;

import com.yuxuan66.modules.tool.entity.JumpPathDto;
import com.yuxuan66.modules.tool.service.JumpPathService;
import com.yuxuan66.support.basic.http.RespEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @author Sir丶雨轩
 * @since 2021/8/20
 */
@RestController
@RequestMapping(path = "/jumpPath")
public class JumpPathController {

    private final JumpPathService jumpPathService;

    public JumpPathController(JumpPathService jumpPathService) {
        this.jumpPathService = jumpPathService;
    }

    /**
     * 模糊查询星系名称
     * @param name 星系名称
     * @return 星系名称列表
     */
    @GetMapping(path = "/getSystemName/{name}")
    public RespEntity getSystemName(@PathVariable("name") String name) {
        return jumpPathService.getSystemName(name);
    }

    /**
     * 旗舰跳计算
     * @param jumpPathDto 跳跃参数
     * @return 标准返回
     * @throws IOException IOException
     */
    @PostMapping(path = "/calculation")
    public RespEntity calculation(@RequestBody JumpPathDto jumpPathDto) throws IOException {
        return jumpPathService.calculation(jumpPathDto);
    }
}
