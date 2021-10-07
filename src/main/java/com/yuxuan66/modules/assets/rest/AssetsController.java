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
package com.yuxuan66.modules.assets.rest;

import com.yuxuan66.cache.modules.EveCache;
import com.yuxuan66.modules.assets.mapper.UserAssetsMapper;
import com.yuxuan66.modules.assets.service.AssetsService;
import com.yuxuan66.modules.user.entity.UserAccount;
import com.yuxuan66.modules.user.mapper.UserAccountMapper;
import com.yuxuan66.support.basic.http.PageEntity;
import com.yuxuan66.support.esi.EsiApi;
import com.yuxuan66.support.esi.entity.EsiAccountInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 资产管理
 * @author Sir丶雨轩
 * @since 2021/8/9
 */
@RestController
@RequestMapping(path = "/assets")
public class AssetsController {

    private final EsiApi esiApi;

    private final EveCache eveCache;
    @Resource
    private UserAccountMapper userAccountMapper;
    @Resource
    private UserAssetsMapper userAssetsMapper;

    private final AssetsService assetsService;

    public AssetsController(EsiApi esiApi, EveCache eveCache, AssetsService assetsService) {
        this.esiApi = esiApi;
        this.eveCache = eveCache;
        this.assetsService = assetsService;
    }

    /**
     * 检查指定用户的资产是否存在
     * @param assetsName 资产名称
     * @param name 用户名称
     * @return 标准返回
     */
    @GetMapping(path = "/checkAssets")
    public PageEntity checkAssets(int page,int size,String assetsName, String name,String joinTime){
        return assetsService.checkAssets(page,size,assetsName,name,joinTime);
    }

    @GetMapping(path = "/getAssets")
    public void getAssets(){




        List<UserAccount> userAccountList = userAccountMapper.selectList(null);
        for (UserAccount userAccount : userAccountList) {
            esiApi.refreshToken(userAccount);
            EsiAccountInfo info = esiApi.getAccountInfo(userAccount.getCharacterId(),userAccount.getAccessToken());
            userAccount.setJoinTime(info.getJoinTime());
            userAccount.setCorpId(info.getCorpId());
            userAccount.setCorpName(info.getCorpName());
            userAccount.setAllianceId(info.getAllianceId());
            userAccount.setAllianceName(info.getAllianceName());
            userAccountMapper.updateById(userAccount);
        }

    }
}
