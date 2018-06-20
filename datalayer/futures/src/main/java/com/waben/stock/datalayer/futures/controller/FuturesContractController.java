package com.waben.stock.datalayer.futures.controller;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.datalayer.futures.entity.FuturesCommodity;
import com.waben.stock.datalayer.futures.entity.FuturesContract;
import com.waben.stock.datalayer.futures.entity.FuturesCurrencyRate;
import com.waben.stock.datalayer.futures.entity.FuturesExchange;
import com.waben.stock.datalayer.futures.service.FuturesCommodityService;
import com.waben.stock.datalayer.futures.service.FuturesContractService;
import com.waben.stock.datalayer.futures.service.FuturesCurrencyRateService;
import com.waben.stock.datalayer.futures.service.FuturesExchangeService;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.admin.futures.FuturesContractAdminDto;
import com.waben.stock.interfaces.dto.futures.FuturesContractDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesContractAdminQuery;
import com.waben.stock.interfaces.pojo.query.futures.FuturesContractQuery;
import com.waben.stock.interfaces.service.futures.FuturesContractInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import com.waben.stock.interfaces.util.PageToPageInfo;
import com.waben.stock.interfaces.util.StringUtil;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/contract")
@Api(description = "期货合约接口列表")
public class FuturesContractController implements FuturesContractInterface {

	@Autowired
	private FuturesContractService futuresContractService;

	@Autowired
	private FuturesExchangeService exchangeService;

	@Autowired
	private FuturesCurrencyRateService futuresCurrencyRateService;

	@Autowired
	private FuturesCommodityService commodityService;

	private SimpleDateFormat daySdf = new SimpleDateFormat("yyyy-MM-dd");

