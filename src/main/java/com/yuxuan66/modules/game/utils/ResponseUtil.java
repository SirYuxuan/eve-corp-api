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
package com.yuxuan66.modules.game.utils;

import com.alibaba.fastjson.JSONObject;
import com.yuxuan66.modules.game.base.Result;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public class ResponseUtil {
    /**
     * 往response 输出错误信息
     *
     * @param response
     * @param msg
     * @throws IOException
     */
    public static void outError(HttpServletResponse response, String msg) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();
        out.print(JSONObject.toJSONString(Result.fail(msg)));
        out.close();
        out.flush();
    }

    /**
     * 往前端输出照片
     * @param response response
     * @param bufferedImage bufferedImage
     * @throws IOException IOException
     */
    public static void outImg(HttpServletResponse response, BufferedImage bufferedImage) throws IOException {
        OutputStream os = response.getOutputStream();
        ImageIO.write(bufferedImage, "PNG", os);
        os.flush();
        os.close();
    }
}
