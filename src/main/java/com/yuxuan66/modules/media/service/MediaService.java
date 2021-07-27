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
package com.yuxuan66.modules.media.service;

import com.yuxuan66.modules.media.mapper.MediaMapper;
import com.yuxuan66.support.basic.http.RespEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 军团媒体
 *
 * @author Sir丶雨轩
 * @since 2021/7/27
 */
@Service
public class MediaService {

    @Resource
    private MediaMapper mediaMapper;

    /**
     * 获取某种类型top5的记录
     *
     * @param type 类型
     * @return top5数据
     */
    public RespEntity top5ByType(Integer type) {
        return RespEntity.success(mediaMapper.top5ByType(type));
    }
}
