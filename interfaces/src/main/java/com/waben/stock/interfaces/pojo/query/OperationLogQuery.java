package com.waben.stock.interfaces.pojo.query;

/**
 * @author: zengzhiwei
 * @date: 2018/7/30 9:34
 * @desc：
 */
public class OperationLogQuery extends PageAndSortQuery{

    private Integer type;

    private Long staffId;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }
}
