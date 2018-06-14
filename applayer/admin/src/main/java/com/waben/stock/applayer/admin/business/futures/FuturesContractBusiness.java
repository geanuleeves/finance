package com.waben.stock.applayer.admin.business.futures;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.admin.futures.FuturesContractAdminDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesContractTimeDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesPreQuantityDto;
import com.waben.stock.interfaces.dto.futures.FuturesContractDto;
import com.waben.stock.interfaces.dto.futures.FuturesExchangeDto;
import com.waben.stock.interfaces.exception.NetflixCircuitException;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesContractAdminQuery;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesExchangeAdminQuery;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesPreQuantityQuery;
import com.waben.stock.interfaces.service.futures.FuturesContractInterface;
import com.waben.stock.interfaces.service.futures.FuturesExchangeInterface;
import com.waben.stock.interfaces.service.futures.FuturesPreQuantityInterface;

@Service
public class FuturesContractBusiness {

	@Autowired
	@Qualifier("futurescontractInterface")
	private FuturesContractInterface reference;

	@Autowired
	@Qualifier("futuresExchangeInterface")
	private FuturesExchangeInterface exchangeReference;

	@Autowired
	@Qualifier("futuresPreQuantityInterface")
	private FuturesPreQuantityInterface preQuantityReference;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Response<List<FuturesContractTimeDto>> getcontractTime(String contractCode) {
		FuturesContractTimeDto dto = new FuturesContractTimeDto();
		dto.setContractNo("1806");
		dto.setExpirationDate(new Date());
		dto.setFirstNoticeDate(new Date());
		dto.setLastTradingDate(new Date());
		List<FuturesContractTimeDto> result = new ArrayList<FuturesContractTimeDto>();
		result.add(dto);
		try {
			FuturesContractTimeDto dto1 = new FuturesContractTimeDto();
			dto1.setContractNo("1807");
			dto1.setExpirationDate(sdf.parse("2018-06-22 00:00:00"));
			dto1.setFirstNoticeDate(sdf.parse("2018-06-22 00:00:00"));
			dto1.setLastTradingDate(sdf.parse("2018-06-22 00:00:00"));
			result.add(dto1);
		} catch (Exception e) {

		}
		return new Response<>(result);
	}

	public Response<String> isEnable(Long contractId) {
		Response<String> response = reference.isCurrent(contractId);
		return response;
	}

	public PageInfo<FuturesPreQuantityDto> queryPre(FuturesPreQuantityQuery query) {
		Response<PageInfo<FuturesPreQuantityDto>> response = preQuantityReference.findAll(query);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public PageInfo<FuturesExchangeDto> pagesExchange(FuturesExchangeAdminQuery query) {
		Response<PageInfo<FuturesExchangeDto>> response = exchangeReference.pagesExchange(query);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public PageInfo<FuturesContractAdminDto> pagesContractAdmin(FuturesContractAdminQuery query) {
		Response<PageInfo<FuturesContractAdminDto>> response = reference.pagesContractAdmin(query);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public Response<String> delete(Long id) {
		return exchangeReference.deleteExchange(id);
	}

	public FuturesExchangeDto save(FuturesExchangeDto query) {
		Response<FuturesExchangeDto> response = exchangeReference.addExchange(query);
		String code = response.getCode();
		if ("200".equals(code)) {
			return response.getResult();
		} else if (ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION.equals(code)) {
			throw new NetflixCircuitException(code);
		}
		throw new ServiceException(response.getCode());
	}

	public FuturesExchangeDto modify(FuturesExchangeDto exchangeDto) {
		Response<FuturesExchangeDto> response = exchangeReference.modifyExchange(exchangeDto);
		String code = response.getCode();
		if ("200".equals(code)) {
			return response.getResult();
		} else if (ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION.equals(code)) {
			throw new NetflixCircuitException(code);
		}
		throw new ServiceException(response.getCode());
	}

	public Response<String> deleteContract(Long id) {
		return reference.deleteContract(id);
	}

	public FuturesContractAdminDto save(FuturesContractAdminDto contractDto) {
		Response<FuturesContractAdminDto> response = reference.addContract(contractDto);
		String code = response.getCode();
		if ("200".equals(code)) {
			return response.getResult();
		} else if (ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION.equals(code)) {
			throw new NetflixCircuitException(code);
		}
		throw new ServiceException(response.getCode());
	}

	public FuturesContractAdminDto modify(FuturesContractAdminDto contractDto) {
		Response<FuturesContractAdminDto> response = reference.modifyContract(contractDto);
		String code = response.getCode();
		if ("200".equals(code)) {
			return response.getResult();
		} else if (ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION.equals(code)) {
			throw new NetflixCircuitException(code);
		}
		throw new ServiceException(response.getCode());
	}

	public List<FuturesContractDto> listByCommodityId(Long commodityId) {
		Response<List<FuturesContractDto>> response = reference.listByCommodityId(commodityId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

}
