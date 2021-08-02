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


package com.yuxuan66.modules.skill.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 军团用户技能表(CorpUserSkill)实体类
 *
 * @author Sir丶雨轩
 * @since 2021-08-02 11:12:12
 */
@Setter
@Getter
@TableName("corp_user_skill")
public class UserSkill implements Serializable {

    private Long id;
    /**
     * 角色id
     */
    private Long characterId;
    /**
     * 角色名称
     */
    private String name;
    /**
     * 角色id
     */
    private Long accountId;
    /**
     * 技能英文名称
     */
    private String skillName;
    /**
     * 技能id
     */
    private Integer skillId;
    /**
     * 技能中文名称
     */
    private String skillZhName;
    /**
     * 技能等级
     */
    private Integer skillLevel;
    /**
     * 创建实践
     */
    private Timestamp createTime;



}
