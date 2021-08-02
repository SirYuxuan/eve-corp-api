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
package com.yuxuan66.modules.skill.rest;

import com.yuxuan66.modules.skill.entity.SkillDetail;
import com.yuxuan66.modules.skill.entity.SkillGroup;
import com.yuxuan66.modules.skill.service.SkillGroupService;
import com.yuxuan66.support.basic.BasicQuery;
import com.yuxuan66.support.basic.http.PageEntity;
import com.yuxuan66.support.basic.http.RespEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * 技能组
 *
 * @author Sir丶雨轩
 * @since 2021/8/2
 */
@RestController
@RequestMapping(path = "/skillGroup")
public class SkillGroupController {

    private final SkillGroupService skillGroupService;

    public SkillGroupController(SkillGroupService skillGroupService) {
        this.skillGroupService = skillGroupService;
    }

    /**
     * 查询所有的技能组
     *
     * @return 技能组
     */
    @GetMapping(path = "/all")
    public RespEntity all() {
        return skillGroupService.all();
    }

    /**
     * 查询技能组的详情
     * @param groupId 技能组id
     * @return 技能组的详情
     */
    @GetMapping(path = "/listSkillDetail")
    public RespEntity listSkillDetail(Long groupId) {
        return skillGroupService.listSkillDetail(groupId);
    }


    /**
     * 分页查询技能组列表
     * @param basicQuery 分页查询参数
     * @return 技能组列表
     */
    @GetMapping
    public PageEntity list(BasicQuery<SkillGroup> basicQuery) {
        return skillGroupService.list(basicQuery);
    }

    /**
     * 添加或修改一个技能组
     * @param skillGroup 技能组
     * @return 标准返回
     */
    @PostMapping
    public RespEntity addOrEdit(@RequestBody SkillGroup skillGroup){
        return skillGroupService.addOrEdit(skillGroup);
    }

    /**
     * 添加一个技能组的技能
     * @param skillDetail 技能组详情
     * @return 标准返回
     */
    @PostMapping(path = "/addSkill")
    public RespEntity addSkill(@RequestBody SkillDetail skillDetail){
        return skillGroupService.addSkill(skillDetail);
    }



    /**
     * 检查指定技能名称是否存在
     * @param name 技能名称
     * @return 标准返回
     */
    @GetMapping(path = "/checkSkillName")
    public RespEntity checkSkillName(String name){
        return skillGroupService.checkSkillName(name);
    }


    /**
     * 批量删除技能组
     * @param ids 技能组
     * @return 标准返回
     */
    @DeleteMapping
    public RespEntity del(@RequestBody Set<Long> ids){
        return skillGroupService.del(ids);
    }

    /**
     * 删除技能组的技能
     *
     * @param ids 技能组的技能id
     * @return 标准返回
     */
    @DeleteMapping(path = "/delSkill")
    public RespEntity delSkill(@RequestBody Set<Long> ids){
        return skillGroupService.delSkill(ids);
    }

    /**
     * 技能检查
     *
     * @param groupId   技能组id
     * @param accountId 角色id
     * @return 检查结果
     */
    @GetMapping(path = "/checkSkillGroup")
    public RespEntity checkSkillGroup(Long groupId, Long accountId) {
        return skillGroupService.checkSkillGroup(groupId, accountId);
    }


    /**
     * 检查某个用户或全体用户是否满足技能
     *
     * @return 技能检查
     */
    @GetMapping(path = "/checkUserSkillGroup")
    public RespEntity checkUserSkillGroup(String name, Long groupId) {
        return skillGroupService.checkUserSkillGroup(name, groupId);
    }
}
