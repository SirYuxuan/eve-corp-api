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


package com.yuxuan66.modules.group.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 军团QQ群表(CorpQqGroup)实体类
 *
 * @author Sir丶雨轩
 * @since 2021-07-27 16:23:01
 */
@Setter
@Getter
@TableName("corp_qq_group")
public class QQGroup implements Serializable {

    private Long id;
    /**
     * 排序后
     */
    private Integer sort;
    /**
     * 群号
     */
    @TableField("`group`")
    private String group;
    /**
     * 标题
     */
    private String title;
    /**
     * 描述
     */
    private String content;
    /**
     * QQ群图片URL
     */
    private String url;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 创建人ID
     */
    private Long createId;
    /**
     * 创建时间
     */
    private Timestamp createTime;



}
