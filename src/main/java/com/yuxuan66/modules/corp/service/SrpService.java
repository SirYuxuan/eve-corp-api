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
package com.yuxuan66.modules.corp.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yuxuan66.cache.modules.EveCache;
import com.yuxuan66.common.utils.Lang;
import com.yuxuan66.common.utils.StpEx;
import com.yuxuan66.modules.corp.entity.SrpBlacklist;
import com.yuxuan66.modules.corp.entity.SrpLog;
import com.yuxuan66.modules.corp.entity.SrpLogDetails;
import com.yuxuan66.modules.corp.entity.SrpRules;
import com.yuxuan66.modules.corp.entity.query.SrpQuery;
import com.yuxuan66.modules.corp.mapper.SrpBlacklistMapper;
import com.yuxuan66.modules.corp.mapper.SrpLogDetailsMapper;
import com.yuxuan66.modules.corp.mapper.SrpLogMapper;
import com.yuxuan66.modules.corp.mapper.SrpRulesMapper;
import com.yuxuan66.modules.eve.entity.EveItemName;
import com.yuxuan66.modules.skill.entity.SkillGroup;
import com.yuxuan66.modules.skill.mapper.SkillGroupMapper;
import com.yuxuan66.modules.skill.service.SkillGroupService;
import com.yuxuan66.modules.user.entity.User;
import com.yuxuan66.modules.user.entity.UserAccount;
import com.yuxuan66.modules.user.mapper.UserAccountMapper;
import com.yuxuan66.support.basic.http.PageEntity;
import com.yuxuan66.support.basic.http.RespEntity;
import com.yuxuan66.support.esi.EsiApi;
import com.yuxuan66.support.esi.entity.EsiAccountInfo;
import org.springframework.data.annotation.Transient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 补损相关
 *
 * @author Sir丶雨轩
 * @since 2021/8/23
 */
@Service
public class SrpService {

    @Resource
    private SrpLogMapper srpLogMapper;
    @Resource
    private SrpRulesMapper srpRulesMapper;

    private final EveCache eveCache;
    @Resource
    private SrpBlacklistMapper srpBlacklistMapper;

    private final EsiApi esiApi;
    @Resource
    private SkillGroupMapper skillGroupMapper;

    private final SkillGroupService skillGroupService;
    @Resource
    private UserAccountMapper userAccountMapper;
    @Resource
    private SrpLogDetailsMapper srpLogDetailsMapper;

    public SrpService(EveCache eveCache, EsiApi esiApi, SkillGroupService skillGroupService) {
        this.eveCache = eveCache;
        this.esiApi = esiApi;
        this.skillGroupService = skillGroupService;
    }

    /**
     * 查询自己的补损提交记录
     * @param basicQuery 基础查询
     * @return 标准分页返回
     */
    public PageEntity list(SrpQuery basicQuery) {
        basicQuery.processingBlurry("name","ship_name");
        QueryWrapper<SrpLog> queryWrapper = basicQuery.getQueryWrapper();
        queryWrapper.orderByDesc("id");
        queryWrapper.eq(basicQuery.getAll() == null ,"create_id",StpEx.getLoginUser().getId());

        return PageEntity.success(srpLogMapper.selectPage(basicQuery.getPage(), queryWrapper));

    }

    public RespEntity exchangeApproval(Long id,Integer status,String spRemark) {

        User loginUser = StpEx.getLoginUser();

        SrpLog srpLog = srpLogMapper.selectById(id);

        if(srpLog == null){
            return RespEntity.fail("没有找到补损记录");
        }
        if(srpLog.getStatus() != 1){
            return RespEntity.fail("当前补损已经处理");
        }
        srpLog.setStatus(status);
        srpLog.setSpRemark(spRemark);
        srpLog.setSpId(loginUser.getId());
        srpLog.setSpTime(Lang.getTime());
        srpLog.setSpName(loginUser.getNickName());
        srpLogMapper.updateById(srpLog);

        return RespEntity.success();
    }
    public RespEntity oldData(Long id){
        Map<String,Object> result = new HashMap<>();
        result.put("data",srpLogMapper.selectById(id));
        result.put("itemList",srpLogDetailsMapper.selectList(new QueryWrapper<SrpLogDetails>().eq("log_id",id)));
        return RespEntity.success(result);
    }

