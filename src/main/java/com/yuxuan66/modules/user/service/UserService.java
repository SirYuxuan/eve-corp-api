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
package com.yuxuan66.modules.user.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yuxuan66.common.utils.StpEx;
import com.yuxuan66.modules.user.entity.User;
import com.yuxuan66.modules.user.entity.UserAccount;
import com.yuxuan66.modules.user.entity.dto.UserInfoDto;
import com.yuxuan66.modules.user.mapper.UserAccountMapper;
import com.yuxuan66.modules.user.mapper.UserMapper;
import com.yuxuan66.support.basic.BasicQuery;
import com.yuxuan66.support.basic.http.PageEntity;
import com.yuxuan66.support.basic.http.RespEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户操作
 *
 * @author Sir丶雨轩
 * @since 2021/7/27
 */
@Service
public class UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserAccountMapper userAccountMapper;

    /**
     * 查询当前登录用户信息
     *
     * @return 用户信息
     */
    public RespEntity info() {
        User oldUser = StpEx.getLoginUser();
        User user = userMapper.selectById(oldUser.getId());
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setUserId(user.getId());
        userInfoDto.setNickName(StrUtil.isBlank(user.getNickName()) ? "无名氏" : user.getNickName());
        userInfoDto.setIsAdmin(user.getIsAdmin());
        List<UserAccount> userAccountList = userAccountMapper.selectList(new QueryWrapper<UserAccount>().eq("user_id", user.getId()).eq("is_main", true));
        if (userAccountList.isEmpty()) {
            return RespEntity.fail("你没有角色？你是怎么做到的，请联系雨轩");
        }
        userInfoDto.setAvatar("https://images.evetech.net/characters/" + userAccountList.get(0).getCharacterId() + "/portrait?size=128");

        return RespEntity.success(userInfoDto);
    }

    /**
     * 个人中心信息查询
     *
     * @return 个人信息
     */
    public RespEntity myInfo() {
        Map<String, Object> result = new HashMap<>();

        User user = userMapper.selectById(StpEx.getLoginUser().getId());

        List<UserAccount> userAccountList = userAccountMapper.selectList(new QueryWrapper<UserAccount>().eq("user_id", user.getId()));

        long nowLP = userAccountList.stream().mapToLong(UserAccount::getLpNow).sum();
        long totalLP = userAccountList.stream().mapToLong(UserAccount::getLpTotal).sum();
        long useLP = userAccountList.stream().mapToLong(UserAccount::getLpUse).sum();
        long skill = userAccountList.stream().mapToLong(UserAccount::getSkill).sum();
        long isk = userAccountList.stream().mapToLong(UserAccount::getIsk).sum();

        result.put("nowLP", nowLP);
        result.put("totalLP", totalLP);
        result.put("useLP", useLP);
        result.put("skill", skill);
        result.put("isk", isk);

        result.put("user", user);


        return RespEntity.success(result);
    }

    /**
     * 查询当前登录账号的所有角色
     *
     * @return 角色列表
     */
    public RespEntity listAccount() {
        return RespEntity.success(getLoginAccount());
    }

    /**
     * 查询系统所有的角色列表,分页查询
     *
     * @return 角色列表
     */
    public PageEntity listAllAccount(BasicQuery<UserAccount> basicQuery) {
        basicQuery.processingBlurry("name");

        if (!basicQuery.getIsLimit()) {
            return PageEntity.success(userAccountMapper.selectList(null));
        }

        return PageEntity.success(userAccountMapper.selectPage(basicQuery.getPage(), basicQuery.getQueryWrapper()));
    }

    /**
     * 获取当前登录账号的所有角色
     *
     * @return 角色列表
     */
    public List<UserAccount> getLoginAccount() {
        return userAccountMapper.selectList(new QueryWrapper<UserAccount>().eq("user_id", StpEx.getLoginUser().getId()).orderByDesc("is_main"));
    }

    /**
     * 修改当前账户的信息
     *
     * @param resources 账户信息
     * @return 标准返回
     */
    public RespEntity saveInfo(User resources) {
        resources.setId(StpEx.getLoginUser().getId());
        userMapper.updateById(resources);
        return RespEntity.success();
    }


    /**
     * 获取一个用户的主角色
     * @param userId 用户id
     * @return 主角色
     */
    public UserAccount getMailAccount(Long userId) {
        List<UserAccount> userAccountList = userAccountMapper.selectList(new QueryWrapper<UserAccount>().eq("user_id", userId).eq("is_main", true));
        if (userAccountList.isEmpty()) {
            return null;
        }
        return userAccountList.get(0);
    }

}
