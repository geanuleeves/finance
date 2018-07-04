package com.waben.stock.datalayer.futures.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
/**
 * 节假日
 * @author pengzhenliang
 *
 */

@Entity
@Table(name = "f_futures_holiday")
public class FuturesHoliday {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 开始日期
	 */
	private Date startTime;
	
	/**
	 * 结束日期
	 */
	private Date endTime;
	
	/**
	 * 节假日备注
	 */
	private String remark;
	
	/**
	 * 是否生效
	 */
	private Boolean enable;
	
	/**
	 * 对应的品种
	 */
	@ManyToOne
	@JoinColumn(name = "commodity_id")
	private FuturesCommodity commodity;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public FuturesCommodity getCommodity() {
		return commodity;
	}

	public void setCommodity(FuturesCommodity commodity) {
		this.commodity = commodity;
	}
}
