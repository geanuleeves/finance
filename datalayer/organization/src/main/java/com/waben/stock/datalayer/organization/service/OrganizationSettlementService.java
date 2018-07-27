package com.waben.stock.datalayer.organization.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.waben.stock.datalayer.organization.business.FuturesCommodityBusiness;
import com.waben.stock.datalayer.organization.entity.BenefitConfig;
import com.waben.stock.datalayer.organization.entity.FuturesAgentPrice;
import com.waben.stock.datalayer.organization.entity.Organization;
import com.waben.stock.datalayer.organization.entity.OrganizationAccountFlow;
import com.waben.stock.datalayer.organization.entity.OrganizationPublisher;
import com.waben.stock.datalayer.organization.entity.SettlementMethod;
import com.waben.stock.datalayer.organization.repository.BenefitConfigDao;
import com.waben.stock.datalayer.organization.repository.FuturesAgentPriceDao;
import com.waben.stock.datalayer.organization.repository.OrganizationAccountFlowDao;
import com.waben.stock.datalayer.organization.repository.OrganizationDao;
import com.waben.stock.datalayer.organization.repository.OrganizationPublisherDao;
import com.waben.stock.datalayer.organization.repository.SettlementMethodDao;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.futures.FuturesCommodityDto;
import com.waben.stock.interfaces.enums.BenefitConfigType;
import com.waben.stock.interfaces.enums.OrganizationAccountFlowType;
import com.waben.stock.interfaces.enums.ResourceType;
import com.waben.stock.interfaces.exception.ServiceException;

/**
 * 机构结算 Service
 *
 * @author lma
 *
 */
@Service
public class OrganizationSettlementService {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private OrganizationPublisherDao orgPublisherDao;

	@Autowired
	private OrganizationDao orgDao;

	@Autowired
	private BenefitConfigDao benefitConfigDao;

	@Autowired
	private OrganizationAccountService accountService;

	@Autowired
	private OrganizationAccountFlowDao flowDao;

	@Autowired
	private SettlementMethodDao settlementMethodDao;

	@Autowired
	private FuturesAgentPriceDao agentPriceDao;

	@Autowired
	private FuturesCommodityBusiness commodityBusiness;

	@Autowired
	private OrganizationDao organizationDao;

	@Transactional
	public void futuresRatioSettlement(Long publisherId, Long benefitResourceId, Long futuresOrderId, String tradeNo,
			BigDecimal totalQuantity, BigDecimal serviceFee, BigDecimal orderCloseFee, BigDecimal deferredFee) {
		// 参与结算返佣金额 = 服务费 + 订单盈亏金额 + 递延费
		BigDecimal comprehensiveFee = serviceFee.subtract(orderCloseFee).add(deferredFee);
		logger.info("参与结算返佣金额, serviceFee:{},orderCloseFee:{},deferredFee:{}", serviceFee, orderCloseFee, deferredFee);
		// 结算
		List<OrganizationAccountFlow> checkFlowList = flowDao.retrieveByTypeAndResourceTypeAndResourceId(
				OrganizationAccountFlowType.FuturesComprehensiveFeeAssign, ResourceType.FUTURESORDER, futuresOrderId);
		// 判断之前是否结算过
		if (checkFlowList == null || checkFlowList.size() == 0) {
			logger.info("参与结算返佣金额未结算前日志, tradeNo{},serviceFee:{},orderCloseFee:{},deferredFee:{}", tradeNo, serviceFee,
					orderCloseFee, deferredFee);
			futuresOrderRatioSettlement(publisherId, BenefitConfigType.FuturesComprehensiveFee,
					OrganizationAccountFlowType.FuturesComprehensiveFeeAssign, comprehensiveFee, benefitResourceId,
					futuresOrderId, tradeNo);
		}
	}

