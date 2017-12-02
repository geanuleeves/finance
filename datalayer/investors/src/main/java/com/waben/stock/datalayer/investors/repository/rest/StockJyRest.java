package com.waben.stock.datalayer.investors.repository.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.buyrecord.BuyRecordDto;
import com.waben.stock.interfaces.enums.EntrustType;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.stock.stockjy.SecuritiesStockEntrust;
import com.waben.stock.interfaces.pojo.stock.stockjy.StockResponse;
import com.waben.stock.interfaces.pojo.stock.stockjy.StockResult;
import com.waben.stock.interfaces.pojo.stock.stockjy.data.*;
import com.waben.stock.interfaces.util.JacksonUtil;
import com.waben.stock.interfaces.web.HttpRest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Created by yuyidi on 2017/11/30.
 * @desc
 */
@Component
public class StockJyRest implements SecuritiesInterface {

    Logger logger = LoggerFactory.getLogger(getClass());
    //券商资金账户登录
    private String loginUrl = "http://106.15.37.226:8445/stockjy/login";
    //券商资金账户股东账户查询
    private String holderUrl = "http://106.15.37.226:8445/stockjy/holder";
    //券商鼓动账户下单
    private String entrustUrl = "http://106.15.37.226:8445/stockjy/entrust";
    //券商委托单查询
    private String queryEntrusUrl = "http://106.15.37.226:8445/stockjy/qryentrust";
    //资金信息
    private String moneyUrl = "http://106.15.37.226:8445/stockjy/money";

    public StockLoginInfo login(String account, String password) {
        loginUrl += "?account_content={accountContent}&password={password}";
        Map<String, String> params = new HashMap<>();
        params.put("account_content", account);
        params.put("password", password);
        String result = HttpRest.get(moneyUrl, String.class, params);
        StockResponse<StockLoginInfo> stockResponse = JacksonUtil.decode(result, new
                TypeReference<StockResponse<StockLoginInfo>>() {
                });
        return handlerResult(stockResponse, ExceptionConstant.INVESTOR_SECURITIES_LOGIN_EXCEPTION).get(0);
    }

    public StockMoney money(String tradeSession) {
        moneyUrl = moneyUrl + "?trade_session={trade_session}";
        Map<String, String> params = new HashMap<>();
        params.put("trade_session", tradeSession);
        String result = HttpRest.get(moneyUrl, String.class, params);
        StockResponse<StockMoney> stockResponse = JacksonUtil.decode(result, new
                TypeReference<StockResponse<StockMoney>>() {
                });
        return handlerResult(stockResponse, ExceptionConstant.INVESTOR_STOCKACCOUNT_MONEY_NOT_ENOUGH).get(0);
    }

    /***
     * @author Administrator 2017-12-01 10:18:09
     * @method retrieveStockHolder
     * @param tradeSession 资金账户登录session
     * @return com.waben.stock.interfaces.pojo.stock.stockjy.data.StockHolder
     * @description 获取资金账户的股东账户列表
     */
    public List<StockHolder> retrieveStockHolder(String tradeSession) {
        holderUrl = holderUrl + "?trade_session={trade_session}";
        Map<String, String> params = new HashMap<>();
        params.put("trade_session", tradeSession);
        String result = HttpRest.get(holderUrl, String.class, params);
        StockResponse<StockHolder> stockResponse = JacksonUtil.decode(result, new
                TypeReference<StockResponse<StockHolder>>() {
                });
        logger.info("获取资金账户股东账户列表:{}", result);
        return handlerResult(stockResponse, ExceptionConstant.INVESTOR_STOCKACCOUNT_NOT_EXIST);
    }

    /***
     * @author yuyidi 2017-12-01 11:03:26
     * @method buyRecordEntrust
     * @param  securitiesStockEntrust 点买交易记录
     * @param tradeSession 资金账户登录session
     * @param entrustType 委托下单买入卖出类型
     * @param stockAccount 股东账户
     * @return void
     * @description 点买交易记录下单
     */
    public String buyRecordEntrust(SecuritiesStockEntrust securitiesStockEntrust, String tradeSession, String stockAccount, String type,
                                   EntrustType
                                           entrustType) {
        entrustUrl = entrustUrl + "?trade_session={trade_session}&" +
                "exchange_type={exchange_type}&" +
                "stock_account={stock_account}&" +
                "stock_code={stock_code}&" +
                "entrust_amount={entrust_amount}&" +
                "entrust_price={entrust_price}&" +
                "entrust_bs={entrust_bs}&" +
                "entrust_prop=0";
        Map<String, String> params = new HashMap<>();
        params.put("trade_session", tradeSession);
        params.put("exchange_type", type);
        params.put("stock_account", stockAccount);
        params.put("stock_code", securitiesStockEntrust.getStockCode());
        params.put("entrust_amount", String.valueOf(securitiesStockEntrust.getBuyingNumber()));
        params.put("entrust_price", String.valueOf(securitiesStockEntrust.getBuyingPrice()));
        params.put("entrust_bs", entrustType.getType());
        String result = HttpRest.get(entrustUrl, String.class, params);
        logger.info("委托交易结果:{}", result);
        StockResponse<StockEntrustResult> stockResponse = JacksonUtil.decode(result, new
                TypeReference<StockResponse<StockEntrustResult>>() {
                });
        StockEntrustResult stockEntrustResult = handlerResult(stockResponse, ExceptionConstant
                .INVESTOR_STOCKENTRUST_FETCH_ERROR).get(0);
        return stockEntrustResult.getEntrustNo();
    }

    /***
     * @author yuyidi 2017-12-01 14:20:16
     * @method queryStockByEntrust
     * @param tradeSession
     * @param entrust
     * @return java.util.List<com.waben.stock.interfaces.pojo.stock.stockjy.data.StockEntrustQueryResult>
     * @description 查询股票委托情况
     */
    public List<StockEntrustQueryResult> queryStockByEntrust(String tradeSession, String entrust) {
        queryEntrusUrl = queryEntrusUrl + "?trade_session={trade_session}&" +
                "entrust_no={entrust_no}&" +
                "request_num=50";
        Map<String, String> params = new HashMap<>();
        params.put("trade_session", tradeSession);
        params.put("entrust_no", entrust);
        String result = HttpRest.get(queryEntrusUrl, String.class, params);
        StockResponse<StockEntrustQueryResult> stockResponse = JacksonUtil.decode(result, new
                TypeReference<StockResponse<StockEntrustQueryResult>>() {
                });
        return handlerResult(stockResponse, ExceptionConstant.INVESTOR_STOCKENTRUST_FETCH_ERROR);
    }

    private <T> List<T> handlerResult(StockResponse<T> stockResponse, String code) {
        List<StockResult<T>> stockDataResults = stockResponse.getResult();
        if (stockDataResults != null) {
            if (stockDataResults.size() > 0) {
                StockResult stockMsgResult = stockDataResults.get(1);
                if (stockMsgResult == null) {
                    throw new ServiceException(code);
                }
                if ("OK".equals(stockMsgResult.getMsg().getErrorInfo())) {
                    StockResult<T> stockDataResult = stockDataResults.get(0);
                    return stockDataResult.getData();
                }
            }
        }
        throw new ServiceException(code);
    }
}
