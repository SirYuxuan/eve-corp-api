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


package com.yuxuan66.modules.corp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * 补损规则表(CorpSrpRules)实体类
 *
 * @author Sir丶雨轩
 * @since 2021-08-23 11:32:04
 */
@Setter
@Getter
@TableName("corp_srp_rules")
public class SrpRules implements Serializable {
    private static final long serialVersionUID = -16619727271516286L;

    private Long id;
    /**
     * 舰船名称
     */
    private String shipName;
    /**
     * 是否支持怪损
     */
    private Boolean isNpc;
    private Boolean isFull;
    /**
     * 指定技能组
     */
    private String skillGroupId;
    private String skillGroup;
    /**
     * 多少天的成员可提交
     */
    private Integer joinTime;
    /**
     * 创建时间
     */
    private Timestamp createTime;
    /**
     * 创建人
     */
    private Long createId;
    /**
     * 创建人
     */
    private String createBy;

    @TableField(exist = false)
    private List<String> skillGroupList;



}
