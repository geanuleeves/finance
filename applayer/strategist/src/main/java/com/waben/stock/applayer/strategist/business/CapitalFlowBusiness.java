package com.waben.stock.applayer.strategist.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.waben.stock.applayer.strategist.dto.publisher.CapitalFlowWithExtendDto;
import com.waben.stock.applayer.strategist.service.CapitalFlowService;
import com.waben.stock.applayer.strategist.service.StrategyTypeService;
import com.waben.stock.interfaces.dto.buyrecord.BuyRecordDto;
import com.waben.stock.interfaces.dto.publisher.CapitalFlowDto;
import com.waben.stock.interfaces.dto.publisher.CapitalFlowExtendDto;
import com.waben.stock.interfaces.dto.stockcontent.StockDto;
import com.waben.stock.interfaces.dto.stockcontent.StrategyTypeDto;
import com.waben.stock.interfaces.enums.CapitalFlowExtendType;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.CapitalFlowQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.util.CopyBeanUtils;

/**
 * 资金流水 Business
 * 
 * @author luomengan
 *
 */
@Service
public class CapitalFlowBusiness {

	@Autowired
	private CapitalFlowService service;

	@Autowired
	private BuyRecordBusiness buyRecordBusiness;

	@Autowired
	private StockBusiness stockBusiness;

	@Autowired
	private StrategyTypeService strategyTypeService;

	public PageInfo<CapitalFlowWithExtendDto> pages(CapitalFlowQuery query) {
		Response<PageInfo<CapitalFlowDto>> response = service.pagesByQuery(query);
		if ("200".equals(response.getCode())) {
			List<CapitalFlowWithExtendDto> content = CopyBeanUtils
					.copyListBeanPropertiesToList(response.getResult().getContent(), CapitalFlowWithExtendDto.class);
			PageInfo<CapitalFlowWithExtendDto> result = new PageInfo<>(content, response.getResult().getTotalPages(),
					response.getResult().getLast(), response.getResult().getTotalElements(),
					response.getResult().getSize(), response.getResult().getNumber(), response.getResult().getFrist());
			// 获取策略类型列表
			Response<List<StrategyTypeDto>> strategyTypeResponse = strategyTypeService.lists(true);
			if (!"200".equals(strategyTypeResponse.getCode())) {
				throw new ServiceException(strategyTypeResponse.getCode());
			}
			// 获取股票代码和股票名称
			for (int i = 0; i < response.getResult().getContent().size(); i++) {
				CapitalFlowDto flow = response.getResult().getContent().get(i);
				CapitalFlowWithExtendDto flowWithExtend = content.get(i);
				if (flow.getExtendList() != null && flow.getExtendList().size() > 0) {
					for (CapitalFlowExtendDto extend : flow.getExtendList()) {
						if (extend.getExtendType() == CapitalFlowExtendType.BUYRECORD) {
							BuyRecordDto buyRecord = buyRecordBusiness.findById(extend.getExtendId());
							if (buyRecord != null) {
								// 设置股票代码和股票名称
								flowWithExtend.setStockCode(buyRecord.getStockCode());
								StockDto stock = stockBusiness.findByCode(buyRecord.getStockCode());
								if (stock != null) {
									flowWithExtend.setStockName(stock.getName());
								}
								// 设置策略类型ID和策略类型名称
								flowWithExtend.setStrategyTypeId(buyRecord.getStrategyTypeId());
								for (StrategyTypeDto strategyType : strategyTypeResponse.getResult()) {
									if (strategyType.getId() == buyRecord.getStrategyTypeId()) {
										flowWithExtend.setStrategyTypeName(strategyType.getName());
									}
								}
							}
						}
					}
				}
			}
			return result;
		}
		throw new ServiceException(response.getCode());
	}

}
