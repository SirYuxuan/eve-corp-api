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
package com.yuxuan66.modules.job.rest;

import com.yuxuan66.job.TestJob;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * @author Sir丶雨轩
 * @since 2021/7/30
 */
@RestController
@RequestMapping(path = "/job")
public class JobController {

    private final Scheduler scheduler;

    public JobController(Scheduler scheduler) {
        this.scheduler = scheduler;
    }


    @GetMapping("/add")
    public void add() throws SchedulerException {

        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("0/8 * * * * ?");
        //根据name 和group获取当前trgger 的身份
        TriggerKey triggerKey = TriggerKey.triggerKey("cj", "123");
        CronTrigger triggerOld = null;
        try {
            //获取 触发器的信息
            triggerOld = (CronTrigger) scheduler.getTrigger(triggerKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        if (triggerOld == null) {
            //将job加入到jobDetail中
            JobDetail jobDetail = JobBuilder.newJob(TestJob.class).withIdentity("cj", "123").build();
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("cj", "123").withSchedule(cronScheduleBuilder).build();
            //执行任务
            scheduler.scheduleJob(jobDetail, trigger);
        } else {
            System.out.println("当前job已存在--------------------------------------------");
        }

    }

    @GetMapping("/list")
    public void list() throws SchedulerException {
        //再获取Scheduler下的所有group
        scheduler.deleteJob(new JobKey("cj","123"));
        List<String> triggerGroupNames = scheduler.getTriggerGroupNames();
        for (String groupName : triggerGroupNames) {
            //组装group的匹配，为了模糊获取所有的triggerKey或者jobKey
            GroupMatcher groupMatcher = GroupMatcher.groupEquals(groupName);
            //获取所有的triggerKey
            Set<TriggerKey> triggerKeySet = scheduler.getTriggerKeys(groupMatcher);
            for (TriggerKey triggerKey : triggerKeySet) {
                //通过triggerKey在scheduler中获取trigger对象
                CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
                //获取trigger拥有的Job
                JobKey jobKey = trigger.getJobKey();
                JobDetailImpl jobDetail = (JobDetailImpl) scheduler.getJobDetail(jobKey);
                //组装页面需要显示的数据
                System.out.println(groupName);
                System.out.println(jobDetail.getName());
                System.out.println(trigger.getCronExpression());
                System.out.println(trigger.getTimeZone().getID());
                System.out.println(scheduler.getTriggerState(triggerKey));

            }
        }
    }
}
