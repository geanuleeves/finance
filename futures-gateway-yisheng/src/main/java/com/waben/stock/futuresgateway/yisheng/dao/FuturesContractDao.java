package com.waben.stock.futuresgateway.yisheng.dao;

import java.util.List;

import org.springframework.data.domain.Page;

import com.waben.stock.futuresgateway.yisheng.entity.FuturesContract;

/**
 * 期货合约 Dao
 * 
 * @author lma
 *
 */
public interface FuturesContractDao {

	public FuturesContract createFuturesContract(FuturesContract futuresContract);

	public void deleteFuturesContractById(Long id);

	public FuturesContract updateFuturesContract(FuturesContract futuresContract);

	public FuturesContract retrieveFuturesContractById(Long id);

	public Page<FuturesContract> pageFuturesContract(int page, int limit);
	
	public List<FuturesContract> listFuturesContract();

	public FuturesContract retrieveByCommodityNoAndContractNo(String commodityNo, String contractNo);

	public List<FuturesContract> retriveByEnable(Boolean enable);

	public List<FuturesContract> retrieveByCommodityNoAndEnable(String commodityNo, boolean enable);

}
