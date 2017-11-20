package com.waben.stock.datalayer.manage.repository.impl.jpa;

import java.util.List;

import org.springframework.data.domain.Sort;

import com.waben.stock.datalayer.manage.entity.Banner;

/**
 * 轮播 Jpa
 * 
 * @author luomengan
 *
 */
public interface BannerRepository extends CustomJpaRepository<Banner, Long> {

	List<Banner> findByState(boolean state, Sort sort);

}