	@Transactional
	public void futuresSettlement(Long publisherId, Long commodityId, Long futuresOrderId, String tradeNo,
			BigDecimal totalQuantity, BigDecimal openingFee, BigDecimal closeFee) {
		// 结算开仓手续费
		if (openingFee != null && openingFee.compareTo(BigDecimal.ZERO) > 0) {
			List<OrganizationAccountFlow> checkFlowList = flowDao.retrieveByTypeAndResourceTypeAndResourceId(
					OrganizationAccountFlowType.FuturesOpeningFeeAssign, ResourceType.FUTURESORDER, futuresOrderId);
			// 判断之前是否结算过
			if (checkFlowList == null || checkFlowList.size() == 0) {
				futuresOrderSettlement(commodityId, publisherId, OrganizationAccountFlowType.FuturesOpeningFeeAssign,
						openingFee, totalQuantity, futuresOrderId, tradeNo);
			}
		}
		// 结算平仓手续费
		if (closeFee != null && closeFee.compareTo(BigDecimal.ZERO) > 0) {
			List<OrganizationAccountFlow> checkFlowList = flowDao.retrieveByTypeAndResourceTypeAndResourceId(
					OrganizationAccountFlowType.FuturesCloseFeeAssigne, ResourceType.FUTURESORDER, futuresOrderId);
			// 判断之前是否结算过
			if (checkFlowList == null || checkFlowList.size() == 0) {
				futuresOrderSettlement(commodityId, publisherId, OrganizationAccountFlowType.FuturesCloseFeeAssigne,
						closeFee, totalQuantity, futuresOrderId, tradeNo);
			}
		}
	}

	@Transactional
	public void futuresDeferredSettlement(Long publisherId, Long commodityId, Long overnightRecordId, String tradeNo,
			BigDecimal totalQuantity, BigDecimal deferredFee) {
		// 结算递延费
		if (deferredFee != null && deferredFee.compareTo(BigDecimal.ZERO) > 0) {
			List<OrganizationAccountFlow> checkFlowList = flowDao.retrieveByTypeAndResourceTypeAndResourceId(
					OrganizationAccountFlowType.FuturesDeferredFeeAssign, ResourceType.FUTURESOVERNIGHTRECORD,
					overnightRecordId);
			// 判断之前是否结算过
			if (checkFlowList == null || checkFlowList.size() == 0) {
				futuresOrderSettlement(commodityId, publisherId, OrganizationAccountFlowType.FuturesDeferredFeeAssign,
						deferredFee, totalQuantity, overnightRecordId, tradeNo);
			}
		}
	}

	@Transactional
	public void strategySettlement(Long publisherId, Long buyRecordId, String tradeNo, Long strategyTypeId,
			BigDecimal serviceFee, BigDecimal deferredFee) {
		// 结算服务费
		if (serviceFee != null && serviceFee.compareTo(BigDecimal.ZERO) > 0) {
			List<OrganizationAccountFlow> checkFlowList = flowDao.retrieveByTypeAndResourceTypeAndResourceId(
					OrganizationAccountFlowType.ServiceFeeAssign, ResourceType.BUYRECORD, buyRecordId);
			// 判断之前是否结算过
			if (checkFlowList == null || checkFlowList.size() == 0) {
				settlement(publisherId, BenefitConfigType.ServiceFee, serviceFee, strategyTypeId, buyRecordId, tradeNo);
			}
		}
		// 结算递延费
		if (deferredFee != null && deferredFee.compareTo(BigDecimal.ZERO) > 0) {
			List<OrganizationAccountFlow> checkFlowList = flowDao.retrieveByTypeAndResourceTypeAndResourceId(
					OrganizationAccountFlowType.DeferredChargesAssign, ResourceType.BUYRECORD, buyRecordId);
			// 判断之前是否结算过
			if (checkFlowList == null || checkFlowList.size() == 0) {
				settlement(publisherId, BenefitConfigType.DeferredFee, deferredFee, strategyTypeId, buyRecordId,
						tradeNo);
			}
		}
	}

