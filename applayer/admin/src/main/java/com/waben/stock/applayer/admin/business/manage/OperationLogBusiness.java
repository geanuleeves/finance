package com.waben.stock.applayer.admin.business.manage;

import com.waben.stock.applayer.admin.security.SecurityUtil;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.manage.OperationLogDto;
import com.waben.stock.interfaces.exception.NetflixCircuitException;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.OperationLogQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.service.manage.OperationLogInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OperationLogBusiness {

	@Autowired
	@Qualifier("operationLogInterface")
	private OperationLogInterface operationLogInterface;

	public OperationLogDto save(Integer type) {
		OperationLogDto operationLogDto = new OperationLogDto();
//		operationLogDto.setStaffId(SecurityUtil.getUserId());
		operationLogDto.setStaffId(1L);
		operationLogDto.setType(type);
		operationLogDto.setCreateTime(new Date());
		Response<OperationLogDto> response = operationLogInterface.add(operationLogDto);
		String code = response.getCode();
		if(code.equals("200")) {
			return response.getResult();
		}else if(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION.equals(code)){
			throw new NetflixCircuitException(code);
		}
		throw new ServiceException(code);
	}

	public PageInfo<OperationLogDto> pages(OperationLogQuery query) {

		Response<PageInfo<OperationLogDto>> response = operationLogInterface.pages(query);
		String code = response.getCode();
		if(code.equals("200")) {
			return response.getResult();
		}else if(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION.equals(code)){
			throw new NetflixCircuitException(code);
		}
		throw new ServiceException(code);
	}
}
