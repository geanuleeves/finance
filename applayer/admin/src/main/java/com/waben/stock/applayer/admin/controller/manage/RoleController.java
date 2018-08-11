package com.waben.stock.applayer.admin.controller.manage;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.waben.stock.applayer.admin.business.manage.MenuBusiness;
import com.waben.stock.applayer.admin.business.manage.RoleBusiness;
import com.waben.stock.applayer.admin.business.manage.StaffBusiness;
import com.waben.stock.applayer.admin.security.SecurityUtil;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.manage.MenuDto;
import com.waben.stock.interfaces.dto.manage.RoleDto;
import com.waben.stock.interfaces.dto.manage.StaffDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.RoleQuery;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import com.waben.stock.interfaces.vo.manage.MenuVo;
import com.waben.stock.interfaces.vo.manage.RoleVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/role")
@Api(description = "角色")
public class RoleController {
	
	@Autowired
	private MenuBusiness menuBusiness;
	
	@Autowired
    private StaffBusiness staffBusiness;
	
	@Autowired
	private RoleBusiness roleBusiness;

	@RequestMapping(value = "/menus", method = RequestMethod.GET)
	@ApiOperation(value = "获取菜单")
	public Response<List<MenuVo>> menus() {
		StaffDto respone = staffBusiness.findById(SecurityUtil.getUserDetails().getUserId());
		List<MenuDto> menuDtos = menuBusiness.menus(respone.getRoleDto().getId());
		List<MenuVo> menuVos = CopyBeanUtils.copyListBeanPropertiesToList(menuDtos, MenuVo.class);
		return new Response<>(menuVos);
	}
	
	@RequestMapping(value = "/pages", method = RequestMethod.GET)
	@ApiOperation(value = "角色分页")
	public Response<PageInfo<RoleVo>> page(RoleQuery query){
		PageInfo<RoleDto> pageInfo = roleBusiness.pages(query);
		List<RoleVo> roleVoContent = CopyBeanUtils.copyListBeanPropertiesToList(pageInfo.getContent(), RoleVo.class);
		PageInfo<RoleVo> response = new PageInfo<>(roleVoContent, pageInfo.getTotalPages(), pageInfo.getLast(),
				pageInfo.getTotalElements(), pageInfo.getSize(), pageInfo.getNumber(), pageInfo.getFrist());
		return new Response<>(response);
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ApiOperation(value = "添加角色")
	public Response<RoleDto> save(String name, String menuIds) {
		return new Response<>(roleBusiness.save(name,menuIds));
	}
	
	@RequestMapping(value = "/revision", method = RequestMethod.POST)
	@ApiOperation(value = "修改角色")
	public Response<RoleVo> revision(Long id, String name, String menuIds) {
		RoleDto roleDto = roleBusiness.revision(id, name, menuIds);
		RoleVo roleVo = CopyBeanUtils.copyBeanProperties(RoleVo.class, roleDto, false);
		return new Response<>(roleVo);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除角色")
	public Response<Long> delete(@PathVariable Long id) {
		List<StaffDto> list = staffBusiness.findAll();
		if(list!=null && list.size()>0){
			for (StaffDto dto : list) {
				if(dto.getRoleDto()!=null && dto.getRoleDto().getId().equals(id)){
					throw new ServiceException(ExceptionConstant.USER_ROLE_EXCEPITON);
				}
			}
		}
		roleBusiness.delete(id);
		return new Response<>(id);
    }

    @PutMapping("/permission/{id}")
	@ApiOperation(value = "修改角色权限")
	public Response<RoleVo> updateRolePermission(@PathVariable Long id,String permissionIds) {
		RoleDto roleDto = roleBusiness.updateRolePermission(id, permissionIds);
		RoleVo roleVo = CopyBeanUtils.copyBeanProperties(RoleVo.class, roleDto, false);
		return new Response<>(roleVo);
	}
}