	public void stockoptionSettlement(Long publisherId, Long stockOptionTradeId, String tradeNo, Long cycleId,
			BigDecimal rightMoneyProfit, BigDecimal rightMoney) {
		// 判断之前是否结算过
		List<OrganizationAccountFlow> checkFlowList = flowDao.retrieveByTypeAndResourceTypeAndResourceId(
				OrganizationAccountFlowType.RightMoneyAssign, ResourceType.STOCKOPTIONTRADE, stockOptionTradeId);
		if (checkFlowList != null && checkFlowList.size() > 0) {
			return;
		}
		// 没有结算过
		SettlementMethod settlementMethod = settlementMethodDao.list().get(0);
		if (settlementMethod.getSettlementType() == 1) {
			// 分成结算，结算权利金收益
			if (rightMoneyProfit != null && rightMoneyProfit.compareTo(BigDecimal.ZERO) > 0) {
				settlement(publisherId, BenefitConfigType.RightMoney, rightMoneyProfit, cycleId, stockOptionTradeId,
						tradeNo);
			}
		} else {
			// 返佣结算，根据用户权利金结算
			if (rightMoney != null && rightMoney.compareTo(BigDecimal.ZERO) > 0) {
				stockOptionRakeBackSettlement(publisherId, BenefitConfigType.RightMoney, rightMoney, cycleId,
						stockOptionTradeId, tradeNo);
			}
		}
	}

	private void stockOptionRakeBackSettlement(Long publisherId, BenefitConfigType benefitConfigType, BigDecimal amount,
			Long benefitResourceId, Long flowResourceId, String tradeNo) {
		OrganizationAccountFlowType flowType = OrganizationAccountFlowType.RightMoneyAssign;
		ResourceType flowResourceType = ResourceType.STOCKOPTIONTRADE;
		Integer benefitResourceType = 2;

		List<Organization> orgTreeList = getPublisherOrgTreeList(publisherId);
		if (orgTreeList != null) {
			List<BenefitConfig> benefitConfigList = getRakeBackBenefitConfigList(orgTreeList, benefitConfigType,
					benefitResourceType, benefitResourceId);
			if (benefitConfigList != null && benefitConfigList.size() > 0
					&& benefitConfigList.get(0).getRatio().compareTo(BigDecimal.ZERO) > 0) {
				int length = benefitConfigList.size();
				// 检查分佣比例是否正确，必须保证
				for (int i = length - 1; i > 0; i--) {
					BigDecimal childRatio = benefitConfigList.get(i).getRatio();
					BigDecimal parentRatio = benefitConfigList.get(i - 1).getRatio();
					if (childRatio.compareTo(parentRatio) > 0) {
						throw new ServiceException(ExceptionConstant.RAKEBACK_RATIO_WRONG_EXCEPTION);
					}
				}
				// 自底向上结算
				for (int i = length - 1; i >= 0; i--) {
					BenefitConfig config = benefitConfigList.get(i);
					BigDecimal ratio = config.getRatio();
					BigDecimal computeRatio = ratio;
					if (i != length - 1) {
						computeRatio = ratio.subtract(benefitConfigList.get(i + 1).getRatio());
					}
					// 给当前机构计算
					BigDecimal childFee = amount.multiply(computeRatio.divide(new BigDecimal("100"))).setScale(2,
							RoundingMode.DOWN);
					if (childFee.compareTo(BigDecimal.ZERO) > 0) {
						accountService.benefit(config.getOrg(), amount, childFee, flowType, flowResourceType,
								flowResourceId, tradeNo);
					}
				}
				// 剩余的结算给一级机构
				BigDecimal platformFee = amount.multiply(new BigDecimal("100")
						.subtract(benefitConfigList.get(0).getRatio()).divide(new BigDecimal("100")))
						.setScale(2, RoundingMode.DOWN);
				accountService.benefit(orgTreeList.get(0), amount, platformFee, flowType, flowResourceType,
						flowResourceId, tradeNo);
			}
		}
		accountService.benefit(null, amount, amount, flowType, flowResourceType, flowResourceId, tradeNo);
	}

