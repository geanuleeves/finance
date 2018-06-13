package com.waben.stock.applayer.admin.business.futures;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.waben.stock.interfaces.dto.admin.futures.FuturesCommodityAdminDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesTradeTimeDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesCommodityAdminQuery;
import com.waben.stock.interfaces.service.futures.FuturesCommodityInterface;

@Service
public class FuturesCommodityBusiness {

	@Autowired
	private FuturesCommodityInterface reference;
	
	public Response<PageInfo<FuturesCommodityAdminDto>> pagesCommodity(FuturesCommodityAdminQuery query){
		return reference.pagesAdmin(query);
	}
	
	public Response<FuturesCommodityAdminDto> save(FuturesCommodityAdminDto dto){
		return reference.save(dto);
	}
	
	public Response<FuturesCommodityAdminDto> modify(FuturesCommodityAdminDto dto){
		return reference.modify(dto);
	}
	
	public Response<String> delete(Long id){
		return reference.deleteCommodity(id);
	}
	
	public Response<FuturesTradeTimeDto> saveAndModify(FuturesTradeTimeDto dto){
		return reference.saveAndModify(dto);
	}
	
	public Response<FuturesTradeTimeDto> queryTradeTime(Long commodityId){
		return  reference.queryTradeTime(commodityId);
	}
	
	public Response<String> isCurrency(Long commodityId){
		return reference.isCurrency(commodityId);
	}

}
