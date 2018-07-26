package com.waben.stock.datalayer.organization.dto;

import java.math.BigDecimal;

public class BenefitConfigOrgDto {

	private Long id;

	private BigDecimal ratio;

	private BigDecimal platformRatio;

	private String code;

	private String treeCode;

	private Long parentId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getRatio() {
		return ratio;
	}

	public void setRatio(BigDecimal ratio) {
		this.ratio = ratio;
	}

	public BigDecimal getPlatformRatio() {
		return platformRatio;
	}

	public void setPlatformRatio(BigDecimal platformRatio) {
		this.platformRatio = platformRatio;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getTreeCode() {
		return treeCode;
	}

	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

}
