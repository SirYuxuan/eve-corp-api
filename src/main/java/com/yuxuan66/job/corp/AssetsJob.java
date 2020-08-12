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
package com.yuxuan66.job.corp;

import cn.hutool.extra.spring.SpringUtil;
import com.yuxuan66.modules.assets.service.AssetsService;
import com.yuxuan66.modules.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Sir丶雨轩
 * @since 2021/8/9
 */
@Slf4j
public class AssetsJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("开始获取所有成员技能");
        SpringUtil.getBean(UserService.class).pullSkill();
        log.info("所有成员技能获取完毕");

        log.info("开始获取所有成员资产");
        SpringUtil.getBean(AssetsService.class).pullAllMemberAssets();
        log.info("所有成员资产获取完毕");
    }
}
