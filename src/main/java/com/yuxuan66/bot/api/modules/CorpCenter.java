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
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yuxuan66.bot.api.ApiHelper;
import com.yuxuan66.bot.api.BotApiDispenser;
import com.yuxuan66.bot.api.BotMsgType;
import com.yuxuan66.bot.api.entity.BotMessage;
import com.yuxuan66.bot.api.entity.BotMessageData;
import com.yuxuan66.modules.user.entity.User;
import com.yuxuan66.modules.user.entity.UserAccount;
import com.yuxuan66.modules.user.mapper.UserAccountMapper;
import com.yuxuan66.modules.user.mapper.UserMapper;
import com.yuxuan66.support.esi.EsiApi;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

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

    private final EsiApi esiApi;

    public CorpCenter(EsiApi esiApi) {
        this.esiApi = esiApi;
    }

    @Override
    public BotMessage dispenser(BotMessage botMessage) {
        if (ApiHelper.textCommand(botMessage)) {
            String command = ApiHelper.textCommandStr(botMessage);

            if ("LP".equalsIgnoreCase(command)) {
                return getLP(botMessage.getQq(), botMessage.getGroup(), botMessage.getQq());
            } else if (command.toUpperCase().startsWith("LP") && command.contains(" ")) {
                return getLP(Convert.toLong(command.split(" ")[1]), botMessage.getGroup(), botMessage.getQq());
            } else if ("INFO".equalsIgnoreCase(command) || (command.toUpperCase().startsWith("INFO") && command.contains(" "))) {
                return getInfo(botMessage);
            }

        }
        return null;
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
            qq = Convert.toLong(name.replace("INFO ", ""), -1L);
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

        if (!user.getIsAdmin() && qq != sendQQ) {
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
}
