package com.waben.stock.datalayer.promotion.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.datalayer.promotion.reference.CnapsReference;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.manage.BankInfoDto;
import com.waben.stock.interfaces.dto.manage.CnapsDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;

/**
 * Cnaps Business
 * 
 * @author luomengan
 *
 */
@Service
public class CnapsBusiness {

	@Autowired
	@Qualifier("cnapsReference")
	private CnapsReference cnapsReference;

	public List<CnapsDto> findByCityCodeAndClsCode(String cityCode, String clsCode) {
		Response<List<CnapsDto>> response = cnapsReference.fetchByCityCodeAndClsCode(cityCode, clsCode);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public BankInfoDto findBankInfo(String bankCard) {
		Response<BankInfoDto> response = cnapsReference.fetchBankInfo(bankCard);
		if ("200".equals(response.getCode())) {
			if (response.getResult() == null) {
				throw new ServiceException(ExceptionConstant.DATANOTFOUND_EXCEPTION);
			}
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public List<BankInfoDto> listBankInfo() {
		Response<List<BankInfoDto>> response = cnapsReference.listBankInfo();
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public List<BankInfoDto> appBankinfo() {
		Response<List<BankInfoDto>> response = cnapsReference.listAppBankInfo();
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

}
