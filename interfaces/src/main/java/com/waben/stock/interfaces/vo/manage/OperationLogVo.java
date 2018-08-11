package com.waben.stock.interfaces.vo.manage;

import java.util.Date;

/**
 * @author: zengzhiwei
 * @date: 2018/7/27 17:43
 * @desc：
 */
public class OperationLogVo {

    private Long id;

    private Long staffId;

    private Integer type;

    private String staffName;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
