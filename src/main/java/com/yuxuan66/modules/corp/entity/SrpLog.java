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

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 补损提交记录(CorpSrpLog)实体类
 *
 * @author Sir丶雨轩
 * @since 2021-08-23 14:35:59
 */
@Setter
@Getter
@TableName("corp_srp_log")
public class SrpLog implements Serializable {

    private static final long serialVersionUID = 448582525663811653L;

    private Long id;

    private Long userId;

    private Long accountId;
    private String kmLocal;
    private Timestamp kmTime;
    /**
     * 角色名
     */
    private String name;
    /**
     * km发生时间
     */
    private Timestamp srpTime;
    /**
     * 星系
     */
    private String systemName;
    /**
     * 是否怪损
     */
    private Boolean isNpc;
    /**
     * 舰船
     */
    private String shipName;
    /**
     * KM价格
     */
    private Long price;
    /**
     * KMURL
     */
    private String url;
    /**
     * KMID
     */
    private String kmId;
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

    private Integer status;
    private String spRemark;
    private String remark;
    private Timestamp spTime;
    private Long spId;
    private String spName;


}
