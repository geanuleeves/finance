package com.waben.stock.datalayer.manage.repository;

import java.util.List;

import com.waben.stock.datalayer.manage.entity.Broadcast;

public interface BroadcastDao extends BaseDao<Broadcast, Long> {

	List<Broadcast> findBytype(String type,boolean enable);
}
