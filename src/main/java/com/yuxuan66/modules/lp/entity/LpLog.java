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


package com.yuxuan66.modules.lp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 军团LP发放记录表(CorpLpLog)实体类
 *
 * @author Sir丶雨轩
 * @since 2021-07-27 16:01:43
 */
@Setter
@Getter
@TableName("corp_lp_log")
public class LpLog implements Serializable {

    private Long id;
    /**
     * 角色名
     */
    private String characterName;
    /**
     * LP数量
     */
    private Long lp;
    /**
     * 角色ID
     */
    private Long accountId;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 1=PAP自动转换,2=手动发放,3=用户转账，4=兑换商品,5=兑换退款,6=物品兑换
     */
    private Integer source;
    /**
     * DKP操作，1=支出，2=收入
     */
    private Integer type;
    /**
     * 说明
     */
    private String content;

    /**
     * 兑换商品的日志ID
     */
    private Long buyLogId;

    private String createBy;

    private Long createId;


    private Timestamp createTime;



}