	private void settlement(Long publisherId, BenefitConfigType benefitConfigType, BigDecimal amount,
			Long benefitResourceId, Long flowResourceId, String tradeNo) {
		OrganizationAccountFlowType flowType = null;
		ResourceType flowResourceType = null;
		Integer benefitResourceType = null;
		if (BenefitConfigType.ServiceFee == benefitConfigType) {
			flowType = OrganizationAccountFlowType.ServiceFeeAssign;
			flowResourceType = ResourceType.BUYRECORD;
			benefitResourceType = 1;
		} else if (BenefitConfigType.DeferredFee == benefitConfigType) {
			flowType = OrganizationAccountFlowType.DeferredChargesAssign;
			flowResourceType = ResourceType.BUYRECORD;
			benefitResourceType = 1;
		} else if (BenefitConfigType.RightMoney == benefitConfigType) {
			flowType = OrganizationAccountFlowType.RightMoneyAssign;
			flowResourceType = ResourceType.STOCKOPTIONTRADE;
			benefitResourceType = 2;
		} else {
			throw new RuntimeException("not supported benefitConfigType?");
		}

		List<Organization> orgTreeList = getPublisherOrgTreeList(publisherId);
		if (orgTreeList != null) {
			List<BenefitConfig> benefitConfigList = getBenefitConfigList(orgTreeList, benefitConfigType,
					benefitResourceType, benefitResourceId);
			if (benefitConfigList != null && benefitConfigList.size() > 0) {
				BigDecimal currentServiceFee = amount;
				for (int i = 0; i < benefitConfigList.size(); i++) {
					BenefitConfig config = benefitConfigList.get(i);
					BigDecimal ratio = config.getRatio();
					BigDecimal childServiceFee = currentServiceFee.multiply(ratio.divide(new BigDecimal("100")))
							.setScale(2, RoundingMode.DOWN);
					// 先给上级结算
					BigDecimal parentServiceFee = currentServiceFee.subtract(childServiceFee);
					accountService.benefit(orgTreeList.get(i), amount, parentServiceFee, flowType, flowResourceType,
							flowResourceId, tradeNo);
					// 最后一个给最低级机构结算
					if (i == benefitConfigList.size() - 1) {
						accountService.benefit(orgTreeList.get(i + 1), amount, childServiceFee, flowType,
								flowResourceType, flowResourceId, tradeNo);
					}
					currentServiceFee = childServiceFee;
				}
			}
		}
		accountService.benefit(null, amount, amount, flowType, flowResourceType, flowResourceId, tradeNo);
	}

	private List<BenefitConfig> getRakeBackBenefitConfigList(List<Organization> orgTreeList, BenefitConfigType type,
			Integer resourceType, Long resourceId) {
		List<BenefitConfig> result = new ArrayList<>();
		for (int i = 1; i < orgTreeList.size(); i++) {
			Organization org = orgTreeList.get(i);
			List<BenefitConfig> innerList = benefitConfigDao.retrieveByOrgAndTypeAndResourceTypeAndResourceId(org, type,
					resourceType, resourceId);
			if (innerList != null && innerList.size() > 0) {
				result.add(innerList.get(0));
			} else {
				BenefitConfig benefit = new BenefitConfig();
				benefit.setRatio(BigDecimal.ZERO);
				benefit.setOrg(org);
				result.add(benefit);
			}
		}
		return result;
	}

	private List<BenefitConfig> getFuturesRakeBackBenefitConfigList(List<Organization> orgTreeList,
			BenefitConfigType type, Integer resourceType) {
		List<BenefitConfig> result = new ArrayList<>();
		for (int i = 0; i < orgTreeList.size(); i++) {
			Organization org = orgTreeList.get(i);
			List<BenefitConfig> innerList = benefitConfigDao.retrieveByOrgAndTypeAndResourceType(org, type,
					resourceType);
			if (innerList != null && innerList.size() > 0) {
				result.add(innerList.get(0));
			} else {
				BenefitConfig benefit = new BenefitConfig();
				benefit.setRatio(BigDecimal.ZERO);
				if (org.getLevel() == 1) {
					if (orgTreeList.size() >= 2) {
						List<BenefitConfig> platformList = benefitConfigDao
								.retrieveByOrgAndTypeAndResourceType(orgTreeList.get(1), type, resourceType);
						if (platformList != null && platformList.size() > 0) {
							benefit.setRatio(platformList.get(0).getPlatformRatio());
						}
					}
				}
				benefit.setOrg(org);
				result.add(benefit);
			}
		}
		return result;
	}

