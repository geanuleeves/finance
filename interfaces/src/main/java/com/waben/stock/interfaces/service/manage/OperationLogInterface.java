package com.waben.stock.interfaces.service.manage;

import com.waben.stock.interfaces.dto.manage.OperationLogDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.OperationLogQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.publisher.CapitalAccountAdminQuery;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author: zengzhiwei
 * @date: 2018/7/27 17:36
 * @desc：后台操作日志服务接口
 */
@FeignClient(name = "manage", path = "operation_log", qualifier = "operationLogInterface")
public interface OperationLogInterface {

    @RequestMapping(value = "/save", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    Response<OperationLogDto> add(OperationLogDto dto);


    @RequestMapping(value = "/pages", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    Response<PageInfo<OperationLogDto>> pages(@RequestBody OperationLogQuery query);
}
