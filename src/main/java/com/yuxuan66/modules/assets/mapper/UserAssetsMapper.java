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
package com.yuxuan66.modules.assets.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuxuan66.modules.assets.entity.UserAssets;
import com.yuxuan66.support.basic.BasicMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * @author Sir丶雨轩
 * @since 2021/8/9
 */
@Mapper
public interface UserAssetsMapper extends BasicMapper<UserAssets> {

    @Select("select ua.`name`,ua.access_token,(select count(1) from corp_user_assets uas where uas.account_id = ua.id and uas.`name` = #{assetsName}) num,corp_name,join_time from corp_user_account ua where ua.name like CONCAT('%',#{name},'%') and ua.join_time >= #{joinTime} order by num desc")
    IPage<Map<String, Object>> selectAssetsPage(@Param("page") Page page, String assetsName, String name, String joinTime);
}
