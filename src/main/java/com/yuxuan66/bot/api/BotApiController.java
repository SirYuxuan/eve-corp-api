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
import com.yuxuan66.support.basic.http.RespEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 雨轩最新版机器人API
 * @author Sir丶雨轩
 * @since 2021/7/30
 */
@RestController
@RequestMapping(path = "/botApi")
public class BotApiController {

    private final BotApiService botApiService;

    public BotApiController(BotApiService botApiService) {
        this.botApiService = botApiService;
    }

    @PostMapping(path = "/dispenser")
    public RespEntity dispenser(@RequestBody BotMessage botMessage){
        return botApiService.dispenser(botMessage);
    }
}
