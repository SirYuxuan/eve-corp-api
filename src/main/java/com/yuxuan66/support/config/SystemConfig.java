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
package com.yuxuan66.support.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 系统配置类，对应 application-system.yml
 *
 * @author Sir丶雨轩
 * @since 2021/7/27
 */
@Component
@Setter
@Getter
public class SystemConfig {

    /**
     * Eve 主军团ID，用来判断权限
     */
    @Value("${eve.mainCorp}")
    private Long eveMainCorp;

    /**
     * Eve 管理员邮箱
     */
    @Value("#{'${eve.managerMail}'.empty ? new String[] : '${eve.managerMail}'.split(',')}")
    private String[] eveManagerMail;

    /**
     * eve esi clientId
     */
    @Value("${eve.esi.clientId}")
    private String eveEsiClientId;

    /**
     * eve esi secretKey
     */
    @Value("${eve.esi.secretKey}")
    private String eveEsiSecretKey;

    /**
     * eve esi callbackPath
     */
    @Value("${eve.esi.callbackPath}")
    private String eveEsiCallbackPath;

    /**
     * eve esi scope
     */
    @Value("${eve.esi.scope}")
    private String eveEsiScope;

    /**
     * 网站前端的地址
     */
    @Value("${web.path}")
    private String webPath;
}
