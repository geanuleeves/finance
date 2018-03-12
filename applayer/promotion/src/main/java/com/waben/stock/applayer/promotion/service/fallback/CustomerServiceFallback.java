package com.waben.stock.applayer.promotion.service.fallback;

import com.waben.stock.applayer.promotion.service.organization.CustomerService;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.organization.CustomerDto;
import com.waben.stock.interfaces.exception.NetflixCircuitException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.organization.CustomerQuery;
import org.springframework.stereotype.Component;

/**
 * 客户 reference服务接口fallback
 *
 * @author luomengan
 */
@Component
public class CustomerServiceFallback implements CustomerService {

    @Override
    public Response<PageInfo<CustomerDto>> adminPage(CustomerQuery query) {
        throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

}
