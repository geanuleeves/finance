package com.waben.stock.risk.schedule.job;

import com.waben.stock.interfaces.enums.EntrustState;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.stock.SecuritiesStockEntrust;
import com.waben.stock.interfaces.pojo.stock.stockjy.data.StockEntrustQueryResult;
import com.waben.stock.risk.container.StockApplyEntrustBuyInContainer;
import com.waben.stock.risk.warpper.ApplicationContextBeanFactory;
import com.waben.stock.risk.warpper.messagequeue.rabbitmq.EntrustProducer;
import com.waben.stock.risk.web.SecuritiesEntrustHttp;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author Created by yuyidi on 2017/12/15.
 * @desc
 */
//@Component
public class StockApplyEntrustBuyInJob implements InterruptableJob {

    Logger logger = LoggerFactory.getLogger(getClass());

    private StockApplyEntrustBuyInContainer securitiesStockEntrustContainer = ApplicationContextBeanFactory.getBean
            (StockApplyEntrustBuyInContainer.class);
    private SecuritiesEntrustHttp securitiesEntrust = ApplicationContextBeanFactory.getBean(SecuritiesEntrustHttp
            .class);
    private EntrustProducer entrustProducer = ApplicationContextBeanFactory.getBean(EntrustProducer.class);

    private Boolean interrupted = false;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("券商股票委托容器对象:{},当前对象{}", securitiesStockEntrustContainer,this);
        String tradeSession = "880003450508";
        while (!interrupted) {
            try {
                logger.info("3秒后开始轮询");
                Thread.sleep(3 * 1000);
                // 容器中委托数据可能包含来自数据库或者消息队列
                Map<String, SecuritiesStockEntrust> stockEntrusts = securitiesStockEntrustContainer
                        .getBuyInContainer();
                logger.info("券商委托股票容器内剩余:{}个委托订单", stockEntrusts.size());
                for (Map.Entry<String, SecuritiesStockEntrust> entry : stockEntrusts.entrySet()) {
                    logger.info("此处执行HTTP，当前委托订单为：{}", entry.getKey());
                    try {
                        SecuritiesStockEntrust securitiesStockEntrust = entry.getValue();
                        String currTradeSession = securitiesStockEntrust.getTradeSession();
                        if (currTradeSession == null) {
                            securitiesStockEntrust.setTradeSession(tradeSession);
                            continue;
                        } else {
                            tradeSession = currTradeSession;
                        }
                        StockEntrustQueryResult stockEntrustQueryResult = securitiesEntrust.queryEntrust
                                (securitiesStockEntrust.getTradeSession(), securitiesStockEntrust
                                        .getEntrustNo());
                        if (stockEntrustQueryResult == null) {
                            stockEntrusts.remove(entry.getKey());
                            continue;
                        }
                        logger.info("委托结果：{}", EntrustState.getByIndex(stockEntrustQueryResult.getEntrustStatus()));
                        if (stockEntrustQueryResult.getEntrustStatus().equals(EntrustState.HASBEENSUCCESS
                                .getIndex())) {
                            logger.info("交易委托单已交易成功，删除容器中交易单号为:{},委托数量为:{},委托价格:{}", securitiesStockEntrust.getTradeNo(),
                                    securitiesStockEntrust.getEntrustNumber(),securitiesStockEntrust.getEntrustPrice());
                            // 若执行结果为true 代表订单状态已成功，则  删除集合中的数据
                            //发送给队列处理，提高委托单轮询处理速度
                            logger.info("委托订单已完成:{}", entry.getKey());
                            //交易委托单委托成功之后，委托价格变成成交价格，委托数量变成成交数量
                            Float amount = Float.valueOf(stockEntrustQueryResult.getEntrustAmount());
                            securitiesStockEntrust.setEntrustNumber(amount.intValue());
                            securitiesStockEntrust.setEntrustPrice(BigDecimal.valueOf(Integer.valueOf
                                    (stockEntrustQueryResult.getBusinessPrice())));
                            entrustProducer.entrustBuyIn(securitiesStockEntrust);
                            stockEntrusts.remove(entry.getKey());
                        }
                    } catch (ServiceException ex) {
                        logger.error("券商委托单查询异常:{}", ex.getMessage());
                    }
                }
            } catch (InterruptedException e) {
                logger.info("中断异常:{}", e.getMessage());
            } catch (Exception e) {
                logger.info("轮询异常：{}", e.getMessage());
            }
        }
    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        logger.info("股票申请委托买入任务被中断");
        interrupted = true;
    }
}
