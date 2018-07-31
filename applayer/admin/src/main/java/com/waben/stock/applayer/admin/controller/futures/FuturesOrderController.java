package com.waben.stock.applayer.admin.controller.futures;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.applayer.admin.business.futures.FuturesOrderBusiness;
import com.waben.stock.applayer.admin.util.PoiUtil;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.admin.futures.FutresOrderEntrustDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesHoldPositionAgentDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesOrderAdminDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesOrderCountDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesTradeActionAgentDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesTradeDto;
import com.waben.stock.interfaces.enums.FuturesOrderState;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesTradeAdminQuery;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 期货订单Controller
 * 
 * @author pengzhenliang
 *
 */
@RestController
@RequestMapping("/futures")
@Api(description = "期货订单")
public class FuturesOrderController {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FuturesOrderBusiness business;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@GetMapping("/pageTradeAdmin")
	@ApiOperation(value = "查询期货成交订单")
	public Response<PageInfo<FuturesTradeActionAgentDto>> pageTradeAdmin(FuturesTradeAdminQuery query){
		return new Response<>(business.pageTradeAdmin(query));
	}

	@GetMapping("/countOrderState")
	@ApiOperation(value = "订单总计")
	public Response<FuturesOrderCountDto> countOrderState(FuturesTradeAdminQuery query) {
		return new Response<>(business.getSUMOrder(query));
	}

	@GetMapping("/pages")
	@ApiOperation(value = "查询期货成交订单")
	public Response<PageInfo<FuturesOrderAdminDto>> pages(FuturesTradeAdminQuery query) {
		return new Response<>(business.adminPagesByQuery(query));
	}
	
	@GetMapping("/pagesHoldingOrderAgent")
	@ApiOperation(value = "查询期货持仓列表")
	public Response<PageInfo<FuturesHoldPositionAgentDto>> pagesHoldingOrderAgent(FuturesTradeAdminQuery query) {
		return new Response<>(business.pagesHoldingOrderAgent(query));
	}

	@GetMapping("/pagesOrderEntust")
	@ApiOperation(value = "查询委托订单")
	public Response<PageInfo<FutresOrderEntrustDto>> pagesOrderEntust(FuturesTradeAdminQuery query) {
		return new Response<>(business.pagesOrderEntrust(query));
	}
	
	@GetMapping("/pageTradeEntust")
	@ApiOperation(value = "查询委托订单")
	public Response<PageInfo<FuturesTradeDto>> pageTradeEntust(FuturesTradeAdminQuery query) {
		return new Response<>(business.pageTradeEnutrs(query));
	}

