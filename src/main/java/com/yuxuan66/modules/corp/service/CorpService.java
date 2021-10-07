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
import com.yuxuan66.modules.user.entity.User;
import com.yuxuan66.modules.user.entity.UserAccount;
import com.yuxuan66.modules.user.mapper.UserAccountMapper;
import com.yuxuan66.modules.user.mapper.UserMapper;
import com.yuxuan66.support.basic.http.RespEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 军团相关操作
 * @author Sir丶雨轩
 * @since 2021/8/20
 */
@Service
public class CorpService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserAccountMapper userAccountMapper;

    /**
     * 判断指定QQ是否注册了军团系统
     * @param qq qq
     * @return 标准返回
     */
    public RespEntity checkQQDoesItExist(String qq){

        List<User> userList = userMapper.selectList(new QueryWrapper<User>().eq("qq",qq));

        if(userList.isEmpty()){
            return RespEntity.success(false);
        }

        int count = userAccountMapper.selectCount(new QueryWrapper<UserAccount>().eq("user_id",userList.get(0).getId()));

        return RespEntity.success(count>0);
    }

    /**
     * 获取当前系统内所有QQ，用于供机器人判断
     * @return 标准返回
     */
    public RespEntity getAllQQ(){

        List<String> allQQ = userMapper.selectList(new QueryWrapper<User>().isNotNull("qq").eq("corp",true)).stream().map(User::getQq).collect(Collectors.toList());

        return RespEntity.success(allQQ);
    }


}
