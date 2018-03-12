package com.waben.stock.applayer.promotion.reference.fallback;

import org.springframework.stereotype.Component;

import com.waben.stock.applayer.promotion.reference.organization.OrganizationAccountFlowReference;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.organization.OrganizationAccountFlowDto;
import com.waben.stock.interfaces.exception.NetflixCircuitException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.organization.OrganizationAccountFlowQuery;

@Component
public class OrganizationAccountFlowFallback implements OrganizationAccountFlowReference {
    @Override
    public Response<PageInfo<OrganizationAccountFlowDto>> pages(OrganizationAccountFlowQuery query) {
        throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

    @Override
    public Response<PageInfo<OrganizationAccountFlowDto>> childpages(OrganizationAccountFlowQuery query) {
        throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }
}