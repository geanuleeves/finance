package com.waben.stock.datalayer.futures.controller;

import com.waben.stock.datalayer.futures.entity.FuturesCurrencyRate;
import com.waben.stock.datalayer.futures.entity.FuturesTradeAction;
import com.waben.stock.datalayer.futures.quote.QuoteContainer;
import com.waben.stock.datalayer.futures.service.FuturesCurrencyRateService;
import com.waben.stock.datalayer.futures.service.FuturesTradeActionService;
import com.waben.stock.interfaces.dto.futures.FuturesTradeActionDto;
import com.waben.stock.interfaces.dto.futures.FuturesTradeActionViewDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesTradeAdminQuery;
import com.waben.stock.interfaces.pojo.query.futures.FuturesTradeActionQuery;
import com.waben.stock.interfaces.service.futures.FuturesTradeActionInterface;
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

import java.util.Map;

/**
 * 订单交易开平仓记录
 *
 * @author chenk 2018/7/26
 */
@RestController
@RequestMapping("/trade_action")
@Api(description = "订单交易开平仓记录接口列表")
public class FuturesTradeActionController implements FuturesTradeActionInterface {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FuturesTradeActionService futuresTradeActionService;

	@Autowired
	private FuturesCurrencyRateService rateService;

	@Autowired
	private QuoteContainer quoteContainer;

	@Override
	public Response<FuturesTradeActionDto> fetchById(@PathVariable Long id) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(FuturesTradeActionDto.class,
				futuresTradeActionService.findById(id), false));
	}

	@Override
	public Response<FuturesTradeActionDto> save(@RequestBody FuturesTradeActionDto dto) {
		FuturesTradeAction futuresTradeAction = CopyBeanUtils.copyBeanProperties(FuturesTradeAction.class, dto, false);
		FuturesTradeAction result = futuresTradeActionService.save(futuresTradeAction);
		FuturesTradeActionDto response = CopyBeanUtils.copyBeanProperties(result, new FuturesTradeActionDto(), false);
		return new Response<>(response);
	}

	@Override
	public Response<FuturesTradeActionDto> modify(FuturesTradeActionDto dto) {
		FuturesTradeAction futuresTradeAction = CopyBeanUtils.copyBeanProperties(FuturesTradeAction.class, dto, false);
		FuturesTradeAction result = futuresTradeActionService.modify(futuresTradeAction);
		FuturesTradeActionDto resultDto = CopyBeanUtils.copyBeanProperties(result, new FuturesTradeActionDto(), false);
		return new Response<>(resultDto);
	}

	@Override
	public Response<String> delete(Long id) {
		futuresTradeActionService.delete(id);
		Response<String> res = new Response<String>();
		res.setMessage("success");
		return res;
	}

	@Override
	public Response<PageInfo<FuturesTradeActionViewDto>> pages(@RequestBody FuturesTradeActionQuery query) {
		Page<FuturesTradeAction> page = futuresTradeActionService.pages(query);
		PageInfo<FuturesTradeActionViewDto> result = PageToPageInfo.pageToPageInfo(page,
				FuturesTradeActionViewDto.class);
		if (result != null && result.getContent() != null) {
			// step 1 : 获取汇率map
			Map<String, FuturesCurrencyRate> rateMap = rateService.getRateMap();
			// step 2 : 设置一些其他信息
			for (FuturesTradeActionViewDto dto : result.getContent()) {
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

	public Response<PageInfo<FuturesTradeActionViewDto>> pagesPhone(@RequestBody FuturesTradeActionQuery query) {
		Page<FuturesTradeAction> page = futuresTradeActionService.pagesPhone(query);
		PageInfo<FuturesTradeActionViewDto> result = PageToPageInfo.pageToPageInfo(page,
				FuturesTradeActionViewDto.class);
		if (result != null && result.getContent() != null) {
			// step 1 : 获取汇率map
			Map<String, FuturesCurrencyRate> rateMap = rateService.getRateMap();
			// step 2 : 设置一些其他信息
			for (FuturesTradeActionViewDto dto : result.getContent()) {
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



	@Override
	public Response<PageInfo<FuturesTradeActionViewDto>> pagesTradeAdmin(@RequestBody FuturesTradeAdminQuery query) {
		Page<FuturesTradeAction> page = futuresTradeActionService.pagesAdmin(query);
		PageInfo<FuturesTradeActionViewDto> result = PageToPageInfo.pageToPageInfo(page,
				FuturesTradeActionViewDto.class);
		if (result != null && result.getContent() != null) {
			for (int i = 0; i < result.getContent().size(); i++) {
				FuturesTradeAction futuresTradeAction = page.getContent().get(i);
				// 合约名称
				// result.getContent().get(i)
				// .setContractName(futuresTradeAction.getOrder().getContract().getContractName());
				// 订单类型
				result.getContent().get(i).setOrderType(futuresTradeAction.getOrder().getOrderType());
				// 成交手数
				result.getContent().get(i).setFilled(futuresTradeAction.getFilled());
				// 成交价格
				result.getContent().get(i).setAvgFillPrice(futuresTradeAction.getAvgFillPrice());
				// 成交盈亏（交易所货币）
				result.getContent().get(i).setCurrencyProfitOrLoss(futuresTradeAction.getCurrencyProfitOrLoss());
				// 成交盈亏（人民币）
				result.getContent().get(i).setPublisherProfitOrLoss(futuresTradeAction.getPublisherProfitOrLoss());
				// 成交编号
				// result.getContent().get(i).setOrderNo(futuresTradeAction.getOrder().getTradeNo());
				// 成交时间
				result.getContent().get(i).setTradeTime(futuresTradeAction.getTradeTime());
				// 订单类型
				result.getContent().get(i)
						.setFuturesTradePriceType(futuresTradeAction.getTradeEntrust().getPriceType());
				// 备注
				// result.getContent().get(i).setRemark(futuresTradeAction.getTradeEntrust().getWindControlType());
			}
		}
		return new Response<>(result);
	}

}
