package com.waben.stock.datalayer.futures.controller;

import java.math.BigDecimal;
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

import com.waben.stock.datalayer.futures.entity.FuturesContract;
import com.waben.stock.datalayer.futures.entity.FuturesContractTerm;
import com.waben.stock.datalayer.futures.entity.FuturesCurrencyRate;
import com.waben.stock.datalayer.futures.entity.FuturesExchange;
import com.waben.stock.datalayer.futures.entity.FuturesPreQuantity;
import com.waben.stock.datalayer.futures.entity.enumconverter.FuturesProductTypeConverter;
import com.waben.stock.datalayer.futures.service.FuturesContractService;
import com.waben.stock.datalayer.futures.service.FuturesContractTermService;
import com.waben.stock.datalayer.futures.service.FuturesCurrencyRateService;
import com.waben.stock.datalayer.futures.service.FuturesExchangeService;
import com.waben.stock.datalayer.futures.service.FuturesPreQuantityService;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.admin.futures.FuturesContractAdminDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesPreQuantityDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesTermAdminDto;
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
@Api(description = "期货品种接口列表")
public class FuturesContractController implements FuturesContractInterface {

	@Autowired
	private FuturesContractService futuresContractService;

	@Autowired
	private FuturesContractTermService futuresContractTermService;

	@Autowired
	private FuturesExchangeService exchangeService;

	@Autowired
	private FuturesCurrencyRateService rateService;

	@Autowired
	private FuturesCurrencyRateService futuresCurrencyRateService;

	@Autowired
	private FuturesPreQuantityService quantityService;

	private SimpleDateFormat daySdf = new SimpleDateFormat("yyyy-MM-dd");

