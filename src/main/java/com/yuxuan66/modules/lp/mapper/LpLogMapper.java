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
package com.yuxuan66.modules.lp.mapper;

import com.yuxuan66.modules.lp.entity.LpLog;
import com.yuxuan66.support.basic.BasicMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * LP操作
 * @author Sir丶雨轩
 * @since 2021/7/27
 */
@Mapper
public interface LpLogMapper extends BasicMapper<LpLog> {

    /**
     * 根据LP发放记录获取总的LP获取排行
     * @return LP获取排行
     */
    @Select( "select character_name as name,sum(lp) as lp from corp_lp_log WHERE type =2  GROUP BY character_name ORDER BY sum(lp) desc limit 10")
    List<Map<String,Object>> top10();


    /**
     * 获取一个月内的LP获取排行
     * @return LP获取排行
     */
    @Select("select character_name as name,sum(lp) as lp from corp_lp_log WHERE type =2 and date_format(create_time,'%Y-%m')=date_format(now(),'%Y-%m') GROUP BY character_name ORDER BY sum(lp) desc limit 10")
    List<Map<String,Object>> top10ByMonth();
}
