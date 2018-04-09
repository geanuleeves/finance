package com.waben.stock.datalayer.stockoption.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.datalayer.stockoption.service.StockOptionQuoteService;
import com.waben.stock.interfaces.dto.stockoption.StockOptionQuoteDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.service.stockoption.StockOptionQuoteInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;

/**
 * 期权报价
 * 
 * @author luomengan
 *
 */
@RestController
@RequestMapping("/stockoptionquote")
public class StockOptionQuoteController implements StockOptionQuoteInterface {

	@Autowired
	private StockOptionQuoteService service;

	@Override
	public Response<StockOptionQuoteDto> quote(@PathVariable Long publisherId, @PathVariable String stockCode,
			@PathVariable Integer cycle) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(StockOptionQuoteDto.class,
				service.quote(publisherId, stockCode, cycle), false));
	}

}
