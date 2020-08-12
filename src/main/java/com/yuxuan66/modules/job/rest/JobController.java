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

import com.yuxuan66.modules.job.entity.dto.JobDto;
import com.yuxuan66.modules.job.service.JobService;
import com.yuxuan66.support.basic.http.PageEntity;
import com.yuxuan66.support.basic.http.RespEntity;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.*;

/**
 * @author Sir丶雨轩
 * @since 2021/7/30
 */
@RestController
@RequestMapping(path = "/job")
public class JobController {


    private final JobService jobService;


    public JobController(JobService jobService) {
        this.jobService = jobService;
    }


    /**
     * 添加一个定时任务
     *
     * @param jobDto 定时任务信息
     * @return 标准返回
     * @throws SchedulerException     SchedulerException
     * @throws ClassNotFoundException ClassNotFoundException
     */
    @PostMapping
    public RespEntity add(@RequestBody JobDto jobDto) throws SchedulerException, ClassNotFoundException {
        return jobService.add(jobDto);
    }


    /**
     * 删除一个定时任务
     * @param groupName 组名
     * @param name 任务名
     * @return 标准返回
     * @throws SchedulerException SchedulerException
     */
    @DeleteMapping(path = "/{groupName}/{name}")
    public RespEntity del(@PathVariable String groupName, @PathVariable String name) throws SchedulerException {
        return jobService.del(groupName, name);
    }

    /**
     * 查询当前所有的定时任务
     *
     * @return 定时任务列表
     * @throws SchedulerException SchedulerException
     */
    @GetMapping
    public PageEntity list() throws SchedulerException {
        return jobService.list();
    }
}
