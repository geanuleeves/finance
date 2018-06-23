package com.waben.stock.interfaces.commonapi.retrivefutures;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.client.RestTemplate;

import com.waben.stock.interfaces.commonapi.retrivefutures.bean.FuturesContractLineData;
import com.waben.stock.interfaces.commonapi.retrivefutures.bean.FuturesContractMarket;
import com.waben.stock.interfaces.commonapi.retrivefutures.bean.FuturesGatewayContract;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.util.JacksonUtil;
import com.waben.stock.interfaces.util.StringUtil;

public class RetriveFuturesOverHttp {

	private static RestTemplate restTemplate = new RestTemplate();
	/**
	 * 盈透api基础路径
	 */
	private static String yingtouBaseUrl = "http://10.0.0.48:9092/";
	/**
	 * 易盛api基础路径
	 */
	private static String yishengBaseUrl = "http://47.75.55.10:9093/";
	/**
	 * api类型
	 * <ul>
	 * <li>1盈透</li>
	 * <li>2易盛</li>
	 * </ul>
	 */
	private static Integer apiType = 2;

	private static String getBaseUrl() {
		if (apiType == 1) {
			return yingtouBaseUrl;
		} else if (apiType == 2) {
			return yishengBaseUrl;
		}
		return "";
	}

	public static List<String> enableContractNo(String commodityNo) {
		String url = getBaseUrl() + "futuresContract/" + commodityNo + "/enableContractNo";
		String response = restTemplate.getForObject(url, String.class);
		Response<List<String>> responseObj = JacksonUtil.decode(response,
				JacksonUtil.getGenericType(Response.class, JacksonUtil.getGenericType(ArrayList.class, String.class)));
		if ("200".equals(responseObj.getCode())) {
			return responseObj.getResult();
		} else {
			throw new RuntimeException("http根据品种编号获取可用的合约编号列表异常!" + responseObj.getCode());
		}
	}

	public static FuturesGatewayContract fetchByCommodityNoAndContractNo(String commodityNo, String contractNo) {
		String url = getBaseUrl() + "futuresContract/" + commodityNo + "/" + contractNo;
		String response = restTemplate.getForObject(url, String.class);
		Response<FuturesGatewayContract> responseObj = JacksonUtil.decode(response,
				JacksonUtil.getGenericType(Response.class, FuturesGatewayContract.class));
		if ("200".equals(responseObj.getCode())) {
			return responseObj.getResult();
		} else {
			throw new RuntimeException("http根据品种编号和合约编号获取合约异常!" + responseObj.getCode());
		}
	}

	public static FuturesContractMarket market(String commodityNo, String contractNo) {
		String url = getBaseUrl() + "market/" + commodityNo + "/" + contractNo;
		String response = restTemplate.getForObject(url, String.class);
		Response<FuturesContractMarket> responseObj = JacksonUtil.decode(response,
				JacksonUtil.getGenericType(Response.class, FuturesContractMarket.class));
		if ("200".equals(responseObj.getCode())) {
			return responseObj.getResult();
		} else {
			throw new RuntimeException("http获取期货行情异常!" + responseObj.getCode());
		}
	}

	public static List<FuturesContractLineData> timeLine(String commodityNo, String contractNo) {
		String url = getBaseUrl() + "market/" + commodityNo + "/" + contractNo + "/minsline";
		String response = restTemplate.getForObject(url, String.class);
		Response<List<FuturesContractLineData>> responseObj = JacksonUtil.decode(response, JacksonUtil
				.getGenericType(Response.class, JacksonUtil.getGenericType(List.class, FuturesContractLineData.class)));
		if ("200".equals(responseObj.getCode())) {
			return responseObj.getResult();
		} else {
			throw new RuntimeException("http获取期分时图数据异常!" + responseObj.getCode());
		}
	}

	public static List<FuturesContractLineData> dayLine(String commodityNo, String contractNo, String startTime,
			String endTime) {
		String url = getBaseUrl() + "market/" + commodityNo + "/" + contractNo + "/dayline?";
		if (!StringUtil.isEmpty(startTime)) {
			url += "&startTime=" + startTime;
		}
		if (!StringUtil.isEmpty(endTime)) {
			url += "&endTime=" + endTime;
		}
		String response = restTemplate.getForObject(url, String.class);
		Response<List<FuturesContractLineData>> responseObj = JacksonUtil.decode(response, JacksonUtil
				.getGenericType(Response.class, JacksonUtil.getGenericType(List.class, FuturesContractLineData.class)));
		if ("200".equals(responseObj.getCode())) {
			return responseObj.getResult();
		} else {
			throw new RuntimeException("http获取期日K线图数据异常!" + responseObj.getCode());
		}
	}

	public static List<FuturesContractLineData> minsLine(String commodityNo, String contractNo, Integer mins) {
		String url = getBaseUrl() + "market/" + commodityNo + "/" + contractNo + "/minsline";
		String response = restTemplate.getForObject(url, String.class);
		Response<List<FuturesContractLineData>> responseObj = JacksonUtil.decode(response, JacksonUtil
				.getGenericType(Response.class, JacksonUtil.getGenericType(List.class, FuturesContractLineData.class)));
		if ("200".equals(responseObj.getCode())) {
			return responseObj.getResult();
		} else {
			throw new RuntimeException("http获取期分钟K线图数据异常!" + responseObj.getCode());
		}
	}

	public static void main(String[] args) {
		System.out.println(enableContractNo("GC"));
		System.out.println(fetchByCommodityNoAndContractNo("GC", "1808"));
	}

}
