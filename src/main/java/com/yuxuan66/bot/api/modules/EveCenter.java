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
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.yuxuan66.bot.api.ApiHelper;
import com.yuxuan66.bot.api.BotApiDispenser;
import com.yuxuan66.bot.api.BotMsgType;
import com.yuxuan66.bot.api.entity.BotMessage;
import com.yuxuan66.bot.api.entity.BotMessageData;
import com.yuxuan66.cache.modules.EveCache;
import com.yuxuan66.modules.bot.entity.BotAlias;
import com.yuxuan66.modules.bot.mapper.BotAliasMapper;
import com.yuxuan66.modules.corp.entity.GroupTime;
import com.yuxuan66.modules.corp.mapper.GroupTimeMapper;
import com.yuxuan66.modules.eve.entity.EveItemName;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
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
    @Resource
    private GroupTimeMapper groupTimeMapper;

    @Override
    public BotMessage dispenser(BotMessage botMessage) {

        if (ApiHelper.textCommand(botMessage)) {
            String command = ApiHelper.textCommandStr(botMessage);
            if (command.startsWith(".tr ") && command.split(" ").length > 1) {
                return translate(botMessage);
            } else if (command.startsWith(".kb ") && command.split(" ").length > 1) {
                return getKB(botMessage);
            } else if (command.toLowerCase().startsWith("jita ") || command.toLowerCase().startsWith("gjita ") || command.toLowerCase().startsWith(".col ") || command.toLowerCase().startsWith(".gcol ")) {
                return getPrice(botMessage);
            } else if (command.startsWith("as ") && command.split(" ").length >= 3) {
                return addAlias(botMessage);
            } else if (command.startsWith(".pic ")) {
                return getPic(botMessage);
            }else if (command.startsWith(".head ")) {
                return getHead(botMessage);
            }else if(command.startsWith(".con") || command.startsWith(".gon")){
                try {
                    return getMPic(botMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        return null;
    }

    public BotMessage getMPic(BotMessage message) throws IOException {
        String data1 = ApiHelper.textCommandStr(message).replace(".con","").replace(".gon","").trim();
        Connection.Response response1 = Jsoup.connect("https://eve.sgfans.org/navigator/jump_path_layout").execute();
        String csrfmiddlewaretoken = response1.parse().getElementsByAttributeValue("name","csrfmiddlewaretoken").get(0).val();
        Map<String,Object> data = new HashMap<>();
        System.out.println(csrfmiddlewaretoken);
        data.put("scanData",data1);
        data.put("csrfmiddlewaretoken",csrfmiddlewaretoken);
        data.put("server",ApiHelper.textCommandStr(message).startsWith(".con")?"tqcn":"srcn");
        HttpRequest request = HttpUtil.createPost("https://tools.ceve-market.org/contract/");
        request.form(data);
        request.header("referer","https://tools.ceve-market.org/contract/");
        request.header("cookie","csrftoken="+csrfmiddlewaretoken+";");
        HttpResponse response = request.execute();
        String path = response.header("location");
        System.out.println(path);
        WebClient web = new WebClient();
        web.getOptions().setCssEnabled(false);
        web.getOptions().setJavaScriptEnabled(true);
        web.getOptions().setThrowExceptionOnScriptError(false);
        web.getOptions().setUseInsecureSSL(true);
        HtmlPage page = web.getPage("https://tools.ceve-market.org/" + path);
        web.waitForBackgroundJavaScript(5000);
        org.jsoup.nodes.Document document = Jsoup.parse(page.asXml());


        return ApiHelper.textAt(message.getQq(),message.getGroup(),"估价为:\r\n" + "收单: " +document.getElementById("contract_buy_all").text()+"\r\n卖单: " + document.getElementById("contract_sell_all").text());
    }

    public BotMessage getPic(BotMessage botMessage) {
        String name = ApiHelper.textCommandStr(botMessage).replace(".pic ", "").trim();

        for (EveItemName eveItemName : eveCache.getEveItemName()) {
            if((eveItemName.getZhName().equals(name) || eveItemName.getEnName().equalsIgnoreCase(name)) && eveItemName.getType() == 8){
                return ApiHelper.image("https://cdn.yuxuan66.com/eve/Types/"+eveItemName.getItemId()+"_64.png");
            }
        }

        return null;
    }
    public BotMessage getHead(BotMessage botMessage) {
        String name = ApiHelper.textCommandStr(botMessage).replace(".head ", "").trim();
        JSONArray jsonArray = JSONArray.parseArray(HttpUtil.get("https://zkillboard.com/autocomplete/"+name+"/"));
        if(jsonArray.isEmpty()){
            return ApiHelper.textAt(botMessage.getQq(),botMessage.getGroup(),"没有找到指定用户");
        }
        int index = 0;
        if(jsonArray.size() != 1){
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject temp = jsonArray.getJSONObject(i);
                if(temp.getString("name").equalsIgnoreCase(name)){
                    index=i;
                    break;
                }
            }
        }

        String cid = jsonArray.getJSONObject(index).getString("id");
        String cname = jsonArray.getJSONObject(index).getString("name");

        JSONObject data =JSONObject.parseObject(HttpUtil.get("https://esi.evetech.net/latest/characters/"+cid+"/?datasource=tranquility"));
        String alliance_id = data.getString("alliance_id");
        String corporation_id = data.getString("corporation_id");
        String corporationName = JSONObject.parseObject(HttpUtil.get("https://esi.evetech.net/latest/corporations/"+corporation_id+"/?datasource=tranquility")).getString("name");
        String allianceName = JSONObject.parseObject(HttpUtil.get("https://esi.evetech.net/latest/alliances/"+alliance_id+"/?datasource=tranquility")).getString("name");

        BotMessage botMessage1 = ApiHelper.image("https://images.evetech.net/characters/"+cid+"/portrait?size=256");
        BotMessageData botMessageData = new BotMessageData();
        botMessageData.setType(BotMsgType.TEXT);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("角色名称: " + cname+"\r\n");
        stringBuilder.append("军团名称: " + corporationName+"\r\n");
        stringBuilder.append("联盟名称: " + allianceName+"\r\n");
        botMessageData.setMsg(stringBuilder.toString());
        botMessage1.getMessageDataList().add(botMessageData);
        return botMessage1;
    }



    /**
     * 添加别名
     *
     * @return 机器人消息
     */
    public BotMessage addAlias(BotMessage botMessage) {
        String[] arr = ApiHelper.textCommandStr(botMessage).split(" ");
        int count = botAliasMapper.selectCount(new QueryWrapper<BotAlias>().eq("alias_name", arr[1]));
        if (count > 0) {
            return ApiHelper.textAt(botMessage.getQq(), botMessage.getGroup(), "改别名已经存在，请勿重复添加");
        }
        BotAlias botAlias = new BotAlias();
        botAlias.setStatus(false);
        botAlias.setAliasName(arr[1]);
        String name = "";
        for (int i = 2; i < arr.length; i++) {
            name += arr[i] + (i == arr.length - 1 ? "" : " ");
        }
        botAlias.setName(name);
        botAliasMapper.insert(botAlias);
        return ApiHelper.textAt(botMessage.getQq(), botMessage.getGroup(), "感谢您的支持，此别名在雨轩审核后生效");
    }

    public static void main1(String[] args) {
        String content = FileUtil.readString(new File("C:\\Users\\Administrator\\Desktop\\A"), Charset.defaultCharset());
        Map<String, String> result = new HashMap<>();
        for (String arr : content.split("\r\n")) {
            String[] item = arr.trim().split("        ");
            if (item.length == 3) {
                if (!result.containsKey(item[1])) {
                    result.put(item[1], item[2]);
                }

            }
            if (item.length == 5) {
                result.put("Metropolis", "美特伯里斯");
                if (!result.containsKey(item[0])) {
                    result.put(item[0], item[3]);
                }
                if (!result.containsKey(item[1])) {
                    result.put(item[1], item[4]);
                }

            }
        }

        List<Map<String, Object>> excel = new ArrayList<>();
        for (Map.Entry<String, String> stringStringEntry : result.entrySet()) {
            Map<String, Object> temp = new HashMap<>();
            temp.put("name", stringStringEntry.getKey());
            temp.put("zhName", stringStringEntry.getValue());
            excel.add(temp);
        }

        ExcelWriter writer = ExcelUtil.getWriter(new File("C://a.xls"));
        writer.write(excel);
        writer.flush();
        System.out.println(JSON.toJSONString(result));
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
                if (enName.equalsIgnoreCase(name)) {
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

        GroupTime groupTime = groupTimeMapper.selectOne(new QueryWrapper<GroupTime>().eq("`group`",botMessage.getGroup()+""));
        if(groupTime == null){
            return ApiHelper.textAt(botMessage.getQq(),botMessage.getGroup(),"对不起 本群尚未开通吉他查询功能，如需开通请联系Q：1718018032，如不需要此服务请踢出机器人");
        }
        if(groupTime.getEndTime().getTime() < System.currentTimeMillis()){
            return ApiHelper.textAt(botMessage.getQq(),botMessage.getGroup(),"对不起 本群吉他查询功能已到期，如需续费请联系Q：1718018032，如不需要此服务请踢出机器人");
        }



        String name = ApiHelper.textCommandStr(botMessage).replace(".kb ", "").trim();

        boolean isAll = name.toLowerCase().startsWith(".col ") || name.toLowerCase().startsWith(".gcol ");

        boolean isEur = name.toLowerCase().startsWith("jita ") || name.toLowerCase().startsWith(".col ");

        name = isEur ? name.substring(5) : name.substring(6);

        // 获取是否存在简写库
        List<BotAlias> botAliasList = botAliasMapper.selectList(new QueryWrapper<BotAlias>().eq("alias_name", name).eq("status", true));

        String enName = "";

        if (botAliasList.size() == 1) {
            name = botAliasList.get(0).getName();
        } else {
            // 简写库不存在 尝试翻译
            Map<String, Object> nameMapping = eveCache.getChineseToEnglishName();
            if (nameMapping.containsKey(name)) {
                enName = nameMapping.get(name).toString();
            } else {
                for (String key : nameMapping.keySet()) {
                    if (nameMapping.get(key).toString().equals(name)) {
                        enName = name;
                        name = key;
                    }
                }
            }
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

        long allBuy = 0;
        long allSell = 0;

        for (int i = 0; i < jsonArray.size(); i++) {

            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String typeId = jsonObject.getString("typeid");

            String itemStr = HttpUtil.get(getPriceUrl(isEur) + "/marketstat?typeid=" + typeId + "&usesystem=30000142");

            Document document = XmlUtil.parseXml(itemStr);
            Node buyNode = document.getDocumentElement().getFirstChild().getFirstChild().getChildNodes().item(0);
            Node sellNode = document.getDocumentElement().getFirstChild().getFirstChild().getChildNodes().item(1);
            String buyMax = buyNode.getChildNodes().item(2).getTextContent();
            String sellMin = sellNode.getChildNodes().item(3).getTextContent();
            allBuy += Convert.toLong(buyMax);
            allSell += Convert.toLong(sellMin);

            if ((jsonObject.getString("typename").contains("涂装") && !name.contains("涂装")) || (jsonObject.getString("typename").contains("蓝图") && !name.contains("蓝图"))) {
                continue;
            }

            String typeName = jsonObject.getString("typename");

            if (typeName.equals(name) && StrUtil.isNotBlank(enName)) {
                typeName = name + "===>" + enName;
            }

            sendMessage.append("\r\n").append(typeName).append(" \r\n收单: ").append(NumberUtil.decimalFormat(",###", Convert.toLong(buyMax))).append(" ISK").append(" \r\n卖单: ").append(NumberUtil.decimalFormat(",###", Convert.toLong(sellMin))).append(" ISK\r\n=============");
            if (name.equals("伊甸币")) {
                sendMessage.append("\r\n500*伊甸币价格");
                sendMessage.append("\r\n收单: ").append(NumberUtil.decimalFormat(",###", Convert.toLong(buyMax) * 500)).append(" ISK");
                sendMessage.append("\r\n卖单: ").append(NumberUtil.decimalFormat(",###", Convert.toLong(sellMin) * 500)).append(" ISK");
                sendMessage.append("\r\n=============");

            }
        }
        if (isAll) {
            sendMessage.append("\r\n全套收单: ").append(NumberUtil.decimalFormat(",###", allBuy)).append(" ISK");
            sendMessage.append("\r\n全套卖单: ").append(NumberUtil.decimalFormat(",###", allSell)).append(" ISK");
            sendMessage.append("\r\n=============");

        }

        return ApiHelper.textAt(botMessage.getQq(), botMessage.getGroup(), sendMessage.toString());
    }
}
