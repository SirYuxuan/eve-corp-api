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
package com.yuxuan66.common.async;

import cn.hutool.extra.mail.MailUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 异步发送邮件
 *
 * @author Sir丶雨轩
 * @since 2021/7/28
 */
@Component
public class AsyncMail {

    /**
     * 发送邮件 异步执行
     *
     * @param to      发送给谁
     * @param subject 主题
     * @param content 内容
     */
    @Async
    public void send(String to, String subject, String content) {
        MailUtil.sendHtml(to, subject, content);
    }
}
