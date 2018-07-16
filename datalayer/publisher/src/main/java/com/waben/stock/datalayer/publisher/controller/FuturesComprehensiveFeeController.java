package com.waben.stock.datalayer.publisher.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.datalayer.publisher.entity.FuturesComprehensiveFee;
import com.waben.stock.datalayer.publisher.entity.WithdrawalsOrder;
import com.waben.stock.datalayer.publisher.service.FuturesComprehensiveFeeService;
import com.waben.stock.interfaces.dto.admin.publisher.FuturesComprehensiveFeeDto;
import com.waben.stock.interfaces.dto.publisher.WithdrawalsOrderDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.FuturesComprehensiveFeeQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.service.publisher.FuturesComprehensiveFeeInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import com.waben.stock.interfaces.util.PageToPageInfo;

@RestController
@RequestMapping("/comprehensive")
public class FuturesComprehensiveFeeController implements FuturesComprehensiveFeeInterface {
	
	@Autowired
	private FuturesComprehensiveFeeService service;

	@Override
	public Response<FuturesComprehensiveFeeDto> save(@RequestBody FuturesComprehensiveFeeDto t) {
		FuturesComprehensiveFee fee = CopyBeanUtils.copyBeanProperties(FuturesComprehensiveFee.class, t, false);
		FuturesComprehensiveFee result = service.save(fee);
		FuturesComprehensiveFeeDto response = CopyBeanUtils.copyBeanProperties(result, new FuturesComprehensiveFeeDto(), false);
		if(fee.getPublisher()!=null){
			response.setPublisherId(result.getPublisher().getId());
			response.setPublisherPhone(result.getPublisher().getPhone());
		}
		return new Response<>(response);
	}

	@Override
	public Response<FuturesComprehensiveFeeDto> modify(@RequestBody FuturesComprehensiveFeeDto t) {
		FuturesComprehensiveFee fee = CopyBeanUtils.copyBeanProperties(FuturesComprehensiveFee.class, t, false);
		FuturesComprehensiveFee result = service.save(fee);
		FuturesComprehensiveFeeDto response = CopyBeanUtils.copyBeanProperties(result, new FuturesComprehensiveFeeDto(), false);
		if(fee.getPublisher()!=null){
			response.setPublisherId(result.getPublisher().getId());
			response.setPublisherPhone(result.getPublisher().getPhone());
		}
		return new Response<>(response);
	}

	@Override
	public Response<Long> delete(@PathVariable("id") Long id) {
		service.delete(id);
		
		return new Response<>(id);
	}

	@Override
	public Response<PageInfo<FuturesComprehensiveFeeDto>> page(@RequestBody FuturesComprehensiveFeeQuery query) {
		Page<FuturesComprehensiveFee> page = service.page(query);
		PageInfo<FuturesComprehensiveFeeDto> response = PageToPageInfo.pageToPageInfo(page, FuturesComprehensiveFeeDto.class);
		if(page!=null && page.getContent()!=null && page.getContent().size()>0){
			for (int i=0;i<page.getContent().size();i++) {
				FuturesComprehensiveFee fee = page.getContent().get(i);
				if(fee.getPublisher()!=null){
					response.getContent().get(i).setPublisherId(fee.getPublisher().getId());
					response.getContent().get(i).setPublisherPhone(fee.getPublisher().getPhone());
				}
			}
			
		}
		return new Response<>(response);
	}

	@Override
	public Response<FuturesComprehensiveFeeDto> retrieve(@PathVariable("id") Long id) {
		FuturesComprehensiveFee fee = service.retrieve(id);
		FuturesComprehensiveFeeDto result = CopyBeanUtils.copyBeanProperties(fee, new FuturesComprehensiveFeeDto(), false);
		if(fee.getPublisher()!=null){
			result.setPublisherId(fee.getPublisher().getId());
			result.setPublisherPhone(fee.getPublisher().getPhone());
		}
		return new Response<>(result);
	}

}
