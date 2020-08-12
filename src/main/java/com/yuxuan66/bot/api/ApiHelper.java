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
package com.yuxuan66.bot.api;

import cn.hutool.core.convert.Convert;
import com.yuxuan66.bot.api.entity.BotMessage;
import com.yuxuan66.bot.api.entity.BotMessageData;

import java.util.ArrayList;

/**
 * BotApi的帮助类
 *
 * @author Sir丶雨轩
 * @since 2021/7/30
 */
public class ApiHelper {


    /**
     * 判断消息是否是纯文本命令
     *
     * @param botMessage 消息
     * @return 结果
     */
    public static boolean textCommand(BotMessage botMessage) {
        if (botMessage.getMessageDataList().size() == 1) {
            BotMessageData messageData = botMessage.getMessageDataList().get(0);
            if (messageData.getType() == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取纯文本命令的内容
     *
     * @param botMessage 消息
     * @return 结果
     */
    public static String textCommandStr(BotMessage botMessage) {
        if (botMessage.getMessageDataList().size() == 1) {
            BotMessageData messageData = botMessage.getMessageDataList().get(0);
            if (messageData.getType() == 0) {
                return messageData.getMsg();
            }
        }
        return "";
    }

    /**
     * 构建返回消息 普通文本+at消息
     *
     * @param group 发送到的群
     * @param qq    @的qq
     * @param msg   消息内容
     * @return BotMessage
     */
    public static BotMessage textAt(Long qq, Long group, String msg) {
        BotMessage botMessage = new BotMessage();
        botMessage.setGroup(group);
        botMessage.setQq(qq);

        botMessage.setMessageDataList(new ArrayList<BotMessageData>() {{

            if (group != null) {
                BotMessageData at = new BotMessageData();
                at.setType(BotMsgType.AT);
                at.setMsg(Convert.toStr(qq));
                add(at);
            }

            BotMessageData message = new BotMessageData();
            message.setType(BotMsgType.TEXT);
            message.setMsg(" " + msg);
            add(message);

        }});

        return botMessage;
    }
    /**
     * 构建返回消息 普通文本
     *
     * @param group 发送到的群
     * @param qq    @的qq
     * @param msg   消息内容
     * @return BotMessage
     */
    public static BotMessage text(Long qq, Long group, String msg) {
        BotMessage botMessage = new BotMessage();
        botMessage.setGroup(group);
        botMessage.setQq(qq);

        botMessage.setMessageDataList(new ArrayList<BotMessageData>() {{
            BotMessageData message = new BotMessageData();
            message.setType(BotMsgType.TEXT);
            message.setMsg(" " + msg);
            add(message);

        }});

        return botMessage;
    }

    /**
     * 构建返回消息 图片消息
     *
     * @param url 图片地址
     * @return BotMessage
     */
    public static BotMessage image(String url) {
        BotMessage botMessage = new BotMessage();

        botMessage.setMessageDataList(new ArrayList<BotMessageData>() {{

            BotMessageData message = new BotMessageData();
            message.setType(BotMsgType.IMG);
            message.setMsg(url);
            add(message);

        }});

        return botMessage;
    }
}
