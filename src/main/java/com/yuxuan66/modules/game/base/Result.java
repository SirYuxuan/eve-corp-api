/*
 * Copyright (C) 2020 projectName:bot-gamecenter,author:yuxuan
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.yuxuan66.modules.game.base;

import cn.hutool.core.convert.Convert;

import java.util.HashMap;

public class Result extends HashMap<String, Object> {

    public boolean isOk() {
        return Convert.toInt(this.get("code"), -1) == 0;
    }

    public static Result fail(String msg) {
        Result result = new Result();
        result.put("code", 500);
        result.put("msg", msg);
        return result;
    }

    public static Result ok() {
        Result result = new Result();
        result.put("code", 0);
        return result;
    }

    public static Result ok(Object data) {
        Result result = ok();
        result.put("data", data);
        return result;
    }
}
