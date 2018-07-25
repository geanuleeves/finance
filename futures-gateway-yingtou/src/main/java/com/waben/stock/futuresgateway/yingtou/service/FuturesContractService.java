package com.waben.stock.futuresgateway.yingtou.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.waben.stock.futuresgateway.yingtou.dao.FuturesContractDao;
import com.waben.stock.futuresgateway.yingtou.entity.FuturesContract;

/**
 * 期货合约 Service
 * 
 * @author lma
 *
 */
@Service
public class FuturesContractService {

	@Autowired
	private FuturesContractDao futuresContractDao;

	public FuturesContract getContractInfoByEnableAndSymbol(String symbol) {
		return futuresContractDao.retrieveContractByEnableAndSymbol(true, symbol);
	}

	public FuturesContract getContractInfo(Long id) {
		return futuresContractDao.retrieveContractById(id);
	}

	@Transactional
	public FuturesContract addContract(FuturesContract futuresContract) {
		return futuresContractDao.createContract(futuresContract);
	}

	@Transactional
	public FuturesContract modifyContract(FuturesContract futuresContract) {
		return futuresContractDao.updateContract(futuresContract);
	}

	@Transactional
	public void deleteContract(Long id) {
		futuresContractDao.deleteContractById(id);
	}

	@Transactional
	public void deleteContracts(String ids) {
		if (ids != null) {
			String[] idArr = ids.split(",");
			for (String id : idArr) {
				if (!"".equals(id.trim())) {
					futuresContractDao.deleteContractById(Long.parseLong(id.trim()));
				}
			}
		}
	}

	public Page<FuturesContract> futuresContracts(int page, int limit) {
		return futuresContractDao.pageContract(page, limit);
	}

	public List<FuturesContract> list() {
		return futuresContractDao.listContract();
	}

}
