package com.waben.stock.datalayer.manage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.datalayer.manage.entity.AppVersionUpgrade;
import com.waben.stock.datalayer.manage.entity.Staff;
import com.waben.stock.datalayer.manage.service.AppVersionUpgradeService;
import com.waben.stock.datalayer.manage.service.StaffService;
import com.waben.stock.interfaces.dto.manage.AppVersionUpgradeDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.AppVersionupgradeQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.service.manage.AppVersionUpgradeInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import com.waben.stock.interfaces.util.PageToPageInfo;

/**
 * app版本升级 Controller
 * 
 * @author lma
 *
 */
@RestController
@RequestMapping("/upgrade")
public class AppVersionUpgradeController implements AppVersionUpgradeInterface {

	@Autowired
	private AppVersionUpgradeService service;
	
	@Autowired
	private StaffService staffService;

	@Override
	public Response<AppVersionUpgradeDto> checkUpgrade(@PathVariable Integer versionCode,
			@PathVariable Integer deviceType, Integer shellIndex) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(AppVersionUpgradeDto.class,
				service.checkUpgrade(versionCode, deviceType, shellIndex), false));
	}

	@Override
	public Response<AppVersionUpgradeDto> addUpgrade(@RequestBody AppVersionUpgradeDto dto) {
		AppVersionUpgrade grade = CopyBeanUtils.copyBeanProperties(AppVersionUpgrade.class, dto, false);
		if(dto.getStaffId()!=null){
			Staff staff = staffService.fetchById(dto.getStaffId());
			grade.setStaff(staff);
		}
		AppVersionUpgrade result = service.addUpgrade(grade, dto.getUploadFilePath());
		AppVersionUpgradeDto response = CopyBeanUtils.copyBeanProperties(result, new AppVersionUpgradeDto(), false);
		return new Response<>(response);
	}

	@Override
	public Response<AppVersionUpgradeDto> modifyUpgrade(@RequestBody AppVersionUpgradeDto dto) {
		AppVersionUpgrade grade = CopyBeanUtils.copyBeanProperties(AppVersionUpgrade.class, dto, false);
		if(dto.getStaffId()!=null){
			Staff staff = staffService.fetchById(dto.getStaffId());
			grade.setStaff(staff);
		}
		AppVersionUpgrade result = service.modifyUpgrade(grade, dto.getUploadFilePath());
		AppVersionUpgradeDto response = CopyBeanUtils.copyBeanProperties(result, new AppVersionUpgradeDto(), false);
		return new Response<>(response);
	}

	@Override
	public Response<Long> delete(@RequestParam Long id) {
		service.deleteUpgrade(id);
		return new Response<>(id);
	}

	@Override
	public Response<Long> isCurrentVersion(@PathVariable Integer deviceType, @RequestParam Long id) {
		service.isCurrentVersion(deviceType, id);
		return new Response<>(id);
	}

	@Override
	public Response<PageInfo<AppVersionUpgradeDto>> page(@RequestBody AppVersionupgradeQuery query) {
		Page<AppVersionUpgrade> page = service.page(query);
		PageInfo<AppVersionUpgradeDto> result = PageToPageInfo.pageToPageInfo(page, AppVersionUpgradeDto.class);
		if(page.getContent()!=null && page.getContent().size()>0){
			List<AppVersionUpgrade> upgrade = page.getContent();
			for(int i=0;i<upgrade.size();i++){
				if(upgrade.get(i).getStaff()!=null){
					result.getContent().get(i).setStaffId(upgrade.get(i).getStaff().getId());
					result.getContent().get(i).setUserName(upgrade.get(i).getStaff().getUserName());
				}
			}
		}
		return new Response<>(result);
	}

	@Override
	public Response<String> dowload(@PathVariable Integer deviceType) {
		String url = service.findByDeviceType(deviceType);
		Response<String> response = new Response<>();
		response.setCode("200");
		response.setMessage("响应成功");
		response.setResult(url);
		
		return response;
	}

}
