package com.waben.stock.futuresgateway.yingtou.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.waben.stock.futuresgateway.yingtou.dao.FuturesContractDao;
import com.waben.stock.futuresgateway.yingtou.dao.impl.jpa.FuturesContractRepository;
import com.waben.stock.futuresgateway.yingtou.entity.FuturesContract;

/**
 * 期货合约 Dao实现
 * 
 * @author lma
 *
 */
@Repository
public class FuturesContractDaoImpl implements FuturesContractDao {

	@Autowired
	private FuturesContractRepository futuresContractRepository;

	@Override
	public FuturesContract createContract(FuturesContract futuresContract) {
		return futuresContractRepository.save(futuresContract);
	}

	@Override
	public void deleteContractById(Long id) {
		futuresContractRepository.delete(id);
	}

	@Override
	public FuturesContract updateContract(FuturesContract futuresContract) {
		return futuresContractRepository.save(futuresContract);
	}

	@Override
	public FuturesContract retrieveContractById(Long id) {
		return futuresContractRepository.findById(id);
	}

	@Override
	public Page<FuturesContract> pageContract(int page, int limit) {
		return futuresContractRepository.findAll(new PageRequest(page, limit));
	}

	@Override
	public List<FuturesContract> listContract() {
		return futuresContractRepository.findAll();
	}

	@Override
	public FuturesContract retrieveContractBySymbol(String symbol) {
		return futuresContractRepository.findBySymbolIgnoreCase(symbol);
	}

	@Override
	public FuturesContract retrieveContractByEnableAndSymbol(boolean enable, String symbol) {
		return futuresContractRepository.findByEnableAndSymbolIgnoreCase(enable, symbol);
	}

}
