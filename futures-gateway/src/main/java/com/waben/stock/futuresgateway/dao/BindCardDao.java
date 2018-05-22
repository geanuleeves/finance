package com.waben.stock.futuresgateway.dao;

import java.util.List;

import org.springframework.data.domain.Page;

import com.waben.stock.futuresgateway.entity.BindCard;

/**
 * 绑卡 Dao
 * 
 * @author luomengan
 *
 */
public interface BindCardDao {

	public BindCard createBindCard(BindCard bindCard);

	public void deleteBindCardById(Long id);

	public BindCard updateBindCard(BindCard bindCard);

	public BindCard retrieveBindCardById(Long id);

	public Page<BindCard> pageBindCard(int page, int limit);
	
	public List<BindCard> listBindCard();

}
