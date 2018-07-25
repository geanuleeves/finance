package com.waben.stock.datalayer.futures.entity;

import java.util.Date;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.waben.stock.datalayer.futures.entity.enumconverter.FuturesTradeLimitTypeConverter;
import com.waben.stock.interfaces.enums.FuturesTradeLimitType;

/**
 * 期货交易 限制
 * 
 * @author lma
 *
 */
@Entity
@Table(name = "f_futures_contract_limit")
public class FuturesTradeLimit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/**
	 * 对应的期货合约
	 */
	@ManyToOne
	@JoinColumn(name = "contract_id")
	private FuturesContract contract;
	/**
	 * 限制类型
	 */
	@Convert(converter = FuturesTradeLimitTypeConverter.class)
	private FuturesTradeLimitType limitType;
	
	/**
	 * 星期
	 */
	private Integer weekDay;
	
	/**
	 * 开始时间
	 */
	private String startLimitTime;
	/**
	 * 结束时间
	 */
	private String endLimitTime;
	/**
	 * 是否可用
	 */
	private Boolean enable;
	/**
	 * 更新时间
	 */
	private Date updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public FuturesContract getContract() {
		return contract;
	}

	public void setContract(FuturesContract contract) {
		this.contract = contract;
	}

	public FuturesTradeLimitType getLimitType() {
		return limitType;
	}

	public void setLimitType(FuturesTradeLimitType limitType) {
		this.limitType = limitType;
	}

	public Integer getWeekDay() {
		return weekDay;
	}

	public void setWeekDay(Integer weekDay) {
		this.weekDay = weekDay;
	}

	public String getStartLimitTime() {
		return startLimitTime;
	}

	public void setStartLimitTime(String startLimitTime) {
		this.startLimitTime = startLimitTime;
	}

	public String getEndLimitTime() {
		return endLimitTime;
	}

	public void setEndLimitTime(String endLimitTime) {
		this.endLimitTime = endLimitTime;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}
