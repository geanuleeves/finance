package com.waben.stock.interfaces.enums;

/**
 * @author: zengzhiwei
 * @date: 2018/7/27 17:06
 * @desc： 后台操作类型枚举
 */
public enum OperationType{
    SEE_PHONE_NUMBER(1,"查看手机号码");

    private Integer index;

    private String type;

    OperationType(Integer index, String type) {
        this.index = index;
        this.type = type;
    }


    public Integer getIndex() {
        return index;
    }

    public String getType() {
        return type;
    }
}
