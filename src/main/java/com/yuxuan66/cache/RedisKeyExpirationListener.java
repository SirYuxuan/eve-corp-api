/*
 * Copyright 2013-2021 Sir丶雨轩
 *
 * This file is part of Sir丶雨轩/ehi-blog.

 * Sir丶雨轩/ehi-blog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.

 * Sir丶雨轩/ehi-blog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with Sir丶雨轩/ehi-blog.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.yuxuan66.cache;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yuxuan66.modules.lp.entity.LpLog;
import com.yuxuan66.modules.lp.mapper.LpLogMapper;
import com.yuxuan66.modules.luck.entity.LuckDraw;
import com.yuxuan66.modules.luck.mapper.LuckDrawMapper;
import com.yuxuan66.modules.user.entity.UserAccount;
import com.yuxuan66.modules.user.mapper.UserAccountMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author Sir丶雨轩
 * @since 2021/8/24
 */
@Slf4j
@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Resource
    private LuckDrawMapper luckDrawMapper;
    @Resource
    private LpLogMapper lpLogMapper;
    @Resource
    private UserAccountMapper userAccountMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 拿到key
        String key = message.toString();
        if(key.startsWith("LUCK:S")){
            String id = key.replace("LUCK:S:","");
            // 开始活动
            LuckDraw luckDraw = new LuckDraw();
            luckDraw.setId(Convert.toLong(id));
            luckDraw.setStatus(2);
            luckDrawMapper.updateById(luckDraw);
        }
        if(key.startsWith("LUCK:E")){
            String id = key.replace("LUCK:E:","");
            // 结束活动
            LuckDraw luckDraw = new LuckDraw();
            luckDraw.setId(Convert.toLong(id));
            luckDraw.setStatus(3);
            luckDrawMapper.updateById(luckDraw);

            LuckDraw oldLuckDraw = luckDrawMapper.selectById(id);

            if(oldLuckDraw.getWinAccountId() == null){
                // 抽奖到期，开始退款
                List<LpLog> lpLogList = lpLogMapper.selectList(new QueryWrapper<LpLog>().eq("buy_log_id", luckDraw.getId()).eq("source",7).eq("type",1));

                for (LpLog lpLog : lpLogList) {
                    LpLog newLpLog = new LpLog();
                    newLpLog.setCreateTime(new Timestamp(System.currentTimeMillis()));
                    newLpLog.setContent(lpLog.getContent() + " 到期退款");
                    newLpLog.setType(2);
                    newLpLog.setSource(7);
                    newLpLog.setCharacterName(lpLog.getCharacterName());
                    newLpLog.setLp(lpLog.getLp());
                    newLpLog.setCreateBy(luckDraw.getCreateBy());
                    newLpLog.setCreateId(lpLog.getAccountId());
                    newLpLog.setAccountId(lpLog.getAccountId());
                    newLpLog.setUserId(lpLog.getUserId());

                    lpLogMapper.insert(newLpLog);
                    // 给用户加回LP
                    UserAccount userAccount = userAccountMapper.selectById(lpLog.getCreateId());
                    userAccount.setLpUse(userAccount.getLpUse() - lpLog.getLp());
                    userAccount.setLpNow(userAccount.getLpNow() + lpLog.getLp());

                    userAccountMapper.updateById(userAccount);
                }
            }

        }
    }
}
