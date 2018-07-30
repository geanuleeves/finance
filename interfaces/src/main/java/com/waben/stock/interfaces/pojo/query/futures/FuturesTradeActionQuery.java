package com.waben.stock.interfaces.pojo.query.futures;

import com.waben.stock.interfaces.pojo.query.PageAndSortQuery;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 订单交易开平仓记录
 *
 * @author chenk 2018/7/27
 */
public class FuturesTradeActionQuery extends PageAndSortQuery {

    @ApiModelProperty(value = "用户ID")
    private Long publisherId;

    @ApiModelProperty(value = "对应的订单")
    private Long orderId;

    @ApiModelProperty(value = "对应的委托")
    private Long tradeEntrustId;

    @ApiModelProperty(value = "委托时间")
    private Date entrustTime;

    @ApiModelProperty(value = "交易开平仓 类型")
    private String tradeActionType;

    @ApiModelProperty(value = "风控类型")
    private String windControlType;

    @ApiModelProperty(value = "委托状态")
    private String state;

    @ApiModelProperty(value = "交易成功时间")
    private Date tradeTime;

    @ApiModelProperty(value = "结算时间")
    private Date settlementTime;

    public FuturesTradeActionQuery() {
        super();
    }

    public FuturesTradeActionQuery(int page, int size) {
        super();
        super.setPage(page);
        super.setSize(size);
    }

    public Long getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getTradeEntrustId() {
        return tradeEntrustId;
    }

    public void setTradeEntrustId(Long tradeEntrustId) {
        this.tradeEntrustId = tradeEntrustId;
    }

    public Date getEntrustTime() {
        return entrustTime;
    }

    public void setEntrustTime(Date entrustTime) {
        this.entrustTime = entrustTime;
    }

    public String getTradeActionType() {
        return tradeActionType;
    }

    public void setTradeActionType(String tradeActionType) {
        this.tradeActionType = tradeActionType;
    }

    public String getWindControlType() {
        return windControlType;
    }

    public void setWindControlType(String windControlType) {
        this.windControlType = windControlType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(Date tradeTime) {
        this.tradeTime = tradeTime;
    }

    public Date getSettlementTime() {
        return settlementTime;
    }

    public void setSettlementTime(Date settlementTime) {
        this.settlementTime = settlementTime;
    }
}
