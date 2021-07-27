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
package com.yuxuan66.support.esi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Esi 一个角色详细信息
 * @author Sir丶雨轩
 * @since 2021/7/27
 */
@Setter
@Getter
public class EsiAccountInfo {

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

}
