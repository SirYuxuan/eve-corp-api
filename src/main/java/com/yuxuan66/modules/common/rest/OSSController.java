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
package com.yuxuan66.modules.common.rest;

import cn.hutool.core.util.IdUtil;
import com.yuxuan66.common.utils.OSSUtil;
import com.yuxuan66.support.basic.http.RespEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

/**
 * 阿里云OSS操作
 * @author Sir丶雨轩
 * @since 2021/7/29
 */
@RestController
@RequestMapping(value = "/oss")
public class OSSController {

    /**
     * OSS 图片上传
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping(path = "/upload")
    public RespEntity upload(@RequestParam("img") MultipartFile file) throws IOException {
        String[] name = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");
        String uuid  = IdUtil.simpleUUID().toUpperCase() +"."+ name[name.length-1];
        OSSUtil.upload(file.getInputStream(),uuid);
        return RespEntity.success("https://yuxuanworkcloud.oss-cn-qingdao.aliyuncs.com/" + uuid);
    }
}
