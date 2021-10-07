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
package com.yuxuan66.modules.game.draw;

import java.awt.*;
public class LineCell implements IMergeCell{

    /**
     * 起点坐标
     */
    private int x1, y1;

    /**
     * 终点坐标
     */
    private int x2, y2;

    /**
     * 颜色
     */
    private Color color;


    /**
     * 是否是虚线
     */
    private boolean dashed;

    /**
     * 虚线样式
     */
    private Stroke stroke = null;


    @Override
    public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        if (!dashed) {
            g2d.drawLine(x1, y1, x2, y2);
        } else { // 绘制虚线时，需要保存下原有的画笔用于恢复
            Stroke origin = g2d.getStroke();
            g2d.setStroke(stroke);
            g2d.drawLine(x1, y1, x2, y2);
            g2d.setStroke(origin);
        }
    }
}
