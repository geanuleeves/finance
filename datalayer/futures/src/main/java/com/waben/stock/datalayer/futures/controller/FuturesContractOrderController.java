package com.waben.stock.datalayer.futures.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.datalayer.futures.entity.FuturesCommodity;
import com.waben.stock.datalayer.futures.entity.FuturesContractOrder;
import com.waben.stock.datalayer.futures.entity.FuturesCurrencyRate;
import com.waben.stock.datalayer.futures.quote.QuoteContainer;
import com.waben.stock.datalayer.futures.service.FuturesCommodityService;
import com.waben.stock.datalayer.futures.service.FuturesContractOrderService;
import com.waben.stock.datalayer.futures.service.FuturesCurrencyRateService;
import com.waben.stock.datalayer.futures.service.FuturesOrderService;
import com.waben.stock.datalayer.futures.service.FuturesTradeActionService;
import com.waben.stock.interfaces.dto.admin.futures.FuturesHoldPositionAgentDto;
import com.waben.stock.interfaces.dto.futures.FuturesContractOrderDto;
import com.waben.stock.interfaces.dto.futures.FuturesContractOrderViewDto;
import com.waben.stock.interfaces.dto.publisher.PublisherDto;
import com.waben.stock.interfaces.dto.publisher.RealNameDto;
import com.waben.stock.interfaces.enums.FuturesOrderType;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesTradeAdminQuery;
import com.waben.stock.interfaces.pojo.query.futures.FuturesContractOrderQuery;
import com.waben.stock.interfaces.service.futures.FuturesContractOrderInterface;
import com.waben.stock.interfaces.service.futures.FuturesCurrencyRateInterface;
import com.waben.stock.interfaces.service.organization.OrganizationInterface;
import com.waben.stock.interfaces.service.organization.OrganizationPublisherInterface;
import com.waben.stock.interfaces.service.publisher.PublisherInterface;
import com.waben.stock.interfaces.service.publisher.RealNameInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import com.waben.stock.interfaces.util.PageToPageInfo;

import io.swagger.annotations.Api;

/**
 * 合约订单
 *
 * @author chenk 2018/7/26
 */
@RestController
@RequestMapping("/contract_order")
@Api(description = "合约订单接口列表")
public class FuturesContractOrderController implements FuturesContractOrderInterface {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FuturesContractOrderService futuresContractOrderService;

	@Autowired
	private FuturesTradeActionService futuresTradeActionService;

	@Autowired
	private FuturesOrderService futuresOrderService;

	@Autowired
	private QuoteContainer quoteContainer;

	@Autowired
	private FuturesCommodityService futuresCommodityService;

	@Autowired
	@Qualifier("realNameInterface")
	private RealNameInterface realnameInterface;

	@Autowired
	@Qualifier("publisherInterface")
	private PublisherInterface publisherInterface;

	@Autowired
	private FuturesCurrencyRateService rateService;

	@Autowired
	@Qualifier("organizationPublisherInterface")
	private OrganizationPublisherInterface organizationPublisherInterface;

	@Autowired
	@Qualifier("organizationInterface")
	private OrganizationInterface organizationInterface;

	@Autowired
	@Qualifier("futuresCurrencyRateInterface")
	private FuturesCurrencyRateInterface futuresCurrencyRateInterface;

