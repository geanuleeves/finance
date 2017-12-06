package com.waben.stock.applayer.tactics.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.waben.stock.applayer.tactics.dto.buyrecord.BuyRecordWithMarketDto;
import com.waben.stock.applayer.tactics.dto.buyrecord.TradeDynamicDto;
import com.waben.stock.applayer.tactics.retrivestock.RetriveStockOverHttp;
import com.waben.stock.applayer.tactics.retrivestock.bean.StockMarket;
import com.waben.stock.applayer.tactics.security.SecurityUtil;
import com.waben.stock.applayer.tactics.service.BuyRecordService;
import com.waben.stock.applayer.tactics.service.CapitalAccountService;
import com.waben.stock.applayer.tactics.service.PublisherService;
import com.waben.stock.applayer.tactics.service.SettlementService;
import com.waben.stock.applayer.tactics.service.StockService;
import com.waben.stock.interfaces.dto.buyrecord.BuyRecordDto;
import com.waben.stock.interfaces.dto.buyrecord.SettlementDto;
import com.waben.stock.interfaces.dto.publisher.CapitalAccountDto;
import com.waben.stock.interfaces.dto.publisher.PublisherDto;
import com.waben.stock.interfaces.dto.stockcontent.StockDto;
import com.waben.stock.interfaces.enums.BuyRecordState;
import com.waben.stock.interfaces.enums.WindControlType;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.BuyRecordQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.SettlementQuery;
import com.waben.stock.interfaces.util.CopyBeanUtils;

@Service
public class BuyRecordBusiness {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private BuyRecordService buyRecordService;

	@Autowired
	private SettlementService settlementService;

	@Autowired
	private CapitalAccountService accountService;

	@Autowired
	private PublisherService publisherService;

	@Autowired
	private StockService stockService;

