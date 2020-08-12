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
package com.yuxuan66.bot.api.modules;

import com.yuxuan66.bot.api.ApiHelper;
import com.yuxuan66.bot.api.BotApiDispenser;
import com.yuxuan66.bot.api.entity.BotMessage;
import com.yuxuan66.modules.game.base.Result;
import com.yuxuan66.modules.game.gobang.GoBang;
import org.springframework.stereotype.Component;

/**
 * @author Sir丶雨轩
 * @since 2021/8/24
 */
@Component
public class GameCenter extends BotApiDispenser {

    private final GoBang goBang;

    public GameCenter(GoBang goBang) {
        this.goBang = goBang;
    }

    @Override
    public BotMessage dispenser(BotMessage botMessage) {
        String command = ApiHelper.textCommandStr(botMessage);
        if (command.equals("创建对局")) {
            Result result = goBang.createGame(botMessage.getGroup() + "", botMessage.getQq() + "");
            if (result.isOk()) {
                return ApiHelper.textAt(botMessage.getQq(), botMessage.getGroup(), "对局创建成功，对局ID: " + result.get("data"));
            }
            return ApiHelper.textAt(botMessage.getQq(), botMessage.getGroup(), "对局创建失败，" + result.get("msg"));
        } else if (command.startsWith("加入对局 ")) {
            Result result = goBang.joinGame(botMessage.getGroup() + "", botMessage.getQq() + "", command.replace("加入对局 ", ""));
            if (result.isOk()) {
                return ApiHelper.textAt(botMessage.getQq(), botMessage.getGroup(), "加入对局成功，请对局双方任意一人发送开始对局，进行游戏");
            }
            return ApiHelper.textAt(botMessage.getQq(), botMessage.getGroup(), "加入对局失败: " + result.get("msg"));
        } else if (command.equals("开始对局")) {
            Result result = goBang.startGame(botMessage.getGroup() + "", botMessage.getQq() + "", false, 999999);
            if(result.isOk()){
                return ApiHelper.image("http://localhost:10002/goBang/showCheckerboard");
            }
            return ApiHelper.textAt(botMessage.getQq(),botMessage.getGroup(),"开始对局失败: " + result.get("msg"));
        }else if(command.startsWith("落子 ")){
            return ApiHelper.image("http://localhost:10002/goBang/fallenSon?fromGroup=" + botMessage.getGroup()+"&fromQQ="+botMessage.getQq()+"&position="+command.replace("落子 ",""));
        }

        return null;
    }
}