	private List<BenefitConfig> getBenefitConfigList(List<Organization> orgTreeList, BenefitConfigType type,
			Integer resourceType, Long resourceId) {
		List<BenefitConfig> result = new ArrayList<>();
		for (int i = 1; i < orgTreeList.size(); i++) {
			Organization org = orgTreeList.get(i);
			List<BenefitConfig> innerList = benefitConfigDao.retrieveByOrgAndTypeAndResourceTypeAndResourceId(org, type,
					resourceType, resourceId);
			if (innerList != null && innerList.size() > 0) {
				result.add(innerList.get(0));
			} else {
				break;
			}
		}
		return result;
	}

	private List<Organization> getPublisherOrgTreeList(Long publisherId) {
		OrganizationPublisher orgPublisher = orgPublisherDao.retrieveByPublisherId(publisherId);
		if (orgPublisher != null) {
			Organization org = orgDao.retrieveByCode(orgPublisher.getOrgCode());
			if (org != null) {
				List<Organization> orgList = new ArrayList<>();
				orgList.add(org);
				while (org.getParent() != null) {
					orgList.add(org.getParent());
					org = org.getParent();
				}
				if (orgList.size() > 0 && orgList.get(orgList.size() - 1).getLevel() == 1) {
					Collections.reverse(orgList);
					return orgList;
				}
			} else {
				List<Organization> orgList = organizationDao.listByLevel(1);
				if (orgList != null && orgList.size() > 0) {
					return orgList;
				}
			}
		}
		return null;
	}

