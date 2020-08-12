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
package com.yuxuan66.modules.skill.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yuxuan66.cache.modules.EveCache;
import com.yuxuan66.common.utils.Lang;
import com.yuxuan66.common.utils.StpEx;
import com.yuxuan66.modules.skill.entity.SkillDetail;
import com.yuxuan66.modules.skill.entity.SkillGroup;
import com.yuxuan66.modules.skill.entity.UserSkill;
import com.yuxuan66.modules.skill.mapper.SkillDetailMapper;
import com.yuxuan66.modules.skill.mapper.SkillGroupMapper;
import com.yuxuan66.modules.skill.mapper.UserSkillMapper;
import com.yuxuan66.modules.user.entity.User;
import com.yuxuan66.modules.user.entity.UserAccount;
import com.yuxuan66.modules.user.mapper.UserAccountMapper;
import com.yuxuan66.support.basic.BasicQuery;
import com.yuxuan66.support.basic.http.PageEntity;
import com.yuxuan66.support.basic.http.RespEntity;
import com.yuxuan66.support.esi.EsiApi;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Sir丶雨轩
 * @since 2021/8/2
 */
@Service
public class SkillGroupService {

    @Resource
    private SkillGroupMapper skillGroupMapper;
    @Resource
    private SkillDetailMapper skillDetailMapper;
    @Resource
    private UserAccountMapper userAccountMapper;
    @Resource
    private UserSkillMapper userSkillMapper;
    private final EveCache eveCache;

    private final EsiApi esiApi;

    public SkillGroupService(EveCache eveCache, EsiApi esiApi) {
        this.eveCache = eveCache;
        this.esiApi = esiApi;
    }

    /**
     * 分页查询技能组列表
     *
     * @param basicQuery 分页查询参数
     * @return 技能组列表
     */
    public PageEntity list(BasicQuery<SkillGroup> basicQuery) {

        basicQuery.processingBlurry("group_name");

        QueryWrapper<SkillGroup> queryWrapper = basicQuery.getQueryWrapper();
        queryWrapper.orderByDesc("id");

        return PageEntity.success(skillGroupMapper.selectPage(basicQuery.getPage(), queryWrapper));

    }

    /**
     * 查询技能组的详情
     *
     * @param groupId 技能组id
     * @return 技能组的详情
     */
    public RespEntity listSkillDetail(Long groupId) {
        return RespEntity.success(skillDetailMapper.selectList(new QueryWrapper<SkillDetail>().eq("group_id", groupId)));
    }

    /**
     * 添加或修改技能组
     *
     * @param skillGroup 技能组
     * @return 标准返回
     */
    public RespEntity addOrEdit(SkillGroup skillGroup) {

        if (skillGroup.getId() != null) {
            skillGroupMapper.updateById(skillGroup);
            return RespEntity.success();
        }

        User user = StpEx.getLoginUser();
        skillGroup.setCreateId(user.getId());
        skillGroup.setCreateBy(user.getNickName());
        skillGroup.setCreateTime(Lang.getTime());
        skillGroupMapper.insert(skillGroup);
        return RespEntity.success();
    }

    /**
     * 检查指定技能名称是否存在
     *
     * @param name 技能名称
     * @return 标准返回
     */
    public RespEntity checkSkillName(String name) {
        return RespEntity.success(eveCache.getEveItemName().stream().filter(item -> item.getType() == 8 && name.equals(item.getZhName())).count());
    }

    /**
     * 添加一个技能组的技能
     *
     * @param skillDetail 技能组详情
     * @return 标准返回
     */
    public RespEntity addSkill(SkillDetail skillDetail) {
        skillDetailMapper.insert(skillDetail);
        return RespEntity.success();
    }

    /**
     * 删除技能组的技能
     *
     * @param ids 技能组的技能id
     * @return 标准返回
     */
    public RespEntity delSkill(Set<Long> ids) {
        skillDetailMapper.deleteBatchIds(ids);
        return RespEntity.success();
    }

    /**
     * 批量删除技能组
     *
     * @param ids 技能组id
     * @return 标准返回
     */
    public RespEntity del(Set<Long> ids) {
        skillGroupMapper.deleteBatchIds(ids);
        return RespEntity.success();
    }

    /**
     * 查询所有的技能组
     *
     * @return 技能组
     */
    public RespEntity all() {
        QueryWrapper<SkillGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        return RespEntity.success(skillGroupMapper.selectList(queryWrapper));
    }

