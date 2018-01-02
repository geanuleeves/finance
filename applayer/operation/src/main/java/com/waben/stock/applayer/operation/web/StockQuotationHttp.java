package com.waben.stock.applayer.operation.web;

import com.waben.stock.applayer.operation.dto.websocket.StockRequestMessage;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.stock.quotation.Resonse;
import com.waben.stock.interfaces.pojo.stock.quotation.StockMarket;
import com.waben.stock.interfaces.util.JacksonUtil;
import com.waben.stock.interfaces.web.HttpRest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Created by yuyidi on 2017/12/22.
 * @desc
 */
@Component
public class StockQuotationHttp {

    Logger logger = LoggerFactory.getLogger(getClass());

    private String context = "http://lemi.esongbai.com/stk/stk";

    public List<StockMarket> fetQuotationByCode(List<StockRequestMessage> requestMessages) {
        StringBuilder codes = new StringBuilder();
        for (StockRequestMessage stockRequestMessage : requestMessages) {
            codes.append(stockRequestMessage.getCode()).append(",");
        }
        String params = codes.toString().substring(0, codes.toString().length() - 1);
        String url = context + "/list.do?codes=" + params;
        String resonse = HttpRest.get(url, String.class);
        Resonse<StockMarket> result = JacksonUtil.decode(resonse, Resonse.class, StockMarket.class);
        List<StockMarket> list = null;
        if ("200".equals(result.getCode())) {
            list = result.getData();
        }
//        try {
//            JsonNode dataNode = JacksonUtil.objectMapper.readValue(resonse, JsonNode.class).get("data");
//            JavaType javaType = JacksonUtil.objectMapper.getTypeFactory().constructParametricType(ArrayList.class,
//                    StockMarket.class);
//            list = JacksonUtil.objectMapper.readValue(dataNode.toString(), javaType);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            logger.error("获取股票行情信息异常:{}",e);
//            throw new ServiceException(ExceptionConstant.UNKNOW_EXCEPTION);
//        }
        if (list != null) {
            return list;
        }
        logger.error("获取股票行情信息异常:{}", result.getMsg());
        throw new ServiceException(ExceptionConstant.UNKNOW_EXCEPTION);
    }
}
