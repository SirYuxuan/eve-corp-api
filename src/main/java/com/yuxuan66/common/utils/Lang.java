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
package com.yuxuan66.common.utils;

import java.sql.Timestamp;

/**
 * 提供一些常用静态工具方法
 * @author Sir丶雨轩
 * @since 2021/7/27
 */
public final class Lang {


    /**
     * 时间戳转换为sql Timestamp
     * @param time 时间戳
     * @return sql Timestamp
     */
    public static Timestamp getTime(long time){
        return new Timestamp(time);
    }

    /**
     * 当前时间转换为sql Timestamp
     * @return sql Timestamp
     */
    public static Timestamp getTime(){
        return getTime(System.currentTimeMillis());
    }
}
