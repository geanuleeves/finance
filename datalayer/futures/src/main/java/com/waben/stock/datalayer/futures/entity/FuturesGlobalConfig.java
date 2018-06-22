package com.waben.stock.datalayer.futures.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.waben.stock.datalayer.futures.entity.enumconverter.FuturesGlobalConfigTypeConverter;
import com.waben.stock.interfaces.enums.FuturesGlobalConfigType;
/**
 * 全局设置
 * @author pzl
 *
 */
@Entity
@Table(name = "f_futures_global_Config")
public class FuturesGlobalConfig {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/**
	 * 风控参数
	 */
	private String windControlParameters;
	
	/**
	 * 设置类型
	 */
	@Column(name = "type")
	@Convert(converter = FuturesGlobalConfigTypeConverter.class)
	private FuturesGlobalConfigType type;
	
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

	public String getWindControlParameters() {
		return windControlParameters;
	}

	public void setWindControlParameters(String windControlParameters) {
		this.windControlParameters = windControlParameters;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public FuturesGlobalConfigType getType() {
		return type;
	}

	public void setType(FuturesGlobalConfigType type) {
		this.type = type;
	}

}
