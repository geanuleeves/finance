package com.waben.stock.datalayer.manage.repository.impl.jpa;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.waben.stock.datalayer.manage.entity.AppVersionUpgrade;

/**
 * app版本升级 Jpa
 * 
 * @author lma
 *
 */
public interface AppVersionUpgradeRepository extends CustomJpaRepository<AppVersionUpgrade, Long> {

	AppVersionUpgrade findByIsCurrentVersionAndDeviceTypeAndShellIndexAndVersionCodeGreaterThan(
			Boolean isCurrentVersion, Integer deviceType, Integer shellIndex, Integer versionCode);
	
	@Transactional
	@Modifying(clearAutomatically = true)  
	@Query(value = "update AppVersionUpgrade sc set sc.isCurrentVersion=false  where sc.deviceType=?1") 
	void isCurrentVersion(Integer deviceType);
	
	AppVersionUpgrade findByIsCurrentVersionAndDeviceType(Boolean isCurrentVersion, Integer deviceType);
}
