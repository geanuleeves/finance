package com.waben.stock.datalayer.stockoption.service;

import com.waben.stock.datalayer.stockoption.entity.InquiryResult;
import com.waben.stock.datalayer.stockoption.entity.OfflineStockOptionTrade;
import com.waben.stock.datalayer.stockoption.entity.StockOptionTrade;
import com.waben.stock.datalayer.stockoption.repository.OfflineStockOptionTradeDao;
import com.waben.stock.datalayer.stockoption.repository.StockOptionTradeDao;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.enums.OfflineStockOptionTradeState;
import com.waben.stock.interfaces.enums.StockOptionTradeState;
import com.waben.stock.interfaces.exception.DataNotFoundException;
import com.waben.stock.interfaces.exception.ExceptionMap;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.util.JacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ConstantException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

@Service
public class OfflineStockOptionTradeService {
    @Autowired
    private OfflineStockOptionTradeDao offlineStockOptionTradeDao;
    @Autowired
    private StockOptionTradeDao stockOptionTradeDao;
    Logger logger = LoggerFactory.getLogger(getClass());

    @Transactional
    public OfflineStockOptionTrade save(OfflineStockOptionTrade offlineStockOptionTrade) {
        StockOptionTrade stockOptionTrade = stockOptionTradeDao.retrieve(offlineStockOptionTrade.getId());
        logger.info("用户申购信息：{}",JacksonUtil.encode(stockOptionTrade));
        //组装线下交易信息
        offlineStockOptionTrade.setId(null);
        offlineStockOptionTrade.setState(OfflineStockOptionTradeState.TURNOVER);
        offlineStockOptionTrade.setStockCode(stockOptionTrade.getStockCode());
        offlineStockOptionTrade.setStockName(stockOptionTrade.getStockName());
        offlineStockOptionTrade.setNominalAmount(stockOptionTrade.getNominalAmount());
        offlineStockOptionTrade.setRightMoney(stockOptionTrade.getNominalAmount().multiply(offlineStockOptionTrade
                .getRightMoneyRatio()));
        offlineStockOptionTrade.setCycle(stockOptionTrade.getCycle());
        offlineStockOptionTrade.setBuyingTime(new Date());
        //到期时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, stockOptionTrade.getCycle());
        offlineStockOptionTrade.setExpireTime(calendar.getTime());
        //将线下交易信息添加到数据库
        OfflineStockOptionTrade result = offlineStockOptionTradeDao.create(offlineStockOptionTrade);
        logger.info("添加线下交易信息结果：{}", JacksonUtil.encode(result));
        //修改申购交易信息
        stockOptionTrade.setOfflineTrade(result);
        stockOptionTradeDao.update(stockOptionTrade);
        return result;
    }

    @Transactional
    public OfflineStockOptionTrade settlement(Long id, BigDecimal sellingPrice) {
        //机构结算
        StockOptionTrade stockOptionTrade = stockOptionTradeDao.retrieve(id);
        OfflineStockOptionTrade offlineStockOptionTrade = stockOptionTrade.getOfflineTrade();
        offlineStockOptionTrade.setSellingPrice(sellingPrice);
        offlineStockOptionTrade.setSellingTime(new Date());
        offlineStockOptionTrade.setState(OfflineStockOptionTradeState.SETTLEMENTED);
        offlineStockOptionTrade.setProfit(stockOptionTrade.getRightMoney().subtract(offlineStockOptionTrade.getRightMoney()));
        return offlineStockOptionTradeDao.update(offlineStockOptionTrade);
    }


    public OfflineStockOptionTrade exercise(Long id) {
        OfflineStockOptionTrade offlineStockOptionTrade = offlineStockOptionTradeDao.retrieve(id);
        offlineStockOptionTrade.setState(OfflineStockOptionTradeState.APPLYRIGHT);
        offlineStockOptionTrade.setRightTime(new Date());
        OfflineStockOptionTrade result = offlineStockOptionTradeDao.update(offlineStockOptionTrade);
        return result;
    }

    public OfflineStockOptionTrade findById(Long id) {
        OfflineStockOptionTrade result = offlineStockOptionTradeDao.retrieve(id);
        if (result == null) {
            throw new DataNotFoundException("机构期权信息未找到");
        }
        return result;
    }
}