	private SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	public Response<PageInfo<FuturesContractDto>> pagesContract(@RequestBody FuturesContractQuery contractQuery) {
		Page<FuturesContract> page = futuresContractService.pagesContract(contractQuery);
		PageInfo<FuturesContractDto> result = PageToPageInfo.pageToPageInfo(page, FuturesContractDto.class);
		// 组装分页数据的content
		List<FuturesContractDto> content = new ArrayList<>();
		for (FuturesContract contract : page.getContent()) {
			if (contract.getCommodity() == null) {
				break;
			}
			FuturesContractDto dto = CopyBeanUtils.copyBeanProperties(FuturesContractDto.class, contract.getCommodity(),
					false);
			dto = CopyBeanUtils.copyBeanProperties(contract, dto);
			content.add(dto);
		}
		// 设置部分额外的属性
		for (FuturesContractDto contractDto : content) {
			contractDto.setState(1);
			FuturesExchange exchange = exchangeService.findById(contractDto.getExchangeId());
			if (exchange == null) {
				contractDto.setState(3);
				contractDto.setCurrentTradeTimeDesc("交易所为空异常");
				// break;
			}
			// 获取汇率信息
			FuturesCurrencyRate rate = futuresCurrencyRateService.findByCurrency(contractDto.getCurrency());
			contractDto.setExchangeEnable(exchange.getEnable());
			contractDto.setTimeZoneGap(exchange.getTimeZoneGap());
			contractDto.setRate(rate == null ? new BigDecimal(0) : rate.getRate());
			contractDto.setCurrencyName(rate == null ? "" : rate.getCurrencyName());
			contractDto.setCurrencySign(rate == null ? "" : rate.getCurrencySign());
			// 判断交易所是否可用
			if (contractDto.getExchangeEnable() != null && !contractDto.getExchangeEnable()) {
				contractDto.setState(3);
				contractDto.setCurrentTradeTimeDesc("交易不可用异常");
				// break;
			}
			// 判断合约是否可用
			if (contractDto.getEnable() != null && !contractDto.getEnable()) {
				contractDto.setState(3);
				contractDto.setCurrentTradeTimeDesc("合约不可用异常");
				// break;
			}
			// 判断是否在交易时间段
			Date now = new Date();
			Integer timeZoneGap = contractDto.getTimeZoneGap();
			// 当天交易时间描述
			// contractDto.setCurrentTradeTimeDesc(retriveTradeTimeStrDesc(timeZoneGap,
			// contractDto, now));
			// 转换后的当前时间
			Date exchangeTime = retriveExchangeTime(now, timeZoneGap);
			// 转换后当前时间的明天
			Date nextTime = nextTime(exchangeTime);
			// 获取交易所提供时间
			boolean isTradeTime = false;
			String tradeTime = retriveExchangeTradeTimeStr(timeZoneGap, contractDto, now);
			if (!StringUtil.isEmpty(tradeTime)) {
				contractDto.setAutomaticWarehouseTime(
						timeZoneConversion(timeZoneGap, tradeTime.substring(tradeTime.lastIndexOf("-") + 1)));
				String[] tradeTimeArr = tradeTime.split(",");
				String dayStr = daySdf.format(exchangeTime);
				String fullStr = fullSdf.format(exchangeTime);
				for (String tradeTimeDuration : tradeTimeArr) {
					String[] tradeTimePointArr = tradeTimeDuration.trim().split("-");
					if (fullStr.compareTo(dayStr + " " + tradeTimePointArr[0].trim()) >= 0
							&& fullStr.compareTo(dayStr + " " + tradeTimePointArr[1].trim()) < 0) {
						contractDto.setCurrentHoldingTime(dayStr + " " + tradeTimePointArr[1].trim());
						contractDto.setCurrentTradeTimeDesc(currentTradeTimeDesc(timeZoneGap,
								tradeTimePointArr[0].trim(), tradeTimePointArr[1].trim()));
						contractDto.setNextTradingTime("");
						isTradeTime = true;
						break;
					} else {
						if (fullStr.compareTo(dayStr + " " + tradeTimePointArr[0].trim()) < 0) {
							contractDto.setNextTradingTime(dayStr + " " + tradeTimePointArr[0].trim());
							contractDto.setCurrentTradeTimeDesc(currentTradeTimeDesc(timeZoneGap,
									tradeTimePointArr[0].trim(), tradeTimePointArr[1].trim()));
							break;
						} else {
							String tomorrow = daySdf.format(nextTime);
							String tomorrowHour = getNextTradingTime(exchangeTime, contractDto) == null ? ""
									: getNextTradingTime(exchangeTime, contractDto);
							// 获取转换后的明天时间交易开始时间
							String tomorrowTime = tomorrow + " " + tomorrowHour.split("-")[0];
							contractDto.setNextTradingTime(tomorrowTime);
							contractDto.setCurrentTradeTimeDesc(currentTradeTimeDesc(timeZoneGap,
									tomorrowHour.split("-")[0].trim(), tomorrowHour.split("-")[1].trim()));
						}
					}
				}
				if (isTradeTime) {
					contractDto.setState(1);
				} else {
					contractDto.setState(2);
				}
			}
		}
		result.setContent(content);

		return new Response<>(result);
	}

