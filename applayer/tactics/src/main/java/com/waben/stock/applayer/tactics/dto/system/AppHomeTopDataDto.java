package com.waben.stock.applayer.tactics.dto.system;

import java.util.List;

import com.waben.stock.interfaces.dto.manage.BannerDto;
import com.waben.stock.interfaces.dto.manage.CircularsDto;

/**
 * APP首页顶部数据
 * 
 * @author luomengan
 *
 */
public class AppHomeTopDataDto {

	/**
	 * 轮播图列表
	 */
	private List<BannerDto> bannerList;
	/**
	 * 通告列表
	 */
	private List<CircularsDto> circularsList;
	/**
	 * 股票市场指数
	 */
	private List<StockMarketIndex> stockMarketIndexList;

	public List<BannerDto> getBannerList() {
		return bannerList;
	}

	public void setBannerList(List<BannerDto> bannerList) {
		this.bannerList = bannerList;
	}

	public List<CircularsDto> getCircularsList() {
		return circularsList;
	}

	public void setCircularsList(List<CircularsDto> circularsList) {
		this.circularsList = circularsList;
	}

	public List<StockMarketIndex> getStockMarketIndexList() {
		return stockMarketIndexList;
	}

	public void setStockMarketIndexList(List<StockMarketIndex> stockMarketIndexList) {
		this.stockMarketIndexList = stockMarketIndexList;
	}

}
