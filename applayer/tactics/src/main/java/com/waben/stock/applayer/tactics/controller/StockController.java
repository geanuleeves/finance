package com.waben.stock.applayer.tactics.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.applayer.tactics.business.FavoriteStockBusiness;
import com.waben.stock.applayer.tactics.business.StockBusiness;
import com.waben.stock.applayer.tactics.dto.stockcontent.StockRecommendWithMarketDto;
import com.waben.stock.applayer.tactics.dto.stockcontent.StockWithFavoriteDto;
import com.waben.stock.applayer.tactics.retrivestock.bean.StockKLine;
import com.waben.stock.applayer.tactics.security.SecurityUtil;
import com.waben.stock.interfaces.dto.stockcontent.StockDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.StockQuery;
import com.waben.stock.interfaces.util.CopyBeanUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 股票 Controller
 * 
 * @author luomengan
 *
 */
@RestController
@RequestMapping("/stock")
@Api(description = "股票")
public class StockController {

	@Autowired
	private StockBusiness stockBusiness;

	@Autowired
	private FavoriteStockBusiness favoriteStockBusiness;

	@GetMapping("/selectStock")
	@ApiOperation(value = "查询股票，匹配股票名称/代码/简拼")
	public Response<List<StockWithFavoriteDto>> selectStock(String keyword) {
		StockQuery stockQuery = new StockQuery();
		stockQuery.setKeyword(keyword);
		stockQuery.setPage(0);
		stockQuery.setSize(20);
		PageInfo<StockDto> pages = stockBusiness.pages(stockQuery);
		List<StockWithFavoriteDto> content = CopyBeanUtils.copyListBeanPropertiesToList(pages.getContent(),
				StockWithFavoriteDto.class);
		if (content != null && content.size() > 0) {
			Long publisherId = SecurityUtil.getUserId();
			if (publisherId != null) {
				List<Long> stockIds = favoriteStockBusiness.listsStockId(publisherId);
				if (stockIds != null && stockIds.size() > 0) {
					for (StockWithFavoriteDto stockDto : content) {
						if (stockIds.contains(stockDto.getId())) {
							stockDto.setFavorite(true);
						}
					}
				}
			}
		}
		return new Response<>(content);
	}

	@GetMapping("/stockRecommend")
	@ApiOperation(value = "获取股票推荐列表")
	public Response<PageInfo<StockRecommendWithMarketDto>> stockRecommend(int page, int size) {
		return new Response<>(stockBusiness.stockRecommend(page, size));
	}

	@GetMapping("/kLine")
	@ApiOperation(value = "获取K线图数据", notes = "type:1表示天K，2表示月K； startTime和endTime格式为:yyyy-MM-DD HH:mm:ss")
	public Response<List<StockKLine>> listKLine(String stockCode, Integer type, String startTime, String endTime) {
		return new Response<>(stockBusiness.listKLine(stockCode, type, startTime, endTime));
	}

}
