package com.waben.stock.applayer.promotion.controller;

import com.waben.stock.applayer.promotion.util.SecurityAccount;
import com.waben.stock.interfaces.dto.organization.OrganizationDto;
import com.waben.stock.interfaces.dto.organization.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.applayer.promotion.business.OrganizationAccountFlowBusiness;
import com.waben.stock.applayer.promotion.business.OrganizationBusiness;
import com.waben.stock.interfaces.dto.organization.OrganizationAccountFlowDto;
import com.waben.stock.interfaces.dto.organization.OrganizationAccountFlowWithTradeInfoDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.organization.OrganizationAccountFlowQuery;

import io.swagger.annotations.Api;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/orgflow")
@Api(description = "结算管理")
public class OrganizationAccountFlowController {

    @Autowired
    public OrganizationAccountFlowBusiness organizationAccountFlowBusiness;

    @Autowired
    public OrganizationBusiness organizationBusiness;
    
    @RequestMapping(value = "/pages", method = RequestMethod.POST)
    public Response<PageInfo<OrganizationAccountFlowDto>> pages(@RequestBody OrganizationAccountFlowQuery query) {
        UserDto userDto = (UserDto) SecurityAccount.current().getSecurity();
        query.setOrgId(userDto.getOrg().getId());
        return new Response<>(organizationAccountFlowBusiness.pages(query));
    }
    
    @RequestMapping(value = "/pagesWithTradeInfo", method = RequestMethod.POST)
    public Response<PageInfo<OrganizationAccountFlowWithTradeInfoDto>> pagesWithTradeInfo(@RequestBody OrganizationAccountFlowQuery query) {
        UserDto userDto = (UserDto) SecurityAccount.current().getSecurity();
        query.setOrgId(userDto.getOrg().getId());
        return new Response<>(organizationAccountFlowBusiness.pagesWithTradeInfo(query));
    }

    //渠道分成报表
    @RequestMapping(value = "/childpages", method = RequestMethod.POST)
    public Response<PageInfo<OrganizationAccountFlowDto>> childPages(@RequestBody OrganizationAccountFlowQuery query) {
        UserDto userDto = (UserDto) SecurityAccount.current().getSecurity();
        List<OrganizationDto> organizationDtos = organizationBusiness.listByParentId(userDto.getOrg().getId());
        List<Long> orgIds = new ArrayList<>();
        for (OrganizationDto orgDto:organizationDtos){
            orgIds.add(orgDto.getId());
        }
        if(orgIds.size()==0){
            PageInfo<OrganizationAccountFlowDto>  organizationAccountFlowDtoPage = new PageInfo<>();
            List<OrganizationAccountFlowDto> organizationAccountFlowDtos = new ArrayList<>();
            organizationAccountFlowDtoPage.setFrist(true);
            organizationAccountFlowDtoPage.setLast(true);
            organizationAccountFlowDtoPage.setNumber(0);
            organizationAccountFlowDtoPage.setSize(10);
            organizationAccountFlowDtoPage.setTotalElements(0l);
            organizationAccountFlowDtoPage.setTotalPages(0);
            organizationAccountFlowDtoPage.setContent(organizationAccountFlowDtos);
            return new Response<>(organizationAccountFlowDtoPage);
        }
        PageInfo<OrganizationAccountFlowDto> organizationAccountFlowDtoPageInfo = organizationAccountFlowBusiness.pages(query);
        return new Response<>(organizationAccountFlowDtoPageInfo);
    }

}
