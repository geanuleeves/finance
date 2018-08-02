package com.waben.stock.datalayer.manage.controller;

import com.waben.stock.datalayer.manage.entity.OperationLog;
import com.waben.stock.datalayer.manage.entity.Permission;
import com.waben.stock.datalayer.manage.service.OperationLogService;
import com.waben.stock.interfaces.dto.manage.OperationLogDto;
import com.waben.stock.interfaces.dto.manage.PermissionDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.OperationLogQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.service.manage.OperationLogInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import com.waben.stock.interfaces.util.PageToPageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: zengzhiwei
 * @date: 2018/7/27 17:38
 * @desc：
 */
@RestController
@RequestMapping("/operation_log")
public class OperationLogController implements OperationLogInterface {

    @Autowired
    private OperationLogService service;

    @Override
    public Response<OperationLogDto> add(@RequestBody OperationLogDto requestDto) {
        OperationLog request = CopyBeanUtils.copyBeanProperties(requestDto, new OperationLog(), false);
        OperationLog response = service.save(request);
        OperationLogDto responseDto = CopyBeanUtils.copyBeanProperties(response, new OperationLogDto(), false);
        return new Response<>(responseDto);
    }

    @Override
    public Response<PageInfo<OperationLogDto>> pages(@RequestBody OperationLogQuery query) {
        Page<OperationLog> page = service.pages(query);
        PageInfo<OperationLogDto> result = PageToPageInfo.pageToPageInfo(page, OperationLogDto.class);
        return new Response<>(result);
    }
}
