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
package com.yuxuan66.modules.media.mapper;

import com.yuxuan66.modules.media.entity.Media;
import com.yuxuan66.support.basic.BasicMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 军团媒体
 *
 * @author Sir丶雨轩
 * @since 2021/7/27
 */
@Mapper
public interface MediaMapper extends BasicMapper<Media> {


    /**
     * 获取某种类型top5的记录
     * @param type 类型
     * @return top5数据
     */
    @Select("select * from corp_media where `type`  = #{type} ORDER BY sort desc limit 5")
    List<Media> top5ByType(@Param("type") Integer type);
}
