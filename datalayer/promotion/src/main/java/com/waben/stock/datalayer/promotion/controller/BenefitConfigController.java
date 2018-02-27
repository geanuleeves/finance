package com.waben.stock.datalayer.promotion.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.datalayer.promotion.entity.BenefitConfig;
import com.waben.stock.datalayer.promotion.service.BenefitConfigService;
import com.waben.stock.interfaces.pojo.Response;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 分成配置 Controller
 * 
 * @author luomengan
 *
 */
@RestController
@RequestMapping("/benefitConfig")
@Api(description = "分成配置接口列表")
public class BenefitConfigController {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	public BenefitConfigService benefitConfigService;

	@GetMapping("/{id}")
	@ApiOperation(value = "根据id获取分成配置")
	public Response<BenefitConfig> fetchById(@PathVariable Long id) {
		return new Response<>(benefitConfigService.getBenefitConfigInfo(id));
	}

	@GetMapping("/page")
	@ApiOperation(value = "获取分成配置分页数据")
	public Response<Page<BenefitConfig>> benefitConfigs(int page, int limit) {
		return new Response<>((Page<BenefitConfig>) benefitConfigService.benefitConfigs(page, limit));
	}
	
	@GetMapping("/list")
	@ApiOperation(value = "获取分成配置列表")
	public Response<List<BenefitConfig>> list() {
		return new Response<>(benefitConfigService.list());
	}
	
	/******************************** 后台管理 **********************************/
	
	@PostMapping("/")
	@ApiOperation(value = "添加分成配置", hidden = true)
	public Response<BenefitConfig> addition(BenefitConfig benefitConfig) {
		return new Response<>(benefitConfigService.addBenefitConfig(benefitConfig));
	}

	@PutMapping("/")
	@ApiOperation(value = "修改分成配置", hidden = true)
	public Response<BenefitConfig> modification(BenefitConfig benefitConfig) {
		return new Response<>(benefitConfigService.modifyBenefitConfig(benefitConfig));
	}

	@DeleteMapping("/{id}")
	@ApiOperation(value = "删除分成配置", hidden = true)
	public Response<Long> delete(@PathVariable Long id) {
		benefitConfigService.deleteBenefitConfig(id);
		return new Response<Long>(id);
	}
	
	@PostMapping("/deletes")
	@ApiOperation(value = "批量删除分成配置（多个id以逗号分割）", hidden = true)
	public Response<Boolean> deletes(String ids) {
		benefitConfigService.deleteBenefitConfigs(ids);
		return new Response<Boolean>(true);
	}
	
	@GetMapping("/adminList")
	@ApiOperation(value = "获取分成配置列表(后台管理)", hidden = true)
	public Response<List<BenefitConfig>> adminList() {
		return new Response<>(benefitConfigService.list());
	}

}