	@Override
	public Response<FuturesContractOrderDto> fetchById(@PathVariable Long id) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(FuturesContractOrderDto.class,
				futuresContractOrderService.findById(id), false));
	}

	@Override
	public Response<FuturesContractOrderDto> fetchByContractIdAndPublisherId(@PathVariable Long contractId,
			@PathVariable Long publisherId) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(FuturesContractOrderDto.class,
				futuresContractOrderService.findByContractIdAndPublisherId(contractId, publisherId), false));
	}

	@Override
	public Response<List<FuturesContractOrderDto>> fetchByPublisherId(Long publisherId) {
		List<FuturesContractOrderDto> futuresContractOrderDtos = new ArrayList<>();
		List<FuturesContractOrder> futuresContractOrders = futuresContractOrderService.findByPublisherId(publisherId);
		for (FuturesContractOrder futuresContractOrder : futuresContractOrders) {
			FuturesContractOrderDto futuresContractOrderDto	= CopyBeanUtils.copyBeanProperties(FuturesContractOrderDto.class,
					futuresContractOrder, false);
			futuresContractOrderDtos.add(futuresContractOrderDto);
		}
		return new Response<>(futuresContractOrderDtos);
	}

	@Override
	public Response<FuturesContractOrderDto> save(@RequestBody FuturesContractOrderDto dto) {
		FuturesContractOrder futuresContractOrder = CopyBeanUtils.copyBeanProperties(FuturesContractOrder.class, dto,
				false);
		FuturesContractOrder result = futuresContractOrderService.save(futuresContractOrder);
		FuturesContractOrderDto response = CopyBeanUtils.copyBeanProperties(result, new FuturesContractOrderDto(),
				false);
		return new Response<>(response);
	}

	@Override
	public Response<FuturesContractOrderDto> modify(@RequestBody FuturesContractOrderDto dto) {
		FuturesContractOrder futuresContractOrder = CopyBeanUtils.copyBeanProperties(FuturesContractOrder.class, dto,
				false);
		FuturesContractOrder result = futuresContractOrderService.modify(futuresContractOrder);
		FuturesContractOrderDto resultDto = CopyBeanUtils.copyBeanProperties(result, new FuturesContractOrderDto(),
				false);
		return new Response<>(resultDto);
	}

	@Override
	public Response<String> delete(Long id) {
		futuresContractOrderService.delete(id);
		Response<String> res = new Response<String>();
		res.setMessage("响应成功");
		return res;
	}

    @Override
    public Response<PageInfo<FuturesContractOrderViewDto>> pages(@RequestBody FuturesContractOrderQuery query) {
        return new Response<>(futuresContractOrderService.handlePages(query));
    }

	@Override
	public Response<PageInfo<FuturesHoldPositionAgentDto>> pagesAgentAdmin(@RequestBody FuturesTradeAdminQuery query) {
		Page<FuturesContractOrder> page = futuresOrderService.pages(query);
		PageInfo<FuturesHoldPositionAgentDto> result = PageToPageInfo.pageToPageInfo(page,
				FuturesHoldPositionAgentDto.class);
		List<FuturesHoldPositionAgentDto> futuresContractOrderViewDtos = new ArrayList<>();
		if (result != null && result.getContent() != null) {
			for (int i = 0; i < result.getContent().size(); i++) {
				FuturesHoldPositionAgentDto futuresContractOrderViewDto = result.getContent().get(i);
				FuturesContractOrder futuresContractOrder = page.getContent().get(i);
				// 合约名称
				// futuresContractOrderViewDto.setContractName(futuresContractOrder.getContract().getContractName());
				// 拷贝两份出来
				try {
					FuturesCommodity futuresCommodity = futuresCommodityService
							.retrieveByCommodityNo(futuresContractOrder.getCommodityNo());
					RealNameDto realName = realnameInterface
							.fetchByResourceId(futuresContractOrderViewDto.getPublisherId()).getResult();
					PublisherDto publisher = publisherInterface.fetchById(futuresContractOrderViewDto.getPublisherId())
							.getResult();
					// 已成交部分最新均价
					BigDecimal lastPrice = quoteContainer.getLastPrice(futuresContractOrder.getCommodityNo(),
							futuresContractOrder.getContractNo());
					// 买涨
					FuturesHoldPositionAgentDto buyDto = futuresContractOrderViewDto.deepClone();
					if (realName != null) {
						buyDto.setPublisherName(realName.getName());
					}
					if (publisher != null) {
						buyDto.setPublisherPhone(publisher.getPhone());
					}
					buyDto.setCommodityName(futuresCommodity.getName());
					buyDto.setCommodityCurrency(futuresCommodity.getCurrency());
					buyDto.setCommoditySymbol(futuresCommodity.getSymbol());
					buyDto.setContractId(futuresContractOrder.getContract().getId());

					buyDto.setOrderType(FuturesOrderType.getByIndex("1"));
					// 已持仓
					buyDto.setBuyUpQuantity(futuresContractOrder.getBuyUpQuantity());
					// 今持仓
					Integer findUpFilledNow = futuresTradeActionService.findFilledNow(
							futuresContractOrder.getPublisherId(), futuresContractOrder.getCommodityNo(),
							futuresContractOrder.getContractNo(), FuturesOrderType.BuyUp.getIndex());
					if (findUpFilledNow != null) {
						buyDto.setQuantityNow(new BigDecimal(findUpFilledNow == null ? 0 : findUpFilledNow));
					}
					// 成交价格
					BigDecimal avgUpFillPrice = futuresOrderService.getOpenAvgFillPrice(
							futuresContractOrder.getPublisherId(), futuresContractOrder.getContract().getId(),
							FuturesOrderType.BuyUp.getIndex());
					buyDto.setAvgFillPrice(avgUpFillPrice);
					buyDto.setLastPrice(lastPrice);
					// 浮动盈亏 (最新价格-成交价格)/波动*每笔波动价格
					if (futuresCommodity != null) {
						buyDto.setFloatingProfitAndLoss(lastPrice.subtract(avgUpFillPrice)
								.divide(futuresCommodity.getMinWave()).multiply(futuresCommodity.getPerWaveMoney())
								.multiply(futuresContractOrder.getBuyUpQuantity()));
						buyDto.setServiceFee(
								futuresCommodity.getOpenwindServiceFee().add(futuresCommodity.getUnwindServiceFee()));
						if (futuresContractOrder.getBuyUpQuantity()
								.compareTo(futuresContractOrder.getBuyFallQuantity()) > 0) {
							buyDto.setReserveFund(futuresCommodity.getPerUnitReserveFund()
									.multiply(futuresContractOrder.getBuyUpQuantity()));
						}
					}

					// 买跌
					FuturesHoldPositionAgentDto sellDto = futuresContractOrderViewDto.deepClone();
					if (realName != null) {
						sellDto.setPublisherName(realName.getName());
					}
					if (publisher != null) {
						sellDto.setPublisherPhone(publisher.getPhone());
					}
					sellDto.setCommodityName(futuresCommodity.getName());
					sellDto.setCommodityCurrency(futuresCommodity.getCurrency());
					sellDto.setCommoditySymbol(futuresCommodity.getSymbol());
					sellDto.setContractId(futuresContractOrder.getContract().getId());

					sellDto.setOrderType(FuturesOrderType.getByIndex("2"));
					sellDto.setBuyFallQuantity(futuresContractOrder.getBuyFallQuantity());
					// 今持仓
					Integer findFallFilledNow = futuresTradeActionService.findFilledNow(
							futuresContractOrder.getPublisherId(), futuresContractOrder.getCommodityNo(),
							futuresContractOrder.getContractNo(), FuturesOrderType.BuyFall.getIndex());
					if (findFallFilledNow != null) {
						sellDto.setQuantityNow(new BigDecimal(findFallFilledNow));
					}
					// 成交价格
					BigDecimal avgFallFillPrice = futuresOrderService.getOpenAvgFillPrice(
							futuresContractOrder.getPublisherId(), futuresContractOrder.getContract().getId(),
							FuturesOrderType.BuyFall.getIndex());
					sellDto.setAvgFillPrice(avgFallFillPrice);
					sellDto.setLastPrice(lastPrice);
					if (avgFallFillPrice == null) {
						avgFallFillPrice = new BigDecimal(0);
					}
					// 浮动盈亏 (最新价格-成交价格)/波动*每笔波动价格
					if (futuresCommodity != null) {
						sellDto.setFloatingProfitAndLoss(lastPrice.subtract(avgFallFillPrice)
								.divide(futuresCommodity.getMinWave()).multiply(futuresCommodity.getPerWaveMoney())
								.multiply(futuresContractOrder.getBuyFallQuantity()));
						sellDto.setServiceFee(
								futuresCommodity.getOpenwindServiceFee().add(futuresCommodity.getUnwindServiceFee()));
						if (futuresContractOrder.getBuyFallQuantity()
								.compareTo(futuresContractOrder.getBuyUpQuantity()) > 0) {
							sellDto.setReserveFund(futuresCommodity.getPerUnitReserveFund()
									.multiply(futuresContractOrder.getBuyFallQuantity()));
						}
					}
					futuresContractOrderViewDtos.add(buyDto);
					futuresContractOrderViewDtos.add(sellDto);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		result.setContent(futuresContractOrderViewDtos);
		return new Response<>(result);
	}
}
