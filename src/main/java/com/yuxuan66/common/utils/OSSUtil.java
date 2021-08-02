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
package com.yuxuan66.common.utils;


import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;

import java.io.InputStream;

/**
 * 阿里云OSS工具栏
 *
 * @author Sir丶雨轩
 * @since 2020/10/22
 */
public class OSSUtil {

    /**
     * 构建OSS client
     *
     * @return oss
     */
    private static OSS ossClient() {
        String endpoint = "http://oss-cn-qingdao.aliyuncs.com";
        String accessKeyId = "gw6yt4VLqGV69vSG";
        String accessKeySecret = "UucgIypNp8hzgn1BDYBaaJdU0NbSCI";
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    /**
     * 上传文件里
     *
     * @param inputStream 文件流
     * @param fileName    文件名
     */
    public static void upload(InputStream inputStream, String fileName) {
        OSS ossClient = ossClient();
        ossClient.putObject("yuxuanworkcloud", fileName, inputStream);
        ossClient.shutdown();
    }

}
