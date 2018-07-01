package com.waben.stock.interfaces.pojo.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author Created by yuyidi on 2017/12/11.
 * @desc
 */
@ApiModel(value="CircularsQuery",description="公告查询对象")

public class CircularsQuery extends PageAndSortQuery{
    @ApiModelProperty(value = "标题")
    private String title;
    
    @ApiModelProperty(value = "类型")
    private String type;
    
    private Integer state;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}
}
