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

import cn.hutool.extra.spring.SpringUtil;
import com.yuxuan66.bot.api.entity.BotMessage;
import com.yuxuan66.support.basic.http.RespEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sir丶雨轩
 * @since 2021/7/30
 */
@Service
public class BotApiService {

    /**
     * 对机器人的消息进行分发
     *
     * @param botMessage 消息内容
     * @return 分发后结果
     */
    public RespEntity dispenser(BotMessage botMessage) {

        List<BotApiDispenser> dispenserList = new ArrayList<>();

        SpringUtil.getBeansOfType(BotApiDispenser.class).forEach((name, bean) -> dispenserList.add(bean));

        // TODO 后期使用Socket可以实现一次消息多次回复，目前仅支持一个分发器完成动作

        for (BotApiDispenser dispenser : dispenserList) {
            BotMessage newMessage = dispenser.dispenser(botMessage);
            if (newMessage != null) {
                return RespEntity.success(newMessage);
            }
        }

        return RespEntity.success();
    }
}
