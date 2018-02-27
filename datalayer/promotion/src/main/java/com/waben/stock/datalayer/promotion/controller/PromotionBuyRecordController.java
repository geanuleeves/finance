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

import com.waben.stock.datalayer.promotion.entity.PromotionBuyRecord;
import com.waben.stock.datalayer.promotion.service.PromotionBuyRecordService;
import com.waben.stock.interfaces.pojo.Response;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 推广渠道产生的策略 Controller
 * 
 * @author luomengan
 *
 */
@RestController
@RequestMapping("/promotionBuyRecord")
@Api(description = "推广渠道产生的策略接口列表")
public class PromotionBuyRecordController {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	public PromotionBuyRecordService promotionBuyRecordService;

	@GetMapping("/{id}")
	@ApiOperation(value = "根据id获取推广渠道产生的策略")
	public Response<PromotionBuyRecord> fetchById(@PathVariable Long id) {
		return new Response<>(promotionBuyRecordService.getPromotionBuyRecordInfo(id));
	}

	@GetMapping("/page")
	@ApiOperation(value = "获取推广渠道产生的策略分页数据")
	public Response<Page<PromotionBuyRecord>> promotionBuyRecords(int page, int limit) {
		return new Response<>((Page<PromotionBuyRecord>) promotionBuyRecordService.promotionBuyRecords(page, limit));
	}
	
	@GetMapping("/list")
	@ApiOperation(value = "获取推广渠道产生的策略列表")
	public Response<List<PromotionBuyRecord>> list() {
		return new Response<>(promotionBuyRecordService.list());
	}
	
	/******************************** 后台管理 **********************************/
	
	@PostMapping("/")
	@ApiOperation(value = "添加推广渠道产生的策略", hidden = true)
	public Response<PromotionBuyRecord> addition(PromotionBuyRecord promotionBuyRecord) {
		return new Response<>(promotionBuyRecordService.addPromotionBuyRecord(promotionBuyRecord));
	}

	@PutMapping("/")
	@ApiOperation(value = "修改推广渠道产生的策略", hidden = true)
	public Response<PromotionBuyRecord> modification(PromotionBuyRecord promotionBuyRecord) {
		return new Response<>(promotionBuyRecordService.modifyPromotionBuyRecord(promotionBuyRecord));
	}

	@DeleteMapping("/{id}")
	@ApiOperation(value = "删除推广渠道产生的策略", hidden = true)
	public Response<Long> delete(@PathVariable Long id) {
		promotionBuyRecordService.deletePromotionBuyRecord(id);
		return new Response<Long>(id);
	}
	
	@PostMapping("/deletes")
	@ApiOperation(value = "批量删除推广渠道产生的策略（多个id以逗号分割）", hidden = true)
	public Response<Boolean> deletes(String ids) {
		promotionBuyRecordService.deletePromotionBuyRecords(ids);
		return new Response<Boolean>(true);
	}
	
	@GetMapping("/adminList")
	@ApiOperation(value = "获取推广渠道产生的策略列表(后台管理)", hidden = true)
	public Response<List<PromotionBuyRecord>> adminList() {
		return new Response<>(promotionBuyRecordService.list());
	}

}
