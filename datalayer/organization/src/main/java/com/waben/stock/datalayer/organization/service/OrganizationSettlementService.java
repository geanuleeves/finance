package com.waben.stock.datalayer.organization.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.waben.stock.datalayer.organization.entity.BenefitConfig;
import com.waben.stock.datalayer.organization.entity.Organization;
import com.waben.stock.datalayer.organization.entity.OrganizationAccountFlow;
import com.waben.stock.datalayer.organization.entity.OrganizationPublisher;
import com.waben.stock.datalayer.organization.entity.SettlementMethod;
import com.waben.stock.datalayer.organization.repository.BenefitConfigDao;
import com.waben.stock.datalayer.organization.repository.OrganizationAccountFlowDao;
import com.waben.stock.datalayer.organization.repository.OrganizationDao;
import com.waben.stock.datalayer.organization.repository.OrganizationPublisherDao;
import com.waben.stock.datalayer.organization.repository.SettlementMethodDao;
import com.waben.stock.interfaces.enums.BenefitConfigType;
import com.waben.stock.interfaces.enums.OrganizationAccountFlowType;
import com.waben.stock.interfaces.enums.ResourceType;

/**
 * 机构结算 Service
 *
 * @author luomengan
 *
 */
@Service
public class OrganizationSettlementService {

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
				// 先等到二级机构的比例，算出来的权利金作为可分配的金额
				BigDecimal secondLevelRatio = benefitConfigList.get(0).getRatio();
				BigDecimal currentFee = amount.multiply(secondLevelRatio.divide(new BigDecimal("100"))).setScale(2,
						RoundingMode.DOWN);
				// 自底向上结算
				int lenth = benefitConfigList.size();
				for (int i = lenth - 1; i >= 0; i--) {
					BenefitConfig config = benefitConfigList.get(i);
					BigDecimal ratio = config.getRatio();
					if (ratio.compareTo(secondLevelRatio) > 0) {
						ratio = secondLevelRatio;
					}
					// 给当前机构计算
					BigDecimal childFee = amount.multiply(ratio.divide(new BigDecimal("100"))).setScale(2,
							RoundingMode.DOWN);
					if (childFee.compareTo(currentFee) > 0) {
						childFee = currentFee;
					}
					if (childFee.compareTo(BigDecimal.ZERO) > 0) {
						accountService.benefit(config.getOrg(), amount, childFee, flowType, flowResourceType,
								flowResourceId, tradeNo);
					}
					// 计算剩余的金额
					currentFee = currentFee.subtract(childFee);
					if (currentFee.compareTo(BigDecimal.ZERO) <= 0) {
						break;
					}
				}
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
			}
		}
		return null;
	}

}
