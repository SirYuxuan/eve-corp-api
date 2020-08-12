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
package com.yuxuan66.modules.corp.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yuxuan66.common.utils.Lang;
import com.yuxuan66.common.utils.StpEx;
import com.yuxuan66.modules.corp.entity.SrpRules;
import com.yuxuan66.modules.corp.mapper.SrpRulesMapper;
import com.yuxuan66.modules.skill.entity.SkillGroup;
import com.yuxuan66.modules.skill.mapper.SkillGroupMapper;
import com.yuxuan66.modules.user.entity.User;
import com.yuxuan66.support.basic.BasicQuery;
import com.yuxuan66.support.basic.http.PageEntity;
import com.yuxuan66.support.basic.http.RespEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Sir丶雨轩
 * @since 2021/8/23
 */
@Service
public class SrpRulesService {

    @Resource
    private SrpRulesMapper srpRulesMapper;
    @Resource
    private SkillGroupMapper skillGroupMapper;

    public PageEntity list(BasicQuery<SrpRules> basicQuery) {

        basicQuery.processingBlurry("group_name");

        QueryWrapper<SrpRules> queryWrapper = basicQuery.getQueryWrapper();
        queryWrapper.orderByDesc("id");

        return PageEntity.success(srpRulesMapper.selectPage(basicQuery.getPage(), queryWrapper));

    }

    /**
     * 添加或修改技能组
     *
     * @param srpRules 技能组
     * @return 标准返回
     */
    public RespEntity addOrEdit(SrpRules srpRules) {

        if (srpRules.getId() != null) {
            if (!srpRules.getSkillGroupList().isEmpty()) {
                String skName = skillGroupMapper.selectBatchIds(srpRules.getSkillGroupList()).stream().map(SkillGroup::getGroupName).collect(Collectors.joining(","));
                srpRules.setSkillGroupId(srpRules.getSkillGroupList().stream().collect(Collectors.joining(",")));
                srpRules.setSkillGroup(skName);
            }
            srpRulesMapper.updateById(srpRules);
            return RespEntity.success();
        }

        User user = StpEx.getLoginUser();
        srpRules.setCreateId(user.getId());
        srpRules.setCreateBy(user.getNickName());
        srpRules.setCreateTime(Lang.getTime());
        if (!srpRules.getSkillGroupList().isEmpty()) {
            String skName = skillGroupMapper.selectBatchIds(srpRules.getSkillGroupList()).stream().map(SkillGroup::getGroupName).collect(Collectors.joining(","));
            srpRules.setSkillGroupId(srpRules.getSkillGroupList().stream().collect(Collectors.joining(",")));
            srpRules.setSkillGroup(skName);
        }

        srpRulesMapper.insert(srpRules);
        return RespEntity.success();
    }

    /**
     * 批量删除技能组
     *
     * @param ids 技能组id
     * @return 标准返回
     */
    public RespEntity del(Set<Long> ids) {
        srpRulesMapper.deleteBatchIds(ids);
        return RespEntity.success();
    }
}
