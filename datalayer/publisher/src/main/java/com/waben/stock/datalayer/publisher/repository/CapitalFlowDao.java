package com.waben.stock.datalayer.publisher.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.waben.stock.datalayer.publisher.entity.CapitalFlow;
import com.waben.stock.datalayer.publisher.entity.Publisher;
import com.waben.stock.interfaces.enums.CapitalFlowExtendType;
import com.waben.stock.interfaces.enums.CapitalFlowType;

/**
 * 资金流水 Dao
 * 
 * @author lma
 *
 */
public interface CapitalFlowDao extends BaseDao<CapitalFlow, Long> {

	CapitalFlow create(Publisher publisher, CapitalFlowType type, BigDecimal amount, Date occurrenceTime,
			CapitalFlowExtendType extendType, Long extendId, BigDecimal availableBalance, BigDecimal frozenCapital);

	List<CapitalFlow> retriveByPublisherIdAndType(Long publisherId, CapitalFlowType flowType);

	List<CapitalFlow> retriveByExtendTypeAndExtendId(CapitalFlowExtendType extendType, Long extendId);

	BigDecimal promotionTotalAmount(Long publisherId);
}