	@RequestMapping(value = "/export", method = RequestMethod.GET)
	@ApiOperation(value = "导出期货订单信息")
	public void export(FuturesTradeAdminQuery query, HttpServletResponse svrResponse) {
		query.setPage(0);
		query.setSize(Integer.MAX_VALUE);
		File file = null;
		FileInputStream is = null;

		List<String> columnDescList = null;
		try {
			String fileName = "";
			List<List<String>> result = new ArrayList<>();
			if (query.getQueryType() == 0) {// 成交订单
				PageInfo<FuturesOrderAdminDto> response = business.adminPagesByQuery(query);
				fileName = "成交订单_" + String.valueOf(System.currentTimeMillis());
				file = File.createTempFile(fileName, ".xls");
				columnDescList = columnDescList();
				result = dataList(response.getContent(), query.getQueryType());
				PoiUtil.writeDataToExcel("成交数据", file, columnDescList, result);
			} else if (query.getQueryType() == 1) {// 持仓中订单
				columnDescList = positionDescList();
				PageInfo<FuturesOrderAdminDto> response = business.adminPagesByQuery(query);
				fileName = "开仓订单_" + String.valueOf(System.currentTimeMillis());
				file = File.createTempFile(fileName, ".xls");
				result = dataList(response.getContent(), query.getQueryType());
				PoiUtil.writeDataToExcel("开仓订单数据", file, columnDescList, result);
			} else if (query.getQueryType() == 2) {// 平仓订单
				columnDescList = eveningDescList();
				PageInfo<FuturesOrderAdminDto> response = business.adminPagesByQuery(query);
				fileName = "平仓结算订单_" + String.valueOf(System.currentTimeMillis());
				file = File.createTempFile(fileName, ".xls");
				result = dataList(response.getContent(), query.getQueryType());
				PoiUtil.writeDataToExcel("平仓结算订单数据", file, columnDescList, result);
			} else if (query.getQueryType() == 3) {// 委托订单
				columnDescList = deputeDescList();
				PageInfo<FutresOrderEntrustDto> response = business.pagesOrderEntrust(query);
				fileName = "委托记录_" + String.valueOf(System.currentTimeMillis());
				file = File.createTempFile(fileName, ".xls");
				result = dataListEntrust(response.getContent(), query.getQueryType());
				PoiUtil.writeDataToExcel("委托记录数据", file, columnDescList, result);
			} else if (query.getQueryType() == 4) {// 退款订单
				columnDescList = accountantList();
				PageInfo<FutresOrderEntrustDto> response = business.pagesOrderEntrust(query);
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

	private List<List<String>> dataList(List<FuturesOrderAdminDto> content, Integer type) {
		List<List<String>> result = new ArrayList<List<String>>();
		if (content == null) {
			return result;
		}
		for (FuturesOrderAdminDto dto : content) {
			List<String> data = new ArrayList<String>();
			String buyingPriceType = "";
			String positionEndTime = "";
			String positionCJTime = "";
			if (dto.getBuyingPriceType() != null) {
				if (dto.getBuyingPriceType().getIndex().equals("1")) {
					buyingPriceType = "市价单";
					positionEndTime = dto.getBuyingTime() == null ? "" : sdf.format(dto.getBuyingTime());
					positionCJTime = dto.getSellingTime() == null ? "" : sdf.format(dto.getSellingTime());
				} else {
					buyingPriceType = "指定价单";
					positionEndTime = dto.getBuyingEntrustTime() == null ? "" : sdf.format(dto.getBuyingEntrustTime());
					positionCJTime = dto.getSellingEntrustTime() == null ? "" : sdf.format(dto.getSellingEntrustTime());
				}
			}
			if (type == 0) {
				data.add(dto.getPublisherName() == null ? "" : dto.getPublisherName());
				data.add(dto.getPublisherPhone() == null ? "" : dto.getPublisherPhone());
				data.add(dto.getSymbol() == null ? "" : dto.getSymbol());
				data.add(dto.getName() == null ? "" : dto.getName()+"/"+dto.getContractNo());
				data.add(dto.getTradeNo() == null ? "" : dto.getTradeNo());
				data.add(dto.getOrderType() == null ? "" : dto.getOrderType());
				data.add(dto.getState() == null ? "" : dto.getState());
				data.add(dto.getTotalQuantity() == null ? "" : dto.getTotalQuantity().toString());
				data.add(dto.getBuyingTime() == null ? "" : sdf.format(dto.getBuyingTime()));
				data.add(dto.getBuyingPrice() == null ? "" : dto.getBuyingPrice().toString());
				data.add(dto.getLastPrice() == null ? "" : dto.getLastPrice().toString());
				data.add(dto.getProfit() == null ? "" : dto.getProfit().toString());
				data.add(buyingPriceType);
				data.add(String.valueOf(dto.getOpenwindServiceFee().add(dto.getUnwindServiceFee()).multiply(dto.getTotalQuantity())));
				data.add(dto.getReserveFund() == null ? "" : dto.getReserveFund().toString());
				data.add(dto.getOvernightServiceFee() == null ? ""
						: dto.getOvernightServiceFee().toString());
				data.add(dto.getOvernightReserveFund() == null ? ""
						: dto.getOvernightReserveFund().toString());
				data.add(dto.getPerUnitLimitLossAmount() == null ? "" : dto.getPerUnitLimitLossAmount().toString());
				data.add(dto.getPerUnitLimitProfitAmount() == null ? "" : dto.getPerUnitLimitProfitAmount().toString());
				data.add(dto.getPositionDays() == null ? "" : dto.getPositionDays().toString());
				data.add(positionCJTime);
				data.add(dto.getSellingTime() == null ? "" : sdf.format(dto.getSellingTime()));
				data.add(dto.getSellingPrice() == null ? "" : dto.getSellingPrice().toString());
				data.add(dto.getSellingProfit() == null ? "" : dto.getSellingProfit().toString());
				data.add(dto.getUnwindServiceFee() == null ? "" : dto.getUnwindServiceFee().toString());
				data.add(dto.getWindControlType() == null ? "" : dto.getWindControlType());

			} else if (type == 1) {
				data.add(dto.getPublisherName() == null ? "" : dto.getPublisherName());
				data.add(dto.getPublisherPhone() == null ? "" : dto.getPublisherPhone());
				data.add(dto.getSymbol() == null ? "" : dto.getSymbol());
				data.add(dto.getName() == null ? "" : dto.getName()+"/"+dto.getContractNo());
				data.add(dto.getTradeNo() == null ? "" : dto.getTradeNo());
				data.add(dto.getOrderType() == null ? "" : dto.getOrderType());
				data.add(dto.getState() == null ? "" : dto.getState());
				data.add(dto.getTotalQuantity() == null ? "" : dto.getTotalQuantity().toString());
				data.add(dto.getBuyingPrice() == null ? "" : dto.getBuyingPrice().toString());
				data.add(dto.getLastPrice() == null ? "" : dto.getLastPrice().toString());
				data.add(dto.getProfit() == null ? "" : dto.getProfit().toString());
				data.add(buyingPriceType);
				data.add(String.valueOf(dto.getOpenwindServiceFee().add(dto.getUnwindServiceFee()).multiply(dto.getTotalQuantity())));
				data.add(dto.getReserveFund() == null ? "" : dto.getReserveFund().toString());
				data.add(dto.getPerUnitLimitLossAmount() == null ? "" : dto.getPerUnitLimitLossAmount().toString());
				data.add(dto.getPerUnitLimitProfitAmount() == null ? "" : dto.getPerUnitLimitProfitAmount().toString());
				data.add(dto.getOvernightServiceFee() == null ? ""
						: dto.getOvernightServiceFee().toString());
				data.add(dto.getOvernightReserveFund() == null ? ""
						: dto.getOvernightReserveFund().toString());
				data.add(positionEndTime);
				data.add(dto.getPositionDays() == null ? "" : dto.getPositionDays().toString());
				data.add(dto.getWindControlType() == null ? "" : dto.getWindControlType());
			} else if (type == 2) {
				data.add(dto.getPublisherName() == null ? "" : dto.getPublisherName());
				data.add(dto.getPublisherPhone() == null ? "" : dto.getPublisherPhone());
				data.add(dto.getSymbol() == null ? "" : dto.getSymbol());
				data.add(dto.getName() == null ? "" : dto.getName()+"/"+dto.getContractNo());
				data.add(dto.getTradeNo() == null ? "" : dto.getTradeNo());
				data.add(dto.getOrderType() == null ? "" : dto.getOrderType());
				data.add(dto.getState() == null ? "" : dto.getState());
				data.add(dto.getTotalQuantity() == null ? "" : dto.getTotalQuantity().toString());
				data.add(dto.getBuyingTime() == null ? "" : sdf.format(dto.getBuyingTime()));
				data.add(dto.getBuyingPrice() == null ? "" : dto.getBuyingPrice().toString());
				data.add(dto.getSellingTime() == null ? "" : sdf.format(dto.getSellingTime()));
				data.add(dto.getSellingPrice() == null ? "" : dto.getSellingPrice().toString());
				data.add(dto.getSellingProfit() == null ? "" : dto.getSellingProfit().toString());
				data.add(dto.getWindControlType() == null ? "" : dto.getWindControlType());

			}
			result.add(data);
		}

		return result;
	}

	private List<List<String>> dataListEntrust(List<FutresOrderEntrustDto> content, Integer type) {
		List<List<String>> result = new ArrayList<List<String>>();
		for (FutresOrderEntrustDto dto : content) {
			List<String> data = new ArrayList<String>();
			String kaiping = "";
			if (dto.getState() != null) {
				if (dto.getState() != FuturesOrderState.SellingEntrust.getType()&& dto.getState() != FuturesOrderState.PartUnwind.getType()) {
					kaiping = "开仓";
				}else{
					kaiping = "平仓";
				}
				
			}
			String buyingPriceType = "";
			if (dto.getBuyingPriceType() != null) {
				if (dto.getBuyingPriceType().getIndex().equals("1")) {
					buyingPriceType = "市价单";
				} else {
					buyingPriceType = "指定价单";
				}
			}
			if (type == 3) {
				data.add(dto.getPublisherName() == null ? "" : dto.getPublisherName());
				data.add(dto.getPublisherPhone() == null ? "" : dto.getPublisherPhone());
				data.add(dto.getSymbol() == null ? "" : dto.getSymbol());
				data.add(dto.getName() == null ? "" : dto.getName()+"/"+dto.getContractNo());
				data.add(dto.getTradeNo() == null ? "" : dto.getTradeNo());
				data.add(dto.getOrderType() == null ? "" : dto.getOrderType());
				data.add(dto.getState() == null ? "" : dto.getState());
				data.add(kaiping);
				data.add(dto.getEntrustAppointPrice() == null ? "" : dto.getEntrustAppointPrice().toString());
				data.add(dto.getTotalQuantity() == null ? "" : dto.getTotalQuantity().toString());
				if(dto.getPostTime()!=null){
					data.add(dto.getTotalQuantity() == null ? "" : dto.getTotalQuantity().toString());
				}
				data.add(dto.getBuyingPrice() == null ? "" : dto.getBuyingPrice().toString());
				data.add(buyingPriceType);
				data.add(dto.getPerUnitLimitLossAmount() == null ? "" : dto.getPerUnitLimitLossAmount().toString());
				data.add(dto.getPerUnitLimitProfitAmount() == null ? "" : dto.getPerUnitLimitProfitAmount().toString());
				data.add(dto.getReserveFund() == null ? "" : dto.getReserveFund().multiply(dto.getTotalQuantity()).toString());
				data.add(String.valueOf(dto.getOpenwindServiceFee().add(dto.getUnwindServiceFee()).multiply(dto.getTotalQuantity())));
				data.add(dto.getPostTime() == null ? "" : sdf.format(dto.getPostTime()));
				data.add(dto.getDealTime() == null ? "" : sdf.format(dto.getDealTime()));
			} else if (type == 4) {
				data.add(dto.getPublisherName() == null ? "" : dto.getPublisherName());
				data.add(dto.getPublisherPhone() == null ? "" : dto.getPublisherPhone());
				data.add(dto.getSymbol() == null ? "" : dto.getSymbol());
				data.add(dto.getName() == null ? "" : dto.getName()+"/"+dto.getContractNo());
				data.add(dto.getTradeNo() == null ? "" : dto.getTradeNo());
				data.add(dto.getOrderType() == null ? "" : dto.getOrderType());
				data.add(dto.getState() == null ? "" : dto.getState());
				data.add(kaiping);
				data.add(dto.getTotalQuantity() == null ? "" : dto.getTotalQuantity().toString());
				data.add(dto.getEntrustPrice() == null ? "" : dto.getEntrustPrice().toString());
				data.add(buyingPriceType);
				data.add(dto.getPerUnitLimitLossAmount() == null ? "" : dto.getPerUnitLimitLossAmount().toString());
				data.add(dto.getPerUnitLimitProfitAmount() == null ? "" : dto.getPerUnitLimitProfitAmount().toString());
				data.add(dto.getReserveFund() == null ? "" : dto.getReserveFund().multiply(dto.getTotalQuantity()).toString());
				data.add(String.valueOf(dto.getOpenwindServiceFee().add(dto.getUnwindServiceFee()).multiply(dto.getTotalQuantity())));
				data.add(dto.getPostTime() == null ? "" : sdf.format(dto.getPostTime()));
				data.add(dto.getState() == null ? "" : dto.getState());
			}
			result.add(data);
		}
		return result;
	}

	// 成交订单
	private List<String> columnDescList() {
		List<String> result = new ArrayList<>();
		result.add("客户姓名");
		result.add("交易账户");
		result.add("合约代码");
		result.add("交易品种");
		result.add("订单编号");
		result.add("交易方向");
		result.add("交易状态");
		result.add("成交手数");
		result.add("买入时间");
		result.add("买入价格");
		result.add("当前价格");
		result.add("浮动盈亏");
		result.add("定单类型");
		result.add("交易综合费");
		result.add("保证金");
		result.add("隔夜手续费");
		result.add("隔夜保证金");
		result.add("止损金额");
		result.add("止盈金额");
		result.add("持仓小时");
		result.add("持仓截止日期");
		result.add("平仓时间");
		result.add("平仓价格");
		result.add("平仓盈亏");
		result.add("平仓手续费");
		result.add("风控状态");
		return result;
	}

	// 持仓中订单
	private List<String> positionDescList() {
		List<String> result = new ArrayList<>();
		result.add("客户姓名");
		result.add("交易账户");
		result.add("合约代码");
		result.add("交易品种");
		result.add("订单编号");
		result.add("交易方向");
		result.add("交易状态");
		result.add("成交手数");
		result.add("成交价格");
		result.add("当前价格");
		result.add("浮动盈亏");
		result.add("定单类型");
		result.add("交易综合费");
		result.add("保证金");
		result.add("止损金额");
		result.add("止盈金额");
		result.add("隔夜手续费");
		result.add("隔夜保证金");
		result.add("持仓截止日期");
		result.add("持仓小时");
		result.add("风控状态");
		return result;
	}

	// 平仓结算记录
	private List<String> eveningDescList() {
		List<String> result = new ArrayList<>();
		result.add("客户姓名");
		result.add("交易账户");
		result.add("合约代码");
		result.add("交易品种");
		result.add("订单编号");
		result.add("交易方向");
		result.add("交易状态");
		result.add("成交手数");
		result.add("买入时间");
		result.add("买入价格");
		result.add("平仓时间");
		result.add("平仓价格");
		result.add("平仓盈亏");
		result.add("平仓类型");
		return result;
	}

	// 委托记录
	private List<String> deputeDescList() {
		List<String> result = new ArrayList<>();
		result.add("客户姓名");
		result.add("交易账户");
		result.add("合约代码");
		result.add("交易品种");
		result.add("委托编号");
		result.add("委托方向");
		result.add("委托状态");
		result.add("开平");
		result.add("委托价格");
		result.add("委托手数");
		result.add("成交手数");
		result.add("成交价");
		result.add("定单类型");
		result.add("止损金额");
		result.add("止盈金额");
		result.add("保证金");
		result.add("交易综合费");
		result.add("委托时间");
		result.add("成交时间");
		return result;
	}

	// 委托/退款记录
	private List<String> accountantList() {
		List<String> result = new ArrayList<>();
		result.add("客户姓名");
		result.add("交易账户");
		result.add("合约代码");
		result.add("交易品种");
		result.add("委托编号");
		result.add("委托方向");
		result.add("委托状态");
		result.add("开平");
		result.add("委托手数");
		result.add("委托价");
		result.add("定单类型");
		result.add("止损金额");
		result.add("止盈金额");
		result.add("保证金");
		result.add("交易综合费");
		result.add("委托时间");
		result.add("状态");
		return result;
	}
}
