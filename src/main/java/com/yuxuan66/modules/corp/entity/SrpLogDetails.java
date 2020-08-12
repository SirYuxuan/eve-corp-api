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

/**
 * 补损记录详情(CorpSrpLogDetails)实体类
 *
 * @author Sir丶雨轩
 * @since 2021-08-23 14:48:52
 */
@Setter
@Getter
@TableName("corp_srp_log_details")
public class SrpLogDetails implements Serializable {

    private static final long serialVersionUID = 380692394362863611L;

    private Long id;
    /**
     * 物品ID
     */
    private Integer itemId;
    /**
     * 物品名称
     */
    private String name;
    /**
     * 物品数量
     */
    private Integer num;
    /**
     * 类型
     */
    private String type;
    /**
     * 价格
     */
    private Long price;
    /**
     * 记录ID
     */
    private Long logId;


}
