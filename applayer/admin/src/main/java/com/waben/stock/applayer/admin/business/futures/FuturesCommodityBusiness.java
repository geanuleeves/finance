package com.waben.stock.applayer.admin.business.futures;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.waben.stock.interfaces.dto.admin.futures.FuturesCommodityAdminDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesTradeTimeDto;
import com.waben.stock.interfaces.dto.admin.futures.SetSlipPointDto;
import com.waben.stock.interfaces.dto.futures.FuturesCommodityDto;
import com.waben.stock.interfaces.dto.futures.FuturesStopLossOrProfitDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesCommodityAdminQuery;
import com.waben.stock.interfaces.service.futures.FuturesCommodityInterface;

@Service
public class FuturesCommodityBusiness {

	@Autowired
	private FuturesCommodityInterface reference;
	
	public Response<Integer> getTradingState(Long id){
		return reference.getTradingState(id);
	}

	public Response<PageInfo<FuturesCommodityAdminDto>> pagesCommodity(FuturesCommodityAdminQuery query) {
		return reference.pagesAdmin(query);
	}

	public Response<FuturesCommodityAdminDto> save(FuturesCommodityAdminDto dto) {
		return reference.save(dto);
	}

	public Response<FuturesCommodityAdminDto> modify(FuturesCommodityAdminDto dto) {
		return reference.modify(dto);
	}

	public Response<String> delete(Long id) {
		return reference.deleteCommodity(id);
	}

	public Response<FuturesCommodityAdminDto> saveAndModify(FuturesTradeTimeDto dto) {
		return reference.saveAndModify(dto);
	}

	public Response<FuturesCommodityAdminDto> queryTradeTime(Long commodityId) {
		return reference.queryTradeTime(commodityId);
	}

	public Response<String> isCurrency(Long commodityId) {
		return reference.isCurrency(commodityId);
	}

	public List<FuturesCommodityDto> listByExchangeId(Long exchangeId) {
		Response<List<FuturesCommodityDto>> response = reference.listByExchangeId(exchangeId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public Response<FuturesCommodityAdminDto> setSlipPoint(SetSlipPointDto dto) {
		return reference.setSlipPoint(dto);
	}

	public Integer saveLossOrProfit(List<FuturesStopLossOrProfitDto> lossOrProfitDto) {
		Response<Integer> response = reference.saveLossOrProfit(lossOrProfitDto);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

}
