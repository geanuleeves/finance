package com.waben.stock.applayer.promotion.controller;

import com.waben.stock.applayer.promotion.business.futures.FuturesTradeBusiness;
import com.waben.stock.applayer.promotion.security.SecurityUtil;
import com.waben.stock.applayer.promotion.util.PoiUtil;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.admin.futures.*;
import com.waben.stock.interfaces.dto.organization.FuturesFowDto;
import com.waben.stock.interfaces.enums.CapitalFlowType;
import com.waben.stock.interfaces.enums.FuturesOrderState;
import com.waben.stock.interfaces.enums.FuturesOrderType;
import com.waben.stock.interfaces.enums.FuturesTradeActionType;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesTradeAdminQuery;
import com.waben.stock.interfaces.pojo.query.organization.FuturesFowQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/futures")
@Api(description = "代理商接口列表")
public class FuturesTradeController {

	@Autowired
	private FuturesTradeBusiness business;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@RequestMapping(value = "/FuturesFow/", method = RequestMethod.GET)
	@ApiOperation(value = "查询期货交易流水")
	public Response<PageInfo<FuturesFowDto>> futuresFowPageByQuery(FuturesFowQuery query) {
		query.setCurrentOrgId(SecurityUtil.getUserDetails().getOrgId());
		query.setTreeCode(SecurityUtil.getUserDetails().getTreeCode());
		PageInfo<FuturesFowDto> result = business.futuresFowPageByQuery(query);
		return new Response<>(result);
	}

	@RequestMapping(value = "/organizationOrder/pages", method = RequestMethod.POST)
	@ApiOperation(value = "查询订单")
	public Response<PageInfo<AgentOrderRecordDto>> pagesOrganizationOrder(FuturesTradeAdminQuery query) {
		query.setTreeCode(SecurityUtil.getUserDetails().getTreeCode());
		return new Response<>(business.pagesOrderRecords(query));
	}

	@RequestMapping(value = "/organizationOrder/pagesEntrust", method = RequestMethod.POST)
	@ApiOperation(value = "查询委托订单")
	public Response<PageInfo<AgentOrderRecordDto>> pagesOrganizationEntrustOrder(FuturesTradeAdminQuery query) {
		query.setTreeCode(SecurityUtil.getUserDetails().getTreeCode());
		return new Response<>(business.pagesOrderRecords(query));
	}

	@GetMapping("/organizationOrder/countOrderState")
	@ApiOperation(value = "订单总计")
	public Response<FuturesOrderCountDto> countOrderState(FuturesTradeAdminQuery query) {
		return business.countOrderState(query);
	}

	@RequestMapping(value = "/futures/trade/action/agent", method = RequestMethod.GET)
	@ApiOperation(value = "获取成交记录和平仓记录")
	public Response<PageInfo<FuturesTradeActionAgentDto>> pagesOrderAgentDealRecord(FuturesTradeAdminQuery query) {
		query.setTreeCode(SecurityUtil.getUserDetails().getTreeCode());
		return new Response<>(business.pagesOrderAgentDealRecord(query));
	}

	@RequestMapping(value = "/futures/hoding/order/agent", method = RequestMethod.GET)
	@ApiOperation(value = "获取持仓订单记录")
	public Response<PageInfo<FuturesHoldPositionAgentDto>> pagesHoldingOrderAgent(FuturesTradeAdminQuery query) {
		query.setPublisherIds(business.getPublisherId(SecurityUtil.getUserDetails().getTreeCode()));
		return new Response<>(business.pagesHoldingOrderAgent(query));
	}

