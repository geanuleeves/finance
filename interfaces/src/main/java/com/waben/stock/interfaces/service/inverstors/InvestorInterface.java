package com.waben.stock.interfaces.service.inverstors;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.waben.stock.interfaces.dto.buyrecord.BuyRecordDto;
import com.waben.stock.interfaces.dto.investor.InvestorDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.InvestorQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.stock.SecuritiesStockEntrust;

/**
 * 投资人 reference服务接口
 * 
 * @author lma
 *
 */
@FeignClient(name = "investors", path = "investor", qualifier = "investorInterface")
public interface InvestorInterface {
	@RequestMapping(value = "/modify", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<Integer> modify(@RequestBody InvestorDto investorDto);

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	void delete(@PathVariable("id") Long id);

	@RequestMapping(value = "/pages", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<PageInfo<InvestorDto>> pagesByQuery(@RequestBody InvestorQuery investorQuery);

	@RequestMapping(value = "/id/{id}", method = RequestMethod.GET)
	Response<InvestorDto> fetchById(@PathVariable("id") Long id);

	@RequestMapping(value = "/{username}", method = RequestMethod.GET)
	Response<InvestorDto> fetchByUserName(@PathVariable("username") String username);

	@RequestMapping(value = "/{investor}/buyrecord/applybuyin", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<BuyRecordDto> stockApplyBuyIn(@PathVariable("investor") Long investor,
			@RequestBody SecuritiesStockEntrust securitiesStockEntrust,
			@RequestParam("tradeSession") String tradeSession);

	@RequestMapping(value = "/volapplybuyin/{buyrecord}/", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<BuyRecordDto> voluntarilyStockApplyBuyIn(@PathVariable("buyrecord") Long buyrecord);

	@RequestMapping(value = "/{investor}/buyrecord/applysellout", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<BuyRecordDto> stockApplySellOut(@PathVariable("investor") Long investor,
			@RequestBody SecuritiesStockEntrust securitiesStockEntrust,
			@RequestParam("tradeSession") String tradeSession);

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	Response<List<InvestorDto>> fetchAllInvestors();
}
