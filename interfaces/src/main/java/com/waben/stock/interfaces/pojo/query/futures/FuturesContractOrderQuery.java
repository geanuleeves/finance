package com.waben.stock.interfaces.pojo.query.futures;

import com.waben.stock.interfaces.pojo.query.PageAndSortQuery;
import io.swagger.annotations.ApiModelProperty;

/**
 * 合约订单
 *
 * @author chenk 2018/7/27
 */
public class FuturesContractOrderQuery extends PageAndSortQuery {

    @ApiModelProperty(value = "发布人ID")
    private Long publisherId;

    @ApiModelProperty(value = "品种编号")
    private String commodityNo;

    @ApiModelProperty(value = "合约编号")
    private String contractNo;

    public FuturesContractOrderQuery() {
        super();
    }

    public FuturesContractOrderQuery(int page, int size) {
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
}
