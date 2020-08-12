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
package com.yuxuan66.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * (CorpUserAccount)实体类
 *
 * @author Sir丶雨轩
 * @since 2021-07-27 09:22:08
 */
@Setter
@Getter
@TableName("corp_user_account")
public class UserAccount implements Serializable {

    private Long id;
    /**
     * 角色名称
     */
    private String name;
    /**
     * 角色ID
     */
    private Long characterId;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 是否是主号
     */
    private Boolean isMain;
    /**
     * ESI/Token
     */
    private String accessToken;
    /**
     * ESI/Token
     */
    private String refreshToken;
    /**
     * 军团ID
     */
    private Long corpId;
    /**
     * 军团名称
     */
    private String corpName;
    /**
     * 联盟ID
     */
    private Long allianceId;
    /**
     * 联盟名称
     */
    private String allianceName;
    /**
     * ISK数量
     */
    private Long isk;
    /**
     * 技能点数量
     */
    private Long skill;
    /**
     * 技能到期中文名字
     */
    private String skillName;
    /**
     * 技能英文名字
     */
    private String skillEnName;
    /**
     * 技能学习到期事件
     */
    private Timestamp skillEndTime;
    /**
     * LP当前数量
     */
    private Long lpNow;
    /**
     * LP总计获得数量
     */
    private Long lpTotal;
    /**
     * LP已使用数量
     */
    private Long lpUse;
    /**
     * 创建时间
     */
    private Timestamp createTime;

    /**
     * 成员加入军团的时间
     */
    private Timestamp joinTime;




}
