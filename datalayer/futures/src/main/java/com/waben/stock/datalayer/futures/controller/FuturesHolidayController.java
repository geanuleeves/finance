package com.waben.stock.datalayer.futures.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.datalayer.futures.entity.FuturesCommodity;
import com.waben.stock.datalayer.futures.entity.FuturesHoliday;
import com.waben.stock.datalayer.futures.service.FuturesCommodityService;
import com.waben.stock.datalayer.futures.service.FuturesHolidayService;
import com.waben.stock.interfaces.dto.admin.futures.FuturesHolidayDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesHolidayQuery;
import com.waben.stock.interfaces.service.futures.FuturesHolidayInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import com.waben.stock.interfaces.util.PageToPageInfo;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/holiday")
@Api(description = "期货合约接口列表")
public class FuturesHolidayController implements FuturesHolidayInterface {
	
	@Autowired
	private FuturesHolidayService service;
	
	@Autowired
	private FuturesCommodityService commodityService;

	@Override
	public Response<FuturesHolidayDto> addHoliday(@RequestBody FuturesHolidayDto dto) {
		FuturesHoliday holiday = CopyBeanUtils.copyBeanProperties(FuturesHoliday.class, dto, false);
		if(dto.getCommodityId() != null){
			FuturesCommodity commodity = commodityService.retrieve(dto.getCommodityId());
			holiday.setCommodity(commodity);
		}
		holiday.setEnable(false);
		FuturesHoliday result = service.saveAndModify(holiday);
		FuturesHolidayDto response = CopyBeanUtils.copyBeanProperties(result, new FuturesHolidayDto(), false);
		if(result.getCommodity()!=null){
			response.setCommodityId(result.getCommodity().getId());
			response.setCommodityName(result.getCommodity().getName());
			response.setCommoditySymbol(result.getCommodity().getSymbol());
		}
		return new Response<>(response);
	}

	@Override
	public Response<FuturesHolidayDto> modityHoliday(@RequestBody FuturesHolidayDto dto) {
		FuturesHoliday holiday = CopyBeanUtils.copyBeanProperties(FuturesHoliday.class, dto, false);
		if(dto.getCommodityId() != null){
			FuturesCommodity commodity = commodityService.retrieve(dto.getCommodityId());
			holiday.setCommodity(commodity);
		}
		holiday.setEnable(false);
		FuturesHoliday result = service.saveAndModify(holiday);
		FuturesHolidayDto response = CopyBeanUtils.copyBeanProperties(result, new FuturesHolidayDto(), false);
		if(result.getCommodity()!=null){
			response.setCommodityId(result.getCommodity().getId());
			response.setCommodityName(result.getCommodity().getName());
			response.setCommoditySymbol(result.getCommodity().getSymbol());
		}
		return new Response<>(response);
	}

	@Override
	public Response<FuturesHolidayDto> featchById(@RequestBody FuturesHolidayQuery query) {
		FuturesHoliday dto = service.findById(query.getId());
		FuturesHolidayDto result = CopyBeanUtils.copyBeanProperties(dto, new FuturesHolidayDto(),false);
		if(dto.getCommodity()!=null){
			result.setCommodityId(dto.getCommodity().getId());
			result.setCommodityName(dto.getCommodity().getName());
			result.setCommoditySymbol(dto.getCommodity().getSymbol());
		}
		return new Response<>(result);
	}

	@Override
	public Response<String> delete(@RequestBody FuturesHolidayQuery query) {
		service.delete(query.getId());
		return new Response<>("1");
	}

	@Override
	public Response<PageInfo<FuturesHolidayDto>> page(@RequestBody FuturesHolidayQuery query) {
		Page<FuturesHoliday> page = service.page(query);
		PageInfo<FuturesHolidayDto> result = PageToPageInfo.pageToPageInfo(page, FuturesHolidayDto.class);
		if(result !=null && result.getContent()!=null){
			List<FuturesHoliday> list = page.getContent();
			for(int i=0;i<list.size();i++){
				result.getContent().get(i).setCommodityId(list.get(i).getCommodity().getId());
				result.getContent().get(i).setCommodityName(list.get(i).getCommodity().getName());
				result.getContent().get(i).setCommoditySymbol(list.get(i).getCommodity().getSymbol());
				if(list.get(i).getEndTime()!=null){
					Long endTime = list.get(i).getEndTime().getTime();
					Long currTime = new Date().getTime();
					if(currTime>endTime){
						result.getContent().get(i).setState("3");
					}else{
						if(list.get(i).getEnable()){
							result.getContent().get(i).setState("2");
						}else{
							result.getContent().get(i).setState("1");
						}
					}
				}
			}
		}
		return new Response<>(result);
	}

	@Override
	public Response<FuturesHolidayDto> isEnable(@RequestBody FuturesHolidayQuery query) {
		FuturesHoliday holiday = service.findById(query.getId());
		holiday.setEnable(true);
		FuturesHoliday result = service.saveAndModify(holiday);
		FuturesHolidayDto response = CopyBeanUtils.copyBeanProperties(result, new FuturesHolidayDto(),false);
		if(result.getCommodity()!=null){
			response.setCommodityId(result.getCommodity().getId());
			response.setCommodityName(result.getCommodity().getName());
			response.setCommoditySymbol(result.getCommodity().getSymbol());
		}
		return new Response<>(response);
	}

}