    @Transient
    public RespEntity newSrp(JSONObject data) {

        DateTime killTime = DateUtil.parseDateTime(data.getString("time"));

        long day = DateUtil.betweenDay(killTime, new Date(), true);

        if(day > 30){
            return RespEntity.fail("对不起 不支持提交超过30天的KM");
        }

        if(killTime.getTime() < DateUtil.parse("2021-09-01 00:00:00").getTime()){
            return RespEntity.fail("2021-09-01号之前的补损无法提交");
        }

        String shipName = data.getString("shipName");
        List<SrpRules> srpRulesList = srpRulesMapper.selectList(new QueryWrapper<SrpRules>().eq("ship_name", shipName));
        String characterName = data.getString("characterName");
        List<UserAccount> userAccountList = userAccountMapper.selectList(new QueryWrapper<UserAccount>().eq("name", characterName));
        if (userAccountList.isEmpty()) {
            return RespEntity.fail("对不起 此角色尚未注册军团系统，无法参与补损");
        }
        if (userAccountList.size() > 1) {
            return RespEntity.fail("对不起 您的角色数据异常，请联系雨轩处理");
        }
        // 入团校验
        UserAccount userAccount = userAccountList.get(0);

        if (!userAccount.getUserId().equals(StpEx.getLoginUser().getId())) {
            return RespEntity.fail("对不起 不能提交非本人的KM");
        }
        // 黑名单拦截
        List<SrpBlacklist> srpBlacklistList = srpBlacklistMapper.selectList(new QueryWrapper<SrpBlacklist>().eq("user_id", userAccount.getUserId()).lt("start_time", killTime).gt("end_time", killTime));
        for (SrpBlacklist srpBlacklist : srpBlacklistList) {
            if(srpBlacklist.getIsFull() || (!srpBlacklist.getIsFull() && srpBlacklist.getName().equals(userAccount.getName()))){
                return RespEntity.fail("由于【"+srpBlacklist.getRemark()+"】,您在【"+DateUtil.formatDateTime(srpBlacklist.getStartTime())+"~"+DateUtil.formatDateTime(srpBlacklist.getEndTime())+"】时间范围内的KM无法提交");
            }
        }
        StringBuilder errorMsg = new StringBuilder();
        boolean isSuccess = true;
        for (SrpRules srpRules : srpRulesList) {
            if (srpRules != null) {
                // 加团时间拦截
                if (srpRules.getJoinTime() != null && srpRules.getJoinTime() > 0) {
                    if (userAccount.getJoinTime() == null) {
                        esiApi.refreshToken(userAccount);
                        EsiAccountInfo esiAccountInfo = esiApi.getAccountInfo(userAccount.getCharacterId(), userAccount.getAccessToken());
                        userAccount.setJoinTime(esiAccountInfo.getJoinTime());
                    }
                    if (userAccount.getJoinTime() == null) {
                        errorMsg.append("无法获取角色加团时间<br>");
                        isSuccess = false;
                        continue;
                    }
                    long num = DateUtil.betweenDay(userAccount.getJoinTime(), new Date(), false);
                    if (num > srpRules.getJoinTime()) {
                        errorMsg.append("对不起 【").append(shipName).append("】仅支持入团").append(srpRules.getJoinTime()).append("天的成员进行补损，您已经入团: ").append(num).append("天<br>");
                        isSuccess = false;
                        continue;
                    }
                }
                // 怪损拦截
                if (srpRules.getIsNpc() != null && !srpRules.getIsNpc() && data.getBoolean("isNPCKill")) {
                   errorMsg.append("对不起 【").append(shipName).append("】不支持怪损补损<br>");
                    isSuccess = false;
                   continue;
                }
                // 技能组拦截
                if (StrUtil.isNotBlank(srpRules.getSkillGroupId())) {
                    String[] skillGroupId = srpRules.getSkillGroupId().split(",");
                    String noSk = "";
                    boolean isAtop = true;
                    for (String sk : skillGroupId) {
                        if(srpRules.getIsFull() == null){
                            srpRules.setIsFull(false);
                        }
                        boolean temp = skillGroupService.isAtopSkill(Convert.toLong(sk), userAccount);
                        if (!temp && srpRules.getIsFull()) {
                            noSk = sk;
                            isAtop = false;
                            break;
                        }
                        // 如果通过，且要求满足任意技能组 则直接通过要求
                        if (temp && !srpRules.getIsFull()) {
                            break;
                        }
                        if ( !srpRules.getIsFull() && !temp) {
                            noSk = sk;
                            isAtop = false;
                            break;
                        }
                    }
                    if (!isAtop) {
                        SkillGroup skillGroup = skillGroupMapper.selectById(Convert.toLong(noSk));
                        errorMsg.append("对不起 此舰船补损需要满足技能规划【").append(skillGroup.getGroupName()).append("】,已学技能每天凌晨2点刷新<br>");
                        isSuccess = false;
                        continue;
                    }

                }

                isSuccess = true;
                break;
            }
        }

        if(!isSuccess){
            errorMsg.append("以上条件需要满足任意一个才可提交");
            return RespEntity.fail(errorMsg.toString());
        }


        SrpLog srpLog = new SrpLog();
        User user = StpEx.getLoginUser();
        srpLog.setKmTime(killTime.toTimestamp());
        srpLog.setKmLocal(data.getString("systemName"));
        srpLog.setCreateBy(user.getNickName());
        srpLog.setCreateId(user.getId());
        srpLog.setCreateTime(Lang.getTime());
        srpLog.setName(userAccount.getName());
        srpLog.setAccountId(userAccount.getId());
        srpLog.setUserId(userAccount.getUserId());
        srpLog.setSrpTime(killTime.toTimestamp());
        srpLog.setSystemName(data.getString("systemName"));
        srpLog.setRemark(data.getString("remark"));
        srpLog.setIsNpc(data.getBoolean("isNPCKill"));
        srpLog.setShipName(shipName);
        srpLog.setStatus(1);
        srpLog.setPrice(data.getLong("price"));
        srpLog.setUrl(data.getString("url"));
        srpLog.setKmId(srpLog.getUrl().replaceAll("https://esi.evetech.net/.*/killmails/","").split("/")[0]);

        int count = srpLogMapper.selectCount(new QueryWrapper<SrpLog>().eq("km_id",srpLog.getKmId()));
        if(count > 0){
            return RespEntity.fail("对不起 此KM已经提交过了，请不要重复提交");
        }
        srpLogMapper.insert(srpLog);

        JSONArray itemList = data.getJSONArray("itemList");
        for (int i = 0; i < itemList.size(); i++) {
            JSONObject item = itemList.getJSONObject(i);
            SrpLogDetails logDetails = new SrpLogDetails();
            logDetails.setLogId(srpLog.getId());
            logDetails.setName(item.getString("name"));
            logDetails.setNum(item.getInteger("num"));
            logDetails.setItemId(item.getIntValue("id"));
            logDetails.setType(item.getString("type"));
            logDetails.setPrice(item.getLong("price"));
            srpLogDetailsMapper.insert(logDetails);
        }
        return RespEntity.success();
    }


