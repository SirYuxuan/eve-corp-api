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
package com.yuxuan66.modules.calendar.service;

import com.yuxuan66.modules.calendar.mapper.CalendarMapper;
import com.yuxuan66.support.basic.http.RespEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 日程服务
 * @author Sir丶雨轩
 * @since 2021/7/27
 */
@Service
public class CalendarService {

    @Resource
    private CalendarMapper calendarMapper;

    /**
     * 查询全部的日程
     * @return 日程
     */
    public RespEntity all(){
        return RespEntity.success(calendarMapper.selectList(null));
    }
}
