package com.waben.stock.datalayer.futures.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.datalayer.futures.entity.FuturesCommodity;
import com.waben.stock.datalayer.futures.entity.FuturesExchange;
import com.waben.stock.datalayer.futures.service.FuturesCommodityService;
import com.waben.stock.datalayer.futures.service.FuturesExchangeService;
import com.waben.stock.interfaces.dto.futures.FuturesExchangeDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesExchangeAdminQuery;
import com.waben.stock.interfaces.service.futures.FuturesExchangeInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import com.waben.stock.interfaces.util.PageToPageInfo;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/exchange")
@Api(description = "期货市场接口列表")
public class FuturesExchangeController implements FuturesExchangeInterface {

	@Autowired
	private FuturesExchangeService exchangeService;

	@Autowired
	private FuturesCommodityService commodityService;

	@Override
	public Response<PageInfo<FuturesExchangeDto>> pagesExchange(@RequestBody FuturesExchangeAdminQuery exchangeQuery) {
		Page<FuturesExchange> page = exchangeService.pagesExchange(exchangeQuery);
		PageInfo<FuturesExchangeDto> result = PageToPageInfo.pageToPageInfo(page, FuturesExchangeDto.class);
		return new Response<>(result);
	}

	@Override
	public Response<FuturesExchangeDto> addExchange(@RequestBody FuturesExchangeDto query) {
		FuturesExchange exchange = CopyBeanUtils.copyBeanProperties(FuturesExchange.class, query, false);
		FuturesExchange result = exchangeService.saveExchange(exchange);
		FuturesExchangeDto exchangeDtoResult = CopyBeanUtils.copyBeanProperties(result, new FuturesExchangeDto(),
				false);
		return new Response<>(exchangeDtoResult);
	}

	@Override
	public Response<FuturesExchangeDto> modifyExchange(@RequestBody FuturesExchangeDto exchangeDto) {
		FuturesExchange exchange = CopyBeanUtils.copyBeanProperties(FuturesExchange.class, exchangeDto, false);
		FuturesExchange result = exchangeService.modifyExchange(exchange);
		FuturesExchangeDto exchangeDtoResult = CopyBeanUtils.copyBeanProperties(result, new FuturesExchangeDto(),
				false);
		return new Response<>(exchangeDtoResult);
	}

	@Override
	public Response<String> deleteExchange(@PathVariable Long id) {
		Response<String> res = new Response<String>();

		List<FuturesCommodity> list = exchangeService.findByExchangId(id);
		for (FuturesCommodity futures : list) {
			commodityService.delete(futures.getId());
		}

		exchangeService.deleteExchange(id);
		res.setMessage("删除成功");
		res.setResult(null);
		return res;
	}

	@Override
	public Response<FuturesExchangeDto> findByExchangeId(Long exchangeId) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(FuturesExchangeDto.class,
				exchangeService.findById(exchangeId), false));
	}

}
