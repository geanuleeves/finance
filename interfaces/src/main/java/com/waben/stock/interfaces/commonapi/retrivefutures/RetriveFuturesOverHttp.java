package com.waben.stock.interfaces.commonapi.retrivefutures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	 * 盈透api基础路径（测试）
	 */
	private static String testYingtouBaseUrl = "http://10.0.0.48:9092/";
	/**
	 * 盈透api基础路径（正式）
	 */
	private static String prodYingtouBaseUrl = "http://10.0.0.48:9092/";
	/**
	 * 易盛api基础路径（测试）
	 */
	// private static String testYishengBaseUrl = "http://10.0.0.99:9093/";
	private static String testYishengBaseUrl = "http://47.75.72.30:9093/";
	/**
	 * 易盛api基础路径（正式）
	 */
	private static String prodYishengBaseUrl = "http://47.75.72.30:9093/";
	/**
	 * api类型
	 * <ul>
	 * <li>1盈透</li>
	 * <li>2易盛</li>
	 * </ul>
	 */
	private static Integer apiType = 2;

	private static String getBaseUrl(boolean isProd) {
		if (apiType == 1) {
			return isProd ? prodYingtouBaseUrl : testYingtouBaseUrl;
		} else if (apiType == 2) {
			return isProd ? prodYishengBaseUrl : testYishengBaseUrl;
		}
		return "";
	}

	public static List<String> enableContractNo(boolean isProd, String commodityNo) {
		String url = getBaseUrl(isProd) + "futuresContract/" + commodityNo + "/enableContractNo";
		String response = restTemplate.getForObject(url, String.class);
		Response<List<String>> responseObj = JacksonUtil.decode(response,
				JacksonUtil.getGenericType(Response.class, JacksonUtil.getGenericType(ArrayList.class, String.class)));
		if ("200".equals(responseObj.getCode())) {
			return responseObj.getResult();
		} else {
			throw new RuntimeException("http根据品种编号获取可用的合约编号列表异常!" + responseObj.getCode());
		}
	}

	public static FuturesGatewayContract fetchByCommodityNoAndContractNo(boolean isProd, String commodityNo,
			String contractNo) {
		String url = getBaseUrl(isProd) + "futuresContract/" + commodityNo + "/" + contractNo;
		String response = restTemplate.getForObject(url, String.class);
		Response<FuturesGatewayContract> responseObj = JacksonUtil.decode(response,
				JacksonUtil.getGenericType(Response.class, FuturesGatewayContract.class));
		if ("200".equals(responseObj.getCode())) {
			return responseObj.getResult();
		} else {
			throw new RuntimeException("http根据品种编号和合约编号获取合约异常!" + responseObj.getCode());
		}
	}

	public static FuturesContractMarket market(boolean isProd, String commodityNo, String contractNo) {
		String url = getBaseUrl(isProd) + "market/" + commodityNo + "/" + contractNo;
		String response = restTemplate.getForObject(url, String.class);
		Response<FuturesContractMarket> responseObj = JacksonUtil.decode(response,
				JacksonUtil.getGenericType(Response.class, FuturesContractMarket.class));
		if ("200".equals(responseObj.getCode())) {
			return responseObj.getResult();
		} else {
			throw new RuntimeException("http获取期货行情异常!" + responseObj.getCode());
		}
	}

	public static Map<String, FuturesContractMarket> marketAll(boolean isProd) {
		String url = getBaseUrl(isProd) + "market/all";
		String response = restTemplate.getForObject(url, String.class);
		Response<HashMap<String, FuturesContractMarket>> responseObj = JacksonUtil.decode(response, JacksonUtil.getGenericType(
				Response.class, JacksonUtil.getGenericType(HashMap.class, FuturesContractMarket.class)));
		if ("200".equals(responseObj.getCode())) {
			return responseObj.getResult();
		} else {
			throw new RuntimeException("http获取期货行情异常!" + responseObj.getCode());
		}
	}

	public static List<FuturesContractLineData> timeLine(boolean isProd, String commodityNo, String contractNo) {
		String url = getBaseUrl(isProd) + "market/" + commodityNo + "/" + contractNo + "/minsline?mins=1";
		String response = restTemplate.getForObject(url, String.class);
		Response<List<FuturesContractLineData>> responseObj = JacksonUtil.decode(response, JacksonUtil
				.getGenericType(Response.class, JacksonUtil.getGenericType(List.class, FuturesContractLineData.class)));
		if ("200".equals(responseObj.getCode())) {
			return responseObj.getResult();
		} else {
			throw new RuntimeException("http获取期分时图数据异常!" + responseObj.getCode());
		}
	}

	public static List<FuturesContractLineData> dayLine(boolean isProd, String commodityNo, String contractNo,
			String startTime, String endTime) {
		String url = getBaseUrl(isProd) + "market/" + commodityNo + "/" + contractNo + "/dayline?";
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

	public static List<FuturesContractLineData> minsLine(boolean isProd, String commodityNo, String contractNo,
			Integer mins) {
		String url = getBaseUrl(isProd) + "market/" + commodityNo + "/" + contractNo + "/minsline";
		if (mins == null || mins < 1) {
			mins = 1;
		}
		url += "?mins=" + mins;
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
		System.out.println(enableContractNo(false, "GC"));
		System.out.println(fetchByCommodityNoAndContractNo(false, "GC", "1808"));
	}

}
