package com.waben.stock.datalayer.futures.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.datalayer.futures.entity.FuturesPreQuantity;
import com.waben.stock.datalayer.futures.service.FuturesCommodityService;
import com.waben.stock.datalayer.futures.service.FuturesPreQuantityService;
import com.waben.stock.interfaces.dto.admin.futures.FuturesPreQuantityDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesPreQuantityQuery;
import com.waben.stock.interfaces.service.futures.FuturesPreQuantityInterface;
import com.waben.stock.interfaces.util.PageToPageInfo;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/preQuantity")
@Api(description = "合约预置手数接口列表")
public class FuturesPreQuantityController implements FuturesPreQuantityInterface {
	
	@Autowired
	private FuturesPreQuantityService service;
	
	@Autowired
	private FuturesCommodityService commoditySerivce;

	@Override
	public Response<PageInfo<FuturesPreQuantityDto>> findAll(@RequestBody FuturesPreQuantityQuery query) {
		List<FuturesPreQuantity> list = service.findByCommodityId(query.getCommodityId());
		
		String[] array = query.getQuantity().split(",");
		ArrayList<Integer> quantity = new ArrayList<Integer>();
		for (String temp : array) {
			quantity.add(Integer.valueOf(temp));
		}
		
		if(list.size()==0){
			for(Integer iq:quantity){
				FuturesPreQuantity qu = new FuturesPreQuantity();
				qu.setCommodity(commoditySerivce.retrieve(query.getCommodityId()));
				qu.setQuantity(iq);
				service.save(qu);
			}
		}else{
			if(list.size()>quantity.size()){
				for(int i=0;i<quantity.size();i++){
					FuturesPreQuantity tity = list.get(i);
					if(list.get(i).getCommodity().getId() == query.getCommodityId()){
						tity.setQuantity(quantity.get(i));
						service.modify(tity);
					}
				}
				for(int j=quantity.size();j<list.size();j++){
					service.delete(list.get(j).getId());
				}
			}else{
				for(int i=0;i<list.size();i++){
					FuturesPreQuantity tity = list.get(i);
					if(list.get(i).getCommodity().getId() == query.getCommodityId()){
						tity.setQuantity(quantity.get(i));
						service.modify(tity);
					}
				}
				for(int j=list.size();j<quantity.size();j++){
					FuturesPreQuantity qu = new FuturesPreQuantity();
					qu.setCommodity(commoditySerivce.retrieve(query.getCommodityId()));
					qu.setQuantity(quantity.get(j));
					service.save(qu);
				}
			}
		}
		Page<FuturesPreQuantity> page = service.pagePre(query);
		PageInfo<FuturesPreQuantityDto> pages = PageToPageInfo.pageToPageInfo(page, FuturesPreQuantityDto.class);
		for(int i=0;i<pages.getContent().size();i++){
			if(page.getContent().get(i).getCommodity()!=null){
				pages.getContent().get(i).setCommodityId(page.getContent().get(i).getCommodity().getId());
			}
		}
		return new Response<>(pages);
	}

}