	public List<FuturesAgentPrice> getAgentPriceList(Long commodityId, List<Organization> orgTreeList) {
		List<FuturesAgentPrice> result = new ArrayList<>();
		for (int i = 0; i < orgTreeList.size(); i++) {
			Organization org = orgTreeList.get(i);
			FuturesAgentPrice agentPrice = agentPriceDao.findByCommodityIdAndOrgId(commodityId, org.getId());
			if (agentPrice != null) {
				result.add(agentPrice);
			} else {
				// if (i > 0) {
				// Organization orgParent = orgTreeList.get(i - 1);
				// FuturesAgentPrice agentPriceParent =
				// agentPriceDao.findByCommodityIdAndOrgId(commodityId,
				// orgParent.getId());
				// if (agentPriceParent != null) {
				// agentPrice = new FuturesAgentPrice();
				// agentPrice.setOrgId(org.getId());
				// agentPrice.setCostReserveFund(agentPriceParent.getCostReserveFund());
				// agentPrice.setCostOpenwindServiceFee(agentPriceParent.getCostOpenwindServiceFee());
				// agentPrice.setCostUnwindServiceFee(agentPriceParent.getCostUnwindServiceFee());
				// agentPrice.setCostDeferredFee(agentPriceParent.getCostDeferredFee());
				// agentPrice.setSaleOpenwindServiceFee(agentPriceParent.getCostOpenwindServiceFee());
				// agentPrice.setSaleUnwindServiceFee(agentPriceParent.getCostUnwindServiceFee());
				// agentPrice.setSaleDeferredFee(agentPriceParent.getCostDeferredFee());
				// } else {
				// FuturesCommodityDto commodity =
				// commodityBusiness.getFuturesByCommodityId(commodityId);
				// agentPrice = new FuturesAgentPrice();
				// agentPrice.setOrgId(org.getId());
				// agentPrice.setCostReserveFund(commodity.getPerUnitReserveFund());
				// agentPrice.setCostOpenwindServiceFee(commodity.getOpenwindServiceFee());
				// agentPrice.setCostUnwindServiceFee(commodity.getUnwindServiceFee());
				// agentPrice.setCostDeferredFee(commodity.getOvernightPerUnitDeferredFee());
				// agentPrice.setSaleOpenwindServiceFee(commodity.getOpenwindServiceFee());
				// agentPrice.setSaleUnwindServiceFee(commodity.getUnwindServiceFee());
				// agentPrice.setSaleDeferredFee(commodity.getOvernightPerUnitDeferredFee());
				// }
				// result.add(agentPrice);
				//
				// } else {
				// FuturesCommodityDto commodity =
				// commodityBusiness.getFuturesByCommodityId(commodityId);
				// agentPrice = new FuturesAgentPrice();
				// agentPrice.setOrgId(org.getId());
				// agentPrice.setCostReserveFund(commodity.getPerUnitReserveFund());
				// agentPrice.setCostOpenwindServiceFee(commodity.getOpenwindServiceFee());
				// agentPrice.setCostUnwindServiceFee(commodity.getUnwindServiceFee());
				// agentPrice.setCostDeferredFee(commodity.getOvernightPerUnitDeferredFee());
				// agentPrice.setSaleOpenwindServiceFee(commodity.getOpenwindServiceFee());
				// agentPrice.setSaleUnwindServiceFee(commodity.getUnwindServiceFee());
				// agentPrice.setSaleDeferredFee(commodity.getOvernightPerUnitDeferredFee());
				//
				// // agentPrice.setCostReserveFund(BigDecimal.ZERO);
				// // agentPrice.setCostOpenwindServiceFee(BigDecimal.ZERO);
				// // agentPrice.setCostUnwindServiceFee(BigDecimal.ZERO);
				// // agentPrice.setCostDeferredFee(BigDecimal.ZERO);
				// // agentPrice.setSaleOpenwindServiceFee(BigDecimal.ZERO);
				// // agentPrice.setSaleUnwindServiceFee(BigDecimal.ZERO);
				// // agentPrice.setSaleDeferredFee(BigDecimal.ZERO);
				// result.add(agentPrice);
				// }

				FuturesCommodityDto commodity = commodityBusiness.getFuturesByCommodityId(commodityId);
				agentPrice = new FuturesAgentPrice();
				agentPrice.setOrgId(org.getId());
				agentPrice.setCostReserveFund(commodity.getPerUnitReserveFund());
				agentPrice.setCostOpenwindServiceFee(commodity.getOpenwindServiceFee());
				agentPrice.setCostUnwindServiceFee(commodity.getUnwindServiceFee());
				agentPrice.setCostDeferredFee(commodity.getOvernightPerUnitDeferredFee());
				agentPrice.setSaleOpenwindServiceFee(commodity.getOpenwindServiceFee());
				agentPrice.setSaleUnwindServiceFee(commodity.getUnwindServiceFee());
				agentPrice.setSaleDeferredFee(commodity.getOvernightPerUnitDeferredFee());
				result.add(agentPrice);
			}
		}
		return result;
	}

	/**
	 * 获取代理商成本价
	 * 
	 * @param agentPriceList
	 *            代理商设置价格列表
	 * @param flowType
	 *            期货结算流水类型
	 * @param index
	 *            当前代理商索引
	 * @return 代理商成本价
	 */
	private BigDecimal retriveCostPrice(List<FuturesAgentPrice> agentPriceList, OrganizationAccountFlowType flowType,
			int index) {
		BigDecimal costPrice = null;
		for (int i = index; i >= 0; i--) {
			FuturesAgentPrice agentPrice = agentPriceList.get(i);
			if (flowType == OrganizationAccountFlowType.FuturesOpeningFeeAssign) {
				if (agentPrice.getCostOpenwindServiceFee() != null) {
					costPrice = agentPrice.getCostOpenwindServiceFee();
					break;
				}
			} else if (flowType == OrganizationAccountFlowType.FuturesCloseFeeAssigne) {
				if (agentPrice.getCostUnwindServiceFee() != null) {
					costPrice = agentPrice.getCostUnwindServiceFee();
					break;
				}
			} else if (flowType == OrganizationAccountFlowType.FuturesDeferredFeeAssign) {
				if (agentPrice.getCostDeferredFee() != null) {
					costPrice = agentPrice.getCostDeferredFee();
					break;
				}
			}
		}
		return costPrice;
	}

