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

import com.yuxuan66.modules.game.base.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("goBang")
public class GoBangController {

    @Autowired
    private GoBang goBang;

    @RequestMapping("createGame")
    public Result createGame(String fromGroup, String fromQQ) {
        return goBang.createGame(fromGroup, fromQQ);
    }

    @RequestMapping("joinGame")
    public Result joinGame(String fromGroup, String fromQQ, String id) {
        return goBang.joinGame(fromGroup, fromQQ, id);
    }

    @RequestMapping("startGame")
    public Result startGame(String fromGroup, String fromQQ, boolean noHands33, long maxTotalTime) {
        return goBang.startGame(fromGroup, fromQQ, noHands33, maxTotalTime);
    }
    @RequestMapping("showCheckerboard")
    public void showCheckerboard( HttpServletResponse response) throws IOException {
        goBang.showCheckerboard( response);
    }
    @RequestMapping("fallenSon")
    public void fallenSon(String fromGroup, String fromQQ, String position, HttpServletResponse response) throws IOException {
        goBang.fallenSon(fromGroup, fromQQ, position, response);
    }
}
