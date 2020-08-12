/*
 * Copyright 2013-2021 Sir丶雨轩
 *
 * This file is part of Sir丶雨轩/ehi-blog.

 * Sir丶雨轩/ehi-blog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.

 * Sir丶雨轩/ehi-blog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with Sir丶雨轩/ehi-blog.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.yuxuan66.modules.corp.service;

import com.alibaba.fastjson.JSONObject;
import com.yuxuan66.support.basic.http.RespEntity;
import org.springframework.stereotype.Service;

/**
 * @author Sir丶雨轩
 * @since 2021/8/24
 */
@Service
public class ScanService {



 /*35841	0M-103 » L-P3XM - LOVE shuhuanzhen	安塞波跳跃星门	2.0 AU
626	不会再被抓了	狂怒者级	-
            33475	移动式牵引装置04	移动式牵引装置	-
            33475	移动式牵引装置	移动式牵引装置	-
            35835	0M-103 - Bat Soup Kitchen	阿塔诺	6.1 AU
35835	0M-103 - suhuanzhen	阿塔诺	6.1 AU
    */

   /* 0010 Guang Jia
2 MuP
4516
        7115
        9527 yan
    A AX
    A-E-86
    AA Purvanen
    AAAAAAAAshterV
            ababacd
    aboder01
            aboder66
    AD 1-4-1
    Ada 118
    Adulyn Alabel
    Aelbrer Neye Audene*/
    public RespEntity scan(JSONObject data){
        String dataStr = data.getString("text");


        return RespEntity.success();
    }


}
