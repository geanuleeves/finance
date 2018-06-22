package com.waben.stock.interfaces.dto.admin.futures;

public class FuturesGlobalConfigDto {

	private Long id;
	
	/**
	 * 风控参数
	 */
	private String windControlParameters;
	
	private Integer type;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWindControlParameters() {
		return windControlParameters;
	}

	public void setWindControlParameters(String windControlParameters) {
		this.windControlParameters = windControlParameters;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
	
}
