package com.waben.stock.applayer.admin.business.manage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.manage.MenuDto;
import com.waben.stock.interfaces.dto.manage.RoleDto;
import com.waben.stock.interfaces.exception.NetflixCircuitException;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.RoleQuery;
import com.waben.stock.interfaces.service.manage.RoleInterface;

/**
 * @author Created by yuyidi on 2017/12/11.
 * @desc
 */
@Service
public class RoleBusiness {

    @Autowired
    @Qualifier("roleInterface")
    private RoleInterface roleReference;
    
    @Autowired
    private MenuBusiness menuBusiness;

    public PageInfo<RoleDto> pages(RoleQuery query) {
        query.setType(1);
        Response<PageInfo<RoleDto>> response = roleReference.pages(query);
        String code = response.getCode();
        if ("200".equals(code)) {
        	for (RoleDto roleDto : response.getResult().getContent()) {
                menuBusiness.childsMenu(roleDto.getMenusDtos());
             }
            return response.getResult();
        }else if(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION.equals(code)){
            throw new NetflixCircuitException(code);
        }
        throw new ServiceException(response.getCode());
    }

    public RoleDto fetchById(Long id) {
        Response<RoleDto> response = roleReference.fetchById(id);
        String code = response.getCode();
        if ("200".equals(code)) {
            return response.getResult();
        }else if(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION.equals(code)){
            throw new NetflixCircuitException(code);
        }
        throw new ServiceException(response.getCode());
    }

    public RoleDto revision(Long id, String name, String menuIds) {
    	RoleDto roleDto = roleReference.role(id).getResult();
        roleDto.setMenusDtos(menuDtos(parentMenuIds(getMenusId(menuIds))));
        roleDto.setName(name);
        Response<RoleDto> response = roleReference.add(roleDto);
        String code = response.getCode();
        if ("200".equals(code)) {
            return response.getResult();
        }else if(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION.equals(code)){
            throw new NetflixCircuitException(code);
        }
        throw new ServiceException(response.getCode());
    }

    public void delete(Long id) {
        roleReference.delete(id);
    }

    public RoleDto save(String name, String menuIds) {
    	RoleDto roleDto = new RoleDto();
        roleDto.setName(name);
        roleDto.setCreateTime(new Date());
        roleDto.setType(1);
        roleDto.setMenusDtos(menuDtos(parentMenuIds(getMenusId(menuIds))));
    	
        Response<RoleDto> response = roleReference.add(roleDto);
        String code = response.getCode();
        if ("200".equals(code)) {
            return response.getResult();
        }else if(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION.equals(code)){
            throw new NetflixCircuitException(code);
        }
        throw new ServiceException(response.getCode());
    }

    public List<RoleDto> fetchRoles() {
        Response<List<RoleDto>> response = roleReference.fetchRoles();
        String code = response.getCode();
        if ("200".equals(code)) {
            return response.getResult();
        } else if (ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION.equals(code)) {
            throw new NetflixCircuitException(code);
        }
        throw new ServiceException(response.getCode());
    }
    
    public List<Long> getMenusId(String menuIds) {
        String[] split = menuIds.split(",");
        List<Long> ids = new ArrayList<>();
        for(String id : split) {
            ids.add(Long.parseLong(id));
        }
        return ids;
    }
    
    public Set<MenuDto> menuDtos(List<Long> menuIds) {
        Set<MenuDto> menuDtos = new HashSet();
        for(Long menuId : menuIds) {
            MenuDto menuDto = new MenuDto();
            menuDto.setId(menuId);
            menuDtos.add(menuDto);
        }
        return menuDtos;
    }

    public List<Long> parentMenuIds(List<Long> menuIds) {
        Set<Long> menus = new HashSet<>();
        for(Long menuId : menuIds) {
            MenuDto menu = menuBusiness.findById(menuId);
            menus.add(menu.getPid());
        }
        menuIds.addAll(menus);
        return menuIds;
    }

    public RoleDto updateRolePermission(Long id, String permissionIds) {
        String[] split = permissionIds.split(",");
        Long[] ids = new Long[split.length];
        for (int i=0; i<split.length; i++){
            ids[i] = Long.getLong(split[i]);
        }
        Response<RoleDto> response = roleReference.addRolePermission(id, ids);
        String code = response.getCode();
        if ("200".equals(code)) {
            return response.getResult();
        }else if(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION.equals(code)){
            throw new NetflixCircuitException(code);
        }
        throw new ServiceException(response.getCode());
    }
}
