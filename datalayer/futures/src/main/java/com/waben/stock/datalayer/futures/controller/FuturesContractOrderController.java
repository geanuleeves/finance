package com.waben.stock.datalayer.futures.controller;

import com.waben.stock.datalayer.futures.entity.FuturesContractOrder;
import com.waben.stock.datalayer.futures.service.FuturesContractOrderService;
import com.waben.stock.interfaces.dto.futures.FuturesContractOrderDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.service.futures.FuturesContractOrderInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 合约订单
 *
 * @author chenk 2018/7/26
 */
@RestController
@RequestMapping("/contract_order")
@Api(description = "合约订单接口列表")
public class FuturesContractOrderController implements FuturesContractOrderInterface {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FuturesContractOrderService futuresContractOrderService;

    @Override
    public Response<FuturesContractOrderDto> fetchById(@PathVariable Long id) {
        return new Response<>(
                CopyBeanUtils.copyBeanProperties(FuturesContractOrderDto.class, futuresContractOrderService.findById(id), false));
    }

    @Override
    public Response<FuturesContractOrderDto> save(@RequestBody FuturesContractOrderDto dto) {
        FuturesContractOrder futuresContractOrder = CopyBeanUtils.copyBeanProperties(
                FuturesContractOrder.class, dto, false);
        FuturesContractOrder result = futuresContractOrderService.save(futuresContractOrder);
        FuturesContractOrderDto response = CopyBeanUtils.copyBeanProperties(result, new FuturesContractOrderDto(),
                false);
        return new Response<>(response);
    }

    @Override
    public Response<FuturesContractOrderDto> modify(@RequestBody FuturesContractOrderDto dto) {
        FuturesContractOrder futuresContractOrder = CopyBeanUtils.copyBeanProperties(FuturesContractOrder.class, dto, false);
        FuturesContractOrder result = futuresContractOrderService.modify(futuresContractOrder);
        FuturesContractOrderDto resultDto = CopyBeanUtils.copyBeanProperties(result, new FuturesContractOrderDto(),
                false);
        return new Response<>(resultDto);
    }

    @Override
    public Response<String> delete(Long id) {
        futuresContractOrderService.delete(id);
        Response<String> res = new Response<String>();
        res.setCode("200");
        res.setMessage("响应成功");
        res.setResult("1");
        return res;
    }

}
