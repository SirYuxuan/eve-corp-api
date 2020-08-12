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
package com.yuxuan66.modules.tool.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yuxuan66.modules.tool.entity.JumpPathDto;
import com.yuxuan66.support.basic.http.RespEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 旗舰跳规划
 *
 * @author Sir丶雨轩
 * @since 2021/8/20
 */
@Service
public class JumpPathService {



    /**
     * 模糊查询星系名称
     *
     * @param name 星系名称
     * @return 星系名称列表
     */
    public RespEntity getSystemName(String name) {
        return RespEntity.success(JSON.parse(HttpUtil.get("https://eve.sgfans.org/navigator/ajax/auto_complete/solar_system_name?term=" + name)));
    }

    /**
     * 旗舰跳计算
     *
     * @param jumpPathDto 跳跃参数
     * @return 标准返回
     * @throws IOException IOException
     */
    public RespEntity calculation(JumpPathDto jumpPathDto) throws IOException {
        try {

            Map<String, Object> result = new HashMap<>();

            Map<String, String> param = JSON.toJavaObject(JSON.parseObject(JSONUtil.toJsonStr(jumpPathDto)), Map.class);

            Document document = Jsoup.connect("https://eve.sgfans.org/navigator/jump_path_layout").data(param).post();

            Elements tables = document.getElementsByClass("table-striped");

            Elements layout = tables.get(0).getElementsByTag("tr").get(1).getElementsByTag("td");

            List<Map<String, Object>> overview = new ArrayList<>();

            Map<String, Object> overviewItem = new HashMap<>();
            // 查询臭氧价格
            JSONObject price = JSON.parseObject(HttpUtil.get("https://www.ceve-market.org/tqapi/market/region/10000002/system/30000142/type/" + jumpPathDto.getIsotope() + ".json"));

            overviewItem.put("startingGalaxy", layout.get(0).html());
            overviewItem.put("targetGalaxy", layout.get(1).html());
            overviewItem.put("flagshipJumpDistance", layout.get(2).html());
            overviewItem.put("totalFuelConsumption", layout.get(3).html());
            overviewItem.put("price", price.getJSONObject("sell").getLongValue("min") * Convert.toLong(layout.get(3).html()));
            overview.add(overviewItem);

            List<Map<String, Object>> resultLine = new ArrayList<>();


            Elements lines = tables.get(1).getElementsByTag("tr");
            for (int i = 1; i < lines.size(); i++) {
                Element element = lines.get(i);
                Map<String, Object> resultLineItem = new HashMap<>();
                resultLineItem.put("number", element.getElementsByTag("td").get(0).text());
                resultLineItem.put("channel", element.getElementsByTag("td").get(1).text());
                resultLineItem.put("galaxy", element.getElementsByTag("td").get(2).text());
                resultLineItem.put("starDomain", element.getElementsByTag("td").get(3).text());
                resultLineItem.put("anEtAl", element.getElementsByTag("td").get(4).text());
                resultLineItem.put("distance", element.getElementsByTag("td").get(5).text());
                resultLineItem.put("fuel", element.getElementsByTag("td").get(6).text());
                resultLineItem.put("price", Convert.toLong(element.getElementsByTag("td").get(6).text()) * price.getJSONObject("sell").getLongValue("min"));

                resultLine.add(resultLineItem);
            }

            result.put("resultLine", resultLine);
            result.put("overview", overview);

            return RespEntity.success(result);
        } catch (Exception e) {
            return RespEntity.fail("请输入正确的星系位置");
        }
    }


}
