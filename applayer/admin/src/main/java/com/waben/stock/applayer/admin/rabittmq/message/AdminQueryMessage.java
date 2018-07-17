package com.waben.stock.applayer.admin.rabittmq.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminQueryMessage {

	/**
	 * 应用id
	 */
	private String appId;
	
	/**
	 * 消息标题
	 */
	private String title;
	
	/**
	 * 消息内容
	 */
	private String content;
	
	/**
	 * 当前消息消费次数
	 */
	private int consumeCount;
	
	public AdminQueryMessage(){
		
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getConsumeCount() {
		return consumeCount;
	}

	public void setConsumeCount(int consumeCount) {
		this.consumeCount = consumeCount;
	}
}
