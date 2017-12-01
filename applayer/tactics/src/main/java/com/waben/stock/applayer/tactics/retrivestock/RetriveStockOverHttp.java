package com.waben.stock.applayer.tactics.retrivestock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.waben.stock.applayer.tactics.retrivestock.bean.StockExponentVariety;
import com.waben.stock.applayer.tactics.retrivestock.bean.StockMarket;
import com.waben.stock.interfaces.util.JacksonUtil;

public class RetriveStockOverHttp {

	public static List<StockMarket> listStockMarket(RestTemplate restTemplate, List<String> codes) {
		String url = "http://lemi.esongbai.com/stk/stk/list.do?codes="
				+ codes.toString().substring(1, codes.toString().length() - 1).replaceAll(" ", "");
		String response = restTemplate.getForObject(url, String.class);
		try {
			JsonNode dataNode = JacksonUtil.objectMapper.readValue(response, JsonNode.class).get("data");
			JavaType javaType = JacksonUtil.objectMapper.getTypeFactory().constructParametricType(ArrayList.class,
					StockMarket.class);
			List<StockMarket> list = JacksonUtil.objectMapper.readValue(dataNode.toString(), javaType);
			return list;
		} catch (IOException e) {
			throw new RuntimeException("http获取股票行情异常!", e);
		}
	}

	public static List<StockExponentVariety> listStockExponentVariety(RestTemplate restTemplate) {
		String url = "http://lemi.esongbai.com/order/order/getStockExponentVariety.do";
		String response = restTemplate.getForObject(url, String.class);
		try {
			JsonNode dataNode = JacksonUtil.objectMapper.readValue(response, JsonNode.class).get("data");
			JavaType javaType = JacksonUtil.objectMapper.getTypeFactory().constructParametricType(ArrayList.class,
					StockExponentVariety.class);
			List<StockExponentVariety> list = JacksonUtil.objectMapper.readValue(dataNode.toString(), javaType);
			return list;
		} catch (IOException e) {
			throw new RuntimeException("http获取指数品种列表异常!", e);
		}
	}

	public static void main(String[] args) {
		List<String> codes = new ArrayList<>();
		codes.add("1A0001");
		codes.add("2A01");
		codes.add("399006");
		System.out.println(listStockMarket(new RestTemplate(), codes));
	}

}