    /**
     * 判断用户是否能通过指定技能组检测
     * @param groupId 技能组id
     * @param userAccount 用户
     * @return 结果
     */
    public boolean isAtopSkill(Long groupId, UserAccount userAccount) {


        List<UserSkill> userSkillList = userSkillMapper.selectList(new QueryWrapper<UserSkill>().eq("account_id",userAccount.getId() ));


        Map<String, Integer> userSkill = new HashMap<>();
        for (UserSkill userSkill1 : userSkillList) {
            userSkill.put(userSkill1.getSkillZhName(), userSkill1.getSkillLevel());
        }

        List<SkillDetail> skillDetailList = skillDetailMapper.selectList(new QueryWrapper<SkillDetail>().eq("group_id", groupId));


        for (SkillDetail skillDetail : skillDetailList) {
            int userLevel = skillDetail.getSkillLevel();
            int nowLevel = userSkill.get(skillDetail.getSkillName()) == null ? 0 : userSkill.get(skillDetail.getSkillName());
            if (nowLevel < userLevel) {
                return false;
            }
        }


        return true;
    }

    /**
     * 技能检查
     *
     * @param groupId   技能组id
     * @param accountId 角色id
     * @return 检查结果
     */
    public RespEntity checkSkillGroup(Long groupId, Long accountId) {

        UserAccount userAccount = userAccountMapper.selectById(accountId);

        Map<String, Integer> userSkill = esiApi.setSkillList(userAccount, false);
       /* Map<String, Integer> userSkill = new HashMap<>();

        for (UserSkill item : userSkillMapper.selectList(new QueryWrapper<UserSkill>().eq("account_id", userAccount.getId()))) {
            userSkill.put(item.getSkillName(), item.getSkillLevel());
        }*/


        List<SkillDetail> skillDetailList = skillDetailMapper.selectList(new QueryWrapper<SkillDetail>().eq("group_id", groupId));

        List<Dict> result = new ArrayList<>();

        Map<String, Object> nameMapping = new HashMap<>();
        eveCache.getEveItemName().stream().filter(item -> item.getType().equals(8)).forEach(item -> {
            nameMapping.put(item.getZhName(), item.getEnName());
        });


        // TODO nameMapping有问题 会有重复的，移除nameMapping 改为针对type的namaMapping

        Map<Integer, String> itemDetails = new HashMap<>();
        eveCache.getEveItemName().stream().filter(item -> item.getType().equals(33)).forEach(item -> {
            itemDetails.put(item.getItemId(), item.getZhName());
        });


        skillDetailList.forEach(item -> {
            Dict dict = new Dict();
            dict.put("name", item.getSkillName());
            dict.put("enName", nameMapping.get(item.getSkillName()));
            dict.put("useLevel", item.getSkillLevel());
            dict.put("nowLevel", userSkill.get(dict.get("enName")) == null ? 0 : userSkill.get(dict.get("enName")));
            dict.put("content", itemDetails.get(item.getSkillId()));
            result.add(dict);
        });


        return RespEntity.success(result);

    }

    /**
     * 检查某个用户或全体用户是否满足技能
     *
     * @return 技能检查
     */
    public RespEntity checkUserSkillGroup(String name, Long groupId) {

        List<UserAccount> userAccountList = userAccountMapper.selectList(new QueryWrapper<UserAccount>().like(StrUtil.isNotBlank(name), "name", name));

        if (userAccountList.isEmpty()) {
            return RespEntity.fail("没有找到您要查询的角色，请注意大小写");
        }

        Map<String, Integer> checkSkill = new HashMap<>();

        skillDetailMapper.selectList(new QueryWrapper<SkillDetail>().eq("group_id", groupId)).forEach(item -> {
            checkSkill.put(item.getSkillName(), item.getSkillLevel());
        });


        List<UserSkill> userSkillList = userSkillMapper.selectList(new QueryWrapper<UserSkill>().in("account_id", userAccountList.stream().map(UserAccount::getId).collect(Collectors.toList())));


        Map<String, Map<String, Integer>> userSkill = new HashMap<>();
        for (UserSkill userSkill1 : userSkillList) {

            Map<String, Integer> skill = userSkill.get(userSkill1.getName());

            if (skill == null) {
                skill = new HashMap<>();
            }

            skill.put(userSkill1.getSkillZhName(), userSkill1.getSkillLevel());

            userSkill.put(userSkill1.getName(), skill);
        }


        List<Map<String, Object>> result = new ArrayList<>();

        for (UserAccount userAccount : userAccountList) {

            Map<String, Object> temp = new HashMap<>();
            Map<String, Integer> _userSkill = userSkill.get(userAccount.getName());
            temp.put("name", userAccount.getName());
            temp.put("skill", "");
            if (_userSkill == null) {
                temp.put("skill", "无法获取技能记录");
                result.add(temp);
                continue;
            }

            for (String key : checkSkill.keySet()) {
                Integer val = checkSkill.get(key);

                if (Convert.toInt(_userSkill.get(key), -1) < val) {
                    temp.put("skill", Convert.toStr(temp.get("skill"), "") + key + "未满足,");
                }

            }
            result.add(temp);

        }
        result.sort((o1, o2) -> new BigDecimal(Convert.toStr(o1.get("skill").toString().length())).compareTo(new BigDecimal(o2.get("skill").toString().length())));

        return RespEntity.success(result);

    }
}