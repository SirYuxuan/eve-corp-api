/*
 * Copyright (C) 2020 projectName:bot-gamecenter,author:yuxuan
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.yuxuan66.modules.game.gobang;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.yuxuan66.modules.game.base.Result;
import com.yuxuan66.modules.game.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 五子棋游戏,重量级对象,所有数据全部缓存在此
 */
@Slf4j
@Service
public class GoBang {
    /**
     * 当前游戏对战
     * key = 群号
     */
    private Map<String, List<GoBangRoom>> currentGame = new HashMap<>();
    /**
     * 黑子
     */
    public static BufferedImage sunspot;

    static {
        try {
            sunspot = ImageIO.read(new File("C:\\Users\\Administrator\\Desktop\\1.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 白子
     */
    public static BufferedImage whiteSeed;

    static {
        try {
            whiteSeed = ImageIO.read(new File("C:\\Users\\Administrator\\Desktop\\2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建一场对局,方法线程同步,避免重复参与游戏
     *
     * @param fromGroup 来源QQ群
     * @param fromQQ    来源QQ
     * @return 200 成功
     */
    public synchronized Result createGame(String fromGroup, String fromQQ) {
        if (isStartGame(fromGroup, fromQQ))
            return Result.fail("当前已存在进行中的对局,无法加入");

        String id = String.valueOf(RandomUtil.randomInt(1000, 999999));
        while (isIdExist(fromGroup, id)) {
            log.warn("ID{},已存在正在重新生成", id);
            id = String.valueOf(RandomUtil.randomInt(1000, 999999));
        }
        List<GoBangRoom> bangRoomList = currentGame.get(fromGroup);

        if (bangRoomList == null)
            bangRoomList = new ArrayList<>();

        //构建一场对局
        GoBangRoom goBangRoom = new GoBangRoom();
        goBangRoom.setId(id);
        //开启游戏者为黑棋
        goBangRoom.setBlackQQ(fromQQ);

        //放入当前群的对局
        bangRoomList.add(goBangRoom);

        //放入整个系统的对局
        currentGame.put(fromGroup, bangRoomList);

        return Result.ok(id);
    }


    /**
     * 加入一场对局
     *
     * @param fromGroup
     * @param fromQQ
     * @param id
     * @return
     */
    public Result joinGame(String fromGroup, String fromQQ, String id) {
        if (!currentGame.containsKey(fromGroup))
            return Result.fail("当前群内不存在五子棋对局");

        //通过id查询当前群内的对局
        GoBangRoom goBangRoom = findGoBangRoomById(fromGroup, id);

        if (goBangRoom == null)
            return Result.fail("对局不存在,您可以创建对局");

        if(goBangRoom.getBlackQQ().equals(fromQQ)){
            return Result.fail("无法加入对局，你想左右互博吗");
        }

        if(StrUtil.isNotBlank(goBangRoom.getWhiteQQ())){
            return Result.fail("房间已满");
        }

        goBangRoom.setWhiteQQ(fromQQ);

        return Result.ok();
    }

    /**
     * 落子
     *
     * @param fromQQ   来源QQ
     * @param position 位置
     */
    public void fallenSon(String fromGroup, String fromQQ, String position, HttpServletResponse response) throws IOException {
        //通过id查询当前群内的对局
       GoBangRoom goBangRoom = findGoBangRoomByQQ(fromGroup, fromQQ);
        if (goBangRoom == null) {
            ResponseUtil.outError(response, "您还没有开始对局");
            return;
        }
        Result result = goBangRoom.fallenSon(fromQQ, position);
        if (!result.isOk()) {
            ResponseUtil.outError(response, Convert.toStr(result.get("msg")));
            return;
        }
        //渲染落子后的图片
        ResponseUtil.outImg(response,goBangRoom.getBufferedImage());
    }




    /**
     * 开始一场对局
     *
     * @param fromGroup    来源群
     * @param fromQQ       来源QQ
     * @param noHands33    是否三三禁手
     * @param maxTotalTime 单局最长时间
     * @return Result
     */
    public Result startGame(String fromGroup, String fromQQ, boolean noHands33, long maxTotalTime) {

        //寻找当前QQ参与的对局
        GoBangRoom goBangRoom = findGoBangRoomByQQ(fromGroup, fromQQ);

        if (goBangRoom == null)
            return Result.fail("您还没有参与的对局,快去创建或加入一场吧");

        //是否三三禁手
        goBangRoom.setNoHands33(noHands33);

        //单局最长时间
        goBangRoom.setMaxTotalTime(maxTotalTime);
        goBangRoom.startGame();

        return Result.ok();
    }


    /**
     * 根据ID找到对局
     *
     * @param fromGroup 来源群
     * @param id        ID
     * @return 五子棋对局
     */
    public GoBangRoom findGoBangRoomById(String fromGroup, String id) {
        if (!currentGame.containsKey(fromGroup))
            return null;

        //当前群内所有进行中的对局
        List<GoBangRoom> bangRoomList = currentGame.get(fromGroup);

        //遍历所有对局,寻找id所在对局
        for (GoBangRoom goBangRoom : bangRoomList) {
            if (goBangRoom.getId().equals(id))
                return goBangRoom;
        }

        return null;
    }

    /**
     * 根据参与者QQ找到对局
     *
     * @param fromGroup 来源群
     * @param fromQQ    来源QQ
     * @return 五子棋对局
     */
    public GoBangRoom findGoBangRoomByQQ(String fromGroup, String fromQQ) {
        if (!currentGame.containsKey(fromGroup))
            return null;

        //当前群内所有进行中的对局
        List<GoBangRoom> bangRoomList = currentGame.get(fromGroup);

        //遍历所有对局,寻找qq所在对局
        for (GoBangRoom goBangRoom : bangRoomList) {
            if (goBangRoom.getWhiteQQ().equals(fromQQ) || goBangRoom.getBlackQQ().equals(fromQQ))
                return goBangRoom;
        }
        return null;
    }

    /**
     * Id是否存在与当前群的对局中
     *
     * @return
     */
    private boolean isIdExist(String fromGroup, String id) {
        if (!currentGame.containsKey(fromGroup))
            return false;

        //当前群内所有进行中的对局
        List<GoBangRoom> bangRoomList = currentGame.get(fromGroup);

        //遍历所有对局,是否存在参与
        for (GoBangRoom goBangRoom : bangRoomList) {
            if (goBangRoom.getId().equals(id))
                return true;
        }

        return false;
    }


    /**
     * 是否有参与中的对局
     *
     * @param fromGroup 来源QQ群
     * @param fromQQ    来源QQ
     * @return 是否存在
     */
    public boolean isStartGame(String fromGroup, String fromQQ) {
        if (!currentGame.containsKey(fromGroup))
            return false;

        //当前群内所有进行中的对局
        List<GoBangRoom> bangRoomList = currentGame.get(fromGroup);

        //遍历所有对局,是否存在参与
        for (GoBangRoom goBangRoom : bangRoomList) {
            if (goBangRoom.getWhiteQQ().equals(fromQQ) || goBangRoom.getBlackQQ().equals(fromQQ))
                return true;
        }

        return false;
    }

    /**
     * 展示棋盘
     * @param response
     * @throws IOException
     */
    public void showCheckerboard(HttpServletResponse response) throws IOException {
        ResponseUtil.outImg(response,ImageIO.read(new File("C:\\Users\\Administrator\\Desktop\\bg.jpg")));
    }
}
