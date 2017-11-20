package com.waben.stock.datalayer.manage.controller;

import com.waben.stock.datalayer.manage.entity.Staff;
import com.waben.stock.datalayer.manage.service.StaffService;
import com.waben.stock.interfaces.dto.manage.StaffDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.StaffQuery;
import com.waben.stock.interfaces.service.manage.StaffInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @Override
    public Response<StaffDto> fetchByUserName(@PathVariable String username) {
        Staff staff = staffService.fetchByUserName(username);
        StaffDto staffDto = CopyBeanUtils.copyBeanProperties(staff, new StaffDto(), false);
        return new Response<>(staffDto);
    }

    @Override
    public Response<PageInfo<StaffDto>> pagesByQuery(@RequestBody StaffQuery staffQuery) {
        Page<Staff> staff = staffService.pagesByQuery(staffQuery);
        List<Staff> staffs = staff.getContent();
        List<StaffDto> staffDtos = CopyBeanUtils.copyListBeanPropertiesToList(staffs, StaffDto.class);
        PageInfo<StaffDto> page = new PageInfo<>(staffDtos, staff.getTotalPages(), staff.isLast(), staff
                .getTotalElements(),staff.getSize(),staff.getNumber(),staff.isFirst());
        return new Response<>(page);
    }
}