	/**
	 * 获取交易所的对应时间
	 * 
	 * @param localTime
	 *            日期
	 * @param timeZoneGap
	 *            和交易所的时差
	 * @return 交易所的对应时间
	 */
	private Date retriveExchangeTime(Date localTime, Integer timeZoneGap) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(localTime);
		cal.add(Calendar.HOUR_OF_DAY, timeZoneGap * -1);
		return cal.getTime();
	}

	private Date nextTime(Date localTime) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(localTime);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}

	private String retriveExchangeTradeTimeStr(Integer timeZoneGap, FuturesContractDto contract, Date date) {
		Date exchangeTime = retriveExchangeTime(date, timeZoneGap);
		Calendar cal = Calendar.getInstance();
		cal.setTime(exchangeTime);
		int week = cal.get(Calendar.DAY_OF_WEEK);
		String tradeTime = null;
		if (week == 1) {
			tradeTime = contract.getSunTradeTime();
		} else if (week == 2) {
			tradeTime = contract.getMonTradeTime();
		} else if (week == 3) {
			tradeTime = contract.getTueTradeTime();
		} else if (week == 4) {
			tradeTime = contract.getWedTradeTime();
		} else if (week == 5) {
			tradeTime = contract.getThuTradeTime();
		} else if (week == 6) {
			tradeTime = contract.getFriTradeTime();
		} else if (week == 7) {
			tradeTime = contract.getSatTradeTime();
		}
		return tradeTime;
	}

	private String getNextTradingTime(Date localTime, FuturesContractDto contract) {
		String nextTime = null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(localTime);
		int dayForweek = cal.get(Calendar.DAY_OF_WEEK);
		if (dayForweek == 1) {
			// str.substring(0, str.indexOf("#"));
			nextTime = contract.getMonTradeTime().trim().split(",")[0];
		} else if (dayForweek == 2) {
			nextTime = contract.getTueTradeTime().trim().split(",")[0];
		} else if (dayForweek == 3) {
			nextTime = contract.getWedTradeTime().trim().split(",")[0];
		} else if (dayForweek == 4) {
			nextTime = contract.getThuTradeTime().trim().split(",")[0];
		} else if (dayForweek == 5) {
			nextTime = contract.getFriTradeTime().trim().split(",")[0];
		} else if (dayForweek == 6) {
			nextTime = contract.getSatTradeTime().trim().split(",")[0];
		} else if (dayForweek == 7) {
			nextTime = contract.getSunTradeTime().trim().split(",")[0];
		}
		return nextTime;
	}

	@Override
	public Response<FuturesContractAdminDto> addContract(@RequestBody FuturesContractAdminDto contractDto) {
		// 判断是否唯一app合约
		if (contractDto.getAppContract()) {
			if (contractDto.getCommodityId() == null) {
				throw new ServiceException(ExceptionConstant.CONTRACT_COMMODITYID_ISNULL_EXCEPTION);
			}
			List<FuturesContract> contList = futuresContractService.findByCommodity(contractDto.getCommodityId());
			for (FuturesContract con : contList) {
				if (con.getAppContract()) {
					con.setAppContract(false);
					futuresContractService.modifyExchange(con);
				}
			}
		}

		FuturesContract fcontract = CopyBeanUtils.copyBeanProperties(FuturesContract.class, contractDto, false);
		fcontract.setCommodity(commodityService.retrieve(contractDto.getCommodityId()));
		fcontract.setEnable(false);
		fcontract.setCreateTime(new Date());
		FuturesContract result = futuresContractService.saveExchange(fcontract);
		FuturesContractAdminDto resultDto = CopyBeanUtils.copyBeanProperties(result, new FuturesContractAdminDto(),
				false);
		return new Response<>(resultDto);
	}

	@Override
	public Response<FuturesContractAdminDto> modifyContract(@RequestBody FuturesContractAdminDto contractDto) {

		FuturesContract fcontract = CopyBeanUtils.copyBeanProperties(FuturesContract.class, contractDto, false);

		fcontract.setCommodity(commodityService.retrieve(contractDto.getCommodityId()));
		fcontract.setEnable(contractDto.getEnable());
		fcontract.setCreateTime(new Date());
		FuturesContract result = futuresContractService.modifyExchange(fcontract);
		FuturesContractAdminDto resultDto = CopyBeanUtils.copyBeanProperties(result, new FuturesContractAdminDto(),
				false);

		return new Response<>(resultDto);
	}

	@Override
	public Response<String> deleteContract(@PathVariable Long id) {
		return futuresContractService.deleteContract(id);
	}

	@Override
	public Response<PageInfo<FuturesContractAdminDto>> pagesContractAdmin(
			@RequestBody FuturesContractAdminQuery query) {
		Page<FuturesContract> page = futuresContractService.pagesContractAdmin(query);
		PageInfo<FuturesContractAdminDto> result = PageToPageInfo.pageToPageInfo(page, FuturesContractAdminDto.class);
		if (result != null && result.getContent() != null) {
			List<FuturesContract> contract = page.getContent();
			for (int i = 0; i < contract.size(); i++) {
				if (contract.get(i).getCommodity() != null) {
					result.getContent().get(i).setSymbol(contract.get(i).getCommodity().getSymbol());
					result.getContent().get(i).setName(contract.get(i).getCommodity().getName());
					result.getContent().get(i)
							.setProductType(contract.get(i).getCommodity().getProductType().getValue());
					result.getContent().get(i).setCommodityId(contract.get(i).getCommodity().getId());
					FuturesCommodity fcom = commodityService.retrieve(contract.get(i).getCommodity().getId());
					if (fcom != null && fcom.getExchange() != null) {
						result.getContent().get(i).setExchangcode(fcom.getExchange().getCode());
						result.getContent().get(i).setExchangeId(fcom.getExchange().getId());
						result.getContent().get(i).setExchangename(fcom.getExchange().getName());
						result.getContent().get(i).setExchangeType(fcom.getExchange().getExchangeType());
					}
				}
				if (contract.get(i).getEnable()) {
					result.getContent().get(i).setState(1);
				} else {
					result.getContent().get(i).setState(1);
				}
				if (contract.get(i).getExpirationDate() != null) {
					Date expira = contract.get(i).getExpirationDate();
					Date current = new Date();
					if (current.getTime() > expira.getTime()) {
						result.getContent().get(i).setEnable(null);
					}
				}
			}

		}

		return new Response<>(result);
	}

	@Override
	public Response<FuturesContractDto> findByContractId(@PathVariable Long contractId) {
		FuturesContract contract = futuresContractService.findByContractId(contractId);
		FuturesContractDto dto = CopyBeanUtils.copyBeanProperties(FuturesContractDto.class, contract.getCommodity(),
				false);
		dto = CopyBeanUtils.copyBeanProperties(contract, dto);
		return new Response<>(dto);
	}

	@Override
	public Response<String> isCurrent(@RequestParam(value = "id") Long id) {
		Integer i = futuresContractService.isCurrent(id);
		Response<String> response = new Response<String>();
		if (i == 1) {
			response.setCode("200");
			response.setMessage("响应成功");
			response.setResult(i.toString());
		}
		return response;
	}

	@Override
	public Response<List<FuturesContractDto>> listByCommodityId(@PathVariable Long commodityId) {
		return new Response<>(CopyBeanUtils.copyListBeanPropertiesToList(
				futuresContractService.listByCommodityId(commodityId), FuturesContractDto.class));
	}

	/**
	 * 将时间转成国内时间
	 * 
	 * @param timeZoneGap
	 *            时差
	 * @param time
	 *            国外时间
	 * @return 国内时间
	 */
	private String timeZoneConversion(Integer timeZoneGap, String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String timeStr = "";
		try {
			if (StringUtil.isEmpty(time)) {
				return "";
			}
			timeStr = sdf.format(exchangeTime(sdf.parse(time.toString()), timeZoneGap));
		} catch (ParseException e) {
			return "";
		}

		return timeStr;
	}

	private Date exchangeTime(Date localTime, Integer timeZoneGap) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(localTime);
		cal.add(Calendar.HOUR_OF_DAY, timeZoneGap);
		return cal.getTime();
	}

	/**
	 * 获取期货合约交易时间描述(期货合约列表显示的时间)
	 * 
	 * @param timeZoneGap
	 *            时差
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @return 期货合约交易时间描述
	 */
	private String currentTradeTimeDesc(Integer timeZoneGap, String startTime, String endTime) {
		String stateT = timeZoneConversion(timeZoneGap, startTime);
		String endT = timeZoneConversion(timeZoneGap, endTime);
		if (stateT.compareTo(endT) > 0) {
			endT = timeZoneConversion(timeZoneGap, endTime) + "（次日）";
		}
		return stateT + "-" + endT;
	}

}
