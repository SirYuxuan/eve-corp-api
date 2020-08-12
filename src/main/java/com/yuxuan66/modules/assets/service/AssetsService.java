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
package com.yuxuan66.modules.assets.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuxuan66.common.utils.Lang;
import com.yuxuan66.modules.assets.entity.UserAssets;
import com.yuxuan66.modules.assets.mapper.UserAssetsMapper;
import com.yuxuan66.modules.user.entity.UserAccount;
import com.yuxuan66.modules.user.mapper.UserAccountMapper;
import com.yuxuan66.support.basic.http.PageEntity;
import com.yuxuan66.support.esi.EsiApi;
import com.yuxuan66.support.esi.entity.EsiAssets;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 资产相关
 *
 * @author Sir丶雨轩
 * @since 2021/8/9
 */
@Service
public class AssetsService {

    private final EsiApi esiApi;

    @Resource
    private UserAccountMapper userAccountMapper;
    @Resource
    private UserAssetsMapper userAssetsMapper;

    public AssetsService(EsiApi esiApi) {
        this.esiApi = esiApi;
    }


    /**
     * 检查指定用户的资产是否存在
     *
     * @param assetsName 资产名称
     * @param name       用户名称
     * @return 标准返回
     */
    public PageEntity checkAssets(int page, int size, String assetsName, String name, String joinTime) {
        if (joinTime == null) {
            joinTime = "1990-01-01";
        }
        return PageEntity.success(userAssetsMapper.selectAssetsPage(new Page(page, size), assetsName, name, joinTime));
    }


    /**
     * 拉取所有成员的资产信息
     */
    public void pullAllMemberAssets() {

        List<UserAccount> userAccountList = userAccountMapper.selectList(new QueryWrapper<UserAccount>().isNotNull("access_token"));

        for (UserAccount userAccount : userAccountList) {
            userAssetsMapper.delete(new QueryWrapper<UserAssets>().eq("account_id", userAccount.getId()));

            Map<String, EsiAssets> assets = esiApi.getAssets(userAccount);

            for (String key : assets.keySet()) {
                UserAssets userAssets = new UserAssets();
                userAssets.setCreateTime(Lang.getTime());
                userAssets.setUserId(userAccount.getUserId());
                userAssets.setAccountId(userAccount.getId());
                userAssets.setAccountName(userAccount.getName());
                userAssets.setName(key);
                userAssets.setNum(assets.get(key).getNum());
                userAssets.setBlueprintCopy(assets.get(key).isBlueprintCopy());
                userAssetsMapper.insert(userAssets);
            }

        }
    }
}
