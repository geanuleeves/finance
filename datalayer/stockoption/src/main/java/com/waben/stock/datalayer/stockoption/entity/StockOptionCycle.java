package com.waben.stock.datalayer.stockoption.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * 期权周期
 * 
 * @author luomengan
 *
 */
@Entity
@Table(name = "stock_option_cycle")
public class StockOptionCycle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/**
	 * 周期名称
	 */
	private String name;
	/**
	 * 周期天数
	 */
	private Integer cycle;
	/**
	 * 名义本金集合
	 */
	@JsonManagedReference
	@OneToMany(targetEntity = StockOptionAmount.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "cycle_id")
	private Set<StockOptionAmount> amountValues = new HashSet<>();

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

	public Integer getCycle() {
		return cycle;
	}

	public void setCycle(Integer cycle) {
		this.cycle = cycle;
	}

	public Set<StockOptionAmount> getAmountValues() {
		return amountValues;
	}

	public void setAmountValues(Set<StockOptionAmount> amountValues) {
		this.amountValues = amountValues;
	}

}
