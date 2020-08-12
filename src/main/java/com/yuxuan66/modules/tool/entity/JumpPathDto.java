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
package com.yuxuan66.modules.tool.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @author Sir丶雨轩
 * @since 2021/8/20
 */
@Setter
@Getter
public class JumpPathDto {

    private String csrfmiddlewaretoken;
    private String ship_type;
    @JsonProperty("CharacterJumpDriveCalibration")
    private String CharacterJumpDriveCalibration;
    @JsonProperty("CharacterJumpFuelConservation")
    private String CharacterJumpFuelConservation;
    @JsonProperty("CharacterJumpFreighters")
    private String CharacterJumpFreighters;
    private String start_solar_system;
    private String end_solar_system;
    private String isotope;

   @SneakyThrows
   public JumpPathDto(){
       Document document = Jsoup.connect("https://eve.sgfans.org/navigator/jump_path_layout").get();
       this.csrfmiddlewaretoken = document.getElementsByAttributeValue("name","csrfmiddlewaretoken").get(0).val();
   }
}
