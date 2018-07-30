package com.waben.stock.interfaces.dto.futures;

import com.waben.stock.interfaces.enums.FuturesOrderType;
import com.waben.stock.interfaces.enums.FuturesTradePriceType;
import com.waben.stock.interfaces.enums.FuturesWindControlType;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author chenk 2018/7/30
 */
public class FuturesTradeActionViewDto extends FuturesTradeActionDto {

    /** 合约名称 */
    @ApiModelProperty(value = "合约名称")
    private String contractName;

    /** 订单编号 */
    @ApiModelProperty(value = "订单编号")
    private String orderNo;

    /** 订单交易类型 */
    @ApiModelProperty(value = "订单交易类型")
    private FuturesOrderType orderType;

    /** 备注 */
    @ApiModelProperty(value = "备注")
    private FuturesWindControlType remark;

    /** 订单类型 */
    @ApiModelProperty(value = "订单类型")
    private FuturesTradePriceType futuresTradePriceType;


    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public FuturesOrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(FuturesOrderType orderType) {
        this.orderType = orderType;
    }

    public FuturesWindControlType getRemark() {
        return remark;
    }

    public void setRemark(FuturesWindControlType remark) {
        this.remark = remark;
    }

    public FuturesTradePriceType getFuturesTradePriceType() {
        return futuresTradePriceType;
    }

    public void setFuturesTradePriceType(FuturesTradePriceType futuresTradePriceType) {
        this.futuresTradePriceType = futuresTradePriceType;
    }
}
