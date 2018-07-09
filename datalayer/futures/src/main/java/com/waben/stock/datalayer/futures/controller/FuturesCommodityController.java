package com.waben.stock.datalayer.futures.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.datalayer.futures.entity.FuturesCommodity;
import com.waben.stock.datalayer.futures.entity.FuturesContract;
import com.waben.stock.datalayer.futures.entity.FuturesCurrencyRate;
import com.waben.stock.datalayer.futures.entity.FuturesExchange;
import com.waben.stock.datalayer.futures.entity.FuturesPreQuantity;
import com.waben.stock.datalayer.futures.entity.enumconverter.FuturesProductTypeConverter;
import com.waben.stock.datalayer.futures.service.FuturesCommodityService;
import com.waben.stock.datalayer.futures.service.FuturesContractService;
import com.waben.stock.datalayer.futures.service.FuturesCurrencyRateService;
import com.waben.stock.datalayer.futures.service.FuturesExchangeService;
import com.waben.stock.datalayer.futures.service.FuturesPreQuantityService;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.admin.futures.FuturesCommodityAdminDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesPreQuantityDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesTradeTimeDto;
import com.waben.stock.interfaces.dto.admin.futures.SetSlipPointDto;
import com.waben.stock.interfaces.dto.futures.FuturesCommodityDto;
import com.waben.stock.interfaces.enums.FuturesProductType;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesCommodityAdminQuery;
import com.waben.stock.interfaces.service.futures.FuturesCommodityInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import com.waben.stock.interfaces.util.PageToPageInfo;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/commodity")
@Api(description = "期货品种接口列表")
public class FuturesCommodityController implements FuturesCommodityInterface {

	@Autowired
	private FuturesCommodityService commodityService;

	@Autowired
	private FuturesContractService contractService;

	@Autowired
	private FuturesExchangeService exchangeService;

	@Autowired
	private FuturesCurrencyRateService rateService;

	@Autowired
	private FuturesPreQuantityService quantityService;

	@Override
	public Response<PageInfo<FuturesCommodityAdminDto>> pagesAdmin(@RequestBody FuturesCommodityAdminQuery query) {
		Page<FuturesCommodity> page = commodityService.pages(query);
		PageInfo<FuturesCommodityAdminDto> result = PageToPageInfo.pageToPageInfo(page, FuturesCommodityAdminDto.class);
		if (result != null && result.getContent() != null) {
			for (int i = 0; i < result.getContent().size(); i++) {
				FuturesCommodity commodity = page.getContent().get(i);
				result.getContent().get(i).setExchangcode(commodity.getExchange().getCode());
				result.getContent().get(i).setExchangename(commodity.getExchange().getName());
				result.getContent().get(i).setExchangeType(commodity.getExchange().getExchangeType());
				result.getContent().get(i).setExchangeId(commodity.getExchange().getId());
				result.getContent().get(i).setProductType(commodity.getProductType().getValue());
				// 查询汇率
				FuturesCurrencyRate rate = rateService.findByCurrency(commodity.getCurrency());
				if (rate != null) {
					result.getContent().get(i).setRate(rate.getRate());
				}
				List<FuturesPreQuantity> quantity = quantityService.findByCommodityId(commodity.getId());
				List<FuturesPreQuantityDto> quDto = new ArrayList<FuturesPreQuantityDto>();
				for (FuturesPreQuantity fq : quantity) {
					quDto.add(CopyBeanUtils.copyBeanProperties(fq, new FuturesPreQuantityDto(), false));
				}
				result.getContent().get(i).setPreQuantityDto(quDto);
			}
		}
		return new Response<>(result);
	}

	@Override
	public Response<FuturesCommodityAdminDto> save(@RequestBody FuturesCommodityAdminDto dto) {
		FuturesProductTypeConverter converter = new FuturesProductTypeConverter();

		FuturesCommodity commodity = CopyBeanUtils.copyBeanProperties(FuturesCommodity.class, dto, false);
		FuturesExchange exchange = exchangeService.findById(dto.getExchangeId());
		commodity.setExchange(exchange);
		commodity.setEnable(false);
		commodity.setUpdateTime(new Date());
		commodity.setProductType(converter.convertToEntityAttribute(Integer.valueOf(dto.getProductType())));
		commodity.setCreateTime(new Date());
		FuturesCommodity result = commodityService.save(commodity);
		FuturesCommodityAdminDto response = CopyBeanUtils.copyBeanProperties(result, new FuturesCommodityAdminDto(),
				false);

		if (response != null) {
			response.setExchangcode(exchange.getCode());
			response.setExchangename(exchange.getName());
			response.setExchangeId(exchange.getId());
			response.setExchangeType(exchange.getExchangeType());

			// 查询汇率
			FuturesCurrencyRate rate = rateService.findByCurrency(response.getCurrency());
			if (rate != null) {
				response.setRate(rate.getRate());
			}
		}
		return new Response<>(response);
	}

