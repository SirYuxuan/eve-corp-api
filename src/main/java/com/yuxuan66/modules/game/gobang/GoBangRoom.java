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
import com.yuxuan66.modules.game.base.BaseGameRoom;
import com.yuxuan66.modules.game.base.Result;
import com.yuxuan66.modules.game.draw.ImgCell;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 五子棋房间
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class GoBangRoom extends BaseGameRoom {

    /**
     * 白棋QQ
     */
    private String whiteQQ = "";
    /**
     * 白棋QQ
     */
    private String blackQQ = "";
    /**
     * 当前棋局绘制
     */
    private Graphics2D currentChessGame;
    /**
     * 棋局图片
     */
    private BufferedImage bufferedImage;

    {
        try {
            bufferedImage = ImageIO.read(new File("C:\\Users\\Administrator\\Desktop\\bg.jpg"));
            currentChessGame = bufferedImage.createGraphics();
        } catch (IOException e) {
            log.error("棋盘背景加载失败!{}", e.getMessage());
        }
    }

    /**
     * 是否33禁手
     */
    private boolean noHands33 = false;
    /**
     * 最大游戏时长 s
     */
    private long maxTotalTime;
    /**
     * 当前游戏共计时长 s
     */
    private long totalTime;
    /**
     * 胜利方 1黑,2白
     */
    private int win;
    /**
     * 当前棋手 1黑,2白
     */
    private int currentChessPlayer = 1;
    /**
     * 当前对局是否开始
     */
    private boolean isStart;

    private int[][] game = new int[15][15];

    private Thread gameThread = new Thread(() -> {
        //如果进程被中断,则退出循环
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            totalTime++;
        }
    });

    /**
     * 开始一场对局
     */
    public void startGame() {
        isStart = true;
        //创建定时器开始计时
        gameThread.start();

    }

    public Result fallenSon(String fromQQ, String position) {
        //1黑,2白
        int chessPlayer = getBlackQQ().equals(fromQQ) ? 1 : 2;
        //落子判断
        if (chessPlayer != currentChessPlayer)
            return Result.fail("当前还不该您落子哦!");


        //x y坐标
        String[] xy = position.toUpperCase().split(" ");

        //x坐标
        int x = Convert.toInt(xy[0], -1);

        if (x < 1 || x > 15)
            return Result.fail("X坐标应为1~15之间");

        //转换为ASCII码
        int y = Convert.toChar(xy[1], '@');
        //转换为坐标
        y -= 64;
        if (y < 1 || y > 15)
            return Result.fail("Y坐标应为A~O之间");

        if (noHands33) {
            //TODO 判断落子位置是否33禁手
        }
        //当前位置棋子判断
        if (game[x - 1][y - 1] != 0) {
            return Result.fail("当前位置有棋子了");
        }


        //当前落子颜色
        BufferedImage piece = chessPlayer == 1 ? GoBang.sunspot : GoBang.whiteSeed;

        //画棋子
        ImgCell mergeCell = new ImgCell();
        mergeCell.setImg(piece.getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        mergeCell.setW(30);
        mergeCell.setH(30);

        mergeCell.setX(12 + (y - 1) * 40);
        mergeCell.setY(14 + (15 - x) * 40);
        mergeCell.draw(currentChessGame);

        Font font = new Font("Default", Font.PLAIN, 12);
        currentChessGame.setFont(font);
        currentChessGame.setColor(Color.black);
        currentChessGame.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        currentChessGame.drawString("暂时无法获取", 600, 47);
        currentChessGame.drawString(getBlackQQ(), 600, 65);
        currentChessGame.drawString(getWhiteQQ(), 600, 85);
        currentChessGame.drawString(getTotalTime() + "s", 600, 105);


        //处理坐标系中棋子
        game[x - 1][y - 1] = chessPlayer;

        //TODO 判断是否胜利
        if (isWin(game, chessPlayer)) {
            destroyGame();
            return Result.fail("WIN");
        }

        //交换落子权
        currentChessPlayer = chessPlayer == 1 ? 2 : 1;
        return Result.ok();
    }

    public static boolean isWin(int[][] qipan, int color) {
        boolean colsWcoln = false;
        int colors = (int) Math.pow(color, 5);
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                //第一种
                if (row <= 10 && col < 4) {
                    //int x = qcolpan[col][row]*qcolpan[col][row]*qcolpan[col][row]*qcolpan[col][row]*qcolpan[col][row]//→
                    int x = qipan[col][row] * qipan[col + 1][row] * qipan[col + 2][row] * qipan[col + 3][row] * qipan[col + 4][row];//→
                    int y = qipan[col][row] * qipan[col + 1][row + 1] * qipan[col + 2][row + 2] * qipan[col + 3][row + 3] * qipan[col + 4][row + 4]; //→
                    int z = qipan[col][row] * qipan[col][row + 1] * qipan[col][row + 2] * qipan[col][row + 3] * qipan[col][row + 4]; //→
                    if (x == colors || y == colors || z == colors) {
                        colsWcoln = true;
                    }
                }
                //第二种
                if (row <= 10 && col >= 4 && col <= 10) {
                    int x = qipan[col][row] * qipan[col + 1][row] * qipan[col + 2][row] * qipan[col + 3][row] * qipan[col + 4][row];//→
                    int y = qipan[col][row] * qipan[col + 1][row + 1] * qipan[col + 2][row + 2] * qipan[col + 3][row + 3] * qipan[col + 4][row + 4]; //→
                    int z = qipan[col][row] * qipan[col][row + 1] * qipan[col][row + 2] * qipan[col][row + 3] * qipan[col][row + 4]; //→
                    int m = qipan[col][row] * qipan[col - 1][row] * qipan[col - 2][row] * qipan[col - 3][row] * qipan[col - 4][row];//→
                    int n = qipan[col][row] * qipan[col - 1][row + 1] * qipan[col - 2][row + 2] * qipan[col - 3][row + 3] * qipan[col - 4][row + 4];//→
                    if (x == colors || y == colors || z == colors || m == colors || n == colors) {
                        colsWcoln = true;
                    }
                }
                //第三种
                if (row <= 10 && col > 10) {
                    int z = qipan[col][row] * qipan[col][row + 1] * qipan[col][row + 2] * qipan[col][row + 3] * qipan[col][row + 4]; //→
                    int m = qipan[col][row] * qipan[col - 1][row] * qipan[col - 2][row] * qipan[col - 3][row] * qipan[col - 4][row];//→
                    int n = qipan[col][row] * qipan[col - 1][row + 1] * qipan[col - 2][row + 2] * qipan[col - 3][row + 3] * qipan[col - 4][row + 4];//→
                    if (z == colors || m == colors || n == colors) {
                        colsWcoln = true;
                    }
                }
                //第四种
                if (row > 10 && col < 4) {
                    int x = qipan[col][row] * qipan[col + 1][row] * qipan[col + 2][row] * qipan[col + 3][row] * qipan[col + 4][row];//→
                    if (x == colors) {
                        colsWcoln = true;
                    }
                }
                //第五种
                if (row > 10 && col >= 4 && col <= 10) {
                    int x = qipan[col][row] * qipan[col + 1][row] * qipan[col + 2][row] * qipan[col + 3][row] * qipan[col + 4][row];//→
                    int m = qipan[col][row] * qipan[col - 1][row] * qipan[col - 2][row] * qipan[col - 3][row] * qipan[col - 4][row];//→
                    if (x == colors || m == colors) {
                        colsWcoln = true;
                    }
                }
                //第六种
                if (row > 10 && col > 10) {
                    int m = qipan[col][row] * qipan[col - 1][row] * qipan[col - 2][row] * qipan[col - 3][row] * qipan[col - 4][row];//→
                    if (m == colors) {
                        colsWcoln = true;
                    }
                }
            }
        }
        return colsWcoln;
    }


    /**
     * 销毁一场对局
     */
    public void destroyGame() {
        //通知进程被中断
        gameThread.interrupt();
    }

}