    public static void main(String[] args) {
        System.out.println("https://esi.evetech.net/latest/killmails/111".replaceAll("",""));
    }


    /**
     * 获取击杀信息
     *
     * @param url
     * @return
     */
    public RespEntity getKillInfo(String url) {
        Map<String, Object> result = new HashMap<>();
        // 基础数据
        List<EveItemName> itemNameList = eveCache.getEveItemName();

        try {
            JSONObject data = JSON.parseObject(HttpUtil.get(url));

            // KM发生时间
            String killDate = DateUtil.formatDateTime(data.getTimestamp("killmail_time"));
            // KM发生地点
            int systemId = data.getInteger("solar_system_id");
            String systemName = JSON.parseObject(HttpUtil.get("https://esi.evetech.net/latest/universe/systems/" + systemId + "/?datasource=tranquility&language=en")).getString("name");
            // 是否NPC攻击致死
            boolean isNPCKill = true;
            JSONArray attackers = data.getJSONArray("attackers");
            for (int i = 0; i < attackers.size(); i++) {
                JSONObject temp = attackers.getJSONObject(i);
                if (temp.containsKey("character_id")) {
                    isNPCKill = false;
                }
            }
            // 舰船数据
            JSONObject victim = data.getJSONObject("victim");
            int shipTypeId = victim.getInteger("ship_type_id");
            Optional<EveItemName> itemName = itemNameList.stream().filter(item -> item.getType() == 8 && item.getItemId() == shipTypeId).findAny();
            if (!itemName.isPresent()) {
                return RespEntity.fail("对不起 没有找到您的舰船数据，请联系雨轩补充");
            }
            String shipName = itemName.get().getZhName();
            int characterId = victim.getInteger("character_id");
            String characterName = JSON.parseObject(HttpUtil.get("https://esi.evetech.net/latest/characters/" + characterId + "/?datasource=tranquility")).getString("name");
            result.put("time", killDate);
            result.put("systemName", systemName);
            result.put("characterName", characterName);
            result.put("isNPCKill", isNPCKill);
            result.put("url", url);
            result.put("shipName", shipName);

            JSONArray items = victim.getJSONArray("items");
            long price = JSONObject.parseObject(HttpUtil.get("https://www.ceve-market.org/tqapi/market/region/10000002/system/30000142/type/" + shipTypeId + ".json")).getJSONObject("sell").getLongValue("min");
            List<Map<String, Object>> itemList = new ArrayList<>();
            for (int i = 0; i < items.size(); i++) {
                JSONObject temp = items.getJSONObject(i);
                int typeId = temp.getInteger("item_type_id");
                Optional<EveItemName> info = itemNameList.stream().filter(item -> item.getType() == 8 && item.getItemId() == typeId).findAny();
                if (info.isPresent()) {
                    EveItemName itemName1 = info.get();
                    int destroyed = -1;
                    if (temp.containsKey("quantity_destroyed")) {
                        destroyed = temp.getInteger("quantity_destroyed");
                    }

                    int dropped = -1;
                    if (temp.containsKey("quantity_dropped")) {
                        dropped = temp.getInteger("quantity_dropped");
                    }
                    int num = (destroyed == -1 ? dropped : destroyed);
                    long itemPrice = JSONObject.parseObject(HttpUtil.get("https://www.ceve-market.org/tqapi/market/region/10000002/system/30000142/type/" + typeId + ".json")).getJSONObject("sell").getLongValue("min") * num;
                    price += itemPrice;
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("name", itemName1.getZhName());
                    itemMap.put("num", num);
                    itemMap.put("type", (destroyed == -1 ? "掉落" : "销毁"));
                    itemMap.put("price", itemPrice);
                    itemMap.put("id", typeId);
                    itemList.add(itemMap);

                }

            }
            result.put("price", price);
            result.put("itemList", itemList);


            return RespEntity.success(result);
        } catch (Exception e) {
            return RespEntity.fail("请输入正确的KM链接");
        }

    }


}
