package com.waben.stock.applayer.admin.controller.manage;

import com.waben.stock.applayer.admin.business.manage.OperationLogBusiness;
import com.waben.stock.applayer.admin.business.manage.StaffBusiness;
import com.waben.stock.interfaces.dto.admin.publisher.CapitalAccountAdminDto;
import com.waben.stock.interfaces.dto.manage.OperationLogDto;
import com.waben.stock.interfaces.dto.manage.StaffDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.OperationLogQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.publisher.CapitalAccountAdminQuery;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import com.waben.stock.interfaces.vo.manage.OperationLogVo;
import com.waben.stock.interfaces.vo.manage.RoleVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: zengzhiwei
 * @date: 2018/7/28 14:17
 * @desc：
 */
@RestController
@RequestMapping("/operation_log")
@Api(description="后台操作日志")
public class OperationLogController {

    @Autowired
    private OperationLogBusiness business;

    @Autowired
    private StaffBusiness staffBusiness;

    @GetMapping("/pages")
    @ApiOperation(value = "操作日志分页")
    public Response<PageInfo<OperationLogVo>> pages(OperationLogQuery query) {
        PageInfo<OperationLogDto> pageInfo = business.pages(query);
        List<OperationLogVo> content = CopyBeanUtils.copyListBeanPropertiesToList(pageInfo.getContent(), OperationLogVo.class);
        PageInfo<OperationLogVo> response = new PageInfo<>(content, pageInfo.getTotalPages(), pageInfo.getLast(),
                pageInfo.getTotalElements(), pageInfo.getSize(), pageInfo.getNumber(), pageInfo.getFrist());
        for(OperationLogVo operationLogVo : response.getContent()) {
            StaffDto staffDto = staffBusiness.findById(operationLogVo.getStaffId());
            operationLogVo.setStaffName(staffDto.getUserName());
        }
        return new Response<>(response);
    }

}
