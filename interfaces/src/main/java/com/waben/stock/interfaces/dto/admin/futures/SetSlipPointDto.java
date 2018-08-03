package com.waben.stock.interfaces.dto.admin.futures;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModelProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SetSlipPointDto {

	/**
	 * 品种ID
	 */
	@ApiModelProperty(value = "品种ID")
	private Long commodityId;
	/**
	 * 买涨开仓滑点
	 */
	@ApiModelProperty(value = "买涨开仓滑点")
	private Integer buyUpOpenSlipPoint;
	/**
	 * 买涨平仓滑点
	 */
	@ApiModelProperty(value = "买涨平仓滑点")
	private Integer buyUpCloseSlipPoint;
	/**
	 * 买跌开仓滑点
	 */
	@ApiModelProperty(value = "买跌开仓滑点")
	private Integer buyFallOpenSlipPoint;
	/**
	 * 买跌平仓滑点
	 */
	@ApiModelProperty(value = "买跌平仓滑点")
	private Integer buyFallCloseSlipPoint;

	public Long getCommodityId() {
		return commodityId;
	}

	public void setCommodityId(Long commodityId) {
		this.commodityId = commodityId;
	}

	public Integer getBuyUpOpenSlipPoint() {
		return buyUpOpenSlipPoint;
	}

	public void setBuyUpOpenSlipPoint(Integer buyUpOpenSlipPoint) {
		this.buyUpOpenSlipPoint = buyUpOpenSlipPoint;
	}

	public Integer getBuyUpCloseSlipPoint() {
		return buyUpCloseSlipPoint;
	}

	public void setBuyUpCloseSlipPoint(Integer buyUpCloseSlipPoint) {
		this.buyUpCloseSlipPoint = buyUpCloseSlipPoint;
	}

	public Integer getBuyFallOpenSlipPoint() {
		return buyFallOpenSlipPoint;
	}

	public void setBuyFallOpenSlipPoint(Integer buyFallOpenSlipPoint) {
		this.buyFallOpenSlipPoint = buyFallOpenSlipPoint;
	}

	public Integer getBuyFallCloseSlipPoint() {
		return buyFallCloseSlipPoint;
	}

	public void setBuyFallCloseSlipPoint(Integer buyFallCloseSlipPoint) {
		this.buyFallCloseSlipPoint = buyFallCloseSlipPoint;
	}

}
