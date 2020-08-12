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

import com.yuxuan66.modules.corp.entity.SrpRules;
import com.yuxuan66.modules.corp.service.SrpRulesService;
import com.yuxuan66.support.basic.BasicQuery;
import com.yuxuan66.support.basic.http.PageEntity;
import com.yuxuan66.support.basic.http.RespEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * @author Sir丶雨轩
 * @since 2021/8/23
 */
@RestController
@RequestMapping(path = "/srpRules")
public class SrpRulesController {

    private final SrpRulesService srpRulesService;

    public SrpRulesController(SrpRulesService srpRulesService) {
        this.srpRulesService = srpRulesService;
    }

    /**
     * 分页查询补损规则列表
     * @param basicQuery 分页查询参数
     * @return 补损规则列表
     */
    @GetMapping
    public PageEntity list(BasicQuery<SrpRules> basicQuery) {
        return srpRulesService.list(basicQuery);
    }

    /**
     * 添加或修改一个补损规则
     * @param srpRules 补损规则
     * @return 标准返回
     */
    @PostMapping
    public RespEntity addOrEdit(@RequestBody SrpRules srpRules){
        return srpRulesService.addOrEdit(srpRules);
    }

    /**
     * 批量删除技能组
     * @param ids 技能组
     * @return 标准返回
     */
    @DeleteMapping
    public RespEntity del(@RequestBody Set<Long> ids){
        return srpRulesService.del(ids);
    }

}

