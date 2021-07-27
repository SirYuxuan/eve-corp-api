
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
package com.yuxuan66.modules.common.rest;

import cn.hutool.http.HttpUtil;
import com.yuxuan66.support.basic.http.RespEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sir丶雨轩
 * @since 2021/7/27
 */
@RestController
@RequestMapping(value = "/common")
public class CommonController {

    /**
     * 获取每日一言
     * @return 每日一言
     */
    @GetMapping(path = "/getDay")
    public RespEntity getDay() {
        try{
            String data = HttpUtil.get("http://guozhivip.com/yy/api/api.php").replaceAll("document.write\\(", "").replaceAll("\\);", "");
            return RespEntity.success(data.substring(1, data.length() - 1));
        }catch (Exception e){
            return RespEntity.success("数据接口异常，请联系系统管理员");
        }
    }
}
