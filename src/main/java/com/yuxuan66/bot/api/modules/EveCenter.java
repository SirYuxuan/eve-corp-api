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

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yuxuan66.bot.api.ApiHelper;
import com.yuxuan66.bot.api.BotApiDispenser;
import com.yuxuan66.bot.api.entity.BotMessage;
import com.yuxuan66.cache.modules.EveCache;
import com.yuxuan66.modules.bot.entity.BotAlias;
import com.yuxuan66.modules.bot.mapper.BotAliasMapper;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EVE相关查询
 *
 * @author Sir丶雨轩
 * @since 2021/7/30
 */
@Component
public class EveCenter extends BotApiDispenser {

    @Resource
    private EveCache eveCache;
    @Resource
    private BotAliasMapper botAliasMapper;

    @Override
    public BotMessage dispenser(BotMessage botMessage) {

        if (ApiHelper.textCommand(botMessage)) {
            String command = ApiHelper.textCommandStr(botMessage);
            if (command.startsWith(".tr ") && command.split(" ").length > 1) {
                return translate(botMessage);
            } else if (command.startsWith(".kb ") && command.split(" ").length > 1) {
                return getKB(botMessage);
            } else if (command.startsWith("jita") || command.startsWith("gjita")) {
                return getPrice(botMessage);
            }
        }


        return null;
    }

    /**
     * 翻译命令
     *
     * @return 机器人消息
     */
    public BotMessage translate(BotMessage botMessage) {
        String name = ApiHelper.textCommandStr(botMessage).replace(".tr ", "").trim();

        Map<String, Object> nameMapping = eveCache.getChineseToEnglishName();

        StringBuilder result = new StringBuilder();


        if (nameMapping.containsKey(name)) {
            result.append(nameMapping.get(name));
        } else {
            // 尝试遍历value
            for (String key : nameMapping.keySet()) {
                String enName = Convert.toStr(nameMapping.get(key));
                if (enName.equals(name)) {
                    result.append(key);
                    break;
                }
            }
        }

        if (0 == result.length()) {
            result = new StringBuilder("没有找到您想要查询的【" + name + "】,请尝试输入完整名称");
        }

        return ApiHelper.textAt(botMessage.getQq(), null, result.toString());
    }

