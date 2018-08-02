package com.waben.stock.interfaces.pojo.query.futures;

import com.waben.stock.interfaces.pojo.query.PageAndSortQuery;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 交易委托
 *
 * @author chenk 2018/7/27
 */
public class FuturesTradeEntrustQuery extends PageAndSortQuery {

    private Long id;
    @ApiModelProperty(value = "委托编号")
    private String entrustNo;

    @ApiModelProperty(value = "用户ID")
    private Long publisherId;

    @ApiModelProperty(value = "品种编号")
    private String commodityNo;

    @ApiModelProperty(value = "合约编号")
    private String contractNo;

    @ApiModelProperty(value = "订单交易类型")
    private String orderType;

    @ApiModelProperty(value = "价格类型")
    private String priceType;

    @ApiModelProperty(value = "交易开平仓 类型")
    private String tradeActionType;

    @ApiModelProperty(value = "委托状态")
    private String state;

    /** 交易成交时间-查询开始时间 */
    @ApiModelProperty(value = "交易成交时间-查询开始时间")
    private Date startTime;
    /** 交易成交时间-查询结束时间 */
    @ApiModelProperty(value = "交易成交时间-查询结束时间")
    private Date endTime;

    public FuturesTradeEntrustQuery() {
        super();
    }

    public FuturesTradeEntrustQuery(int page, int size) {
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

    public String getCommodityNo() {
        return commodityNo;
    }

    public void setCommodityNo(String commodityNo) {
        this.commodityNo = commodityNo;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public String getTradeActionType() {
        return tradeActionType;
    }

    public void setTradeActionType(String tradeActionType) {
        this.tradeActionType = tradeActionType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getEntrustNo() {
        return entrustNo;
    }

    public void setEntrustNo(String entrustNo) {
        this.entrustNo = entrustNo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