	/**
	 * 获取代理商销售价
	 * 
	 * @param agentPriceList
	 *            代理商设置价格列表
	 * @param flowType
	 *            期货结算流水类型
	 * @param index
	 *            当前代理商索引
	 * @return 代理商销售价
	 */
	private BigDecimal retriveSalePrice(List<FuturesAgentPrice> agentPriceList, OrganizationAccountFlowType flowType,
			int index) {
		BigDecimal salePrice = null;
		for (int i = index; i >= 0; i--) {
			FuturesAgentPrice agentPrice = agentPriceList.get(i);
			if (flowType == OrganizationAccountFlowType.FuturesOpeningFeeAssign) {
				if (agentPrice.getSaleOpenwindServiceFee() != null) {
					salePrice = agentPrice.getSaleOpenwindServiceFee();
					break;
				}
			} else if (flowType == OrganizationAccountFlowType.FuturesCloseFeeAssigne) {
				if (agentPrice.getSaleUnwindServiceFee() != null) {
					salePrice = agentPrice.getSaleUnwindServiceFee();
					break;
				}
			} else if (flowType == OrganizationAccountFlowType.FuturesDeferredFeeAssign) {
				if (agentPrice.getSaleDeferredFee() != null) {
					salePrice = agentPrice.getSaleDeferredFee();
					break;
				}
			}
		}
		return salePrice;
	}

	/**
	 * 获取系统设置的统一成本价
	 * 
	 * @param commodity
	 *            品种
	 * @param flowType
	 *            期货结算流水类型
	 * @return 系统设置的统一成本价
	 */
	private BigDecimal retriveSystemCostPrice(FuturesCommodityDto commodity, OrganizationAccountFlowType flowType) {
		BigDecimal systemCostPrice = null;
		if (flowType == OrganizationAccountFlowType.FuturesOpeningFeeAssign) {
			if (commodity.getOpenwindServiceFee() != null) {
				systemCostPrice = commodity.getOpenwindServiceFee();
			}
		} else if (flowType == OrganizationAccountFlowType.FuturesCloseFeeAssigne) {
			if (commodity.getUnwindServiceFee() != null) {
				systemCostPrice = commodity.getUnwindServiceFee();
			}
		} else if (flowType == OrganizationAccountFlowType.FuturesDeferredFeeAssign) {
			if (commodity.getOvernightPerUnitDeferredFee() != null) {
				systemCostPrice = commodity.getOvernightPerUnitDeferredFee();
			}
		}
		return systemCostPrice;
	}

