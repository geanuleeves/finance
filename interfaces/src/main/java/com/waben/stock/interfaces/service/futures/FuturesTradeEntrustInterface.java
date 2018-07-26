package com.waben.stock.interfaces.service.futures;

import com.waben.stock.interfaces.dto.futures.FuturesTradeEntrustDto;
import com.waben.stock.interfaces.pojo.Response;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 交易委托
 *
 * @author chenk 2018/7/26
 */
@FeignClient(name = "futures", path = "trade_entrust", qualifier = "futuresTradeEntrustInterface")
public interface FuturesTradeEntrustInterface {

    /**
     * 根据ID获取交易委托
     *
     * @param id 订单ID
     * @return 订单
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    Response<FuturesTradeEntrustDto> fetchById(@PathVariable("id") Long id);

    /**
     * 新增交易委托
     *
     * @param dto 订单ID
     * @return 订单
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST, consumes = "application/json")
    Response<FuturesTradeEntrustDto> save(@RequestBody FuturesTradeEntrustDto dto);

    /**
     * 修改交易委托
     *
     * @param dto 订单ID
     * @return 订单
     */
    @RequestMapping(value = "/modify", method = RequestMethod.POST, consumes = "application/json")
    Response<FuturesTradeEntrustDto> modify(@RequestBody FuturesTradeEntrustDto dto);

    /**
     * 删除交易委托
     *
     * @param id 订单ID
     * @return 订单
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    Response<String> delete(@PathVariable("id") Long id);


}
