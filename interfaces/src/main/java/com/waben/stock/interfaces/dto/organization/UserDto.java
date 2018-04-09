package com.waben.stock.interfaces.dto.organization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties
public class UserDto {
	private Long id;
	/**
	 * 登陆用户名
	 */
	private String username;
	/**
	 * 登陆密码
	 */
	private String password;
	/**
	 * 昵称
	 */
	private String nickname;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 所属机构
	 */
	private OrganizationDto org;

	private Long role;
	/**
	 * 是否只有期权功能
	 */
	private boolean onlyStockoption;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public OrganizationDto getOrg() {
		return org;
	}

	public void setOrg(OrganizationDto org) {
		this.org = org;
	}

	public Long getRole() {
		return role;
	}

	public void setRole(Long role) {
		this.role = role;
	}

	public boolean isOnlyStockoption() {
		return onlyStockoption;
	}

	public void setOnlyStockoption(boolean onlyStockoption) {
		this.onlyStockoption = onlyStockoption;
	}

}