	private void futuresOrderSettlement(Long commodityId, Long publisherId, OrganizationAccountFlowType flowType,
			BigDecimal salePrice, BigDecimal totalQuantity, Long flowResourceId, String tradeNo) {
		ResourceType flowResourceType = ResourceType.FUTURESORDER;
		List<Organization> orgTreeList = getPublisherOrgTreeList(publisherId);
		if (orgTreeList != null) {
			// 获取品种
			FuturesCommodityDto commodity = commodityBusiness.getFuturesByCommodityId(commodityId);
			if (commodity == null) {
				return;
			}
			// 获取代理商价格
			List<FuturesAgentPrice> agentPriceList = getAgentPriceList(commodityId, orgTreeList);
			int length = agentPriceList.size();
			// 自底向上结算
			for (int i = length - 1; i >= 0; i--) {
				Organization org = orgDao.findByOrgId(agentPriceList.get(i).getOrgId());
				// 当前层级的自带销售价
				BigDecimal carrySalePrice = retriveSalePrice(agentPriceList, flowType, i) == null ? BigDecimal.ZERO
						: retriveSalePrice(agentPriceList, flowType, i);
				if (i == length - 1) {
					// 给最后一级结算，销售价-成本价
					BigDecimal costPrice = retriveCostPrice(agentPriceList, flowType, i);
					if (costPrice == null) {
						costPrice = retriveSystemCostPrice(commodity, flowType);
					}
					if (costPrice != null && costPrice.compareTo(salePrice) <= 0) {
						accountService.benefit(org, carrySalePrice.multiply(totalQuantity),
								salePrice.subtract(costPrice).multiply(totalQuantity), flowType, flowResourceType,
								flowResourceId, tradeNo);
					}
				} else {
					// 给其他级结算，下级的成本价-自己的成本价
					BigDecimal selfCostPrice = retriveCostPrice(agentPriceList, flowType, i);
					BigDecimal childCostPrice = retriveCostPrice(agentPriceList, flowType, i + 1);
					// 给平台结算
					if (org.getLevel() == 1) {
						if (childCostPrice != null) {
							salePrice = childCostPrice;
						}
						if (BigDecimal.ZERO.compareTo(salePrice) <= 0) {
							accountService.benefit(org, carrySalePrice.multiply(totalQuantity),
									salePrice.subtract(BigDecimal.ZERO).multiply(totalQuantity), flowType,
									flowResourceType, flowResourceId, tradeNo);
						}
					} else if (selfCostPrice != null && childCostPrice != null
					/* && selfCostPrice.compareTo(childCostPrice) <= 0 */) {
						accountService.benefit(org, carrySalePrice.multiply(totalQuantity),
								childCostPrice.subtract(selfCostPrice).multiply(totalQuantity), flowType,
								flowResourceType, flowResourceId, tradeNo);
					}
				}
			}
		}
		accountService.benefit(null, salePrice.multiply(totalQuantity), salePrice.multiply(totalQuantity), flowType,
				flowResourceType, flowResourceId, tradeNo);
	}

	public void futuresOrderRatioSettlement(Long publisherId, BenefitConfigType benefitConfigType,
			OrganizationAccountFlowType flowType, BigDecimal amount, Long benefitResourceId, Long flowResourceId,
			String tradeNo) {
		logger.info("参与结算返佣金额进入结算方法, tradeNo{}", tradeNo);
		ResourceType flowResourceType = ResourceType.FUTURESORDER;
		Integer benefitResourceType = 3;
		List<Organization> orgTreeList = getPublisherOrgTreeList(publisherId);
		if (orgTreeList != null) {
			List<BenefitConfig> benefitConfigList = getFuturesRakeBackBenefitConfigList(orgTreeList, benefitConfigType,
					benefitResourceType);
			if (benefitConfigList != null && benefitConfigList.size() > 0
					&& benefitConfigList.get(0).getRatio().compareTo(BigDecimal.ZERO) >= 0) {
				int length = benefitConfigList.size();
				BigDecimal ratio = BigDecimal.ZERO;
				// 自底向上结算
				for (int i = length - 1; i >= 0; i--) {
					BenefitConfig config = benefitConfigList.get(i);
					BigDecimal computeRatio = config.getRatio() == null ? BigDecimal.ZERO : config.getRatio();
					if (i == 0) {
						// 剩余的结算给一级机构
						BigDecimal platformFee = amount
								.multiply(new BigDecimal("100").subtract(ratio).divide(new BigDecimal("100")))
								.setScale(2, RoundingMode.DOWN);
						accountService.futureBenefit(config.getOrg(), amount, platformFee, flowType, flowResourceType,
								flowResourceId, tradeNo);
					} else {
						// 给当前机构计算
						BigDecimal childFee = amount.multiply(computeRatio.divide(new BigDecimal("100"))).setScale(2,
								RoundingMode.DOWN);
						ratio = ratio.add(computeRatio);
						accountService.futureBenefit(config.getOrg(), amount, childFee, flowType, flowResourceType,
								flowResourceId, tradeNo);
					}
				}
			}
		}
		accountService.futureBenefit(null, amount, amount, flowType, flowResourceType, flowResourceId, tradeNo);
	}

}
