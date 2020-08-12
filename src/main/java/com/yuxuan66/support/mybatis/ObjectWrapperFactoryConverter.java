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
package com.yuxuan66.support.mybatis;

/**
 * @author Sir丶雨轩
 * @since 2021/7/27
 */

import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * 转换一下mybatis配置的类名，实现返回数据为map时自动下划线转驼峰
 * @author Sir丶雨轩
 * @since 2021/4/15
 */
@Component
@ConfigurationPropertiesBinding
public class ObjectWrapperFactoryConverter implements Converter<String, ObjectWrapperFactory> {


    @Override
    public ObjectWrapperFactory convert(String className) {
        try {
            return (ObjectWrapperFactory) Class.forName(className).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