	public BuyRecordDto findById(Long id) {
		Response<BuyRecordDto> response = buyRecordService.fetchBuyRecord(id);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public BuyRecordDto buy(BuyRecordDto buyRecordDto) {
		Response<BuyRecordDto> response = buyRecordService.addBuyRecord(buyRecordDto);
		if ("200".equals(response.getCode())) {
			// 扣去金额、冻结保证金，增加流水记录
			Response<CapitalAccountDto> accountOperationResp = accountService.serviceFeeAndReserveFund(
					SecurityUtil.getUserId(), response.getResult().getId(), response.getResult().getSerialCode(),
					response.getResult().getServiceFee(), response.getResult().getReserveFund());
			if (!"200".equals(accountOperationResp.getCode())) {
				// 扣款失败，删除点买记录，TODO 删除失败，再处理
				buyRecordService.dropBuyRecord(response.getResult().getId());
				throw new ServiceException(accountOperationResp.getCode());
			}
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public PageInfo<BuyRecordDto> pages(BuyRecordQuery buyRecordQuery) {
		Response<PageInfo<BuyRecordDto>> response = buyRecordService.pagesByQuery(buyRecordQuery);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public PageInfo<BuyRecordWithMarketDto> pagesSettlement(SettlementQuery query) {
		Response<PageInfo<SettlementDto>> response = settlementService.pagesByQuery(query);
		if ("200".equals(response.getCode())) {
			List<BuyRecordWithMarketDto> content = new ArrayList<>();
			List<SettlementDto> settlementContent = response.getResult().getContent();
			if (settlementContent != null && settlementContent.size() > 0) {
				for (SettlementDto settlement : settlementContent) {
					BuyRecordWithMarketDto buyRecord = wrapMarketInfo(settlement.getBuyRecord());
					buyRecord.setProfitOrLoss(settlement.getProfitOrLoss());
					buyRecord.setPublisherProfitOrLoss(settlement.getPublisherProfitOrLoss());
					content.add(buyRecord);
				}
			}
			return new PageInfo<>(content, response.getResult().getTotalPages(), response.getResult().getLast(),
					response.getResult().getTotalElements(), response.getResult().getSize(),
					response.getResult().getNumber(), response.getResult().getFrist());
		}
		throw new ServiceException(response.getCode());
	}

	public BuyRecordWithMarketDto wrapMarketInfo(BuyRecordDto buyRecord) {
		List<BuyRecordDto> list = new ArrayList<>();
		list.add(buyRecord);
		List<BuyRecordWithMarketDto> marketList = wrapMarketInfo(list);
		return marketList.get(0);
	}

	public List<BuyRecordWithMarketDto> wrapMarketInfo(List<BuyRecordDto> list) {
		List<BuyRecordWithMarketDto> result = CopyBeanUtils.copyListBeanPropertiesToList(list,
				BuyRecordWithMarketDto.class);
		List<String> codes = new ArrayList<>();
		for (BuyRecordWithMarketDto record : result) {
			codes.add(record.getStockCode());
		}
		if (codes.size() > 0) {
			List<StockMarket> stockMarketList = RetriveStockOverHttp.listStockMarket(restTemplate, codes);
			if (stockMarketList != null && stockMarketList.size() > 0) {
				for (int i = 0; i < stockMarketList.size(); i++) {
					StockMarket market = stockMarketList.get(i);
					BuyRecordWithMarketDto record = result.get(i);
					record.setStockName(market.getName());
					record.setLastPrice(market.getLastPrice());
					record.setUpDropPrice(market.getUpDropPrice());
					record.setUpDropSpeed(market.getUpDropSpeed());
				}
			}
		}
		return result;
	}

	public BuyRecordDto sellLock(Long lockUserId, Long id) {
		Response<BuyRecordDto> response = buyRecordService.sellLock(lockUserId, id,
				WindControlType.PUBLISHERAPPLY.getIndex());
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public BuyRecordDto sellOut(Long investorId, Long id, BigDecimal sellingPrice) {
		Response<BuyRecordDto> response = buyRecordService.sellOut(investorId, id, sellingPrice);
		if ("200".equals(response.getCode())) {
			SettlementQuery query = new SettlementQuery(0, 1);
			query.setBuyRecordId(id);
			Response<PageInfo<SettlementDto>> settlement = settlementService.pagesByQuery(query);
			accountService.returnReserveFund(response.getResult().getPublisherId(), response.getResult().getId(),
					response.getResult().getSerialCode(),
					settlement.getResult().getContent().get(0).getPublisherProfitOrLoss());
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public PageInfo<TradeDynamicDto> tradeDynamic(int page, int size) {
		SettlementQuery sQuery = new SettlementQuery(page, size / 2);
		sQuery.setOnlyProfit(true);
		Response<PageInfo<SettlementDto>> sResponse = settlementService.pagesByQuery(sQuery);
		if ("200".equals(sResponse.getCode())) {
			BuyRecordQuery bQuery = new BuyRecordQuery(page, size - sResponse.getResult().getContent().size(), null,
					new BuyRecordState[] { BuyRecordState.POSTED, BuyRecordState.BUYLOCK, BuyRecordState.HOLDPOSITION,
							BuyRecordState.SELLLOCK });
			PageInfo<BuyRecordDto> pageInfo = pages(bQuery);

			int total = sResponse.getResult().getContent().size() + pageInfo.getContent().size();
			int sSize = sResponse.getResult().getContent().size();
			int bSize = pageInfo.getContent().size();
			int i = sSize;
			int j = bSize;

			List<TradeDynamicDto> content = new ArrayList<>();
			for (int n = 0; n < total; n++) {
				TradeDynamicDto inner = new TradeDynamicDto();
				if (n % 2 == 0 && i > 0) {
					SettlementDto settlement = sResponse.getResult().getContent().get(sSize - i);
					inner.setTradeType(2);
					inner.setPublisherId(settlement.getBuyRecord().getPublisherId());
					inner.setStockCode(settlement.getBuyRecord().getStockCode());
					inner.setProfit(settlement.getPublisherProfitOrLoss());
					i--;
				} else {
					BuyRecordDto buyRecord = pageInfo.getContent().get(bSize - j);
					inner.setTradeType(1);
					inner.setPublisherId(buyRecord.getPublisherId());
					inner.setStockCode(buyRecord.getStockCode());
					j--;
				}
				// TODO 获取Phone 和 StockName，下面的实现效率太低，要优化，使用sql查询？
				Response<PublisherDto> pResponse = publisherService.fetchById(inner.getPublisherId());
				if ("200".equals(pResponse.getCode())) {
					if (pResponse.getResult() != null) {
						inner.setPhone(pResponse.getResult().getPhone());
					}
				} else {
					throw new ServiceException(pResponse.getCode());
				}
				Response<StockDto> stockResponse = stockService.fetchWithExponentByCode(inner.getStockCode());
				if ("200".equals(stockResponse.getCode())) {
					if (stockResponse.getResult() != null) {
						inner.setStockName(stockResponse.getResult().getName());
					}
				} else {
					throw new ServiceException(stockResponse.getCode());
				}
				content.add(inner);
			}
			return new PageInfo<TradeDynamicDto>(content, 0, false, 0L, size, page, false);
		}
		throw new ServiceException(sResponse.getCode());
	}

}
