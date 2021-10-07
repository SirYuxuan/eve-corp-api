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


package com.yuxuan66.modules.assets.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 军团用户资产表(CorpUserAssets)实体类
 *
 * @author Sir丶雨轩
 * @since 2021-08-09 08:59:54
 */
@Setter
@Getter
@TableName("corp_user_assets")
public class UserAssets implements Serializable {

    private static final long serialVersionUID = 626108079870108969L;

    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 角色ID
     */
    private Long accountId;
    /**
     * 角色名称
     */
    private String accountName;
    /**
     * 资产名称
     */
    private String name;
    /**
     * 资产数量
     */
    private Integer num;
    /**
     * 是否是蓝图复制
     */
    private Boolean blueprintCopy;
    /**
     * 创建时间
     */
    private Timestamp createTime;


}
