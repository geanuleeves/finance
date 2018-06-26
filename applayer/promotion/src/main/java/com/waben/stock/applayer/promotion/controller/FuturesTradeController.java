package com.waben.stock.applayer.promotion.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.applayer.promotion.business.futures.FuturesTradeBusiness;
import com.waben.stock.applayer.promotion.security.SecurityUtil;
import com.waben.stock.applayer.promotion.util.PoiUtil;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.admin.futures.FutresOrderEntrustDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesOrderAdminDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesOrderCountDto;
import com.waben.stock.interfaces.dto.organization.FuturesFowDto;
import com.waben.stock.interfaces.dto.organization.FuturesTradeOrganizationDto;
import com.waben.stock.interfaces.dto.organization.TradingFowDto;
import com.waben.stock.interfaces.enums.CapitalFlowType;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesTradeAdminQuery;
import com.waben.stock.interfaces.pojo.query.organization.FuturesFowQuery;
import com.waben.stock.interfaces.pojo.query.organization.TradingFowQuery;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/futures")
@Api(description = "代理商接口列表")
public class FuturesTradeController {
	
	@Autowired
	private FuturesTradeBusiness business;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@RequestMapping(value = "/FuturesFow/", method = RequestMethod.GET)
	@ApiOperation(value = "查询期货交易流水")
	public Response<PageInfo<FuturesFowDto>> futuresFowPageByQuery(FuturesFowQuery query){
		query.setCurrentOrgId(SecurityUtil.getUserDetails().getOrgId());
		PageInfo<FuturesFowDto> result = business.futuresFowPageByQuery(query);
		
		return new Response<>(result);
	}
	
	@RequestMapping(value = "/organizationOrder/pages", method = RequestMethod.POST)
	@ApiOperation(value = "查询订单")
	public Response<PageInfo<FuturesTradeOrganizationDto>> pagesOrganizationOrder(FuturesTradeAdminQuery query){
		return business.pagesOrganizationOrder(query);
	}
	
	@RequestMapping(value = "/organizationOrder/pagesEntrust", method = RequestMethod.POST)
	@ApiOperation(value = "查询委托订单")
	public Response<PageInfo<FutresOrderEntrustDto>> pagesOrganizationEntrustOrder(FuturesTradeAdminQuery query){
		return business.pagesOrganizationEntrustOrder(query);
	}
	
	@GetMapping("/organizationOrder/countOrderState")
	@ApiOperation(value = "订单总计")
	public Response<FuturesOrderCountDto> countOrderState(FuturesTradeAdminQuery query){
		return business.countOrderState(query);
	}
	
