package com.waben.stock.applayer.admin.controller.manage;

import com.waben.stock.applayer.admin.business.manage.PermissionBusiness;
import com.waben.stock.interfaces.dto.manage.PermissionDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import com.waben.stock.interfaces.vo.manage.PermissionVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: zengzhiwei
 * @date: 2018/7/30 11:09
 * @desc：
 */
@RestController
@RequestMapping("/permission")
@Api(description="权限")
public class PermissionController {


    @Autowired
    private PermissionBusiness business;

    @GetMapping("/")
    @ApiOperation(value = "获取所有权限")
    public Response<List<PermissionVo>> getPermissions() {
        List<PermissionDto> permissions = business.getPermissionsByVariety();
        List<PermissionVo> permissionVos = CopyBeanUtils.copyListBeanPropertiesToList(permissions, PermissionVo.class);
        return new Response<>(permissionVos);
    }

}