    /**
     * 查询某个角色的KB
     *
     * @param botMessage 消息
     * @return 发送消息
     */
    public BotMessage getKB(BotMessage botMessage) {

        String name = ApiHelper.textCommandStr(botMessage).replace(".kb ", "").trim();

        try {
            String url = "https://zkillboard.com/autocomplete/" + name + "/";
            String result = HttpUtil.get(url);
            JSONArray jsonArray = JSONObject.parseArray(result);
            String cid = "";
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getString("name").equalsIgnoreCase(name.toString())) {
                    cid = jsonObject.getString("id");
                    break;
                }
            }
            StringBuilder stringBuffer = new StringBuilder();
            stringBuffer.append(name).append(" 击杀报告:\r\n");
            if (StrUtil.isNotBlank(cid)) {
                JSONObject jsonObject = JSONObject.parseObject(HttpUtil.get("https://zkillboard.com/api/stats/characterID/" + cid + "/"));
                // 击杀船只
                stringBuffer.append("击杀船只数: ").append(jsonObject.getString("allTimeSum")).append("\r\n");
                stringBuffer.append("击毁点数: ").append(jsonObject.getString("pointsDestroyed")).append("\r\n");
                stringBuffer.append("击毁ISK价值: ").append(NumberUtil.decimalFormat(",###", Convert.toLong(jsonObject.getString("iskDestroyed")))).append("\r\n");
                stringBuffer.append("损失船只数: ").append(jsonObject.getString("shipsLost")).append("\r\n");
                stringBuffer.append("损失点数: ").append(jsonObject.getString("pointsLost")).append("\r\n");
                stringBuffer.append("损失ISK价值: ").append(NumberUtil.decimalFormat(",###", Convert.toLong(jsonObject.getString("iskLost")))).append("\r\n");
                stringBuffer.append("威胁度: ").append(jsonObject.getString("dangerRatio")).append("%\r\n");
                return ApiHelper.textAt(botMessage.getQq(), botMessage.getGroup(), stringBuffer.toString());
            } else {
                return ApiHelper.textAt(botMessage.getQq(), botMessage.getGroup(), "对不起 没有找到您的击杀报告");
            }
        } catch (Exception e) {
            return ApiHelper.textAt(botMessage.getQq(), botMessage.getGroup(), "对不起 没有找到您的击杀报告");
        }
    }


    /**
     * 获取价格查询的标题
     *
     * @param isEur 是否是欧服
     * @return 标题
     */
    private StringBuilder getPriceTitle(boolean isEur) {
        String nowSer = isEur ? "欧服" : "国服";
        String reSer = isEur ? "国服" : "欧服";
        String tips = isEur ? "gjita" : "jita";
        return new StringBuilder("\r\n本查询结果为" + nowSer + "吉他价格," + reSer + "查询使用 " + tips + " xxxx\r\n=================");
    }

    /**
     * 获取价格查询的请求地址
     *
     * @param isEur 是否是欧服
     * @return 地址
     */
    private String getPriceUrl(boolean isEur) {
        return "https://www.ceve-market.org/" + (isEur ? "tqapi" : "api");
    }

    /**
     * 吉他价格查询
     *
     * @param botMessage 消息
     * @return 发送消息
     */
    public BotMessage getPrice(BotMessage botMessage) {

        String name = ApiHelper.textCommandStr(botMessage).replace(".kb ", "").trim();

        boolean isEur = name.startsWith("jita ");

        name = isEur ? name.substring(5) : name.substring(6);

        // 获取是否存在简写库
        List<BotAlias> botAliasList = botAliasMapper.selectList(new QueryWrapper<BotAlias>().eq("alias_name", name));

        if(botAliasList.size() == 1){
            name = botAliasList.get(0).getName();
        }

        String url = "https://www.ceve-market.org/api/searchname";

        Map<String, Object> param = new HashMap<>();
        param.put("name", name);

        String result = HttpUtil.post(url, param);

        JSONArray jsonArray = JSONObject.parseArray(result);

        if (jsonArray.isEmpty()) {
            return ApiHelper.textAt(botMessage.getQq(), botMessage.getGroup(), "查点人能看懂的?");
        }

        StringBuilder sendMessage = new StringBuilder();

        for (int i = 0; i < jsonArray.size(); i++) {

            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String typeId = jsonObject.getString("typeid");

            String itemStr = HttpUtil.get(getPriceUrl(isEur) + "/marketstat?typeid=" + typeId + "&usesystem=30000142");

            Document document = XmlUtil.parseXml(itemStr);
            Node buyNode = document.getDocumentElement().getFirstChild().getFirstChild().getChildNodes().item(0);
            Node sellNode = document.getDocumentElement().getFirstChild().getFirstChild().getChildNodes().item(1);
            String buyMax = buyNode.getChildNodes().item(2).getTextContent();
            String sellMin = sellNode.getChildNodes().item(3).getTextContent();

            if ((jsonObject.getString("typename").contains("涂装") && !name.contains("涂装")) ||(jsonObject.getString("typename").contains("蓝图") && !name.contains("蓝图"))) {
                continue;
            }

            sendMessage.append("\r\n").append(jsonObject.getString("typename")).append(" \r\n收单: ").append(NumberUtil.decimalFormat(",###", Convert.toLong(buyMax))).append(" ISK").append(" \r\n卖单: ").append(NumberUtil.decimalFormat(",###", Convert.toLong(sellMin))).append(" ISK\r\n=============");

        }
        return ApiHelper.textAt(botMessage.getQq(), botMessage.getGroup(), sendMessage.toString());
    }
}
