package com.waben.stock.datalayer.futures.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.datalayer.futures.entity.FuturesContract;
import com.waben.stock.datalayer.futures.entity.FuturesTradeLimit;
import com.waben.stock.datalayer.futures.entity.enumconverter.FuturesTradeLimitTypeConverter;
import com.waben.stock.datalayer.futures.service.FuturesContractService;
import com.waben.stock.datalayer.futures.service.FuturesTradeLimitService;
import com.waben.stock.interfaces.dto.admin.futures.FuturesTradeLimitDto;
import com.waben.stock.interfaces.enums.FuturesTradeLimitType;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesTradeLimitQuery;
import com.waben.stock.interfaces.service.futures.FuturesTradeLimitInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import com.waben.stock.interfaces.util.PageToPageInfo;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/tradeLimit")
@Api(description = "合约风控接口列表")
public class FuturesTradeLimitController implements FuturesTradeLimitInterface {

	@Autowired
	private FuturesTradeLimitService limitService;

	@Autowired
	private FuturesContractService contractService;

	@Override
	public Response<FuturesTradeLimitDto> addLimit(@RequestBody FuturesTradeLimitDto query) {
		FuturesTradeLimit limit = CopyBeanUtils.copyBeanProperties(FuturesTradeLimit.class, query, false);

		FuturesContract contract = contractService.findByContractId(query.getContractId());
		limit.setContract(contract);

		if (query.getLimitType() != null && !"".equals(query.getLimitType())) {
			limit.setLimitType(FuturesTradeLimitType.getByIndex(query.getLimitType()));
		}
		FuturesTradeLimit result = limitService.save(limit);
		FuturesTradeLimitDto dtoResult = CopyBeanUtils.copyBeanProperties(result, new FuturesTradeLimitDto(), false);
		if (result.getContract() != null) {
			dtoResult.setContractId(result.getContract().getId());
		}
		if (result.getLimitType() != null) {
			dtoResult.setLimitType(result.getLimitType().getType());
		}
		return new Response<>(dtoResult);
	}

	@Override
	public Response<FuturesTradeLimitDto> modifyLimit(@RequestBody FuturesTradeLimitDto query) {
		FuturesTradeLimit limit = CopyBeanUtils.copyBeanProperties(FuturesTradeLimit.class, query, false);

		FuturesContract contract = contractService.findByContractId(query.getContractId());
		limit.setContract(contract);

		FuturesTradeLimitTypeConverter converter = new FuturesTradeLimitTypeConverter();
		if (query.getLimitType() != null && !"".equals(query.getLimitType())) {
			limit.setLimitType(converter.convertToEntityAttribute(Integer.valueOf(query.getLimitType())));
		}

		FuturesTradeLimit result = limitService.modify(limit);
		FuturesTradeLimitDto dtoResult = CopyBeanUtils.copyBeanProperties(result, new FuturesTradeLimitDto(), false);
		if (result.getContract() != null) {
			dtoResult.setContractId(result.getContract().getId());
		}
		if (result.getLimitType() != null) {
			dtoResult.setLimitType(result.getLimitType().getType());
		}
		return new Response<>(dtoResult);
	}

	@Override
	public void deleteLimit(@PathVariable Long id) {
		limitService.delete(id);
	}

	@Override
	public Response<PageInfo<FuturesTradeLimitDto>> pagesLimit(@RequestBody FuturesTradeLimitQuery query) {
		Page<FuturesTradeLimit> page = limitService.pagesTradeLimit(query);
		PageInfo<FuturesTradeLimitDto> pages = PageToPageInfo.pageToPageInfo(page, FuturesTradeLimitDto.class);
		for (int i = 0; i < page.getContent().size(); i++) {
			FuturesTradeLimit li = page.getContent().get(i);
			if (li.getContract() != null) {
				pages.getContent().get(i).setContractId(li.getContract().getId());
				pages.getContent().get(i).setContractNo(li.getContract().getContractNo());
				if (li.getContract().getCommodity() != null) {
					pages.getContent().get(i).setCommodityId(li.getContract().getCommodity().getId());
					pages.getContent().get(i).setSymbol(li.getContract().getCommodity().getSymbol());
					pages.getContent().get(i).setName(li.getContract().getCommodity().getName());
					if (li.getContract().getCommodity().getExchange() != null) {
						pages.getContent().get(i).setExchangeId(li.getContract().getCommodity().getExchange().getId());
						pages.getContent().get(i)
								.setExchangcode(li.getContract().getCommodity().getExchange().getCode());
						pages.getContent().get(i)
								.setExchangename(li.getContract().getCommodity().getExchange().getName());
						pages.getContent().get(i)
								.setExchangeType(li.getContract().getCommodity().getExchange().getExchangeType());
					}
				}
			}
			if (li.getLimitType() != null) {
				pages.getContent().get(i).setLimitType(li.getLimitType().getType());
			}
		}
		return new Response<>(pages);
	}

}
