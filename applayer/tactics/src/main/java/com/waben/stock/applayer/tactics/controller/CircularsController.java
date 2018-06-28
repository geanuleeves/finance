package com.waben.stock.applayer.tactics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.applayer.tactics.business.CircularsBusiness;
import com.waben.stock.interfaces.dto.manage.CircularsDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.CircularsQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @Author pengzhenliang
 * @Create 2018/6/28 17:38
 */
@RestController
@RequestMapping("/circulars")
@Api(description="新闻公告")
public class CircularsController {

	@Autowired
    private CircularsBusiness circularsBusiness;
	
	@GetMapping("/pages")
    @ApiOperation(value = "公告分页")
    public Response<PageInfo<CircularsDto>> pages(CircularsQuery query) {
        PageInfo<CircularsDto> response = circularsBusiness.pages(query);
        return new Response<>(response);
    }
	
	@GetMapping("/{id}")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "新闻公告id", required = true)
    @ApiOperation(value = "通过公告id获取公告")
    public Response<CircularsDto> fetchById(@PathVariable Long id) {
        CircularsDto response = circularsBusiness.findById(id);
        return new Response<>(response);
    }
}
