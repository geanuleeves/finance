package com.waben.stock.applayer.strategist.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.applayer.strategist.dto.system.AppHomeTopDataDto;
import com.waben.stock.applayer.strategist.dto.system.StockMarketExponentDto;
import com.waben.stock.applayer.strategist.service.BannerService;
import com.waben.stock.applayer.strategist.service.CircularsService;
import com.waben.stock.applayer.strategist.service.StockMarketService;
import com.waben.stock.interfaces.dto.manage.BannerDto;
import com.waben.stock.interfaces.dto.manage.CircularsDto;
import com.waben.stock.interfaces.enums.BannerForwardCategory;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.BannerQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author Created by yuyidi on 2017/11/7.
 * @desc
 */
@RestController
@RequestMapping("/system")
@RefreshScope
@Api(description = "系统")
public class SystemController {

	@Value("${spring.jpa.show-sql:error}")
	private String show;

	@GetMapping("/index")
	public String index() {
		return show;
	}

	@Autowired
	private BannerService bannerService;

	@Autowired
	private CircularsService circularsService;

	@Autowired
	private StockMarketService stockMarketService;

	@GetMapping("/getEnabledBannerList")
	@ApiOperation(value = "获取轮播图列表")
	public Response<List<BannerDto>> getBannerList() {
		BannerQuery query = new BannerQuery();
		query.setPage(0);
		query.setSize(10);
		query.setCategory(BannerForwardCategory.PC);
		Response<PageInfo<BannerDto>> pages = bannerService.pages(query);
		if ("200".equals(pages.getCode())) {
			return new Response<>(pages.getResult().getContent());
		} else {
			throw new ServiceException(pages.getCode());
		}
	}

	@GetMapping("/getEnabledCircularsList")
	@ApiOperation(value = "获取通告列表")
	public Response<List<CircularsDto>> getCircularsList() {
		return circularsService.fetchCirculars(true);
	}

	@GetMapping("/stockMarketExponent")
	@ApiOperation(value = "获取大盘指数")
	public Response<List<StockMarketExponentDto>> stockMarketExponent() {
		return new Response<>(stockMarketService.listStockExponent());
	}

	@GetMapping("/getAppHomeTopData")
	@ApiOperation(value = "获取APP首页顶部数据（轮播图、公告、大盘指数）")
	public Response<AppHomeTopDataDto> getAppHomeTopData() {
		Response<AppHomeTopDataDto> result = new Response<>(new AppHomeTopDataDto());
		// 获取轮播图
		Response<List<BannerDto>> bannerListResp = bannerService.fetchBanners(true);
		if ("200".equals(bannerListResp.getCode())) {
			result.getResult().setBannerList(bannerListResp.getResult());
		} else {
			result.setCode(bannerListResp.getCode());
			result.setMessage(bannerListResp.getMessage());
			return result;
		}
		// 获取公告
		Response<List<CircularsDto>> capitalAccountResp = circularsService.fetchCirculars(true);
		if ("200".equals(capitalAccountResp.getCode())) {
			result.getResult().setCircularsList(capitalAccountResp.getResult());
		} else {
			result.setCode(capitalAccountResp.getCode());
			result.setMessage(capitalAccountResp.getMessage());
			return result;
		}
		// 获取股票市场指数
		result.getResult().setStockMarketIndexList(stockMarketService.listStockExponent());
		return result;
	}

}
