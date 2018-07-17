package com.waben.stock.datalayer.publisher.repository;

import java.util.List;

import com.waben.stock.datalayer.publisher.entity.CapitalAccount;
import com.waben.stock.datalayer.publisher.entity.CapitalAccountRecord;

/**
 * @author Created by yuyidi on 2018/5/4.
 * @desc
 */
public interface CapitalAccountRecordDao extends BaseDao<CapitalAccountRecord,Long>{

	List<CapitalAccountRecord> findByCapitalAccount(CapitalAccount capitalAccount);
}
