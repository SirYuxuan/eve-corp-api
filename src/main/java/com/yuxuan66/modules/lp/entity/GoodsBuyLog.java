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
 * 军团LP商品购买记录表(CorpGoodsBuyLog)实体类
 *
 * @author Sir丶雨轩
 * @since 2021-07-28 14:16:22
 */
@Setter
@Getter
@TableName("corp_goods_buy_log")
public class GoodsBuyLog implements Serializable {

    private Long id;
    /**
     * 标题
     */
    private String title;
    /**
     * 数量
     */
    private Integer num;
    /**
     * 状态1=等待，2=通过，3=拒绝
     */
    private Integer status;
    /**
     * 兑换备注
     */
    private String content;
    /**
     * 审批备注
     */
    private String examineContent;
    /**
     * 审批时间
     */
    private Timestamp examineTime;
    /**
     * 审批人
     */
    private String examineBy;
    /**
     * 审批人ID
     */
    private Long examineId;
    /**
     * 申请人ID
     */
    private Long accountId;
    /**
     * 申请人角色名称
     */
    private String accountName;
    /**
     * 申请人账号ID
     */
    private Long userId;

    /**
     * 申请人账号昵称
     */
    private String userName;
    /**
     * 创建时间
     */
    private Timestamp createTime;


}
