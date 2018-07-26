package com.waben.stock.applayer.promotion.business;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.interfaces.dto.futures.FuturesCommodityDto;
import com.waben.stock.interfaces.dto.manage.RoleDto;
import com.waben.stock.interfaces.dto.organization.BenefitConfigDto;
import com.waben.stock.interfaces.dto.organization.FuturesAgentPriceDto;
import com.waben.stock.interfaces.dto.organization.OrganizationDetailDto;
import com.waben.stock.interfaces.dto.organization.OrganizationDto;
import com.waben.stock.interfaces.dto.organization.OrganizationStaDto;
import com.waben.stock.interfaces.dto.organization.TradingFowDto;
import com.waben.stock.interfaces.dto.organization.TreeNode;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.form.organization.OrganizationForm;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.organization.OrganizationQuery;
import com.waben.stock.interfaces.pojo.query.organization.OrganizationStaQuery;
import com.waben.stock.interfaces.pojo.query.organization.TradingFowQuery;
import com.waben.stock.interfaces.service.futures.FuturesCommodityInterface;
import com.waben.stock.interfaces.service.futures.FuturesContractInterface;
import com.waben.stock.interfaces.service.organization.OrganizationInterface;

/**
 * 机构 Business
 *
 * @author lma
 */
@Service
public class OrganizationBusiness {

	@Autowired
	@Qualifier("organizationInterface")
	private OrganizationInterface reference;

	@Autowired
	private RoleBusiness roleBusiness;

	@Autowired
	@Qualifier("futurescontractInterface")
	private FuturesContractInterface futuresContractInterface;

	@Autowired
	@Qualifier("futuresCommodityInterface")
	private FuturesCommodityInterface futuresCommodityInterface;

	public OrganizationDto addition(OrganizationForm orgForm) {
		Response<OrganizationDto> response = reference.addition(orgForm);
		// 创建机构后为机构默认创建一个管理员角色
		if ("200".equals(response.getCode())) {
			RoleDto orgAdminRole = new RoleDto();
			orgAdminRole.setCode("ADMIN");
			orgAdminRole.setName("管理员");
			orgAdminRole.setDescription("渠道管理员");
			orgAdminRole.setOrganization(response.getResult().getId());
			RoleDto role = roleBusiness.save(orgAdminRole);
			if (role != null) {
				return response.getResult();
			}
		}
		throw new ServiceException(response.getCode());
	}

	public PageInfo<OrganizationDto> adminPage(OrganizationQuery query) {
		Response<PageInfo<OrganizationDto>> response = reference.adminPage(query);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public PageInfo<OrganizationDto> pages(OrganizationQuery query) {
		Response<PageInfo<OrganizationDto>> response = reference.pages(query);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public List<TreeNode> adminTree(Long orgId) {
		return reference.adminTree(orgId);
	}

	public List<OrganizationDto> listByParentId(Long parentId) {
		Response<List<OrganizationDto>> response = reference.listByParentId(parentId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public OrganizationDetailDto detail(Long orgId) {
		Response<OrganizationDetailDto> response = reference.detail(orgId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public OrganizationDto modifyName(Long id, String name, BigDecimal billCharge, Integer settlementType) {
		Response<OrganizationDto> response = reference.modifyName(id, name, billCharge, settlementType);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public OrganizationDto findByCode(String code) {
		Response<OrganizationDto> response = reference.fetchByCode(code);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public PageInfo<OrganizationDetailDto> adminAgentPageByQuery(OrganizationQuery query) {
		Response<PageInfo<OrganizationDetailDto>> response = reference.adminAgentPageByQuery(query);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public PageInfo<OrganizationStaDto> adminStaPageByQuery(OrganizationStaQuery query) {
		Response<PageInfo<OrganizationStaDto>> response = reference.adminStaPageByQuery(query);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public OrganizationDto agent(OrganizationForm orgForm) {
		Response<OrganizationDto> response = reference.addition(orgForm);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public PageInfo<TradingFowDto> tradingFowPageByQuery(TradingFowQuery query) {
		Response<PageInfo<TradingFowDto>> response = reference.tradingFowPageByQuery(query);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public List<OrganizationDto> findAll() {
		Response<List<OrganizationDto>> response = reference.fetchAll();
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public OrganizationDto findByOrgId(Long orgId) {
		Response<OrganizationDto> response = reference.fetchByOrgId(orgId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public List<FuturesAgentPriceDto> getListByFuturesAgentPrice(Long orgId) {
		Response<List<FuturesAgentPriceDto>> response = reference.getListByFuturesAgentPrice(orgId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public Integer saveFuturesAgentPrice(List<FuturesAgentPriceDto> futuresAgentPricedto) {
		Response<Integer> response = reference.saveFuturesAgentPrice(futuresAgentPricedto);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public FuturesAgentPriceDto getCurrentAgentPrice(Long orgId, Long commodityId) {
		Response<FuturesAgentPriceDto> response = reference.getCurrentAgentPrice(orgId, commodityId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public FuturesAgentPriceDto getSuperiorAgentPrice(Long orgId, Long commodityId) {
		Response<FuturesAgentPriceDto> response = reference.getSuperiorAgentPrice(orgId, commodityId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public FuturesCommodityDto getFuturesByContractId(Long commodityId) {
		Response<FuturesCommodityDto> response = futuresCommodityInterface.getFuturesByCommodityId(commodityId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public Integer addAgentPartition(BigDecimal ratio, BigDecimal platformRatio, Long orgId, Long id) {
		Response<Integer> response = reference.addAgentPartition(ratio, platformRatio, orgId, id);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public BenefitConfigDto getSuperiorAgentPartition(Long orgId) {
		Response<BenefitConfigDto> response = reference.getSuperiorAgentPartition(orgId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public BigDecimal getSumRatio(Long orgId) {
		Response<BigDecimal> response = reference.getSumRatio(orgId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

}
