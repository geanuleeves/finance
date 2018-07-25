package com.waben.stock.datalayer.manage.service;

import java.io.FileInputStream;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.waben.stock.datalayer.manage.entity.AppVersionUpgrade;
import com.waben.stock.datalayer.manage.repository.AppVersionUpgradeDao;
import com.waben.stock.interfaces.util.Md5Util;

/**
 * app版本升级 Service
 * 
 * @author luomengan
 *
 */
@Service
public class AppVersionUpgradeService {

	@Autowired
	private AppVersionUpgradeDao dao;

	public AppVersionUpgrade checkUpgrade(Integer versionCode, Integer deviceType, Integer shellIndex) {
		AppVersionUpgrade upgrade = dao.getGreaterThanCurrentVersion(versionCode, deviceType, shellIndex);
		return upgrade;
	}
	
	public AppVersionUpgrade addUpgrade(AppVersionUpgrade grade){
		grade.setIsCurrentVersion(false);
		grade.setPublishDate(new Date());
		grade.setShellIndex(1);
		grade.setUpgradStrategy(0);
		if(grade.getDeviceType()!=null && grade.getDeviceType()==0){
		}
		
		return null;
	}

}
