package com.waben.stock.futuresgateway.yingtou.dao;

import java.util.List;

import org.springframework.data.domain.Page;

import com.waben.stock.futuresgateway.yingtou.entity.FuturesContract;

/**
 * 期货合约 Dao
 * 
 * @author lma
 *
 */
public interface FuturesContractDao {

	public FuturesContract createContract(FuturesContract futuresContract);

	public void deleteContractById(Long id);

	public FuturesContract updateContract(FuturesContract futuresContract);

	public FuturesContract retrieveContractById(Long id);

	public Page<FuturesContract> pageContract(int page, int limit);

	public List<FuturesContract> listContract();

	public FuturesContract retrieveContractBySymbol(String symbol);

	public FuturesContract retrieveContractByEnableAndSymbol(boolean enable, String symbol);

}
