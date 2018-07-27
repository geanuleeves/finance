package com.waben.stock.futuresgateway.yisheng.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.waben.stock.futuresgateway.yisheng.entity.FuturesContract;
import com.waben.stock.futuresgateway.yisheng.service.FuturesContractService;

/**
 * 常用数据缓存
 * 
 * @author lma
 *
 */
@Component
public class CommonDataCache {

	public static Map<Long, FuturesContract> contractMap = new HashMap<>();

	@Autowired
	private FuturesContractService futuresContractService;

	@PostConstruct
	public void init() {
		initContractMap();
	}

	public void initContractMap() {
		List<FuturesContract> contractList = futuresContractService.getByEnable(true);
		if (contractList != null && contractList.size() > 0) {
			for (FuturesContract contract : contractList) {
				contractMap.put(contract.getId(), contract);
			}
		}
	}

}
