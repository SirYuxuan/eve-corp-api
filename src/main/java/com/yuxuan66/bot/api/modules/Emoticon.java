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
package com.yuxuan66.bot.api.modules;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yuxuan66.bot.api.ApiHelper;
import com.yuxuan66.bot.api.BotApiDispenser;
import com.yuxuan66.bot.api.entity.BotMessage;
import com.yuxuan66.modules.bot.entity.BotEmoticon;
import com.yuxuan66.modules.bot.mapper.BotEmoticonMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表情包
 *
 * @author Sir丶雨轩
 * @since 2021/7/30
 */
@Component
public class Emoticon extends BotApiDispenser {

    @Resource
    private BotEmoticonMapper botEmoticonMapper;

    @Override
    public BotMessage dispenser(BotMessage botMessage) {
        String name = ApiHelper.textCommandStr(botMessage);
        String[] arr = name.split(" ");

        if (arr.length > 2 && arr[0].equals("#表情包")) {
            String key = arr[1];
            List<BotEmoticon> botEmoticonList = botEmoticonMapper.selectList(new QueryWrapper<BotEmoticon>().eq("name", key));
            if (botEmoticonList.size() == 1) {
                String API_PATH = "https://www.52doutu.cn/api/";

                Map<String, Object> param = new HashMap<>();
                param.put("types", "maker");
                param.put("id", botEmoticonList.get(0).getValue());
                for (int i = 0; i < arr.length - 2; i++) {
                    param.put("str" + (i + 1), arr[i + 2]);
                }
                JSONObject jsonObject = JSONObject.parseObject(HttpUtil.post(API_PATH, param));
                String imgPath = jsonObject.getString("url");
                return ApiHelper.image(imgPath);
            }
        }


        return null;
    }


}
