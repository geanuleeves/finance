package com.waben.stock.datalayer.futures.repository.impl;

import com.waben.stock.datalayer.futures.entity.FuturesOrder;
import com.waben.stock.datalayer.futures.repository.FuturesOrderDao;
import com.waben.stock.datalayer.futures.repository.impl.jpa.FuturesOrderRepository;
import com.waben.stock.interfaces.enums.FuturesOrderState;
import com.waben.stock.interfaces.enums.FuturesOrderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 期货订单 Impl
 * 
 * @author sunl
 *
 */
@Repository
public class FuturesOrderDaoImpl implements FuturesOrderDao {

	@Autowired
	private FuturesOrderRepository repository;

	@Override
	public FuturesOrder create(FuturesOrder t) {
		return repository.save(t);
	}

	@Override
	public void delete(Long id) {
		repository.delete(id);
	}

	@Override
	public FuturesOrder update(FuturesOrder t) {
		return repository.save(t);
	}

	@Override
	public FuturesOrder retrieve(Long id) {
		return repository.findById(id);
	}

	@Override
	public Page<FuturesOrder> page(int page, int limit) {
		return repository.findAll(new PageRequest(page, limit));
	}

	@Override
	public Page<FuturesOrder> page(Specification<FuturesOrder> specification, Pageable pageable) {
		return repository.findAll(specification, pageable);
	}

	@Override
	public List<FuturesOrder> list() {
		return repository.findAll();
	}

	@Override
	public Integer countOrderByType(Long contractId, FuturesOrderType orderType) {
		return repository.countOrderByType(contractId, orderType);
	}

	@Override
	public Integer sumByListOrderContractIdAndPublisherId(Long contractId, Long publisherId, Integer type) {
		return repository.sumByListOrderContractIdAndPublisherId(contractId, publisherId, type);
	}

	@Override
	public List<FuturesOrder> getListFuturesOrderPositionByPublisherId(Long publisherId) {
		return repository.getListFuturesOrderPositionByPublisherId(publisherId);
	}

	@Override
	public BigDecimal settlementOrderPositionByPublisherId(Long publisherId) {
		return repository.settlementOrderPositionByPublisherId(publisherId);
	}

	@Override
	public List<FuturesOrder> getListFuturesOrderEntrustByPublisherId(Long publisherId) {
		return repository.getListFuturesOrderEntrustByPublisherId(publisherId);
	}

	@Override
	public BigDecimal settlementOrderEntrustByPublisherId(Long publisherId) {
		return repository.settlementOrderEntrustByPublisherId(publisherId);
	}

	@Override
	public List<FuturesOrder> getListFuturesOrderUnwindByPublisherId(Long publisherId) {
		return repository.getListFuturesOrderUnwindByPublisherId(publisherId);
	}

	@Override
	public BigDecimal settlementOrderUnwindByPublisherId(Long publisherId) {
		return repository.settlementOrderUnwindByPublisherId(publisherId);
	}

	@Override
	public List<FuturesOrder> findByContractTermId(List<Long> contractTermId) {
		return repository.findByContractTermId(contractTermId);
	}

	@Override
	public List<FuturesOrder> findByContractId(List<Long> contractId) {
		return repository.findByContractId(contractId);
	}

	@Override
	public List<FuturesOrder> retrieveByPublisherIdAndState(Long publisherId, FuturesOrderState state) {
		return repository.findByPublisherIdAndState(publisherId, state);
	}

	@Override
	public List<FuturesOrder> retrieveByBackhandSourceOrderId(Long backhandSourceOrderId) {
//		return repository.findByBackhandSourceOrderId(backhandSourceOrderId);
		return null;
	}

	@Override
	public List<Object> queryByState(List<Integer> state) {
		return repository.queryByState(state);
	}

	@Override
	public FuturesOrder retrieveByOrderIdAndPublisherId(Long orderId, Long publisherId) {
		return repository.findByIdAndPublisherId(orderId, publisherId);
	}

	@Override
	public Integer countByPublisherId(Long publisherId) {
		return repository.countByPublisherId(publisherId);
	}

	@Override
	public BigDecimal getAvgFillPrice(Long publisherId, String contractNo, String commodityNo, String orderType) {
		return repository.getAvgFillPrice(publisherId, contractNo, commodityNo, orderType);
	}

}
