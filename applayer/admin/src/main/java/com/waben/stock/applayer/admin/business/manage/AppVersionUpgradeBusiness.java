package com.waben.stock.applayer.admin.business.manage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.waben.stock.applayer.admin.security.CustomUserDetails;
import com.waben.stock.applayer.admin.security.SecurityUtil;
import com.waben.stock.interfaces.dto.manage.AppVersionUpgradeDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.AppVersionupgradeQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.service.manage.AppVersionUpgradeInterface;

@Service
public class AppVersionUpgradeBusiness {

	@Autowired
	private AppVersionUpgradeInterface reference;
	
	@Value("${uploadFilePath}")
    private String uploadFilePath;
	
	public AppVersionUpgradeDto addUpgrade(AppVersionUpgradeDto dto){
		CustomUserDetails userDetails = SecurityUtil.getUserDetails();
		dto.setStaffId(userDetails.getUserId());
		dto.setUploadFilePath(uploadFilePath);
		Response<AppVersionUpgradeDto> response = reference.addUpgrade(dto);
		if("200".equals(response.getCode())){
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
	
	public AppVersionUpgradeDto modifyUpgrade(AppVersionUpgradeDto dto){
		dto.setUploadFilePath(uploadFilePath);
		Response<AppVersionUpgradeDto> response = reference.modifyUpgrade(dto);
		if("200".equals(response.getCode())){
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
	
	public PageInfo<AppVersionUpgradeDto> page(AppVersionupgradeQuery query){
		Response<PageInfo<AppVersionUpgradeDto>> response = reference.page(query);
		if("200".equals(response.getCode())){
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
	
	public Long delete(Long id){
		Response<Long> response = reference.delete(id);
		if("200".equals(response.getCode())){
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
	
	public Long isCurrentVersion(Integer deviceType, Long id){
		Response<Long> response = reference.isCurrentVersion(deviceType, id);
		if("200".equals(response.getCode())){
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
}
