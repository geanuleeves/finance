package com.waben.stock.datalayer.futures.service;

import com.waben.stock.datalayer.futures.entity.FuturesCommodity;
import com.waben.stock.datalayer.futures.entity.FuturesContract;
import com.waben.stock.datalayer.futures.entity.FuturesContractOrder;
import com.waben.stock.datalayer.futures.entity.FuturesCurrencyRate;
import com.waben.stock.datalayer.futures.quote.QuoteContainer;
import com.waben.stock.datalayer.futures.repository.FuturesContractDao;
import com.waben.stock.datalayer.futures.repository.FuturesContractOrderDao;
import com.waben.stock.interfaces.dto.futures.FuturesContractOrderViewDto;
import com.waben.stock.interfaces.enums.FuturesOrderType;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesTradeAdminQuery;
import com.waben.stock.interfaces.pojo.query.futures.FuturesContractOrderQuery;
import com.waben.stock.interfaces.util.PageToPageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 合约订单
 *
 * @author chenk 2018/7/26
 */
@Service
public class FuturesContractOrderService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FuturesContractOrderDao futuresContractOrderDao;
    
    @Autowired
    private FuturesContractDao contractDao;

    @Autowired
    private FuturesCommodityService futuresCommodityService;

    @Autowired
    private QuoteContainer quoteContainer;

    @Autowired
    private FuturesTradeActionService futuresTradeActionService;

    @Autowired
    private FuturesOrderService futuresOrderService;

    @Autowired
    private FuturesCurrencyRateService rateService;

    public FuturesContractOrder findById(Long id) {
        return futuresContractOrderDao.retrieve(id);
    }

    public FuturesContractOrder save(FuturesContractOrder futuresContractOrder) {
        return futuresContractOrderDao.create(futuresContractOrder);
    }

    public FuturesContractOrder modify(FuturesContractOrder futuresContractOrder) {
        return futuresContractOrderDao.update(futuresContractOrder);
    }

    public void delete(Long id) {
        futuresContractOrderDao.delete(id);
    }

	public FuturesContractOrder findByContractIdAndPublisherId(Long contractId, Long publisherId) {
		FuturesContract contract = contractDao.retrieve(contractId);
		if(contract != null) {
			return futuresContractOrderDao.retrieveByContractAndPublisherId(contract, publisherId);
		}
		return null;
	}

    public List<FuturesContractOrder> findByPublisherId(Long publisherId) {
        return futuresContractOrderDao.retrieveByPublisherId(publisherId);
    }

    public Page<FuturesContractOrder> pages(final FuturesContractOrderQuery query) {
        Pageable pageable = new PageRequest(query.getPage(), query.getSize());
        Page<FuturesContractOrder> page = futuresContractOrderDao.page(new Specification<FuturesContractOrder>() {
            @Override
            public Predicate toPredicate(Root<FuturesContractOrder> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicateList = new ArrayList<Predicate>();
                //用户ID
                if (query.getPublisherId() != null && query.getPublisherId() != 0) {
                    predicateList.add(criteriaBuilder.equal(root.get("publisherId").as(Long.class), query.getPublisherId()));
                }
                //品种编号
                if (!StringUtils.isEmpty(query.getCommodityNo())) {
                    predicateList.add(criteriaBuilder.equal(root.get("commodityNo").as(String.class), query.getCommodityNo()));
                }
                //合约编号
                if (!StringUtils.isEmpty(query.getContractNo())) {
                    predicateList.add(criteriaBuilder.equal(root.get("contractNo").as(String.class), query.getContractNo()));
                }
                if (predicateList.size() > 0) {
                    criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
                }
                //以更新时间排序
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("updateTime").as(Date.class)));
                return criteriaQuery.getRestriction();
            }
        }, pageable);
        return page;
    }
    
    public Page<FuturesContractOrder> pages(final FuturesTradeAdminQuery query) {
		Pageable pageable = new PageRequest(query.getPage(), query.getSize());
		Page<FuturesContractOrder> page = futuresContractOrderDao.page(new Specification<FuturesContractOrder>() {
			@Override
			public Predicate toPredicate(Root<FuturesContractOrder> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<Predicate>();
				
				// 以更新时间排序
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("updateTime").as(Date.class)));
				return criteriaQuery.getRestriction();
			}
		}, pageable);
		return page;
	}

    public PageInfo<FuturesContractOrderViewDto> handlePages(FuturesContractOrderQuery query) {
        Page<FuturesContractOrder> page = this.pages(query);
        PageInfo<FuturesContractOrderViewDto> result = PageToPageInfo.pageToPageInfo(page, FuturesContractOrderViewDto.class);
        List<FuturesContractOrderViewDto> futuresContractOrderViewDtos = new ArrayList<>();
        if (result != null && result.getContent() != null) {
            for (int i = 0; i < result.getContent().size(); i++) {
                FuturesContractOrderViewDto futuresContractOrderViewDto = result.getContent().get(i);
                FuturesContractOrder futuresContractOrder = page.getContent().get(i);
                //合约id
                futuresContractOrderViewDto.setContractId(futuresContractOrder.getContract().getId());
                //拷贝两份出来
                try {
                    FuturesCommodity futuresCommodity = futuresCommodityService.retrieveByCommodityNo(
                            futuresContractOrder.getCommodityNo());
                    //合约名称
                    futuresContractOrderViewDto.setCommodityName(futuresCommodity != null ? futuresCommodity.getName() : "");
                    futuresContractOrderViewDto.setUnwindPointType(futuresCommodity != null ?
                            futuresCommodity.getUnwindPointType() : 0);
                    futuresContractOrderViewDto.setPerUnitUnwindPoint(futuresCommodity != null ?
                            futuresCommodity.getPerUnitUnwindPoint() : new BigDecimal(0));
                    //已成交部分最新均价
                    BigDecimal lastPrice = quoteContainer.getLastPrice(futuresContractOrder.getCommodityNo(),
                            futuresContractOrder.getContractNo());
                    lastPrice = lastPrice == null ? new BigDecimal(0) : lastPrice;
                    //买涨
                    FuturesContractOrderViewDto buyDto = futuresContractOrderViewDto.deepClone();
                    //订单类型
                    buyDto.setOrderType(FuturesOrderType.BuyUp);
                    //买涨手数
                    BigDecimal buyUpQuantity = futuresContractOrder.getBuyUpQuantity();
                    buyDto.setBuyUpQuantity(buyUpQuantity);
                    //今持仓
                    Integer findUpFilledNow = futuresTradeActionService.findFilledNow(futuresContractOrder.getPublisherId(),
                            futuresContractOrder.getCommodityNo(), futuresContractOrder.getContractNo(),
                            FuturesOrderType.BuyUp.getIndex());
                    //浮动盈亏 (最新价格-成交价格)/波动*每笔波动价格*手数
                    if (futuresCommodity != null && buyUpQuantity != null && buyUpQuantity.compareTo(BigDecimal.ZERO) > 0) {
                        buyDto.setQuantityNow(new BigDecimal(findUpFilledNow != null ? findUpFilledNow : 0));
                        //成交价格
                        BigDecimal avgUpFillPrice = futuresOrderService.getOpenAvgFillPrice(futuresContractOrder.getPublisherId(),
                                futuresContractOrder.getContract().getId(), FuturesOrderType.BuyUp.getIndex());
                        avgUpFillPrice = avgUpFillPrice == null ? new BigDecimal(0) : avgUpFillPrice;
                        avgUpFillPrice = avgUpFillPrice.divideAndRemainder(futuresCommodity.getMinWave())[0].multiply(futuresCommodity.getMinWave());
                        buyDto.setAvgFillPrice(avgUpFillPrice);
                        //最新价
                        buyDto.setLastPrice(lastPrice);
                        //买方对手价
                        buyDto.setBidPrice(quoteContainer.getBidPrice(futuresContractOrder.getCommodityNo(),
                                futuresContractOrder.getContractNo()));
                        //最小波动
                        buyDto.setMinWave(futuresCommodity.getMinWave());
                        //最小波动价格
                        buyDto.setPerWaveMoney(futuresCommodity.getPerWaveMoney());
                        // 查询汇率
                        FuturesCurrencyRate rate = rateService.findByCurrency(futuresCommodity.getCurrency());
                        buyDto.setRate(rate.getRate());
                        buyDto.setCurrencySign(rate.getCurrencySign());
                        buyDto.setFloatingProfitAndLoss(lastPrice.subtract(avgUpFillPrice).divide(futuresCommodity.getMinWave())
                                .multiply(futuresCommodity.getPerWaveMoney()).multiply(futuresContractOrder.getBuyUpQuantity())
                                .multiply(rate.getRate()));
                        buyDto.setServiceFee(futuresCommodity.getOpenwindServiceFee().add(futuresCommodity.getUnwindServiceFee()));
                        //保证金
                        buyDto.setReserveFund(futuresCommodity.getPerUnitReserveFund().stripTrailingZeros());
                        buyDto.setLimitProfitType(futuresContractOrder.getBuyUpLimitProfitType());
                        buyDto.setPerUnitLimitProfitAmount(futuresContractOrder.getBuyUpPerUnitLimitProfitAmount());
                        buyDto.setLimitLossType(futuresContractOrder.getBuyUpLimitLossType());
                        buyDto.setPerUnitLimitLossAmount(futuresContractOrder.getBuyUpPerUnitLimitLossAmount());
                        futuresContractOrderViewDtos.add(buyDto);
                    }
                    //买跌
                    FuturesContractOrderViewDto sellDto = futuresContractOrderViewDto.deepClone();
                    sellDto.setOrderType(FuturesOrderType.BuyFall);
                    BigDecimal buyFallQuantity = futuresContractOrder.getBuyFallQuantity();
                    sellDto.setBuyFallQuantity(buyFallQuantity);
                    //今持仓
                    Integer findFallFilledNow = futuresTradeActionService.findFilledNow(futuresContractOrder.getPublisherId(),
                            futuresContractOrder.getCommodityNo(), futuresContractOrder.getContractNo(),
                            FuturesOrderType.BuyFall.getIndex());
                    //浮动盈亏 (最新价格-成交价格)/波动*每笔波动价格*手数
                    if (futuresCommodity != null && buyFallQuantity != null && buyFallQuantity.compareTo(new BigDecimal(0)) > 0) {
                        sellDto.setQuantityNow(new BigDecimal(findFallFilledNow == null ? 0 : findFallFilledNow));
                        //成交价格
                        BigDecimal avgFallFillPrice = futuresOrderService.getOpenAvgFillPrice(futuresContractOrder.getPublisherId(),
                                futuresContractOrder.getContract().getId(), FuturesOrderType.BuyFall.getIndex());
                        avgFallFillPrice = avgFallFillPrice == null ? new BigDecimal(0) : avgFallFillPrice;
                        avgFallFillPrice = avgFallFillPrice.divideAndRemainder(futuresCommodity.getMinWave())[0].multiply(futuresCommodity.getMinWave());
                        sellDto.setAvgFillPrice(avgFallFillPrice);
                        sellDto.setLastPrice(lastPrice);
                        //卖方对手价
                        sellDto.setAskPrice(quoteContainer.getAskPrice(futuresContractOrder.getCommodityNo(),
                                futuresContractOrder.getContractNo()));
                        //最小波动
                        sellDto.setMinWave(futuresCommodity.getMinWave());
                        //最小波动价格
                        sellDto.setPerWaveMoney(futuresCommodity.getPerWaveMoney());
                        // 查询汇率
                        FuturesCurrencyRate rate = rateService.findByCurrency(futuresCommodity.getCurrency());
                        sellDto.setRate(rate.getRate());
                        sellDto.setCurrencySign(rate.getCurrencySign());
                        sellDto.setFloatingProfitAndLoss(avgFallFillPrice.subtract(lastPrice).divide(futuresCommodity.getMinWave())
                                .multiply(futuresCommodity.getPerWaveMoney().multiply(futuresContractOrder.getBuyFallQuantity()))
                                .multiply(rate.getRate()));
                        sellDto.setServiceFee(futuresCommodity.getOpenwindServiceFee().add(futuresCommodity.getUnwindServiceFee()));
                        //保证金
                        sellDto.setReserveFund(futuresCommodity.getPerUnitReserveFund().stripTrailingZeros());
                        sellDto.setLimitProfitType(futuresContractOrder.getBuyFallLimitProfitType());
                        sellDto.setPerUnitLimitProfitAmount(futuresContractOrder.getBuyFallPerUnitLimitProfitAmount());
                        sellDto.setLimitLossType(futuresContractOrder.getBuyFallLimitLossType());
                        sellDto.setPerUnitLimitLossAmount(futuresContractOrder.getBuyFallPerUnitLimitLossAmount());
                        futuresContractOrderViewDtos.add(sellDto);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        result.setContent(futuresContractOrderViewDtos);
        return result;
    }

}
