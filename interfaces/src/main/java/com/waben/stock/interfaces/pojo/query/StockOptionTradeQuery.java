package com.waben.stock.interfaces.pojo.query;

import java.util.Date;

public class StockOptionTradeQuery extends PageAndSortQuery {
    /**
     * 发布人手机号码
     */
    private Long publisherPhone;
    /**
     * 申购单号
     */
    private String applyNo;
    /**
     * 申购状态
     */
    private Integer state;
    private Date beginTime;
    private Date endTime;
    public Long getPublisherPhone() {
        return publisherPhone;
    }

    public void setPublisherPhone(Long publisherPhone) {
        this.publisherPhone = publisherPhone;
    }

    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