	private SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	public Response<PageInfo<FuturesContractDto>> pagesContract(@RequestBody FuturesContractQuery contractQuery) {
		Page<FuturesContract> page = futuresContractService.pagesContract(contractQuery);
		PageInfo<FuturesContractDto> result = PageToPageInfo.pageToPageInfo(page, FuturesContractDto.class);
		List<FuturesContractDto> contractDtoList = result.getContent();
		for (FuturesContractDto futuresContractDto : contractDtoList) {
			FuturesExchange exchange = exchangeService.findById(futuresContractDto.getExchangeId());
			if (exchange == null) {
				futuresContractDto.setState(3);
				futuresContractDto.setCurrentTradeTimeDesc("交易异常");
				break;
			}
			// 获取汇率信息
			FuturesCurrencyRate rate = futuresCurrencyRateService.findByCurrency(futuresContractDto.getCurrency());
			futuresContractDto.setExchangeEnable(exchange.getEnable());
			futuresContractDto.setTimeZoneGap(exchange.getTimeZoneGap());
			futuresContractDto.setRate(rate == null ? new BigDecimal(0) : rate.getRate());
			futuresContractDto.setCurrencyName(rate == null ? "" : rate.getCurrencyName());
			futuresContractDto.setCurrencySign(rate == null ? "" : rate.getCurrencySign());
			// 判断交易所是否可用
			if (futuresContractDto.getExchangeEnable() != null && !futuresContractDto.getExchangeEnable()) {
				futuresContractDto.setState(3);
				futuresContractDto.setCurrentTradeTimeDesc("交易异常");
				break;
			}
			// 判断合约是否可用
			if (futuresContractDto.getEnable() != null && !futuresContractDto.getEnable()) {
				futuresContractDto.setState(3);
				futuresContractDto.setCurrentTradeTimeDesc("交易异常");
				break;
			}
			List<FuturesContractTerm> termList = futuresContractTermService
					.findByListContractId(futuresContractDto.getId());

			if (termList == null || termList.size() == 0) {
				futuresContractDto.setState(1);
				futuresContractDto.setCurrentTradeTimeDesc("全天交易");
				break;
			}
			// 获取交易所对应的交易期限数据
			FuturesContractTerm term = termList.get(0);
			// 判断是否在交易时间段
			Date now = new Date();
			Integer timeZoneGap = futuresContractDto.getTimeZoneGap();
			// 当天交易时间描述
			futuresContractDto.setCurrentTradeTimeDesc(retriveTradeTimeStrDesc(timeZoneGap, term, now));
			// 转换后的当前时间
			Date exchangeTime = retriveExchangeTime(now, timeZoneGap);
			// 转换后当前时间的明天
			Date nextTime = nextTime(exchangeTime);
			// 获取交易所提供时间
			String tradeTime = retriveExchangeTradeTimeStr(timeZoneGap, term, now);
			boolean isTradeTime = false;
			if (!StringUtil.isEmpty(tradeTime)) {
				String[] tradeTimeArr = tradeTime.split(",");
				String dayStr = daySdf.format(exchangeTime);
				String fullStr = fullSdf.format(exchangeTime);
				for (String tradeTimeDuration : tradeTimeArr) {
					String[] tradeTimePointArr = tradeTimeDuration.trim().split("-");
					if (fullStr.compareTo(dayStr + " " + tradeTimePointArr[0].trim()) >= 0
							&& fullStr.compareTo(dayStr + " " + tradeTimePointArr[1].trim()) < 0) {
						futuresContractDto.setCurrentHoldingTime(dayStr + " " + tradeTimePointArr[1].trim());
						futuresContractDto.setNextTradingTime("");
						isTradeTime = true;
						break;
					} else {
						if (fullStr.compareTo(dayStr + " " + tradeTimePointArr[0].trim()) < 0) {
							futuresContractDto.setNextTradingTime(dayStr + " " + tradeTimePointArr[0].trim());
							break;
						} else {
							String tomorrow = daySdf.format(nextTime);
							String tomorrowHour = getNextTradingTime(exchangeTime, term) == null ? ""
									: getNextTradingTime(exchangeTime, term);
							// 获取转换后的明天时间交易开始时间
							String tomorrowTime = tomorrow + " " + tomorrowHour;
							futuresContractDto.setNextTradingTime(tomorrowTime);
						}
					}
				}
				if (isTradeTime) {
					futuresContractDto.setState(1);
				} else {
					futuresContractDto.setState(2);
				}
			}
		}

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

	private String retriveExchangeTradeTimeStr(Integer timeZoneGap, FuturesContractTerm term, Date date) {
		Date exchangeTime = retriveExchangeTime(date, timeZoneGap);
		Calendar cal = Calendar.getInstance();
		cal.setTime(exchangeTime);
		int week = cal.get(Calendar.DAY_OF_WEEK);
		String tradeTime = null;
		if (week == 1) {
			tradeTime = term.getSunTradeTime();
		} else if (week == 2) {
			tradeTime = term.getMonTradeTime();
		} else if (week == 3) {
			tradeTime = term.getTueTradeTime();
		} else if (week == 4) {
			tradeTime = term.getWedTradeTime();
		} else if (week == 5) {
			tradeTime = term.getThuTradeTime();
		} else if (week == 6) {
			tradeTime = term.getFriTradeTime();
		} else if (week == 7) {
			tradeTime = term.getSatTradeTime();
		}
		return tradeTime;
	}

	private String retriveTradeTimeStrDesc(Integer timeZoneGap, FuturesContractTerm term, Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int week = cal.get(Calendar.DAY_OF_WEEK);
		String tradeTimeDesc = null;
		if (week == 1) {
			tradeTimeDesc = term.getSunTradeTimeDesc();
		} else if (week == 2) {
			tradeTimeDesc = term.getMonTradeTimeDesc();
		} else if (week == 3) {
			tradeTimeDesc = term.getTueTradeTimeDesc();
		} else if (week == 4) {
			tradeTimeDesc = term.getWedTradeTimeDesc();
		} else if (week == 5) {
			tradeTimeDesc = term.getThuTradeTimeDesc();
		} else if (week == 6) {
			tradeTimeDesc = term.getFriTradeTimeDesc();
		} else if (week == 7) {
			tradeTimeDesc = term.getSatTradeTimeDesc();
		}
		return tradeTimeDesc;
	}

	private String getNextTradingTime(Date localTime, FuturesContractTerm term) {
		String nextTime = null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(localTime);
		int dayForweek = cal.get(Calendar.DAY_OF_WEEK);
		if (dayForweek == 1) {
			nextTime = term.getMonTradeTime().trim().substring(0, 5);
		} else if (dayForweek == 2) {
			nextTime = term.getTueTradeTime().trim().substring(0, 5);
		} else if (dayForweek == 3) {
			nextTime = term.getWedTradeTime().trim().substring(0, 5);
		} else if (dayForweek == 4) {
			nextTime = term.getThuTradeTime().trim().substring(0, 5);
		} else if (dayForweek == 5) {
			nextTime = term.getFriTradeTime().trim().substring(0, 5);
		} else if (dayForweek == 6) {
			nextTime = term.getSatTradeTime().trim().substring(0, 5);
		} else if (dayForweek == 7) {
			nextTime = term.getSunTradeTime().trim().substring(0, 5);
		}
		return nextTime;
	}

	@Override
	public Response<FuturesContractAdminDto> addContract(@RequestBody FuturesContractAdminDto contractDto) {
		FuturesContract fcontract = CopyBeanUtils.copyBeanProperties(FuturesContract.class, contractDto, false);

		// 判断警戒线是否低于强平点
		if (fcontract.getCordon().compareTo(fcontract.getPerUnitUnwindPoint()) < 0) {
			throw new ServiceException(ExceptionConstant.CONTRACT_CORDON_UNITUNWINDPOINT_EXCEPTION);
		}
		fcontract.setExchange(exchangeService.findById(contractDto.getExchangeId()));
		fcontract.setCurrency(contractDto.getCurrency());
		if (contractDto.getProductType() != null && !"".equals(contractDto.getProductType())) {
			FuturesProductTypeConverter converter = new FuturesProductTypeConverter();
			fcontract.setProductType(converter.convertToEntityAttribute(Integer.valueOf(contractDto.getProductType())));
		}
		fcontract.setEnable(false);
		FuturesContract result = futuresContractService.saveExchange(fcontract);
		FuturesContractAdminDto resultDto = CopyBeanUtils.copyBeanProperties(result, new FuturesContractAdminDto(),
				false);

		if (result.getExchange() != null) {
			resultDto.setExchangcode(result.getExchange().getCode());
			resultDto.setExchangename(result.getExchange().getName());
			resultDto.setExchangeId(result.getExchange().getId());
			resultDto.setExchangeType(result.getExchange().getExchangeType());
		}
		if (result.getProductType() != null) {
			resultDto.setProductType(result.getProductType().getValue());
		}
		if (result.getCurrency() != null && !"".equals(result.getCurrency())) {
			FuturesCurrencyRate rate = rateService.queryByName(result.getCurrency());
			;
			if (rate != null) {
				resultDto.setRate(rate.getRate());
			}
		}

		return new Response<>(resultDto);
	}

	@Override
	public Response<FuturesContractAdminDto> modifyContract(@RequestBody FuturesContractAdminDto contractDto) {

		FuturesContract fcontract = CopyBeanUtils.copyBeanProperties(FuturesContract.class, contractDto, false);

		// 判断警戒线是否低于强平点
		if (fcontract.getCordon().compareTo(fcontract.getPerUnitUnwindPoint()) < 0) {
			throw new ServiceException(ExceptionConstant.CONTRACT_CORDON_UNITUNWINDPOINT_EXCEPTION);
		}

		fcontract.setExchange(exchangeService.findById(contractDto.getExchangeId()));
		fcontract.setCurrency(contractDto.getCurrency());

		if (contractDto.getProductType() != null && !"".equals(contractDto.getProductType())) {
			FuturesProductTypeConverter converter = new FuturesProductTypeConverter();
			fcontract.setProductType(converter.convertToEntityAttribute(Integer.valueOf(contractDto.getProductType())));
		}
		FuturesContract result = futuresContractService.modifyExchange(fcontract);
		FuturesContractAdminDto resultDto = CopyBeanUtils.copyBeanProperties(result, new FuturesContractAdminDto(),
				false);

		if (result.getExchange() != null) {
			resultDto.setExchangcode(result.getExchange().getCode());
			resultDto.setExchangename(result.getExchange().getName());
			resultDto.setExchangeId(result.getExchange().getId());
			resultDto.setExchangeType(result.getExchange().getExchangeType());
		}
		if (result.getProductType() != null) {
			resultDto.setProductType(result.getProductType().getValue());
		}
		if (result.getCurrency() != null && !"".equals(result.getCurrency())) {
			FuturesCurrencyRate rate = rateService.queryByName(result.getCurrency());
			;
			if (rate != null) {
				resultDto.setRate(rate.getRate());
			}
		}
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
		for (int i = 0; i < result.getContent().size(); i++) {
			if (page.getContent().get(i).getExchange() != null) {
				result.getContent().get(i).setExchangcode(page.getContent().get(i).getExchange().getCode());
				result.getContent().get(i).setExchangename(page.getContent().get(i).getExchange().getName());
				result.getContent().get(i).setExchangeId(page.getContent().get(i).getExchange().getId());
				result.getContent().get(i).setExchangeType(page.getContent().get(i).getExchange().getExchangeType());
			}
			if (page.getContent().get(i).getProductType() != null) {
				result.getContent().get(i).setProductType(page.getContent().get(i).getProductType().getValue());
			}

			if (page.getContent().get(i).getCurrency() != null && !"".equals(page.getContent().get(i).getCurrency())) {
				FuturesCurrencyRate rate = rateService.queryByName(page.getContent().get(i).getCurrency());
				if (rate != null) {
					result.getContent().get(i).setRate(rate.getRate());
				}
			}
			List<FuturesContractTerm> list = futuresContractService
					.findByListContractId(page.getContent().get(i).getId());
			List<FuturesTermAdminDto> resultList = new ArrayList<FuturesTermAdminDto>();
			for (FuturesContractTerm term : list) {
				FuturesTermAdminDto dto = CopyBeanUtils.copyBeanProperties(term, new FuturesTermAdminDto(), false);
				resultList.add(dto);
			}
			result.getContent().get(i).setFuturesTermAdminDto(resultList);

			// 获取预设置手数
			List<FuturesPreQuantity> preList = quantityService.findByContractId(result.getContent().get(i).getId());
			List<FuturesPreQuantityDto> preResult = new ArrayList<FuturesPreQuantityDto>();
			for (FuturesPreQuantity tity : preList) {
				FuturesPreQuantityDto dto = CopyBeanUtils.copyBeanProperties(tity, new FuturesPreQuantityDto(), false);
				dto.setContractId(tity.getContract().getId());
				preResult.add(dto);
			}
			result.getContent().get(i).setFuturesPreQuantityDto(preResult);
		}
		return new Response<>(result);
	}

	@Override
	public Response<FuturesContractDto> findByContractId(@PathVariable Long contractId) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(FuturesContractDto.class,
				futuresContractService.findByContractId(contractId), false));
	}

	@Override
	public Response<String> isCurrent(@RequestParam(value = "id") Long id) {
		FuturesContract contract = futuresContractService.findByContractId(id);
		Boolean current = false;
		if (contract.getEnable() != null) {
			if (contract.getEnable()) {
				current = false;
			} else {
				current = true;
			}
		} else {
			current = false;
		}
		if (current) {
			List<FuturesContractTerm> termList = futuresContractService.findByListContractId(id);
			if (termList.size() > 0) {
				Boolean isCurrent = false;
				for (FuturesContractTerm term : termList) {
					if (term.isCurrent()) {
						isCurrent = true;
					}
				}
				if (!isCurrent) {
					throw new ServiceException(ExceptionConstant.CONTRACTTERM_ISCURRENT_EXCEPTION);
				}
			}

			List<FuturesPreQuantity> pre = quantityService.findByContractId(id);
			if (pre.size() == 0 || pre == null) {
				throw new ServiceException(ExceptionConstant.CONTRACT_PREQUANTITY_EXCEPTION);
			}
		}

		Integer i = futuresContractService.isCurrent(current, id);
		Response<String> res = new Response<String>();
		res.setCode("200");
		res.setMessage("启用/停用成功");
		res.setResult(i.toString());
		return res;
	}

}