	@Override
	public Response<FuturesCommodityAdminDto> modify(@RequestBody FuturesCommodityAdminDto dto) {
		FuturesCommodity oldCommodity = commodityService.retrieve(dto.getId());
		FuturesExchange exchange = exchangeService.findById(dto.getExchangeId());
		oldCommodity.setExchange(exchange);
		oldCommodity.setUpdateTime(new Date());
		oldCommodity.setProductType(FuturesProductType.getByIndex(dto.getProductType()));
		oldCommodity.setCreateTime(new Date());
		oldCommodity.setEnable(dto.getEnable());

		oldCommodity.setContractDesc(dto.getContractDesc());
		oldCommodity.setCordon(dto.getCordon());
		oldCommodity.setSymbol(dto.getSymbol());
		oldCommodity.setName(dto.getName());
		oldCommodity.setCurrency(dto.getCurrency());
		oldCommodity.setQutoteUnit(dto.getQutoteUnit());
		oldCommodity.setTradeUnit(dto.getTradeUnit());
		oldCommodity.setMinWave(dto.getMinWave());
		oldCommodity.setPerWaveMoney(dto.getPerWaveMoney());
		oldCommodity.setPerUnitReserveFund(dto.getPerUnitReserveFund());
		oldCommodity.setPerUnitUnwindPoint(dto.getPerUnitUnwindPoint());
		oldCommodity.setUnwindPointType(dto.getUnwindPointType());
		oldCommodity.setOpenwindServiceFee(dto.getOpenwindServiceFee());
		oldCommodity.setUnwindServiceFee(dto.getUnwindServiceFee());
		oldCommodity.setOvernightTime(dto.getOvernightTime());
		oldCommodity.setReturnOvernightReserveFundTime(dto.getReturnOvernightReserveFundTime());
		oldCommodity.setIcon(dto.getIcon());
		oldCommodity.setOvernightPerUnitReserveFund(dto.getOvernightPerUnitReserveFund());
		oldCommodity.setOvernightPerUnitDeferredFee(dto.getOvernightPerUnitDeferredFee());
		oldCommodity.setPerContractValue(dto.getPerContractValue());

		FuturesCommodity result = commodityService.modify(oldCommodity);
		FuturesCommodityAdminDto response = CopyBeanUtils.copyBeanProperties(result, new FuturesCommodityAdminDto(),
				false);

		if (response != null) {
			response.setExchangcode(exchange.getCode());
			response.setExchangename(exchange.getName());
			response.setExchangeId(exchange.getId());
			response.setExchangeType(exchange.getExchangeType());

			// 查询汇率
			FuturesCurrencyRate rate = rateService.findByCurrency(response.getCurrency());
			if (rate != null) {
				response.setRate(rate.getRate());
			}
		}
		return new Response<>(response);
	}

	@Override
	public Response<String> deleteCommodity(@PathVariable("id") Long id) {
		List<FuturesContract> result = contractService.findByCommodity(id);
		if (result != null && result.size() > 0) {
			throw new ServiceException(ExceptionConstant.COMMODITY_HAVING_CONTRACT_EXCEPTION);
		}
		// for (int i = 0; i < result.size(); i++) {
		// contractService.deleteContract(result.get(i).getId());
		// }
		quantityService.deleteByCommodityId(id);
		commodityService.delete(id);
		Response<String> res = new Response<String>();
		res.setCode("200");
		res.setMessage("响应成功");
		res.setResult("1");
		return res;
	}

	@Override
	public Response<FuturesCommodityAdminDto> queryTradeTime(@PathVariable("id") Long id) {
		FuturesCommodity commodity = commodityService.retrieve(id);
		FuturesTradeTimeDto result = CopyBeanUtils.copyBeanProperties(commodity, new FuturesTradeTimeDto(), false);
		result.setCommodityId(commodity.getId());
		return new Response<>();
	}