	@RequestMapping(value = "/export", method = RequestMethod.GET)
	@ApiOperation(value = "导出期货订单信息")
	public void export(FuturesTradeAdminQuery query, HttpServletResponse svrResponse){
		query.setPage(0);
		query.setSize(Integer.MAX_VALUE);
		File file = null;
		FileInputStream is = null;
		
		
		List<String> columnDescList = null;
		try {
			String fileName = "";
			List<List<String>> result = new ArrayList<>();
			if (query.getQueryType() == 0) {// 成交订单
				PageInfo<FuturesTradeOrganizationDto> response = business.pagesOrganizationOrder(query).getResult();
				fileName = "成交订单_" + String.valueOf(System.currentTimeMillis());
				file = File.createTempFile(fileName, ".xls");
				columnDescList = columnDescList();
				result = dataList(response.getContent(), query.getQueryType());
				PoiUtil.writeDataToExcel("成交数据", file, columnDescList, result);
			} else if (query.getQueryType() == 1) {// 持仓中订单
				columnDescList = positionDescList();
				PageInfo<FuturesTradeOrganizationDto> response = business.pagesOrganizationOrder(query).getResult();
				fileName = "开仓订单_" + String.valueOf(System.currentTimeMillis());
				file = File.createTempFile(fileName, ".xls");
				result = dataList(response.getContent(), query.getQueryType());
				PoiUtil.writeDataToExcel("开仓订单数据", file, columnDescList, result);
			} else if (query.getQueryType() == 2) {// 平仓订单
				columnDescList = eveningDescList();
				PageInfo<FuturesTradeOrganizationDto> response = business.pagesOrganizationOrder(query).getResult();
				fileName = "平仓结算订单_" + String.valueOf(System.currentTimeMillis());
				file = File.createTempFile(fileName, ".xls");
				result = dataList(response.getContent(), query.getQueryType());
				PoiUtil.writeDataToExcel("平仓结算订单数据", file, columnDescList, result);
			} else if (query.getQueryType() == 3) {// 委托订单
				columnDescList = deputeDescList();
				PageInfo<FutresOrderEntrustDto> response = business.pagesOrganizationEntrustOrder(query).getResult();
				fileName = "委托记录_" + String.valueOf(System.currentTimeMillis());
				file = File.createTempFile(fileName, ".xls");
				result = dataListEntrust(response.getContent(), query.getQueryType());
				PoiUtil.writeDataToExcel("委托记录数据", file, columnDescList, result);
			} else if (query.getQueryType() == 4) {// 退款订单
				columnDescList = accountantList();
				PageInfo<FutresOrderEntrustDto> response = business.pagesOrganizationEntrustOrder(query).getResult();
				fileName = "退款订单_" + String.valueOf(System.currentTimeMillis());
				file = File.createTempFile(fileName, ".xls");
				result = dataListEntrust(response.getContent(), query.getQueryType());
				PoiUtil.writeDataToExcel("退款订单数据", file, columnDescList, result);
			} 
			
			
			is = new FileInputStream(file);
			
			svrResponse.setHeader("Content-Disposition",
					"attachment;filename=\"" + new String(fileName.getBytes("gb2312"), "ISO8859-1") + ".xls" + "\"");
			svrResponse.setContentType("application/vnd.ms-excel");
			IOUtils.copy(is, svrResponse.getOutputStream());
			svrResponse.getOutputStream().flush();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ExceptionConstant.UNKNOW_EXCEPTION, "导出数据到excel异常：" + e.getMessage());
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			if (file != null) {
				file.delete();
			}
		}
	}
	
	@RequestMapping(value = "/futuresTrade/export", method = RequestMethod.GET)
	@ApiOperation(value = "导出交易流水数据")
	public void tradingExport(FuturesFowQuery query, HttpServletResponse svrResponse) {
		query.setPage(0);
		query.setSize(Integer.MAX_VALUE);
		// query.setCurrentOrgId(SecurityUtil.getUserDetails().getOrgId());
		PageInfo<FuturesFowDto> result = business.futuresFowPageByQuery(query);
		File file = null;
		FileInputStream is = null;
		try {
			String fileName = "交易流水_" + String.valueOf(System.currentTimeMillis());
			file = File.createTempFile(fileName, ".xls");
			List<String> columnDescList = columnFuturesList();
			List<List<String>> dataList = tradingList(result.getContent());
			PoiUtil.writeDataToExcel("交易流水数据", file, columnDescList, dataList);

			is = new FileInputStream(file);
			svrResponse.setHeader("Content-Disposition",
					"attachment;filename=\"" + new String(fileName.getBytes("gb2312"), "ISO8859-1") + ".xls" + "\"");
			svrResponse.setContentType("application/vnd.ms-excel");
			IOUtils.copy(is, svrResponse.getOutputStream());
			svrResponse.getOutputStream().flush();
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServiceException(ExceptionConstant.UNKNOW_EXCEPTION, "导出交易流水数据到excel异常：" + e.getMessage());
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			if (file != null) {
				file.delete();
			}
		}
	}
	
	private List<List<String>> dataList(List<FuturesTradeOrganizationDto> content, Integer type){
		List<List<String>> result = new ArrayList<List<String>>();
		if(content==null){
			return result;
		}
		for (FuturesTradeOrganizationDto dto : content) {
			List<String> data = new ArrayList<String>();
			if(type == 0){
				data.add(dto.getPublisherName() == null ? "" : dto.getPublisherName());
				data.add(dto.getPublisherPhone() == null ? "" : dto.getPublisherPhone());
				data.add(dto.getName() == null ? "" : dto.getName());
				data.add(dto.getTradeNo() == null ? "" : dto.getTradeNo());
				data.add(dto.getOrderType() == null ? "" : dto.getOrderType());
				data.add(dto.getState() == null ? "" : dto.getState());
				data.add(dto.getTotalQuantity() == null ? "" : dto.getTotalQuantity().toString());
				data.add(dto.getBuyingTime() == null ? "" : sdf.format(dto.getBuyingTime()));
				data.add(dto.getBuyingPrice() == null ? "" : dto.getBuyingPrice().toString());
				data.add(dto.getProfit() == null ? "" : dto.getProfit().toString());
				data.add(dto.getOpenwindServiceFee() == null ? "" : dto.getOpenwindServiceFee().toString());
				data.add(dto.getReserveFund() == null ? "" : dto.getReserveFund().toString());
				data.add(dto.getOvernightServiceFee() == null ? "" : dto.getOvernightServiceFee().toString());
				data.add(dto.getPerUnitLimitLossAmount() == null ? "" : dto.getPerUnitLimitLossAmount().toString());
				data.add(dto.getPerUnitLimitProfitAmount() == null ? "" : dto.getPerUnitLimitProfitAmount().toString());
				data.add(dto.getPositionDays() == null ? "" : dto.getPositionDays().toString());
				data.add(dto.getPositionEndTime() == null ? "" : sdf.format(dto.getPositionEndTime()));
				data.add(dto.getSellingTime() == null ? "" : sdf.format(dto.getSellingTime()));
				data.add(dto.getSellingPrice() == null ? "" : dto.getSellingPrice().toString());
				data.add(dto.getSellingProfit() == null ? "" : dto.getSellingProfit().toString());
				data.add(dto.getUnwindServiceFee() == null ? "" : dto.getUnwindServiceFee().toString());
				data.add(dto.getWindControlType() == null ? "" : dto.getWindControlType());
				data.add(dto.getOrgName() == null ? "" : dto.getOrgName());
			}else if(type == 1){
				data.add(dto.getPublisherName() == null ? "" : dto.getPublisherName());
				data.add(dto.getPublisherPhone() == null ? "" : dto.getPublisherPhone());
				data.add(dto.getName() == null ? "" : dto.getName());
				data.add(dto.getTradeNo() == null ? "" : dto.getTradeNo());
				data.add(dto.getOrderType() == null ? "" : dto.getOrderType());
				data.add(dto.getState() == null ? "" : dto.getState());
				data.add(dto.getTotalQuantity() == null ? "" : dto.getTotalQuantity().toString());
				data.add(dto.getBuyingTime() == null ? "" : sdf.format(dto.getBuyingTime()));
				data.add(dto.getBuyingPrice() == null ? "" : dto.getBuyingPrice().toString());
				data.add(dto.getProfit() == null ? "" : dto.getProfit().toString());
				data.add(dto.getOpenwindServiceFee() == null ? "" : dto.getOpenwindServiceFee().toString());
				data.add(dto.getReserveFund() == null ? "" : dto.getReserveFund().toString());
				data.add(dto.getPerUnitLimitLossAmount() == null ? "" : dto.getPerUnitLimitLossAmount().toString());
				data.add(dto.getPerUnitLimitProfitAmount() == null ? "" : dto.getPerUnitLimitProfitAmount().toString());
				data.add(dto.getOvernightServiceFee() == null ? "" : dto.getOvernightServiceFee().toString());
				data.add("");
				data.add(dto.getPositionEndTime() == null ? "" : sdf.format(dto.getPositionEndTime()));
				data.add(dto.getPositionDays() == null ? "" : dto.getPositionDays().toString());
				data.add(dto.getWindControlType() == null ? "" : dto.getWindControlType());
				data.add(dto.getOrgName() == null ? "" : dto.getOrgName());
			}else if(type == 2){
				data.add(dto.getPublisherName() == null ? "" : dto.getPublisherName());
				data.add(dto.getPublisherPhone() == null ? "" : dto.getPublisherPhone());
				data.add(dto.getName() == null ? "" : dto.getName());
				data.add(dto.getTradeNo() == null ? "" : dto.getTradeNo());
				data.add(dto.getOrderType() == null ? "" : dto.getOrderType());
				data.add(dto.getState() == null ? "" : dto.getState());
				data.add(dto.getTotalQuantity() == null ? "" : dto.getTotalQuantity().toString());
				data.add(dto.getBuyingTime() == null ? "" : sdf.format(dto.getBuyingTime()));
				data.add(dto.getBuyingPrice() == null ? "" : dto.getBuyingPrice().toString());
				data.add(dto.getProfit() == null ? "" : dto.getProfit().toString());
				data.add(dto.getOpenwindServiceFee() == null ? "" : dto.getOpenwindServiceFee().toString());
				data.add(dto.getReserveFund() == null ? "" : dto.getReserveFund().toString());
				data.add(dto.getPerUnitLimitLossAmount() == null ? "" : dto.getPerUnitLimitLossAmount().toString());
				data.add(dto.getPerUnitLimitProfitAmount() == null ? "" : dto.getPerUnitLimitProfitAmount().toString());
				data.add(dto.getOvernightServiceFee() == null ? "" : dto.getOvernightServiceFee().toString());
				data.add(dto.getPositionDays() == null ? "" : dto.getPositionDays().toString());
				data.add(dto.getPositionEndTime() == null ? "" : sdf.format(dto.getPositionEndTime()));
				data.add(dto.getSellingTime() == null ? "" : sdf.format(dto.getSellingTime()));
				data.add(dto.getSellingPrice() == null ? "" : dto.getSellingPrice().toString());
				data.add(dto.getSellingProfit() == null ? "" : dto.getSellingProfit().toString());
				data.add(dto.getUnwindServiceFee() == null ? "" : dto.getUnwindServiceFee().toString());
				data.add(dto.getWindControlType() == null ? "" : dto.getWindControlType());
				data.add("");
				data.add(dto.getOrgName() == null ? "" : dto.getOrgName());
			}
			result.add(data);
		}
		
		return result;
	}
	
	private List<List<String>> dataListEntrust(List<FutresOrderEntrustDto> content, Integer type) {
		List<List<String>> result = new ArrayList<List<String>>();
		for (FutresOrderEntrustDto dto : content) {
			List<String> data = new ArrayList<String>();
			if(type == 3){
				data.add(dto.getPublisherName() == null ? "" : dto.getPublisherName());
				data.add(dto.getPublisherPhone() == null ? "" : dto.getPublisherPhone());
				data.add(dto.getSymbol() == null ? "" : dto.getSymbol());
				data.add(dto.getName() == null ? "" : dto.getName());
				data.add(dto.getTradeNo() == null ? "" : dto.getTradeNo());
				data.add(dto.getOrderType() == null ? "" : dto.getOrderType());
				data.add(dto.getState() == null ? "" : dto.getState());
				data.add(dto.getEntrustAppointPrice() == null ? "" : dto.getEntrustAppointPrice().toString());
				data.add(dto.getLastPrice() == null ? "" : dto.getLastPrice().toString());
				data.add(dto.getTotalQuantity() == null ? "" : dto.getTotalQuantity().toString());
				data.add(dto.getServiceFee() == null ? "" : dto.getServiceFee().toString());
				data.add(dto.getReserveFund() == null ? "" : dto.getReserveFund().toString());
				data.add(dto.getPerUnitLimitLossAmount() == null ? "" : dto.getPerUnitLimitLossAmount().toString());
				data.add(dto.getPerUnitLimitProfitAmount() == null ? "" : dto.getPerUnitLimitProfitAmount().toString());
				data.add(dto.getPostTime() == null ? "" : sdf.format(dto.getPostTime()));
				data.add(dto.getDealTime() == null ? "" : sdf.format(dto.getDealTime()));
				data.add(dto.getOrgName() == null ? "" : dto.getOrgName());
			}else if(type == 4){
				data.add(dto.getPublisherName() == null ? "" : dto.getPublisherName());
				data.add(dto.getPublisherPhone() == null ? "" : dto.getPublisherPhone());
				data.add(dto.getName() == null ? "" : dto.getName());
				data.add(dto.getTradeNo() == null ? "" : dto.getTradeNo());
				data.add(dto.getOrderType() == null ? "" : dto.getOrderType());
				data.add(dto.getState() == null ? "" : dto.getState());
				data.add(dto.getEntrustAppointPrice() == null ? "" : dto.getEntrustAppointPrice().toString());
				data.add(dto.getTotalQuantity() == null ? "" : dto.getTotalQuantity().toString());
				data.add(dto.getServiceFee() == null ? "" : dto.getServiceFee().toString());
				data.add(dto.getReserveFund() == null ? "" : dto.getReserveFund().toString());
				data.add(dto.getPerUnitLimitLossAmount() == null ? "" : dto.getPerUnitLimitLossAmount().toString());
				data.add(dto.getPerUnitLimitProfitAmount() == null ? "" : dto.getPerUnitLimitProfitAmount().toString());
				data.add(dto.getPostTime() == null ? "" : sdf.format(dto.getPostTime()));
				data.add("已退款");
				data.add(dto.getOrgName() == null ? "" : dto.getOrgName());
			}
			result.add(data);
		}
		return result;
	}
	
	private List<List<String>> tradingList(List<FuturesFowDto> content) {
		List<List<String>> result = new ArrayList<>();
		for (FuturesFowDto trade : content) {
			CapitalFlowType type = trade.getType();
			String busType = "";
			if (type != null) {
				busType = type.getType();
			}
			List<String> data = new ArrayList<>();
			data.add(String.valueOf(trade.getId() == null ? "" : trade.getId()));
			data.add(trade.getPublisherName() == null ? "" : trade.getPublisherName());
			data.add(trade.getPublisherPhone() == null ? "" : trade.getPublisherPhone());
			data.add(trade.getFlowNo() == null ? "" : trade.getFlowNo());
			data.add(trade.getOccurrenceTime() != null ? sdf.format(trade.getOccurrenceTime()) : "");
			data.add(busType);
			data.add(String.valueOf(trade.getAmount() == null ? "" : trade.getAmount()));
			data.add(String.valueOf(trade.getAvailableBalance() == null ? "" : trade.getAvailableBalance()));
			data.add(trade.getCommoditySymbol() == null ? "" : trade.getCommoditySymbol());
			data.add(trade.getCommodityName() == null ? "" : trade.getCommodityName()+"/"+trade.getContractNo());
			data.add(trade.getAgentCode() + "/" + trade.getAgentCodeName());
			result.add(data);
		}
		return result;
	}
	
	//成交订单
	private List<String> columnDescList() {
		List<String> result = new ArrayList<>();
		result.add("客户姓名");
		result.add("交易账户");
		result.add("合约名称");
		result.add("订单编号");
		result.add("交易方向");
		result.add("交易状态");
		result.add("成交手数");
		result.add("买入时间");
		result.add("买入价格");
		result.add("浮动盈亏");
		result.add("开仓手续费");
		result.add("保证金");
		result.add("隔夜手续费");
		result.add("止损金额");
		result.add("止盈金额");
		result.add("持仓小时");
		result.add("持仓截止日期");
		result.add("平仓时间");
		result.add("平仓价格");
		result.add("平仓盈亏");
		result.add("平仓手续费");
		result.add("风控状态");
		result.add("所属代理商代码/名称");
		return result;
	}
	
	//持仓中订单
	private List<String> positionDescList() {
		List<String> result = new ArrayList<>();
		result.add("客户姓名");
		result.add("交易账户");
		result.add("合约名称");
		result.add("订单编号");
		result.add("交易方向");
		result.add("交易状态");
		result.add("成交手数");
		result.add("开仓时间");
		result.add("买入价格");
		result.add("浮动盈亏");
		result.add("开仓手续费");
		result.add("保证金");
		result.add("止损金额");
		result.add("止盈金额");
		result.add("隔夜手续费");
		result.add("隔夜保证金");
		result.add("持仓截止日期");
		result.add("持仓小时");
		result.add("风控状态");
		result.add("所属代理商代码/名称");
		return result;
	}
	
	//平仓结算记录
	private List<String> eveningDescList() {
		List<String> result = new ArrayList<>();
		result.add("客户姓名");
		result.add("交易账户");
		result.add("合约名称");
		result.add("订单编号");
		result.add("交易方向");
		result.add("交易状态");
		result.add("成交手数");
		result.add("买入时间");
		result.add("买入价格");
		result.add("浮动盈亏");
		result.add("开仓手续费");
		result.add("保证金");
		result.add("止损金额");
		result.add("止盈金额");
		result.add("隔夜手续费");
		result.add("持仓小时");
		result.add("持仓截止日期");
		result.add("平仓时间");
		result.add("平仓价格");
		result.add("平仓盈亏");
		result.add("平仓手续费");
		result.add("平仓类型");
		result.add("备注");
		result.add("所属代理商代码/名称");
		return result;
	}
	
	//委托记录
	private List<String> deputeDescList() {
		List<String> result = new ArrayList<>();
		result.add("客户姓名");
		result.add("交易账户");
		result.add("交易代码");
		result.add("交易品种");
		result.add("委托编号");
		result.add("委托方向");
		result.add("委托状态");
		result.add("委托指定价格");
		result.add("当前价");
		result.add("委托手数");
		result.add("手续费");
		result.add("保证金");
		result.add("止损金额");
		result.add("止盈金额");
		result.add("委托时间");
		result.add("成交时间");
		result.add("所属代理商代码/名称");
		return result;
	}
	
	//委托/退款记录
	private List<String> accountantList() {
		List<String> result = new ArrayList<>();
		result.add("客户姓名");
		result.add("交易账户");
		result.add("合约名称");
		result.add("委托编号");
		result.add("委托方向");
		result.add("委托状态");
		result.add("委托指定价格");
		result.add("委托手数");
		result.add("手续费");
		result.add("保证金");
		result.add("止损金额");
		result.add("止盈金额");
		result.add("委托时间");
		result.add("状态");
		result.add("所属代理商代码/名称");
		return result;
	}
	
	//交易流水
	private List<String> columnFuturesList() {
			List<String> result = new ArrayList<>();
			result.add("订单ID");
			result.add("客户姓名");
			result.add("交易帐号");
			result.add("交易编码");
			result.add("交易时间");
			result.add("业务类型");
			result.add("交易金额");
			result.add("账户余额");
			result.add("交易代码");
			result.add("交易品种");
			result.add("所属代理商代码/名称");
			return result;
		}	

}
