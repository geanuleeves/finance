package com.waben.stock.datalayer.manage.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.datalayer.manage.entity.Role;
import com.waben.stock.datalayer.manage.entity.Staff;
import com.waben.stock.datalayer.manage.service.RoleService;
import com.waben.stock.datalayer.manage.service.StaffService;
import com.waben.stock.interfaces.dto.manage.RoleDto;
import com.waben.stock.interfaces.dto.manage.StaffDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.StaffQuery;
import com.waben.stock.interfaces.service.manage.StaffInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import com.waben.stock.interfaces.util.PageToPageInfo;

/**
 * @author Created by yuyidi on 2017/11/15.
 * @desc
 */
@RestController
@RequestMapping("/staff")
public class StaffController implements StaffInterface {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StaffService staffService;
    
    @Autowired
    private RoleService roleService;

    @Override
    public Response<StaffDto> fetchByUserName(@PathVariable String username) {
        Staff staff = staffService.findByUserName(username);
        StaffDto staffDto = CopyBeanUtils.copyBeanProperties(staff, new StaffDto(), false);
        staffDto.setRoleDto(CopyBeanUtils.copyBeanProperties(staff.getRole(), new RoleDto(), false));
        return new Response<>(staffDto);
    }

    @Override
    public Response<PageInfo<StaffDto>> pagesByQuery(@RequestBody StaffQuery staffQuery) {
        Page<Staff> page = staffService.pagesByQuery(staffQuery);
        PageInfo<StaffDto> result = PageToPageInfo.pageToPageInfo(page, StaffDto.class);
        if(page.getContent()!=null && page.getContent().size()>0){
        	for(int i=0;i<page.getContent().size();i++){
        		Staff staff = page.getContent().get(i);
        		RoleDto dto = CopyBeanUtils.copyBeanProperties(staff.getRole(), new RoleDto(), false);
        		result.getContent().get(i).setRoleDto(dto);
        		result.getContent().get(i).setRoleId(staff.getRole().getId());;
        	}
        	
        }
        return new Response<>(result);
    }

    @Override
    public Response<StaffDto> saveStaff(@RequestBody StaffDto staffDto) {
        Staff staff = CopyBeanUtils.copyBeanProperties(staffDto, new Staff(), false);
        staff.setRole(roleService.fetchById(staffDto.getRoleId()));
        staff.setState(true);
        Staff result = staffService.saveStaff(staff);
        StaffDto staffDtoResult = CopyBeanUtils.copyBeanProperties(result, new StaffDto(), false);
        return new Response<>(staffDtoResult);
    }

    @Override
    public Response<StaffDto> fetchById(@PathVariable Long id) {
        Staff staff = staffService.fetchById(id);
        StaffDto staffDto = CopyBeanUtils.copyBeanProperties(staff, new StaffDto(), false);
        staffDto.setRoleDto(CopyBeanUtils.copyBeanProperties(staff.getRole(), new RoleDto(), false));
        return new Response<>(staffDto);
    }

    @Override
    public Response<StaffDto> modify(@RequestBody StaffDto staffDto) {
        Staff staff = CopyBeanUtils.copyBeanProperties(Staff.class, staffDto, false);
        staff.setRole(roleService.fetchById(staffDto.getRoleId()));
        staff.setState(true);
        Staff result = staffService.revision(staff);
        StaffDto response = CopyBeanUtils.copyBeanProperties(StaffDto.class, result, false);
        return new Response<>(response);
    }

    @Override
    public void delete(@PathVariable Long id) {
        staffService.delete(id);
    }

	@Override
	public Response<List<StaffDto>> findAll() {
		List<Staff> result = staffService.findAll();
		List<StaffDto> response = new ArrayList<StaffDto>();
		if(result!=null && result.size()>0){
			for(Staff sta : result){
				StaffDto dto = CopyBeanUtils.copyBeanProperties(StaffDto.class, sta, false);
				RoleDto roldto = CopyBeanUtils.copyBeanProperties(RoleDto.class, sta.getRole(), false);
				dto.setRoleDto(roldto);
				response.add(dto);
			}
		}
		return new Response<>(response);
	}


}