	@Override
	public Response<FuturesCommodityAdminDto> saveAndModify(@RequestBody FuturesTradeTimeDto dto) {
		FuturesCommodity commodity = commodityService.retrieve(dto.getCommodityId());
		commodity.setFriTradeTime(dto.getFriTradeTime());
		commodity.setFriTradeTimeDesc(dto.getFriTradeTimeDesc());

		commodity.setMonTradeTime(dto.getMonTradeTime());
		commodity.setMonTradeTimeDesc(dto.getMonTradeTimeDesc());
		commodity.setSatTradeTime(dto.getSatTradeTime());
		commodity.setSatTradeTimeDesc(dto.getSatTradeTimeDesc());
		commodity.setSunTradeTime(dto.getSunTradeTime());
		commodity.setSunTradeTimeDesc(dto.getSunTradeTimeDesc());
		commodity.setThuTradeTime(dto.getThuTradeTime());
		commodity.setThuTradeTimeDesc(dto.getThuTradeTimeDesc());
		commodity.setTueTradeTime(dto.getTueTradeTime());
		commodity.setTueTradeTimeDesc(dto.getTueTradeTimeDesc());

		commodity.setWedTradeTime(dto.getWedTradeTime());
		commodity.setWedTradeTimeDesc(dto.getWedTradeTimeDesc());
		commodity.setUpdateTime(new Date());
		commodityService.modify(commodity);
		FuturesCommodity result = commodityService.modify(commodity);
		FuturesCommodityAdminDto response = CopyBeanUtils.copyBeanProperties(result, new FuturesCommodityAdminDto(),
				false);

		if (response != null) {
			response.setExchangcode(result.getExchange().getCode());
			response.setExchangename(result.getExchange().getName());
			response.setExchangeId(result.getExchange().getId());
			response.setExchangeType(result.getExchange().getExchangeType());

			// 查询汇率
			FuturesCurrencyRate rate = rateService.findByCurrency(response.getCurrency());
			if (rate != null) {
				response.setRate(rate.getRate());
			}
		}
		return new Response<>(response);
	}

	@Override
	public Response<String> isCurrency(@PathVariable("id") Long id) {
		FuturesCommodity commodity = commodityService.retrieve(id);
		boolean isCurrency = commodity.getEnable() == null ? false : commodity.getEnable();
		List<FuturesContract> list = contractService.findByCommodity(commodity.getId());
		if (!isCurrency) {
			for (FuturesContract contract : list) {
				if (contract.getEnable()) {
					isCurrency = true;
				}
			}
			if (isCurrency) {
				commodity.setEnable(isCurrency);
			} else {
				throw new ServiceException(ExceptionConstant.CONTRACTTERM_ISCURRENT_EXCEPTION);
			}

			List<FuturesPreQuantity> quantity = quantityService.findByCommodityId(commodity.getId());
			if (quantity == null || quantity.size() == 0) {
				throw new ServiceException(ExceptionConstant.CONTRACT_PREQUANTITY_EXCEPTION);
			}

			if (commodity.getFriTradeTime() == null || "".equals(commodity.getFriTradeTime())) {
				throw new ServiceException(ExceptionConstant.COMMODITY_TRADETIME_ISNULL_EXCEPTION);
			}

		} else {
			for (FuturesContract contract : list) {
				if (contract.getEnable()) {
					contractService.isCurrent(contract.getId());
				}
			}
			commodity.setEnable(false);

		}
		commodityService.modify(commodity);
		Response<String> res = new Response<String>();
		res.setCode("200");
		res.setMessage("响应成功");
		res.setResult("1");
		return res;
	}

	@Override
	public Response<FuturesCommodityDto> getFuturesByCommodityId(@PathVariable Long commodityId) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(FuturesCommodityDto.class,
				commodityService.retrieve(commodityId), false));
	}

	public Response<List<FuturesCommodityDto>> listByExchangeId(@PathVariable Long exchangeId) {
		return new Response<>(CopyBeanUtils.copyListBeanPropertiesToList(commodityService.listByExchangeId(exchangeId),
				FuturesCommodityDto.class));
	}

	@Override
	public Response<FuturesCommodityAdminDto> setSlipPoint(@RequestBody SetSlipPointDto dto) {
		FuturesCommodity commodity = commodityService.setSlipPoint(dto.getCommodityId(), dto.getBuyUpOpenSlipPoint(),
				dto.getBuyUpCloseSlipPoint(), dto.getBuyFallOpenSlipPoint(), dto.getBuyFallCloseSlipPoint());
		return new Response<>(CopyBeanUtils.copyBeanProperties(FuturesCommodityAdminDto.class, commodity, false));
	}

}
