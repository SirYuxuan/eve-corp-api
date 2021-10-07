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

import com.yuxuan66.bot.api.entity.BotMessage;

/**
 * 机器人分发器，所有组件需要继承
 * @author Sir丶雨轩
 * @since 2021/7/30
 */
public abstract class BotApiDispenser {

    /**
     * 消息分发。由各个子模块进行实现
     * @param botMessage 机器人消息
     */
    public abstract BotMessage dispenser(BotMessage botMessage);


}
