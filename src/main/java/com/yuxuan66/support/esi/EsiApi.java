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
package com.yuxuan66.support.esi;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.yuxuan66.support.config.SystemConfig;
import com.yuxuan66.support.esi.entity.EsiAccountInfo;
import com.yuxuan66.support.esi.entity.EsiTokenInfo;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Eve Esi Api
 *
 * @author Sir丶雨轩
 * @since 2021/7/27
 */
@Component
public class EsiApi {

    private final SystemConfig systemConfig;

    public EsiApi(SystemConfig systemConfig) {
        this.systemConfig = systemConfig;
    }

    /**
     * 构建授权地址
     *
     * @param userId 用户ID
     * @return 授权地址
     */
    public String buildAuthPath(Long userId) {
        return "https://login.eveonline.com/v2/oauth/authorize?response_type=code&redirect_uri=" + URLUtil.encode(systemConfig.getEveEsiCallbackPath()) +
                "&client_id=" +
                systemConfig.getEveEsiClientId() +
                "&state=" +
                (userId == null ? "NONE" : userId) +
                "&scope=" +
                systemConfig.getEveEsiScope();
    }


    /**
     * 通过授权后的Code换取Info信息
     * @param code 授权后的Code
     * @return token信息
     */
    public EsiTokenInfo codeToInfo(String code) {

        Map<String, Object> param = new HashMap<String, Object>() {{
            put("grant_type", "authorization_code");
            put("code", code);
        }};

        HttpRequest request = HttpUtil.createPost("https://login.eveonline.com/v2/oauth/token");
        request.basicAuth(systemConfig.getEveEsiClientId(), systemConfig.getEveEsiSecretKey());

        request.form(param);

        String requestBody = request.execute().body();

        JSONObject token = JSONObject.parseObject(requestBody);

        String accessToken = token.getString("access_token");

        String tokenBase64 = accessToken.split("\\.")[1];

        JSONObject userInfo = JSONObject.parseObject(Base64.decodeStr(tokenBase64));

        String characterId = userInfo.getString("sub").split(":")[2];

        EsiTokenInfo tokenInfo = new EsiTokenInfo();
        tokenInfo.setAccessToken(token.getString("access_token"));
        tokenInfo.setRefreshToken(token.getString("refresh_token"));
        tokenInfo.setName(userInfo.getString("name"));
        tokenInfo.setCharacterId(Convert.toLong(characterId));

        return tokenInfo;

    }

    /**
     * 获取一个角色的权限
     * @param characterId 角色ID
     * @param accessToken token
     * @return 角色列表
     */
    public static List<String> getUserRoles(Long characterId, String accessToken) {
        HttpRequest request = HttpUtil.createGet("https://esi.evetech.net/legacy/characters/" + characterId + "/roles/");
        request.header("Authorization", "Bearer " + accessToken);
        String body = request.execute().body();
        return JSONObject.parseObject(body).getJSONArray("roles").toJavaList(String.class);
    }

    /**
     * 获取一个角色的详细信息
     * @param characterId 角色ID
     * @param accessToken Token信息
     * @return 角色详细信息
     */
    public static EsiAccountInfo getAccountInfo(Long characterId, String accessToken) {
        HttpRequest request = HttpUtil.createGet("https://esi.evetech.net/latest/characters/" + characterId);
        request.header("Authorization", "Bearer " + accessToken);
        JSONObject info = JSONObject.parseObject(request.execute().body());

        EsiAccountInfo accountInfo = new EsiAccountInfo();

        accountInfo.setAllianceId(info.getLong("alliance_id"));
        accountInfo.setCorpId(info.getLong("corporation_id"));

        request = HttpUtil.createGet("https://esi.evetech.net/latest/corporations/" + accountInfo.getCorpId());
        request.header("Authorization", "Bearer " + accessToken);
        info = JSONObject.parseObject(request.execute().body());
        accountInfo.setCorpName(StrUtil.isBlank(info.getString("name")) ? "暂无军团" : info.getString("name"));

        request = HttpUtil.createGet("https://esi.evetech.net/latest/alliances/" + accountInfo.getAllianceId());
        request.header("Authorization", "Bearer " + accessToken);
        info = JSONObject.parseObject(request.execute().body());
        accountInfo.setAllianceName(StrUtil.isBlank(info.getString("name")) ? "暂无联盟" : info.getString("name"));

        return accountInfo;
    }


}
