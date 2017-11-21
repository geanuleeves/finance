package com.waben.stock.applayer.tactics.dto.manage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by yuyidi on 2017/11/16.
 * @desc
 */
public class MenuDto implements Serializable {

    private Long id;
    private String name;
    private Long pid;
    private Boolean state;
    private Integer sort;

    private List<MenuDto> childs = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public List<MenuDto> getChilds() {
        return childs;
    }

    public void setChilds(List<MenuDto> childs) {
        this.childs = childs;
    }
}
