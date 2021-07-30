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
package com.yuxuan66.modules.lp.rest;

import com.yuxuan66.modules.lp.entity.LpGoods;
import com.yuxuan66.modules.lp.entity.dto.BuyGoodsDto;
import com.yuxuan66.modules.lp.entity.dto.ExchangeApprovalDto;
import com.yuxuan66.modules.lp.entity.query.BuyLogQuery;
import com.yuxuan66.modules.lp.service.LpGoodsService;
import com.yuxuan66.support.basic.BasicQuery;
import com.yuxuan66.support.basic.http.PageEntity;
import com.yuxuan66.support.basic.http.RespEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * @author Sir丶雨轩
 * @since 2021/7/27
 */
@RestController
@RequestMapping(path = "/lpGoods")
public class LpGoodsController {

    private final LpGoodsService lpGoodsService;

    public LpGoodsController(LpGoodsService lpGoodsService) {
        this.lpGoodsService = lpGoodsService;
    }

    /**
     * 分页查询LP商品列表
     * @param basicQuery 查询兼分页参数
     * @return 标准分页返回
     */
    @GetMapping
    public PageEntity list(BasicQuery<LpGoods> basicQuery){
        return lpGoodsService.list(basicQuery);
    }

   /**
     * 分页查询商品购买日志
     * @param buyLogQuery 查询兼分页参数
     * @return 标准分页返回
     */
    @GetMapping(path = "/listBuyLog")
    public PageEntity listBuyLog(BuyLogQuery buyLogQuery){
        return lpGoodsService.listBuyLog(buyLogQuery);
    }

    /**
     * 购买lp商品
     *
     * @param buyGoodsDto 购买参数
     * @return 标准返回
     */
    @PostMapping(path = "/buyGoods")
    public RespEntity buyGoods(@RequestBody BuyGoodsDto buyGoodsDto){
        return lpGoodsService.buyGoods(buyGoodsDto);
    }


    /**
     * LP商品兑换申请审批
     *
     * @param exchangeApprovalDto 审批信息
     * @return 标准返回
     */
    @PostMapping(path = "/exchangeApproval")
    public RespEntity exchangeApproval(@RequestBody ExchangeApprovalDto exchangeApprovalDto){
        return lpGoodsService.exchangeApproval(exchangeApprovalDto);
    }

    /**
     * LP商品新增或修改
     * @param lpGoods lp商品信息
     * @return 标准返回
     */
    @PostMapping
    public RespEntity addOrEdit(@RequestBody LpGoods lpGoods){
        return lpGoodsService.addOrEdit(lpGoods);
    }

    /**
     * 批量删除LP商品
     * @param ids id列表
     * @return 标准返回
     */
    @DeleteMapping
    public RespEntity del(@RequestBody Set<Long> ids) {
        return lpGoodsService.del(ids);
    }
}
