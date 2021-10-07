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
 * 军团用户表(CorpUser)实体类
 *
 * @author Sir丶雨轩
 * @since 2021-07-27 09:13:50
 */
@Setter
@Getter
@TableName("corp_user")
public class User implements Serializable {

    private Long id;
    private String uuid;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * QQ号
     */
    private String qq;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 是否是管理员
     */
    private Boolean isAdmin;
    /**
     * 最后登录时间
     */
    private Timestamp lastTime;
    /**
     * 最后登录IP
     */
    private String lastIp;
    /**
     * 最后登录城市
     */
    private String lastCity;
    /**
     * 创建时间
     */
    private Timestamp createTime;

    /**
     * 是否在军团
     */
    private Boolean corp;

}
