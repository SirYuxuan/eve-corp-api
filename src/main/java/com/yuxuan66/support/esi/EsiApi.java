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
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.yuxuan66.cache.modules.EveCache;
import com.yuxuan66.modules.user.entity.UserAccount;
import com.yuxuan66.modules.user.mapper.UserAccountMapper;
import com.yuxuan66.support.config.SystemConfig;
import com.yuxuan66.support.esi.entity.EsiAccountInfo;
import com.yuxuan66.support.esi.entity.EsiMail;
import com.yuxuan66.support.esi.entity.EsiTokenInfo;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
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

    @Resource
    private UserAccountMapper userAccountMapper;

    private final SystemConfig systemConfig;
    private final EveCache eveCache;

    public EsiApi(SystemConfig systemConfig, EveCache eveCache) {
        this.systemConfig = systemConfig;
        this.eveCache = eveCache;
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
     *
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
     *
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
     *
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

    /**
     * 刷新指定角色的Token
     *
     * @param userAccount 角色
     */
    public void refreshToken(UserAccount userAccount) {

        if (userAccount.getAccessToken() == null) {
            return;
        }

        // token 过期时间
        long exp = JWT.decode(userAccount.getAccessToken()).getClaims().get("exp").asLong();

        if (exp < System.currentTimeMillis() / 1000) {
            HttpRequest request = HttpUtil.createPost("https://login.eveonline.com/v2/oauth/token");
            request.form("grant_type", "refresh_token");
            request.form("refresh_token", userAccount.getRefreshToken());
            request.header("Authorization", "Basic " + Base64.encode(systemConfig.getEveEsiClientId() + ":" + systemConfig.getEveEsiSecretKey()));
            JSONObject tokenInfo = JSONObject.parseObject(request.execute().body());
            userAccount.setAccessToken(tokenInfo.getString("access_token"));
            userAccount.setRefreshToken(tokenInfo.getString("refresh_token"));
            userAccountMapper.updateById(userAccount);
        }
    }


    /**
     * 邮件发送
     *
     * @param form 发送人
     * @param to   接收人
     */
    @Async
    public void sendMail(UserAccount form, UserAccount to, String title, String body) {
        try {
            refreshToken(form);

            String url = "https://esi.evetech.net/latest/characters/" + form.getCharacterId() + "/mail/?datasource=tranquility";
            HttpRequest request = HttpRequest.post(url);
            request.header("Authorization", "Bearer " + form.getAccessToken());
            EsiMail esiMail = new EsiMail();
            esiMail.setBody(body);
            esiMail.setSubject(title);
            EsiMail.Recipients recipients = new EsiMail.Recipients();
            recipients.setRecipient_id(Convert.toInt(to.getCharacterId()));

            esiMail.setRecipients(new ArrayList<EsiMail.Recipients>() {{
                add(recipients);
            }});


            request.body(JSONObject.toJSONString(esiMail));
            request.execute().body();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 设置一个角色的ISK余额并更新
     *
     * @param userAccount 角色
     * @return isk余额
     */
    public void setIskBalance(UserAccount userAccount) {
        refreshToken(userAccount);
        HttpRequest request = HttpUtil.createGet("https://esi.evetech.net/latest/characters/" + userAccount.getCharacterId() + "/wallet/");
        request.header("Authorization", "Bearer " + userAccount.getAccessToken());
        Long isk = Convert.toLong(request.execute().body(), 0L);
        userAccount.setIsk(isk);
        userAccountMapper.updateById(userAccount);
    }

    /**
     * 设置一个角色的技能点数量
     *
     * @param userAccount 角色
     */
    public void setSkillNum(UserAccount userAccount) {
        refreshToken(userAccount);
        HttpRequest request = HttpUtil.createGet("https://esi.evetech.net/latest/characters/" + userAccount.getCharacterId() + "/skills/");
        request.header("Authorization", "Bearer " + userAccount.getAccessToken());
        JSONObject info = JSONObject.parseObject(request.execute().body());
        userAccount.setSkill(info.getLongValue("total_sp"));
        userAccountMapper.updateById(userAccount);
    }

    /**
     * 设置一个角色的技能信息
     *
     * @param userAccount 角色
     */
    public void setSkillInfo(UserAccount userAccount) {
        refreshToken(userAccount);
        HttpRequest request = HttpUtil.createGet("https://esi.evetech.net/latest/characters/" + userAccount.getCharacterId() + "/skillqueue/");
        request.header("Authorization", "Bearer " + userAccount.getAccessToken());
        String body = request.execute().body();
        if(!JSONUtil.isJsonArray(body)){
            userAccount.setSkillName("角色授权异常");
            return;
        }
        JSONArray info = JSONObject.parseArray(body);

        if (info.isEmpty()) {
            userAccount.setSkillName("队列中无技能");
            return;
        }
        userAccount.setSkillEndTime(info.getJSONObject(info.size() - 1).getTimestamp("finish_date"));

        request = HttpUtil.createGet("https://esi.evetech.net/dev/universe/names/?datasource=tranquility");
        request.header("Authorization", "Bearer " + userAccount.getAccessToken());
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(info.getJSONObject(0).getString("skill_id"));
        request.body(jsonArray.toJSONString());
        JSONArray info1 = JSONObject.parseArray(request.execute().body());

        Map<String, Object> nameMapping = eveCache.getChineseToEnglishName();

        String skillZhName = "无法翻译";

        for (Object key : nameMapping.keySet()) {
            String zhName = Convert.toStr(key);
            String enName = Convert.toStr(nameMapping.get(key));
            if (enName.equals(info1.getJSONObject(0).getString("name"))) {
                skillZhName = zhName;
                break;
            }
        }


        userAccount.setSkillName(skillZhName + " " + info.getJSONObject(0).getString("finished_level"));
        userAccount.setSkillEnName((info1.getJSONObject(0).getString("name") + " " + info.getJSONObject(0).getString("finished_level")));
        userAccountMapper.updateById(userAccount);
    }

}
