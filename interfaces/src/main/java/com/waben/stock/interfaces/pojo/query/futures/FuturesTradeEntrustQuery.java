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

    @ApiModelProperty(value = "委托编号")
    private String entrustNo;

    @ApiModelProperty(value = "用户ID")
    private Long publisherId;

    @ApiModelProperty(value = "对应的合约ID")
    private Long contractId;

    @ApiModelProperty(value = "品种编号")
    private String commodityNo;

    @ApiModelProperty(value = "合约编号")
    private String contractNo;

    @ApiModelProperty(value = "订单交易类型")
    private String orderType;

    @ApiModelProperty(value = "委托时间")
    private Date entrustTime;

    @ApiModelProperty(value = "价格类型")
    private String priceType;

    @ApiModelProperty(value = "交易开平仓 类型")
    private String tradeActionType;

    @ApiModelProperty(value = "委托状态")
    private String state;

    @ApiModelProperty(value = "交易成功时间")
    private Date tradeTime;

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

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
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

    public Date getEntrustTime() {
        return entrustTime;
    }

    public void setEntrustTime(Date entrustTime) {
        this.entrustTime = entrustTime;
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

    public Date getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(Date tradeTime) {
        this.tradeTime = tradeTime;
    }

    public String getEntrustNo() {
        return entrustNo;
    }

    public void setEntrustNo(String entrustNo) {
        this.entrustNo = entrustNo;
    }
}
