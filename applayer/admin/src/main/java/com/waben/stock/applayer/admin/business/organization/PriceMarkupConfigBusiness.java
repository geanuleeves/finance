package com.waben.stock.applayer.admin.business.organization;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.applayer.admin.reference.PriceMarkupConfigReference;
import com.waben.stock.interfaces.dto.organization.PriceMarkupConfigDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.form.organization.PriceMarkupForm;

/**
 * 加价配置 Business
 * 
 * @author luomengan
 *
 */
@Service
public class PriceMarkupConfigBusiness {

	@Autowired
	@Qualifier("priceMarkupConfigReference")
	private PriceMarkupConfigReference reference;

	public List<PriceMarkupConfigDto> priceMarkupConfigList(Long orgId, Integer resourceType) {
		Response<List<PriceMarkupConfigDto>> response = reference.priceMarkupConfigList(orgId, resourceType);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public String priceMarkupConfig(List<PriceMarkupForm> configFormList) {
		Response<String> response = reference.priceMarkupConfig(configFormList);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

}
