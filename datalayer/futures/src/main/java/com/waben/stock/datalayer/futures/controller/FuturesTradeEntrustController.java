package com.waben.stock.datalayer.futures.controller;

import com.waben.stock.datalayer.futures.entity.FuturesCommodity;
import com.waben.stock.datalayer.futures.entity.FuturesCurrencyRate;
import com.waben.stock.datalayer.futures.entity.FuturesTradeEntrust;
import com.waben.stock.datalayer.futures.quote.QuoteContainer;
import com.waben.stock.datalayer.futures.service.FuturesCommodityService;
import com.waben.stock.datalayer.futures.service.FuturesCurrencyRateService;
import com.waben.stock.datalayer.futures.service.FuturesTradeEntrustService;
import com.waben.stock.interfaces.dto.admin.futures.FuturesTradeDto;
import com.waben.stock.interfaces.dto.futures.FuturesTradeEntrustDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesTradeAdminQuery;
import com.waben.stock.interfaces.pojo.query.futures.FuturesTradeEntrustQuery;
import com.waben.stock.interfaces.service.futures.FuturesTradeEntrustInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import com.waben.stock.interfaces.util.PageToPageInfo;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 交易委托
 *
 * @author chenk 2018/7/26
 */
@RestController
@RequestMapping("/trade_entrust")
@Api(description = "交易委托接口列表")
public class FuturesTradeEntrustController implements FuturesTradeEntrustInterface {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FuturesTradeEntrustService futuresTradeEntrustService;

	@Autowired
	private FuturesCurrencyRateService rateService;

	@Autowired
	private QuoteContainer quoteContainer;

	@Autowired
	private FuturesCommodityService futuresCommodityService;

