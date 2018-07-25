package com.waben.stock.datalayer.publisher.controller;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.datalayer.publisher.entity.CapitalFlow;
import com.waben.stock.datalayer.publisher.service.CapitalFlowService;
import com.waben.stock.interfaces.dto.admin.publisher.CapitalFlowAdminDto;
import com.waben.stock.interfaces.dto.admin.publisher.CapitalFlowFuturesAdminDto;
import com.waben.stock.interfaces.dto.publisher.CapitalFlowDto;
import com.waben.stock.interfaces.enums.CapitalFlowExtendType;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.CapitalFlowQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.publisher.CapitalFlowAdminQuery;
import com.waben.stock.interfaces.pojo.query.admin.publisher.CapitalFlowFuturesAdminQuery;
import com.waben.stock.interfaces.service.publisher.CapitalFlowInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import com.waben.stock.interfaces.util.PageToPageInfo;

/**
 * 资金流水 Controller
 * 
 * @author lma
 *
 */
@RestController
@RequestMapping("/capitalFlow")
public class CapitalFlowController implements CapitalFlowInterface {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CapitalFlowService capitalFlowService;

	@Override
	public Response<PageInfo<CapitalFlowAdminDto>> adminPagesByQuery(@RequestBody CapitalFlowAdminQuery query) {
		Page<CapitalFlowAdminDto> page = capitalFlowService.adminPagesByQuery(query);
		PageInfo<CapitalFlowAdminDto> result = PageToPageInfo.pageToPageInfo(page, CapitalFlowAdminDto.class);
		return new Response<>(result);
	}

	@Override
	public Response<BigDecimal> adminAccumulateAmountByQuery(@RequestBody CapitalFlowAdminQuery query) {
		return new Response<>(capitalFlowService.adminAccumulateAmountByQuery(query));
	}

	@Override
	public Response<PageInfo<CapitalFlowDto>> pagesByQuery(@RequestBody CapitalFlowQuery query) {
		Page<CapitalFlow> page = capitalFlowService.pagesByQuery(query);
		PageInfo<CapitalFlowDto> result = PageToPageInfo.pageToPageInfo(page, CapitalFlowDto.class);
		return new Response<>(result);
	}

	@Override
	public Response<BigDecimal> promotionTotalAmount(@PathVariable Long publisherId) {
		return new Response<>(capitalFlowService.promotionTotalAmount(publisherId));
	}

	@Override
	public Response<CapitalFlowDto> fetchById(@PathVariable Long capitalFlowId) {
		CapitalFlow capitalFlow = capitalFlowService.findById(capitalFlowId);
		CapitalFlowDto capitalFlowDto = CopyBeanUtils.copyBeanProperties(CapitalFlowDto.class, capitalFlow, false);
		return new Response<>(capitalFlowDto);
	}

	@Override
	public Response<List<CapitalFlowDto>> fetchByExtendTypeAndExtendId(String extendTypeIndex, Long extendId) {
		List<CapitalFlow> flowList = capitalFlowService
				.findByExtendTypeAndExtendId(CapitalFlowExtendType.getByIndex(extendTypeIndex), extendId);
		return new Response<>(CopyBeanUtils.copyListBeanPropertiesToList(flowList, CapitalFlowDto.class));
	}

	@Override
	public Response<PageInfo<CapitalFlowFuturesAdminDto>> adminFuturesPagesByQuery(CapitalFlowFuturesAdminQuery query) {
		Page<CapitalFlowFuturesAdminDto> page = capitalFlowService.adminFuturesPagesByQuery(query);
		PageInfo<CapitalFlowFuturesAdminDto> result = PageToPageInfo.pageToPageInfo(page, CapitalFlowFuturesAdminDto.class);
		return new Response<>(result);
	}

	@Override
	public Response<BigDecimal> adminAccumulateFuturesAmountByQuery(CapitalFlowFuturesAdminQuery query) {
		return new Response<>(capitalFlowService.adminAccumulateFuturesAmountByquery(query));
	}

}
