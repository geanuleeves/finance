package com.waben.stock.interfaces.service.futures;

import java.math.BigDecimal;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.waben.stock.interfaces.dto.admin.futures.AgentOrderRecordDto;
import com.waben.stock.interfaces.dto.admin.futures.FutresOrderEntrustDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesHoldPositionAgentDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesOrderAdminDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesOrderCountDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesTradeActionAgentDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesTradeAdminQuery;
import com.waben.stock.interfaces.pojo.query.futures.FuturesContractOrderQuery;

/**
 * 期货交易 reference服务接口
 *
 * @author pengzhenliang
 */
@FeignClient(name = "futures", path = "futuresTrade", qualifier = "futuresTradeInterface")
public interface FuturesTradeInterface {

	/**
	 * 分页查询期货订单（管理后台）
	 * 
	 * @param query
	 *            查询条件
	 * @return 期货订单分页数据
	 */
	@RequestMapping(value = "/adminpages", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<PageInfo<FuturesOrderAdminDto>> adminPagesByQuery(@RequestBody FuturesTradeAdminQuery query);

	@RequestMapping(value = "/pagesOrderEntrust", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<PageInfo<FutresOrderEntrustDto>> pagesOrderEntrust(@RequestBody FuturesTradeAdminQuery query);

	@RequestMapping(value = "/countOrderState", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<Object[]> countOrderState(@RequestParam(name = "state") String state);

	@RequestMapping(value = "/getSUMOrder", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<FuturesOrderCountDto> getSUMOrder(@RequestBody FuturesTradeAdminQuery query);

	@RequestMapping(value = "/pages/order/record", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<PageInfo<AgentOrderRecordDto>> pagesOrderRecord(@RequestBody FuturesTradeAdminQuery query);

	@RequestMapping(value = "/sum/over/night/record/{orderId}", method = RequestMethod.GET)
	Response<BigDecimal> getSUMOvernightRecord(@PathVariable("orderId") Long orderId);

	@RequestMapping(value = "/pages/order/agent/deal/record", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<PageInfo<FuturesTradeActionAgentDto>> pagesOrderAgentDealRecord(@RequestBody FuturesTradeAdminQuery query);

	@RequestMapping(value = "/pages/agent/position", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<PageInfo<FuturesHoldPositionAgentDto>> pagesAgentAdmin(@RequestBody FuturesTradeAdminQuery query);
}
