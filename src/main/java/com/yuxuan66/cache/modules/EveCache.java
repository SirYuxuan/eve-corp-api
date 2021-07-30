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
package com.yuxuan66.cache.modules;

import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSONObject;
import com.yuxuan66.cache.CacheKey;
import com.yuxuan66.cache.RedisUtil;
import com.yuxuan66.modules.eve.entity.EveItemName;
import com.yuxuan66.modules.eve.mapper.EveItemNameMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sir丶雨轩
 * @since 2021/7/30
 */
@Component
public class EveCache {

    @Resource
    private RedisUtil redisUtil;
    @Resource
    private EveItemNameMapper eveItemNameMapper;


    /**
     * 获取EVE的物品名称列表
     *
     * @return 物品名称列表
     */
    public List<EveItemName> getEveItemName() {
        if (redisUtil.hasKey(CacheKey.CACHE_EVE_ITEM_NAME_LIST)) {
            return JSONObject.parseArray(redisUtil.get(CacheKey.CACHE_EVE_ITEM_NAME_LIST).toString(),EveItemName.class);
        }

        List<EveItemName> eveItemNameList = eveItemNameMapper.selectList(null);
        redisUtil.set(CacheKey.CACHE_EVE_ITEM_NAME_LIST, JSONObject.toJSONString(eveItemNameList));

        return eveItemNameList;
    }

    /**
     * 获取EVE物品名称 英文到中文的映射
     * @return 映射关系
     */
    public Map<String, Object> getChineseToEnglishName() {
        if (redisUtil.hasKey(CacheKey.CACHE_EVE_CHINESE_TO_ENGLISH_NAME)) {
            return JSONObject.parseObject(Convert.toStr(redisUtil.get(CacheKey.CACHE_EVE_CHINESE_TO_ENGLISH_NAME)));
        }

        Map<String, Object> nameMapping = new HashMap<>();

        for (EveItemName eveItemName : getEveItemName()) {

            nameMapping.put(eveItemName.getZhName(), eveItemName.getEnName());
        }
        redisUtil.set(CacheKey.CACHE_EVE_CHINESE_TO_ENGLISH_NAME,JSONObject.toJSONString(nameMapping));
        return nameMapping;

    }


}
