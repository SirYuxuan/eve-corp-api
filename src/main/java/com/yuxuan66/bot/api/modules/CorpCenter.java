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
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yuxuan66.bot.api.ApiHelper;
import com.yuxuan66.bot.api.BotApiDispenser;
import com.yuxuan66.bot.api.BotMsgType;
import com.yuxuan66.bot.api.entity.BotMessage;
import com.yuxuan66.bot.api.entity.BotMessageData;
import com.yuxuan66.cache.modules.EveCache;
import com.yuxuan66.modules.assets.entity.UserAssets;
import com.yuxuan66.modules.assets.mapper.UserAssetsMapper;
import com.yuxuan66.modules.bot.entity.BotDict;
import com.yuxuan66.modules.bot.mapper.BotDictMapper;
import com.yuxuan66.modules.user.entity.User;
import com.yuxuan66.modules.user.entity.UserAccount;
import com.yuxuan66.modules.user.mapper.UserAccountMapper;
import com.yuxuan66.modules.user.mapper.UserMapper;
import com.yuxuan66.support.esi.EsiApi;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * 军团中心。负责军团消息的分发
 *
 * @author Sir丶雨轩
 * @since 2021/7/30
 */
@Component
public class CorpCenter extends BotApiDispenser {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserAccountMapper userAccountMapper;

    @Resource
    private UserAssetsMapper userAssetsMapper;
    @Resource
    private BotDictMapper botDictMapper;

    private final EsiApi esiApi;

    private final EveCache eveCache;

    public CorpCenter(EsiApi esiApi, EveCache eveCache) {
        this.esiApi = esiApi;
        this.eveCache = eveCache;
    }

    @Override
    public BotMessage dispenser(BotMessage botMessage) {

        if (botMessage.getGroup().equals(513542202L)) {
            return null;
        }

        if (ApiHelper.textCommand(botMessage)) {
            String command = ApiHelper.textCommandStr(botMessage);

            if ("LP".equalsIgnoreCase(command)) {
                return getLP(botMessage.getQq(), botMessage.getGroup(), botMessage.getQq());
            } else if (command.toUpperCase().startsWith("LP") && command.contains(" ")) {
                return getLP(Convert.toLong(command.split(" ")[1]), botMessage.getGroup(), botMessage.getQq());
            } else if ("INFO".equalsIgnoreCase(command) || (command.toUpperCase().startsWith("INFO") && command.contains(" "))) {
                return getInfo(botMessage);
            } else if (command.toUpperCase().startsWith("CK ") || command.toUpperCase().startsWith("CKQ ")) {
                return checkAssets(botMessage);
            } else if (command.equalsIgnoreCase("make")) {
                return getMake(botMessage);
            } else if (command.equalsIgnoreCase("rat")) {
                return rat(botMessage);
            } else{
                return thesaurus(botMessage);
            }

        }
        return null;
    }



    /**
     * 词库
     *
     * @param botMessage 机器人消息
     * @return 机器人消息
     */
    public BotMessage thesaurus(BotMessage botMessage) {
        String key = ApiHelper.textCommandStr(botMessage);
        List<BotDict> botDictList = botDictMapper.selectList(new QueryWrapper<BotDict>().eq("label", key));
        if (!botDictList.isEmpty()) {
            return ApiHelper.text(botMessage.getQq(), botMessage.getGroup(), botDictList.get(0).getValue());
        }
        return null;
    }

    /**
     * 校验是否存在指定资产
     *
     * @param botMessage 机器人消息
     * @return 机器人消息
     */
    public BotMessage checkAssets(BotMessage botMessage) {

        long qq = botMessage.getQq();
        String message = ApiHelper.textCommandStr(botMessage);

        String name = message.toUpperCase().replace("CK ", "");

        if (message.toUpperCase().startsWith("CKQ ")) {
            qq = Convert.toLong(message.toUpperCase().split(" ")[1]);
            name = message.toUpperCase().replace("CKQ " + qq + " ", "");
        }


        List<User> sendUserList = userMapper.selectList(new QueryWrapper<User>().eq("qq", botMessage.getQq()));

        if (!sendUserList.get(0).getIsAdmin() && qq != botMessage.getQq()) {
            return ApiHelper.textAt(botMessage.getQq(), botMessage.getGroup(), "对不起 您无权查询别人的资产.");
        }


        List<User> userList = userMapper.selectList(new QueryWrapper<User>().eq("qq", qq));

        List<UserAssets> assetsList = userAssetsMapper.selectList(new QueryWrapper<UserAssets>().eq("user_id", userList.get(0).getId()));

        StringBuilder result = new StringBuilder("\r\n查询到的资产如下\r\n");

        for (UserAssets userAssets : assetsList) {
            if (userAssets.getName() != null && userAssets.getName().contains(name)) {
                result.append("角色：" + userAssets.getAccountName() + "," + userAssets.getName() + "->" + userAssets.getNum() + (!userAssets.getBlueprintCopy() && (userAssets.getName().contains("蓝图") || userAssets.getName().contains("配方")) ? "(原图)" : "")).append("\r\n");
            }
        }


        return ApiHelper.textAt(botMessage.getQq(), botMessage.getGroup(), result.toString());
    }