	@RequestMapping(value = "/futures/hoding/order/entruts", method = RequestMethod.GET)
	@ApiOperation(value = "获取委托订单记录")
	public Response<PageInfo<FuturesTradeDto>> pageTradeEnturs(FuturesTradeAdminQuery query) {
		query.setTreeCode(SecurityUtil.getUserDetails().getTreeCode());
		return new Response<>(business.pageTradeEnturs(query));
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
				PageInfo<FuturesTradeActionAgentDto> response = business.pagesOrderAgentDealRecord(query);
				fileName = "成交订单_" + String.valueOf(System.currentTimeMillis());
				file = File.createTempFile(fileName, ".xls");
				columnDescList = columnDescList();
				result = dataList(response.getContent(), query.getQueryType());
				PoiUtil.writeDataToExcel("成交数据", file, columnDescList, result);
			} else if (query.getQueryType() == 1) {// 持仓中订单
				columnDescList = positionDescList();
				PageInfo<FuturesHoldPositionAgentDto> response = business.pagesHoldingOrderAgent(query);
				fileName = "开仓订单_" + String.valueOf(System.currentTimeMillis());
				file = File.createTempFile(fileName, ".xls");
				result = dataHoldPositionList(response.getContent());
				PoiUtil.writeDataToExcel("开仓订单数据", file, columnDescList, result);
			} else if (query.getQueryType() == 2) {// 平仓订单
				columnDescList = eveningDescList();
				PageInfo<FuturesTradeActionAgentDto> response = business.pagesOrderAgentDealRecord(query);
				fileName = "平仓结算订单_" + String.valueOf(System.currentTimeMillis());
				file = File.createTempFile(fileName, ".xls");
				result = dataList(response.getContent(), query.getQueryType());
				PoiUtil.writeDataToExcel("平仓结算订单数据", file, columnDescList, result);
			} else if (query.getQueryType() == 3) {// 委托订单
				columnDescList = deputeDescList();
				PageInfo<FuturesTradeDto> response = business.pageTradeEnturs(query);
				fileName = "委托记录_" + String.valueOf(System.currentTimeMillis());
				file = File.createTempFile(fileName, ".xls");
				result = dataListEntrust(response.getContent(), query.getQueryType());
				PoiUtil.writeDataToExcel("委托记录数据", file, columnDescList, result);
			} else if (query.getQueryType() == 4) {// 退款订单
				columnDescList = accountantList();
				PageInfo<FuturesTradeDto> response = business.pageTradeEnturs(query);
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

	private List<List<String>> dataList(List<FuturesTradeActionAgentDto> content, Integer type) {
		List<List<String>> result = new ArrayList<List<String>>();
		if (content == null) {
			return result;
		}
		for (FuturesTradeActionAgentDto dto : content) {
			List<String> data = new ArrayList<String>();
			String orderType = "";
			if (dto.getOrderType() != null) {
				orderType = FuturesOrderType.getByIndex(dto.getOrderType().toString()).getType();
			}
			String tradeActionType = "";
			if (dto.getTradeActionType() != null) {
				tradeActionType = FuturesOrderState.getByIndex(dto.getTradeActionType().toString()).getType();
			}
			String priceType = "";
			if (dto.getPriceType() != null) {
				tradeActionType = FuturesOrderState.getByIndex(dto.getPriceType().toString()).getType();
			}
			String windControlType = "";
			if (dto.getWindControlType() != null) {
				tradeActionType = FuturesOrderState.getByIndex(dto.getWindControlType().toString()).getType();
			}

			if (type == 0) {
				data.add(dto.getPublisherName() == null ? "" : dto.getPublisherName());
				data.add(dto.getPublisherPhone() == null ? "" : dto.getPublisherPhone());
				data.add(dto.getCommoditySymbol() == null ? "" : dto.getCommoditySymbol());
				data.add(dto.getCommodityName() + "/" + dto.getContractNo());
				data.add(orderType);
				data.add(tradeActionType);
				data.add(String.valueOf(dto.getFilled() == null ? "" : dto.getFilled()));
				data.add(String.valueOf(dto.getTradePrice() == null ? "" : dto.getTradePrice()));
				data.add(String.valueOf(dto.getPublisherProfitOrLoss() == null ? "" : dto.getPublisherProfitOrLoss()));
				data.add(dto.getActionNo() == null ? "" : dto.getActionNo());
				data.add(String.valueOf(dto.getTradeTime() == null ? "" : dto.getTradeTime()));
				data.add("投机");
				data.add(priceType);
				data.add(windControlType);
				data.add(dto.getCode() + "/" + dto.getOrgName());
			}
			if (type == 2) {
				data.add(dto.getPublisherName() == null ? "" : dto.getPublisherName());
				data.add(dto.getPublisherPhone() == null ? "" : dto.getPublisherPhone());
				data.add(dto.getCommoditySymbol() == null ? "" : dto.getCommoditySymbol());
				data.add(dto.getCommodityName() + "/" + dto.getContractNo());
				data.add(orderType);
				data.add(tradeActionType);
				data.add(String.valueOf(dto.getFilled() == null ? "" : dto.getFilled()));
				data.add(String.valueOf(dto.getTradePrice() == null ? "" : dto.getTradePrice()));
				data.add(String.valueOf(dto.getPublisherProfitOrLoss() == null ? "" : dto.getPublisherProfitOrLoss()));
				data.add(dto.getActionNo() == null ? "" : dto.getActionNo());
				data.add(String.valueOf(dto.getTradeTime() == null ? "" : dto.getTradeTime()));
				data.add("投机");
				data.add(priceType);
				data.add(windControlType);
				data.add(dto.getCode() + "/" + dto.getOrgName());
			}
			result.add(data);
		}

		return result;
	}

	private List<List<String>> dataListEntrust(List<FuturesTradeDto> content, Integer type) {
		List<List<String>> result = new ArrayList<List<String>>();
		for (FuturesTradeDto dto : content) {
			List<String> data = new ArrayList<String>();
			String orderType = "";
			if (dto.getOrderType() != null) {
				orderType = FuturesOrderType.getByIndex(dto.getOrderType().toString()).getType();
			}
			String state = "";
			if (dto.getState() != null) {
				state = FuturesOrderState.getByIndex(dto.getState().toString()).getType();
			}
			String tradeActionType = "";
			if (dto.getTradeActionType() != null) {
				tradeActionType = FuturesTradeActionType.getByIndex(dto.getTradeActionType().toString()).getType();
			}
			if (type == 3 || type == 4) {
				data.add(dto.getPublisherName() == null ? "" : dto.getPublisherName());
				data.add(dto.getPublisherPhone() == null ? "" : dto.getPublisherPhone());
				data.add(dto.getSymbol() == null ? "" : dto.getSymbol());
				data.add(dto.getName() + "/" + dto.getContractNo());
				data.add(orderType);
				data.add(state);
				data.add(tradeActionType);
				data.add(String.valueOf(dto.getEntrustPrice() == null ? "" : dto.getEntrustPrice()));
				data.add(String.valueOf(dto.getQuantity() == null ? "" : dto.getQuantity()));
				data.add(String.valueOf(dto.getFilled() == null ? "" : dto.getFilled()));
				data.add(String.valueOf(dto.getTradePrice() == null ? "" : dto.getTradePrice()));
				data.add(String.valueOf(dto.getEntrustTime() == null ? "" : dto.getEntrustTime()));
				data.add(String.valueOf(dto.getTradeTime() == null ? "" : dto.getTradeTime()));
				data.add(dto.getEntrustNo() == null ? "" : dto.getEntrustNo());
				data.add(dto.getCode() + "/" + dto.getOrgName());
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
			data.add(trade.getCommodityName() == null ? "" : trade.getCommodityName() + "/" + trade.getContractNo());
			data.add(trade.getAgentCode() + "/" + trade.getAgentCodeName());
			result.add(data);
		}
		return result;
	}

	// 开仓订单
	private List<List<String>> dataHoldPositionList(List<FuturesHoldPositionAgentDto> content) {
		List<List<String>> result = new ArrayList<>();
		for (FuturesHoldPositionAgentDto trade : content) {
			List<String> data = new ArrayList<>();
			String orderType = "";
			String totalHolding = "";
			if (trade.getOrderType() != null) {
				orderType = trade.getOrderType().getType();
				if (trade.getOrderType().equals(FuturesOrderType.BuyUp)) {
					totalHolding = trade.getBuyUpTotalQuantity().toString();
				} else {
					totalHolding = trade.getBuyFallTotalQuantity().toString();
				}
			}
			data.add(trade.getPublisherName() == null ? "" : trade.getPublisherName());
			data.add(trade.getPublisherPhone() == null ? "" : trade.getPublisherPhone());
			data.add(trade.getCommoditySymbol() == null ? "" : trade.getCommoditySymbol());
			data.add(trade.getCommodityName() + "/" + trade.getContractNo());
			data.add(trade.getCode() + "/" + trade.getOrgName());
			data.add(orderType);
			data.add(totalHolding);
			data.add(String.valueOf(trade.getQuantityNow() == null ? "" : trade.getQuantityNow()));
			data.add(String.valueOf(trade.getQuantityNow() == null ? "" : trade.getQuantityNow()));
			data.add(String.valueOf(trade.getAvgFillPrice()));
			data.add(String.valueOf(trade.getLastPrice() == null ? "" : trade.getLastPrice()));
			data.add(String.valueOf(trade.getFloatingProfitAndLoss() == null ? "" : trade.getFloatingProfitAndLoss()));
			data.add(
					String.valueOf(trade.getPerUnitLimitLossAmount() == null ? "" : trade.getPerUnitLimitLossAmount()));
			data.add(String
					.valueOf(trade.getPerUnitLimitProfitAmount() == null ? "" : trade.getPerUnitLimitProfitAmount()));
			data.add(String.valueOf(trade.getServiceFee() == null ? "" : trade.getServiceFee()));
			data.add(String.valueOf(trade.getReserveFund() == null ? "" : trade.getReserveFund()));
			data.add(String.valueOf(trade.getDeferredFee() == null ? "" : trade.getDeferredFee()));
			data.add("投保");
			data.add(trade.getCode() + "/" + trade.getOrgName());
			result.add(data);
		}
		return result;
	}

	// 成交订单
	private List<String> columnDescList() {
		List<String> result = new ArrayList<>();
		result.add("客户姓名");
		result.add("交易账户");
		result.add("交易代码");
		result.add("交易品种");
		result.add("交易方向");
		result.add("开平");
		result.add("成交手数");
		result.add("成交价格");
		result.add("成交盈亏");
		result.add("成交编号");
		result.add("成交时间");
		result.add("投保");
		result.add("定单类型");
		result.add("平仓类型");
		result.add("所属代理商代码/名称");
		return result;
	}

	// 持仓中订单
	private List<String> positionDescList() {
		List<String> result = new ArrayList<>();
		result.add("客户姓名");
		result.add("交易账户");
		result.add("交易代码");
		result.add("交易品种");
		result.add("交易方向");
		result.add("总持仓");
		result.add("今持仓");
		result.add("可平仓");
		result.add("成交价格");
		result.add("当前价格");
		result.add("浮动盈亏");
		result.add("止损金额");
		result.add("止盈金额");
		result.add("交易综合费");
		result.add("保证金");
		result.add("递延费");
		result.add("投保");
		result.add("所属代理商代码/名称");
		return result;
	}

	// 平仓结算记录
	private List<String> eveningDescList() {
		List<String> result = new ArrayList<>();
		result.add("客户姓名");
		result.add("交易账户");
		result.add("交易代码");
		result.add("交易品种");
		result.add("交易方向");
		result.add("开平");
		result.add("成交手数");
		result.add("成交价格");
		result.add("成交盈亏");
		result.add("成交编号");
		result.add("成交时间");
		result.add("投保");
		result.add("定单类型");
		result.add("平仓类型");
		result.add("所属代理商代码/名称");
		return result;
	}

	// 委托记录
	private List<String> deputeDescList() {
		List<String> result = new ArrayList<>();
		result.add("客户姓名");
		result.add("交易账户");
		result.add("交易代码");
		result.add("交易品种");
		result.add("买卖");
		result.add("委托状态");
		result.add("开平");
		result.add("委托价格");
		result.add("委托手数");
		result.add("成交手数");
		result.add("成交价格");
		result.add("委托时间");
		result.add("成交时间");
		result.add("委托编号");
		result.add("所属代理商代码/名称");
		return result;
	}

	// 委托/退款记录
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

	// 交易流水
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
