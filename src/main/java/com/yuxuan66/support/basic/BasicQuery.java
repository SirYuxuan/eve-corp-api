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
package com.yuxuan66.support.basic;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Sir丶雨轩
 * @since 2021/7/27
 */
@Setter
@Getter
public class BasicQuery<T> {


    private String blurry;
    private String sort;

    private int page;
    private int size;

    private String[] createTime;


    private final QueryWrapper<T> queryWrapper = new QueryWrapper<>();

    public Page<T> getPage() {
        return new Page<T>(page, size);
    }


    /**
     * 处理模糊查询数据
     * @param params 字段
     */
    public void processingBlurry(String... params) {
        if (StrUtil.isNotBlank(this.getBlurry())) {
            for (int i = 0; i < params.length; i++) {
                queryWrapper.like(params[i], this.getBlurry());
                if (i != params.length - 1) {
                    queryWrapper.or();
                }
            }
        }
    }



}