    /**
     * 获取指定QQ的LP信息
     *
     * @param qq    qq
     * @param group group
     * @return lp信息
     */
    private BotMessage getLP(Long qq, Long group, Long sendQQ) {

        List<User> userList = userMapper.selectList(new QueryWrapper<User>().eq("qq", qq));
        List<User> sendUserList = userMapper.selectList(new QueryWrapper<User>().eq("qq", sendQQ));
        if (userList.isEmpty() || sendUserList.isEmpty()) {
            return ApiHelper.textAt(qq, group, (qq.equals(sendQQ) ? "您" : "他") + "没还有注册军团系统。\r\n军团系统地址：http://www.hd-eve.com");
        }


        if (userList.size() > 1 || sendUserList.size() > 1) {
            BotMessage botMessage = ApiHelper.textAt(qq, group, "您的用户数据异常，请联系雨轩处理！ ");

            List<BotMessageData> botMessageData = botMessage.getMessageDataList();

            BotMessageData at = new BotMessageData();
            at.setMsg("1718018032");
            at.setType(BotMsgType.AT);
            botMessageData.add(at);

            return botMessage;
        }


        if (!sendUserList.get(0).getIsAdmin() && !qq.equals(sendQQ)) {
            return ApiHelper.textAt(sendQQ, group, "对不起 您无权查询别人的LP.");
        }

        List<UserAccount> userAccountList = userAccountMapper.selectList(new QueryWrapper<UserAccount>().eq("user_id", userList.get(0).getId()));

        long nowLP = userAccountList.stream().mapToLong(UserAccount::getLpNow).sum();
        long totalLP = userAccountList.stream().mapToLong(UserAccount::getLpTotal).sum();
        long useLP = userAccountList.stream().mapToLong(UserAccount::getLpUse).sum();

        String result = userList.get(0).getNickName() + " 您的LP统计如下:\r\n";
        result += "共获得: " + totalLP;
        result += "\r\n已使用: " + useLP;
        result += "\r\n现剩余: " + nowLP;
        result += "\r\n感谢您为混沌做出的贡献!";

        return ApiHelper.textAt(sendQQ, group, result);
    }
    public BotMessage rat(BotMessage botMessage){

        List<User> userList = userMapper.selectList(new QueryWrapper<User>().eq("qq", botMessage.getQq()));

        if (userList.size() != 1) {
            return ApiHelper.textAt(botMessage.getQq(), botMessage.getGroup(), "对不起 您的数据异常.");
        }

        List<UserAccount> userAccountList = userAccountMapper.selectList(new QueryWrapper<UserAccount>().eq("user_id", userList.get(0).getId()));


        if (userAccountList.isEmpty()) {
            return ApiHelper.textAt(botMessage.getQq(),botMessage.getGroup(),"对不起您还没有注册系统或绑定QQ号.\r\nhttp://www.hd-eve.com");
        }
        StringBuilder result = new StringBuilder();
        try {

            result.append(userList.get(0).getNickName()).append(",刷怪信息:\r\n");

            long dayMoneyT = 0L;
            long weekMoneyT = 0L;
            long monthMoneyT = 0L;
            for (UserAccount user : userAccountList) {
                result.append(user.getName()).append("\r\n");
                if (user.getAccessToken() == null) {
                    result.append("当前角色授权丢失,请重新授权\r\n");
                    continue;
                }
                esiApi.refreshToken(user);

                String url = "https://esi.evetech.net/latest/characters/" + user.getCharacterId() + "/wallet/journal/?datasource=tranquility";
                HttpRequest request = HttpRequest.get(url);
                request.header("Authorization", "Bearer " + user.getAccessToken());
                String jsonStr= request.execute().body();
                if(!JSONUtil.isJsonArray(jsonStr)){
                    result.append("当前角色授权丢失,请重新授权\r\n");
                    continue;
                }
                JSONArray data = JSONArray.parseArray(jsonStr);

                Date dayDate = DateUtil.parseDate(DateUtil.today());
                Date weekDate = DateUtil.beginOfWeek(new Date());
                Date monthDate = DateUtil.beginOfMonth(new Date());

                long dayMoney = 0L;
                long weekMoney = 0L;
                long monthMoney = 0L;
                String dayLo = "";
                for (int i = 0; i < data.size(); i++) {
                    JSONObject brushStrange = data.getJSONObject(i);
                    if (brushStrange.getString("ref_type").equals("bounty_prizes")) {
                        //获取刷怪地点
                        String description = brushStrange.getString("description");

                        String amount = brushStrange.getString("amount");
                        String balance = brushStrange.getString("balance");
                        String local = "未知";
                        try {
                            local = description.substring(description.lastIndexOf("in") + 3);
                        } catch (Exception e) {
                        }
                        Date date = DateUtil.parse(brushStrange.getString("date")).setTimeZone(TimeZone.getDefault());
                        if (date.getTime() > dayDate.getTime()) {
                            dayMoney += Convert.toLong(amount) * 1.3;
                            if (!dayLo.contains(local)) {
                                dayLo += local + ",";
                            }

                        }
                        if (date.getTime() > weekDate.getTime()) {
                            weekMoney += Convert.toLong(amount) * 1.3;
                        }
                        if (date.getTime() > monthDate.getTime()) {
                            monthMoney += Convert.toLong(amount) * 1.3;
                        }
                    }
                }
                dayMoneyT += dayMoney;
                weekMoneyT += weekMoney;
                monthMoneyT += monthMoney;
                if (dayLo.length() > 0) {
                    dayLo = dayLo.substring(0, dayLo.length() - 1);
                }
                result.append("位置:" + dayLo + "\r\n");
                result.append("本日:" + NumberUtil.decimalFormat(",###", (dayMoney) / 1000000) + "M ISK\r\n");
                result.append("本周:" + NumberUtil.decimalFormat(",###", (weekMoney) / 1000000) + "M ISK\r\n");
                result.append("本月:" + NumberUtil.decimalFormat(",###", (monthMoney) / 1000000) + "M ISK\r\n");
            }
            result.append("==================\r\n");
            result.append("总计\r\n");
            result.append("本日:" + NumberUtil.decimalFormat(",###", (dayMoneyT) / 1000000) + "M ISK\r\n");
            result.append("本周:" + NumberUtil.decimalFormat(",###", (weekMoneyT) / 1000000) + "M ISK\r\n");
            result.append("本月:" + NumberUtil.decimalFormat(",###", (monthMoneyT) / 1000000) + "M ISK\r\n");
            result.append("本月共纳税:" + NumberUtil.decimalFormat(",###", (monthMoneyT / 1000000 * 0.15)) + "M ISK\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiHelper.textAt(botMessage.getQq(),botMessage.getGroup(),result.toString());
    }
    /**
     * 获取Info信息
     *
     * @param botMessage 消息
     * @return 返回
     */
    private BotMessage getInfo(BotMessage botMessage) {
        String name = ApiHelper.textCommandStr(botMessage);

        long sendQQ = botMessage.getQq();
        long qq = botMessage.getQq();
        if (name.toUpperCase().startsWith("INFO ")) {
            qq = Convert.toLong(name.toUpperCase().replace("INFO ", ""), -1L);
        }

        List<User> userList = userMapper.selectList(new QueryWrapper<User>().eq("qq", qq));
        if (userList.isEmpty()) {
            return ApiHelper.textAt(qq, botMessage.getGroup(), (qq == sendQQ ? "您" : "他") + "没还有注册军团系统。\r\n军团系统地址：http://www.hd-eve.com");
        }

        if (userList.size() > 1) {
            BotMessage newBotMessage = ApiHelper.textAt(qq, botMessage.getGroup(), "您的用户数据异常，请联系雨轩处理！ ");

            List<BotMessageData> botMessageData = newBotMessage.getMessageDataList();

            BotMessageData at = new BotMessageData();
            at.setMsg("1718018032");
            at.setType(BotMsgType.AT);
            botMessageData.add(at);

            return botMessage;
        }

        User user = userList.get(0);

        // 查询发消息的人

        List<User> userList1 = userMapper.selectList(new QueryWrapper<User>().eq("qq", botMessage.getQq()));
        if (userList1.size() != 1) {
            return ApiHelper.textAt(sendQQ, botMessage.getGroup(), "对不起 您的数据异常.");
        }

        if (!userList1.get(0).getIsAdmin() && qq != sendQQ) {
            return ApiHelper.textAt(sendQQ, botMessage.getGroup(), "对不起 您无权查询别人的信息.");
        }

        List<UserAccount> userAccountList = userAccountMapper.selectList(new QueryWrapper<UserAccount>().eq("user_id", userList.get(0).getId()));


        StringBuilder result = new StringBuilder();

        result.append(userList.get(0).getNickName()).append(",角色信息:\r\n");

        long lp = 0;
        long skill = 0;
        long isk = 0;

        for (UserAccount userAccount : userAccountList) {
            result.append(userAccount.getName()).append(":\r\n");

            if (StrUtil.isBlank(userAccount.getAccessToken())) {
                result.append("当前角色授权丢失,请重新授权\r\n");
                continue;
            }

            esiApi.setIskBalance(userAccount);
            esiApi.setSkillNum(userAccount);
            esiApi.setSkillInfo(userAccount);


            result.append("LP数: ").append(userAccount.getLpNow()).append("\r\n");
            result.append("技能点数: ").append(NumberUtil.decimalFormat(",###", userAccount.getSkill())).append("\r\n");
            result.append("ISK: ").append(NumberUtil.decimalFormat(",###", userAccount.getIsk() / 1000000)).append("M ISK\r\n");
            result.append("当前学习技能: ").append(userAccount.getSkillName()).append("\r\n");
            result.append("技能队列结束时间: ").append(userAccount.getSkillEndTime()).append("\r\n");

            lp += userAccount.getLpNow();
            skill += userAccount.getSkill();
            isk += userAccount.getIsk();
        }

        result.append("===============\r\n总资产:\r\n");

        result.append("LP数: ").append(lp).append("\r\n");
        result.append("技能点数: ").append(NumberUtil.decimalFormat(",###", skill)).append("\r\n");
        result.append("ISK: ").append(NumberUtil.decimalFormat(",###", isk / 1000000)).append("M ISK\r\n");


        return ApiHelper.textAt(botMessage.getQq(), botMessage.getGroup(), result.toString());
    }

    private BotMessage getMake(BotMessage botMessage) {

        String name = ApiHelper.textCommandStr(botMessage);

        long sendQQ = botMessage.getQq();
        long qq = botMessage.getQq();
        if (name.toUpperCase().startsWith("INFO ")) {
            qq = Convert.toLong(name.toUpperCase().replace("INFO ", ""), -1L);
        }

        List<User> userList = userMapper.selectList(new QueryWrapper<User>().eq("qq", qq));
        if (userList.isEmpty()) {
            return ApiHelper.textAt(qq, botMessage.getGroup(), (qq == sendQQ ? "您" : "他") + "没还有注册军团系统。\r\n军团系统地址：http://www.hd-eve.com");
        }

        if (userList.size() > 1) {
            BotMessage newBotMessage = ApiHelper.textAt(qq, botMessage.getGroup(), "您的用户数据异常，请联系雨轩处理！ ");

            List<BotMessageData> botMessageData = newBotMessage.getMessageDataList();

            BotMessageData at = new BotMessageData();
            at.setMsg("1718018032");
            at.setType(BotMsgType.AT);
            botMessageData.add(at);

            return botMessage;
        }


        // 查询发消息的人

        List<User> userList1 = userMapper.selectList(new QueryWrapper<User>().eq("qq", botMessage.getQq()));
        if (userList1.size() != 1) {
            return ApiHelper.textAt(sendQQ, botMessage.getGroup(), "对不起 您的数据异常.");
        }

        if (!userList1.get(0).getIsAdmin() && qq != sendQQ) {
            return ApiHelper.textAt(sendQQ, botMessage.getGroup(), "对不起 您无权查询别人的信息.");
        }

        List<UserAccount> userAccountList = userAccountMapper.selectList(new QueryWrapper<UserAccount>().eq("user_id", userList.get(0).getId()));


        StringBuilder result = new StringBuilder();

        result.append(userList.get(0).getNickName()).append(",工业制造信息:\r\n");



        for (UserAccount userAccount : userAccountList) {


            if (StrUtil.isBlank(userAccount.getAccessToken())) {
                result.append("当前角色授权丢失,请重新授权\r\n");
                continue;
            }
            esiApi.refreshToken(userAccount);
            HttpRequest request = HttpUtil.createGet("https://esi.evetech.net/latest/characters/"+userAccount.getCharacterId()+"/industry/jobs/?datasource=tranquility&include_completed=false");
            request.header("Authorization", "Bearer " + userAccount.getAccessToken());
            String jsonStr = request.execute().body();
            JSONArray jsonArray = JSONObject.parseArray(jsonStr);

            Map<Integer,String> nameMapping = new HashMap<>();
            eveCache.getEveItemName().stream().filter(item->item.getType() == 8).forEach(item->nameMapping.put(item.getItemId(), item.getZhName()));

            if(!jsonArray.isEmpty()){
                result.append(userAccount.getName()).append(":\r\n");
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject data = jsonArray.getJSONObject(i);
                    int typeId = data.getInteger("blueprint_type_id");

                    result.append(nameMapping.get(typeId)+"," + DateUtil.formatDateTime(data.getTimestamp("end_date")) + "\r\n");

                }
            }


        }


        return ApiHelper.textAt(botMessage.getQq(), botMessage.getGroup(), result.toString());
    }
}
