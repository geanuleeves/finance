package com.waben.stock.futuresgateway.yisheng.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.waben.stock.futuresgateway.yisheng.dao.FuturesContractDao;
import com.waben.stock.futuresgateway.yisheng.dao.impl.jpa.FuturesContractRepository;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesContract;

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
	public FuturesContract createFuturesContract(FuturesContract futuresContract) {
		return futuresContractRepository.save(futuresContract);
	}

	@Override
	public void deleteFuturesContractById(Long id) {
		futuresContractRepository.delete(id);
	}

	@Override
	public FuturesContract updateFuturesContract(FuturesContract futuresContract) {
		return futuresContractRepository.save(futuresContract);
	}

	@Override
	public FuturesContract retrieveFuturesContractById(Long id) {
		return futuresContractRepository.findById(id);
	}

	@Override
	public Page<FuturesContract> pageFuturesContract(int page, int limit) {
		return futuresContractRepository.findAll(new PageRequest(page, limit));
	}

	@Override
	public List<FuturesContract> listFuturesContract() {
		return futuresContractRepository.findAll();
	}

	@Override
	public FuturesContract retrieveByCommodityNoAndContractNo(String commodityNo, String contractNo) {
		return futuresContractRepository.findByCommodityNoAndContractNo(commodityNo, contractNo);
	}

	@Override
	public List<FuturesContract> retriveByEnable(Boolean enable) {
		return futuresContractRepository.findByEnable(enable);
	}

	@Override
	public List<FuturesContract> retrieveByCommodityNoAndEnable(String commodityNo, boolean enable) {
		return futuresContractRepository.findByCommodityNoAndEnable(commodityNo, enable);
	}

}
