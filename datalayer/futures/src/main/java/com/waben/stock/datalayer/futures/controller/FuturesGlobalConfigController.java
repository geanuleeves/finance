package com.waben.stock.datalayer.futures.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.datalayer.futures.entity.FuturesGlobalConfig;
import com.waben.stock.datalayer.futures.entity.enumconverter.FuturesGlobalConfigTypeConverter;
import com.waben.stock.datalayer.futures.service.FuturesGlobalConfigService;
import com.waben.stock.interfaces.dto.admin.futures.FuturesGlobalConfigDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesGlobalConfigQuery;
import com.waben.stock.interfaces.service.futures.FuturesGlobalConfigInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import com.waben.stock.interfaces.util.PageToPageInfo;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/globalConfig")
@Api(description = "风控-警戒线  接口列表")
public class FuturesGlobalConfigController implements FuturesGlobalConfigInterface {

	@Autowired
	private FuturesGlobalConfigService configService;
	
	@Override
	public Response<FuturesGlobalConfigDto> addConfig(FuturesGlobalConfigDto global) {
		FuturesGlobalConfig config = CopyBeanUtils.copyBeanProperties(FuturesGlobalConfig.class, global,false);
		FuturesGlobalConfig result = configService.save(config);
		FuturesGlobalConfigDto response = CopyBeanUtils.copyBeanProperties(result, new FuturesGlobalConfigDto(),false);
		return new Response<>(response);
	}

	@Override
	public Response<FuturesGlobalConfigDto> modifyConfig(FuturesGlobalConfigDto global) {
		FuturesGlobalConfig config = CopyBeanUtils.copyBeanProperties(FuturesGlobalConfig.class, global,false);
		FuturesGlobalConfig result = configService.modify(config);
		FuturesGlobalConfigDto response = CopyBeanUtils.copyBeanProperties(result, new FuturesGlobalConfigDto(),false);
		return new Response<>(response);
	}

	@Override
	public void deleteConfig(Long id) {
		configService.delete(id);
	}

	@Override
	public Response<PageInfo<FuturesGlobalConfigDto>> pagesConfig(FuturesGlobalConfigQuery query) {
		Page<FuturesGlobalConfig> result = configService.pagesGlobal(query);
		PageInfo<FuturesGlobalConfigDto> response = PageToPageInfo.pageToPageInfo(result, FuturesGlobalConfigDto.class);
		return new Response<>(response);
	}

	@Override
	public Response<PageInfo<FuturesGlobalConfigDto>> findAll() {
		return null;
	}

	@Override
	public Response<FuturesGlobalConfigDto> saveAndModify(FuturesGlobalConfigDto global) {
		FuturesGlobalConfigTypeConverter convert = new FuturesGlobalConfigTypeConverter();
		FuturesGlobalConfig config = CopyBeanUtils.copyBeanProperties(FuturesGlobalConfig.class, global,false);
		config.setType(convert.convertToEntityAttribute(global.getType()));
		FuturesGlobalConfig result = configService.saveAndModif(config);
		FuturesGlobalConfigDto response = CopyBeanUtils.copyBeanProperties(result, new FuturesGlobalConfigDto(),false);
		return new Response<>(response);
	}

}
