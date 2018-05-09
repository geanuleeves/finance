package com.waben.stock.applayer.promotion.controller;

import java.util.Date;
import java.util.List;

import com.waben.stock.applayer.promotion.business.OrganizationBusiness;
import com.waben.stock.interfaces.vo.manage.RoleVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import com.waben.stock.applayer.promotion.business.RoleBusiness;
import com.waben.stock.applayer.promotion.business.UserBusiness;
import com.waben.stock.applayer.promotion.util.SecurityAccount;
import com.waben.stock.interfaces.dto.manage.RoleDto;
import com.waben.stock.interfaces.dto.organization.OrganizationDto;
import com.waben.stock.interfaces.dto.organization.UserDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.organization.UserQuery;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import com.waben.stock.interfaces.vo.organization.OrganizationVo;
import com.waben.stock.interfaces.vo.organization.UserVo;

@RestController
@RequestMapping("/user")
@Api(description = "管理员")
public class UserController {

    @Autowired
    private UserBusiness userBusiness;

    @Autowired
    private RoleBusiness roleBusiness;

    @Autowired
    private OrganizationBusiness organizationBusiness;

    @Value("${onlystockoption:false}")
    private boolean onlyStockoption;

    @RequestMapping(value = "/role",method = RequestMethod.GET)
    @ApiOperation(value = "获取角色")
    public Response<List<RoleVo>> fetchRoleByOrganization(){
        UserDto userDto = (UserDto) SecurityAccount.current().getSecurity();
        List<RoleDto> roleDtos = roleBusiness.findByOrganization(userDto.getOrg().getId());
        List<RoleVo> roleVos = CopyBeanUtils.copyListBeanPropertiesToList(roleDtos,RoleVo.class);
        return new Response<>(roleVos);
    }

    @RequestMapping(value = "/org",method = RequestMethod.GET)
    @ApiOperation(value = "获取机构")
    public Response<List<OrganizationDto>> fetchOrgByParentOrg(){
        UserDto userDto = (UserDto) SecurityAccount.current().getSecurity();
        List<OrganizationDto> organizationDtos = organizationBusiness.listByParentId(userDto.getOrg().getId());
        organizationDtos.add(userDto.getOrg());
        return new Response<>(organizationDtos);
    }
//
////    @PreAuthorize("hasRole('USER_SAVE')")
    @ApiOperation(value = "添加管理员")
    @ApiImplicitParam(paramType = "query", dataType = "UserVo", name = "vo", value = "管理员对象", required = true)
    @RequestMapping(value = "/",method = RequestMethod.POST)
     public Response<UserVo> add(@RequestBody UserVo vo){
        vo.setState(false);
        vo.setCreateTime(new Date());
        UserDto requestDto = CopyBeanUtils.copyBeanProperties(UserDto.class, vo, false);
//        OrganizationDto org = null;
//        if(vo.getOrg()!=null) {
//            org = CopyBeanUtils.copyBeanProperties(OrganizationDto.class,vo.getOrg(),false);
//        }else{
//        UserDto current = (UserDto) SecurityAccount.current().getSecurity();
//            org = current.getOrg();
//        }
//        requestDto.setOrg(org);
        UserDto userDto = userBusiness.save(requestDto);
        UserVo userVo = CopyBeanUtils.copyBeanProperties(UserVo.class,userDto , false);
        return new Response<>(userVo);
    }
//
    @RequestMapping(value = "/",method = RequestMethod.PUT)
    @ApiOperation(value = "修改管理员")
    @ApiImplicitParam(paramType = "query", dataType = "UserVo", name = "vo", value = "管理员对象", required = true)
    public Response<UserVo> modify(@RequestBody UserVo vo){
        UserDto requestDto = CopyBeanUtils.copyBeanProperties(UserDto.class, vo, false);
        UserDto userDto = userBusiness.save(requestDto);
        UserVo userVo = CopyBeanUtils.copyBeanProperties(UserVo.class,userDto , false);
        return new Response<>(userVo);
    }
//
    @RequestMapping(value = "/state/{id}",method = RequestMethod.PUT)
    @ApiOperation(value = "冻结/恢复 管理员")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "管理员id", required = true)
    public Response<UserVo> modifyState(@PathVariable Long id){
        UserDto userDto = userBusiness.revisionState(id);
        UserVo userVo = CopyBeanUtils.copyBeanProperties(UserVo.class,userDto , false);
        return new Response<>(userVo);
    }
//
    @RequestMapping(value = "/pages",method = RequestMethod.GET)
    @ApiImplicitParam(paramType = "query", dataType = "UserQuery", name = "query", value = "管理员查询对象", required = false)
    @ApiOperation(value = "管理员分页")
    public Response<PageInfo<UserVo>> pages(UserQuery query) {
        PageInfo<UserDto> pageInfo = userBusiness.pages(query);
        List<UserVo> userVoContent = CopyBeanUtils.copyListBeanPropertiesToList(pageInfo.getContent(), UserVo.class);
        PageInfo<UserVo> response = new PageInfo<>(userVoContent, pageInfo.getTotalPages(), pageInfo.getLast(), pageInfo.getTotalElements(), pageInfo.getSize(), pageInfo.getNumber(), pageInfo.getFrist());
        for (int i = 0; i < pageInfo.getContent().size(); i++) {
            OrganizationVo organizationVo = CopyBeanUtils.copyBeanProperties(
                    OrganizationVo.class, pageInfo.getContent().get(i).getOrg(), false);
            userVoContent.get(i).setOrgName(organizationVo.getName());
            Long role = pageInfo.getContent().get(i).getRole();
            if(role!=null) {
                RoleDto roleDto = roleBusiness.findById(role);
                userVoContent.get(i).setRoleName(roleDto.getName());
                userVoContent.get(i).setCode(roleDto.getCode());
            }
        }
        return new Response<>(response);
    }

//    @Deprecated
//    @PreAuthorize("hasRole('USER_ROLE_REVISION')")
//    @RequestMapping("/{id}/role")
//    public Response<UserVo> bindRole(@PathVariable Long id, Long roleId){
//        UserDto userDto = userBusiness.saveUserRole(id,roleId);
//        UserVo userVo = CopyBeanUtils.copyBeanProperties(UserVo.class,userDto , false);
//        return new Response<>(userVo);
//    }
//
//    @RequestMapping(value = "/getCurrent", method = RequestMethod.GET)
//	public Response<UserDto> getCurrent() {
//    	AccountCredentials details = SecurityAccount.current();
//    	UserDto result = (UserDto)details.getSecurity();
//    	result.setOnlyStockoption(onlyStockoption);
//    	result.setPassword(null);
//		return new Response<>(result);
//	}
//
//    @RequestMapping(value = "/password", method = RequestMethod.PUT)
//	public Response<Void> modifyPassword(String oldPassword, String password) {
//    	AccountCredentials details = SecurityAccount.current();
//    	UserDto result = (UserDto)details.getSecurity();
//    	userBusiness.modifyPassword(result.getId(), oldPassword, password);
//		return new Response<>();
//	}
    
}
