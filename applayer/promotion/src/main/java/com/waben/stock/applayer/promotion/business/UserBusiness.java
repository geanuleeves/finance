package com.waben.stock.applayer.promotion.business;

import com.waben.stock.applayer.promotion.reference.manage.RoleReference;
import com.waben.stock.interfaces.dto.manage.RoleDto;
import com.waben.stock.interfaces.dto.organization.OrganizationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.applayer.promotion.reference.organization.UserReference;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.organization.UserDto;
import com.waben.stock.interfaces.exception.NetflixCircuitException;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;

@Service
public class UserBusiness {

    @Autowired
    @Qualifier("userReference")
    private UserReference userReference;
    @Autowired
    private RoleBusiness roleBusiness;

    public UserDto fetchByUserName(String userName) {
        Response<UserDto> response = userReference.fetchByUserName(userName);
        String code = response.getCode();
        if ("200".equals(code)) {
            return response.getResult();
        }else if(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION.equals(code)){
            throw new NetflixCircuitException(code);
        }
        throw new ServiceException(response.getCode());
    }


    public UserDto save(UserDto userDto, OrganizationDto organizationDto) {
        //获取用户所属机构的管理员角色并绑定给当前用户
        RoleDto roleDto = roleBusiness.findByOrganization(organizationDto);
        userDto.setOrg(organizationDto);
        userDto.setRoleDto(roleDto);
        Response<UserDto> response = userReference.addition(userDto);
        String code = response.getCode();
        if ("200".equals(code)) {
            return response.getResult();
        } else if (ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION.equals(code)) {
            throw new NetflixCircuitException(code);
        }
        throw new ServiceException(response.getCode());
    }

    public UserDto saveUserRole(Long id, Long[] roleIds) {
        return null;
    }
}
