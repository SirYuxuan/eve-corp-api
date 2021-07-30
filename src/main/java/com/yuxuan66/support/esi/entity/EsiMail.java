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
package com.yuxuan66.support.esi.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Mail格式
 * @author Sir丶雨轩
 * @since 2021/7/29
 */
@Setter
@Getter
public class EsiMail {

    private String body;
    private Integer approved_cost = 0;
    private String subject;
    private List<Recipients> recipients;

    @Setter
    @Getter
    public static class Recipients{
        private Integer recipient_id;
        private String recipient_type = "character";
    }
}
