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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yuxuan66.common.utils.Lang;
import com.yuxuan66.common.utils.StpEx;
import com.yuxuan66.modules.corp.entity.SrpBlacklist;
import com.yuxuan66.modules.corp.mapper.SrpBlacklistMapper;
import com.yuxuan66.modules.user.entity.UserAccount;
import com.yuxuan66.modules.user.mapper.UserAccountMapper;
import com.yuxuan66.support.basic.BasicQuery;
import com.yuxuan66.support.basic.http.PageEntity;
import com.yuxuan66.support.basic.http.RespEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @author Sir丶雨轩
 * @since 2021/8/26
 */
@Service
public class SrpBlacklistService {

    @Resource
    private SrpBlacklistMapper srpBlacklistMapper;
    @Resource
    private UserAccountMapper userAccountMapper;

    public PageEntity list(BasicQuery<SrpBlacklist> basicQuery) {

        QueryWrapper<SrpBlacklist> queryWrapper = basicQuery.getQueryWrapper();
        queryWrapper.orderByDesc("id");

        return PageEntity.success(srpBlacklistMapper.selectPage(basicQuery.getPage(), queryWrapper));

    }

    public RespEntity addOrEdit(SrpBlacklist srpRules) {

        List<UserAccount> userAccountList = userAccountMapper.selectList(new QueryWrapper<UserAccount>().eq("name",srpRules.getName()));
        if(userAccountList.size() != 1){
            return RespEntity.fail("角色不存在或不明确.区分大小写");
        }
        srpRules.setUserId(userAccountList.get(0).getUserId());
        srpRules.setCreateBy(StpEx.getLoginUser().getNickName());
        srpRules.setCreateId(StpEx.getLoginUser().getId());
        srpRules.setCreateTime(Lang.getTime());
        srpBlacklistMapper.insert(srpRules);
        return RespEntity.success();
    }

    public RespEntity del(Set<Long> ids) {
        srpBlacklistMapper.deleteBatchIds(ids);
        return RespEntity.success();
    }
}
