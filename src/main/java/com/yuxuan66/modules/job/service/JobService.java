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
package com.yuxuan66.modules.job.service;

import com.yuxuan66.modules.job.entity.dto.JobDto;
import com.yuxuan66.support.basic.http.PageEntity;
import com.yuxuan66.support.basic.http.RespEntity;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 定时任务处理
 *
 * @author Sir丶雨轩
 * @since 2021/8/9
 */
@Service
public class JobService {

    private final Scheduler scheduler;

    public JobService(Scheduler scheduler) {
        this.scheduler = scheduler;
    }


    /**
     * 添加一个定时任务
     *
     * @param jobDto 定时任务信息
     * @return
     * @throws SchedulerException     SchedulerException
     * @throws ClassNotFoundException ClassNotFoundException
     */
    public RespEntity add(JobDto jobDto) throws SchedulerException, ClassNotFoundException {
        CronScheduleBuilder cronScheduleBuilder;

        try {
            cronScheduleBuilder = CronScheduleBuilder.cronSchedule(jobDto.getCron());
        } catch (Exception e) {
            return RespEntity.fail("CRON表达式不正确");
        }

        TriggerKey triggerKey = TriggerKey.triggerKey(jobDto.getName(), jobDto.getGroupName());

        CronTrigger triggerOld = (CronTrigger) scheduler.getTrigger(triggerKey);
        ;

        if (triggerOld == null) {
            //将job加入到jobDetail中
            Class<?> clazz;
            try {
                clazz = Class.forName(jobDto.getClazz());
            }catch (Exception e){
                return RespEntity.fail("任务主类不存在");
            }

            JobDetail jobDetail = JobBuilder.newJob((Class<? extends Job>) clazz).withIdentity(jobDto.getName(), jobDto.getGroupName()).build();
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobDto.getName(), jobDto.getGroupName()).withSchedule(cronScheduleBuilder).build();
            //执行任务
            scheduler.scheduleJob(jobDetail, trigger);
        } else {
            return RespEntity.fail("当前定时任务已经存在");
        }


        return RespEntity.success();
    }

    /**
     * 删除一个定时任务
     *
     * @param groupName 组名
     * @param name      任务名字
     * @return 标准返回
     * @throws SchedulerException SchedulerException
     */
    public RespEntity del(String groupName, String name) throws SchedulerException {
        scheduler.deleteJob(new JobKey(name, groupName));
        return RespEntity.success();
    }

    /**
     * 查询当前所有的定时任务
     *
     * @return 定时任务列表
     * @throws SchedulerException SchedulerException
     */
    public PageEntity list() throws SchedulerException {

        List<String> triggerGroupNames = scheduler.getTriggerGroupNames();

        List<JobDto> result = new ArrayList<>();
        for (String groupName : triggerGroupNames) {
            //组装group的匹配，为了模糊获取所有的triggerKey或者jobKey
            GroupMatcher<TriggerKey> groupMatcher = GroupMatcher.groupEquals(groupName);
            //获取所有的triggerKey
            Set<TriggerKey> triggerKeySet = scheduler.getTriggerKeys(groupMatcher);

            for (TriggerKey triggerKey : triggerKeySet) {
                CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
                JobKey jobKey = trigger.getJobKey();
                JobDetailImpl jobDetail = (JobDetailImpl) scheduler.getJobDetail(jobKey);
                JobDto jobDto = new JobDto();
                jobDto.setGroupName(groupName);
                jobDto.setName(jobDetail.getName());
                jobDto.setCron(trigger.getCronExpression());
                jobDto.setClazz(jobDetail.getJobClass().getName());
                jobDto.setStatus(scheduler.getTriggerState(triggerKey).name());
                result.add(jobDto);
            }
        }
        return PageEntity.success(result);
    }
}
