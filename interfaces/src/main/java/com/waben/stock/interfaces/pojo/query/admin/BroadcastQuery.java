package com.waben.stock.interfaces.pojo.query.admin;

import com.waben.stock.interfaces.pojo.query.PageAndSortQuery;

public class BroadcastQuery extends PageAndSortQuery {

	private Long id;
	/**
	 * 直播名称
	 */
	private String name;
	
	/**
	 * 直播地址
	 */
	private String url;
	
	/**
	 * 客户端
	 */
	private String type;
	
	/**
	 * 是否生效
	 */
	private boolean enable;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}
}
