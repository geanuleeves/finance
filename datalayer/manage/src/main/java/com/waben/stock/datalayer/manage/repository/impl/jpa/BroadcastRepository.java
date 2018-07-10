package com.waben.stock.datalayer.manage.repository.impl.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.waben.stock.datalayer.manage.entity.Broadcast;

public interface BroadcastRepository extends CustomJpaRepository<Broadcast, Long> {

	@Query(value="select * from broadcast where type=?1 and enable=?2",nativeQuery = true)
	List<Broadcast> findBytype(String type,boolean enable);
}