	@Override
	public Response<FuturesTradeEntrustDto> fetchById(@PathVariable Long id) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(FuturesTradeEntrustDto.class,
				futuresTradeEntrustService.findById(id), false));
	}

	@Override
	public Response<FuturesTradeEntrustDto> save(@RequestBody FuturesTradeEntrustDto dto) {
		FuturesTradeEntrust futuresTradeEntrust = CopyBeanUtils.copyBeanProperties(FuturesTradeEntrust.class, dto,
				false);
		FuturesTradeEntrust result = futuresTradeEntrustService.save(futuresTradeEntrust);
		FuturesTradeEntrustDto response = CopyBeanUtils.copyBeanProperties(result, new FuturesTradeEntrustDto(), false);
		return new Response<>(response);
	}

	@Override
	public Response<FuturesTradeEntrustDto> modify(FuturesTradeEntrustDto dto) {
		FuturesTradeEntrust futuresTradeEntrust = CopyBeanUtils.copyBeanProperties(FuturesTradeEntrust.class, dto,
				false);
		FuturesTradeEntrust result = futuresTradeEntrustService.modify(futuresTradeEntrust);
		FuturesTradeEntrustDto resultDto = CopyBeanUtils.copyBeanProperties(result, new FuturesTradeEntrustDto(),
				false);
		return new Response<>(resultDto);
	}

	@Override
	public Response<FuturesTradeEntrustDto> cancelEntrust(@PathVariable Long id, Long publisherId) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(FuturesTradeEntrustDto.class,
				futuresTradeEntrustService.cancelEntrust(id, publisherId), false));
	}

	@Override
	public Response<String> delete(Long id) {
		futuresTradeEntrustService.delete(id);
		Response<String> res = new Response<String>();
		res.setMessage("响应成功");
		return res;
	}

	@Override
	public Response<PageInfo<FuturesTradeEntrustDto>> pages(@RequestBody FuturesTradeEntrustQuery query) {
		Page<FuturesTradeEntrust> page = futuresTradeEntrustService.pages(query);
		PageInfo<FuturesTradeEntrustDto> result = PageToPageInfo.pageToPageInfo(page, FuturesTradeEntrustDto.class);
		if (result != null && result.getContent() != null) {
			// step 1 : 获取汇率map
			Map<String, FuturesCurrencyRate> rateMap = rateService.getRateMap();
			// step 2 : 设置一些其他信息
			for (FuturesTradeEntrustDto dto : result.getContent()) {
				String commodityNo = dto.getCommodityNo();
				FuturesCommodity futuresCommodity = futuresCommodityService
						.retrieveByCommodityNo(commodityNo);
				BigDecimal openwindServiceFee = futuresCommodity.getOpenwindServiceFee() != null ?
						futuresCommodity.getOpenwindServiceFee() : new BigDecimal(0);
				BigDecimal unwindServiceFee = futuresCommodity.getUnwindServiceFee() != null ?
						futuresCommodity.getUnwindServiceFee() : new BigDecimal(0);
				dto.setServiceFee(openwindServiceFee.add(unwindServiceFee));
				BigDecimal perUnitReserveFund = futuresCommodity.getPerUnitReserveFund() != null ?
						futuresCommodity.getPerUnitReserveFund() : new BigDecimal(0);
				BigDecimal quantity = dto.getQuantity() != null ? dto.getQuantity() : new BigDecimal(0);
				dto.setReserveFund(perUnitReserveFund.multiply(quantity));
				String contractNo = dto.getContractNo();
				dto.setLastPrice(quoteContainer.getLastPrice(commodityNo, contractNo));
				FuturesCurrencyRate rate = rateMap.get(dto.getCurrency());
				if (rate != null) {
					dto.setCurrencySign(rate.getCurrencySign());
					dto.setRate(rate.getRate());
				}
			}
		}
		return new Response<>(result);
	}

	@Override
	public Response<PageInfo<FuturesTradeEntrustDto>> pagesAction(@RequestBody FuturesTradeEntrustQuery query) {
		Page<FuturesTradeEntrust> page = futuresTradeEntrustService.pagesAction(query);
		PageInfo<FuturesTradeEntrustDto> result = PageToPageInfo.pageToPageInfo(page, FuturesTradeEntrustDto.class);
		if (result != null && result.getContent() != null) {
			// step 1 : 获取汇率map
			Map<String, FuturesCurrencyRate> rateMap = rateService.getRateMap();
			// step 2 : 设置一些其他信息
			for (FuturesTradeEntrustDto dto : result.getContent()) {
				String commodityNo = dto.getCommodityNo();
				String contractNo = dto.getContractNo();
				dto.setLastPrice(quoteContainer.getLastPrice(commodityNo, contractNo));
				FuturesCurrencyRate rate = rateMap.get(dto.getCurrency());
				if (rate != null) {
					dto.setCurrencySign(rate.getCurrencySign());
					dto.setRate(rate.getRate());
				}
			}
		}
		return new Response<>(result);
	}

	public Response<PageInfo<FuturesTradeEntrustDto>> pagesPhoneEntrust(@RequestBody FuturesTradeEntrustQuery query) {
		Page<FuturesTradeEntrust> page = futuresTradeEntrustService.pagesPhoneEntrust(query);
		PageInfo<FuturesTradeEntrustDto> result = PageToPageInfo.pageToPageInfo(page,
				FuturesTradeEntrustDto.class);
		if (result != null && result.getContent() != null) {
			// step 1 : 获取汇率map
			Map<String, FuturesCurrencyRate> rateMap = rateService.getRateMap();
			// step 2 : 设置一些其他信息
			for (FuturesTradeEntrustDto dto : result.getContent()) {
				String commodityNo = dto.getCommodityNo();
				FuturesCommodity futuresCommodity = futuresCommodityService
						.retrieveByCommodityNo(commodityNo);
				BigDecimal openwindServiceFee = futuresCommodity.getOpenwindServiceFee() != null ?
						futuresCommodity.getOpenwindServiceFee() : new BigDecimal(0);
				BigDecimal unwindServiceFee = futuresCommodity.getUnwindServiceFee() != null ?
						futuresCommodity.getUnwindServiceFee() : new BigDecimal(0);
				dto.setServiceFee(openwindServiceFee.add(unwindServiceFee));
				BigDecimal perUnitReserveFund = futuresCommodity.getPerUnitReserveFund() != null ?
						futuresCommodity.getPerUnitReserveFund() : new BigDecimal(0);
				BigDecimal quantity = dto.getQuantity() != null ? dto.getQuantity() : new BigDecimal(0);
				dto.setReserveFund(perUnitReserveFund.multiply(quantity));
				String contractNo = dto.getContractNo();
				dto.setLastPrice(quoteContainer.getLastPrice(commodityNo, contractNo));
				FuturesCurrencyRate rate = rateMap.get(dto.getCurrency());
				if (rate != null) {
					dto.setCurrencySign(rate.getCurrencySign());
					dto.setRate(rate.getRate());
				}
			}
		}
		return new Response<>(result);
	}

    public Response<PageInfo<FuturesTradeEntrustDto>> pagesPhoneAction(@RequestBody FuturesTradeEntrustQuery query) {
        Page<FuturesTradeEntrust> page = futuresTradeEntrustService.pagesPhoneAction(query);
        PageInfo<FuturesTradeEntrustDto> result = PageToPageInfo.pageToPageInfo(page,
                FuturesTradeEntrustDto.class);
        if (result != null && result.getContent() != null) {
            // step 1 : 获取汇率map
            Map<String, FuturesCurrencyRate> rateMap = rateService.getRateMap();
            // step 2 : 设置一些其他信息
            for (FuturesTradeEntrustDto dto : result.getContent()) {
                String commodityNo = dto.getCommodityNo();
                FuturesCommodity futuresCommodity = futuresCommodityService
                        .retrieveByCommodityNo(commodityNo);
                BigDecimal openwindServiceFee = futuresCommodity.getOpenwindServiceFee() != null ?
                        futuresCommodity.getOpenwindServiceFee() : new BigDecimal(0);
                BigDecimal unwindServiceFee = futuresCommodity.getUnwindServiceFee() != null ?
                        futuresCommodity.getUnwindServiceFee() : new BigDecimal(0);
                dto.setServiceFee(openwindServiceFee.add(unwindServiceFee));
                BigDecimal perUnitReserveFund = futuresCommodity.getPerUnitReserveFund() != null ?
                        futuresCommodity.getPerUnitReserveFund() : new BigDecimal(0);
                BigDecimal filled = dto.getFilled() != null ? dto.getFilled() : new BigDecimal(0);
                dto.setReserveFund(perUnitReserveFund.multiply(filled));
                String contractNo = dto.getContractNo();
                dto.setLastPrice(quoteContainer.getLastPrice(commodityNo, contractNo));
                FuturesCurrencyRate rate = rateMap.get(dto.getCurrency());
                if (rate != null) {
                    dto.setCurrencySign(rate.getCurrencySign());
                    dto.setRate(rate.getRate());
                }
            }
        }
        return new Response<>(result);
    }

	public Response<FuturesTradeEntrustDto> detail(@PathVariable Long id) {
		FuturesTradeEntrustDto futuresTradeEntrustDto = CopyBeanUtils.copyBeanProperties(FuturesTradeEntrustDto.class,
				futuresTradeEntrustService.findById(id), false);
		// step 1 : 获取汇率map
		Map<String, FuturesCurrencyRate> rateMap = rateService.getRateMap();
		if (futuresTradeEntrustDto != null) {
			String commodityNo = futuresTradeEntrustDto.getCommodityNo();
			FuturesCommodity futuresCommodity = futuresCommodityService
					.retrieveByCommodityNo(commodityNo);
			BigDecimal openwindServiceFee = futuresCommodity.getOpenwindServiceFee() != null ?
					futuresCommodity.getOpenwindServiceFee() : new BigDecimal(0);
			BigDecimal unwindServiceFee = futuresCommodity.getUnwindServiceFee() != null ?
					futuresCommodity.getUnwindServiceFee() : new BigDecimal(0);
			futuresTradeEntrustDto.setServiceFee(openwindServiceFee.add(unwindServiceFee));
			BigDecimal perUnitReserveFund = futuresCommodity.getPerUnitReserveFund() != null ?
					futuresCommodity.getPerUnitReserveFund() : new BigDecimal(0);
			BigDecimal filled = futuresTradeEntrustDto.getFilled() != null ?
					futuresTradeEntrustDto.getFilled() : new BigDecimal(0);
			futuresTradeEntrustDto.setReserveFund(perUnitReserveFund.multiply(filled));
			String contractNo = futuresTradeEntrustDto.getContractNo();
			futuresTradeEntrustDto.setLastPrice(quoteContainer.getLastPrice(commodityNo, contractNo));
			FuturesCurrencyRate rate = rateMap.get(futuresTradeEntrustDto.getCurrency());
			if (rate != null) {
				futuresTradeEntrustDto.setCurrencySign(rate.getCurrencySign());
				futuresTradeEntrustDto.setRate(rate.getRate());
			}
		}
		return new Response<>(futuresTradeEntrustDto);
	}


	@Override
	public Response<PageInfo<FuturesTradeDto>> pagesEntrust(@RequestBody FuturesTradeAdminQuery query) {
		Page<FuturesTradeDto> page = futuresTradeEntrustService.pageTradeAdmin(query);
		PageInfo<FuturesTradeDto> result = PageToPageInfo.pageToPageInfo(page, FuturesTradeDto.class);
		return new Response<>(result);
	}

}
