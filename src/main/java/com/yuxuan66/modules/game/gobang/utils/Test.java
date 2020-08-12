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
package com.yuxuan66.modules.game.gobang.utils;

import com.yuxuan66.modules.game.draw.ImgCell;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        BufferedImage bg = ImageIO.read(new BufferedInputStream(new FileInputStream(new File("C:\\Users\\Administrator\\Desktop\\bg.jpg"))));
        BufferedImage b = ImageIO.read(new BufferedInputStream(new FileInputStream(new File("C:\\Users\\Administrator\\Desktop\\1.png"))));

        Graphics2D g = bg.createGraphics();

        ImgCell mergeCell = new ImgCell();
        mergeCell.setImg(b.getScaledInstance(22, 22, Image.SCALE_SMOOTH));
        mergeCell.setW(32);
        mergeCell.setH(32);
        int x = 8;
        int y = 8;


        mergeCell.setX(22 + (x - 1) * 35);
        mergeCell.setY(22 + (y - 1) * 35);
        mergeCell.draw(g);
        Font font = new Font("Default",Font.PLAIN,12);
        g.setFont(font);
        g.setColor(Color.black);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //47
        g.drawString("DNF虐杀小分队",600,67);
        ImageIO.write(bg, "png", new File("C:\\Users\\Administrator\\Desktop\\tmp.png"));
    }
}
