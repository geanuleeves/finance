package com.waben.stock.datalayer.manage.repository;

import com.waben.stock.datalayer.manage.entity.AppVersionUpgrade;

/**
 * app版本升级 Dao
 * 
 * @author lma
 *
 */
public interface AppVersionUpgradeDao extends BaseDao<AppVersionUpgrade, Long> {

	AppVersionUpgrade getGreaterThanCurrentVersion(Integer versionCode, Integer deviceType, Integer shellIndex);
	
	void isCurrentVersion(Integer deviceType);
	
	AppVersionUpgrade findByIsCurrentVersionAndDeviceType(Boolean isCurrentVersion, Integer deviceType);
}
