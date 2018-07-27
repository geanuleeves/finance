package com.waben.stock.datalayer.manage.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.waben.stock.datalayer.manage.entity.AppVersionUpgrade;
import com.waben.stock.datalayer.manage.repository.AppVersionUpgradeDao;
import com.waben.stock.datalayer.manage.repository.StaffDao;
import com.waben.stock.interfaces.pojo.query.AppVersionupgradeQuery;
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
	
	public String findByDeviceType(Integer deviceType){
		AppVersionUpgrade grade = dao.findByIsCurrentVersionAndDeviceType(true, deviceType);
		if(grade!=null){
			return grade.getDownloadLink();
		}else{
			return null;
		}
	}
	
	public AppVersionUpgrade addUpgrade(AppVersionUpgrade grade, String uploadFilePath){
		grade.setIsCurrentVersion(false);
		grade.setPublishDate(new Date());
		grade.setShellIndex(1);
		grade.setUpgradStrategy(0);
		try {
			String paths[] = grade.getDownloadLink().split("/");
			byte[] data = IOUtils.toByteArray(new FileInputStream(uploadFilePath+"/"+paths[paths.length-2]+"/"+paths[paths.length-1]));
			grade.setMd5Abstract(Md5Util.md5(data.toString()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return dao.create(grade);
	}
	
	public AppVersionUpgrade modifyUpgrade(AppVersionUpgrade grade, String uploadFilePath){
		grade.setIsCurrentVersion(false);
		grade.setPublishDate(new Date());
		grade.setShellIndex(1);
		grade.setUpgradStrategy(0);
		try {
			String paths[] = grade.getDownloadLink().split("/");
			byte[] data = IOUtils.toByteArray(new FileInputStream(uploadFilePath+"/"+paths[paths.length-2]+"/"+paths[paths.length-1]));
			grade.setMd5Abstract(Md5Util.md5(data.toString()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return dao.create(grade);
	}
	
	public void deleteUpgrade(Long id){
		dao.delete(id);
	}
	
	public Page<AppVersionUpgrade> page(final AppVersionupgradeQuery query){
		Pageable pageable = new PageRequest(query.getPage(), query.getSize());
		Page<AppVersionUpgrade> page = dao.page(new Specification<AppVersionUpgrade>() {
			
			@Override
			public Predicate toPredicate(Root<AppVersionUpgrade> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicatesList = new ArrayList<>();
				if(query.getDeviceType() != null){
					predicatesList.add(criteriaBuilder.equal(root.get("deviceType").as(Integer.class), query.getDeviceType()));
				}
				
				if (predicatesList.size() > 0) {
					criteriaQuery.where(predicatesList.toArray(new Predicate[predicatesList.size()]));
				}
                return criteriaQuery.getRestriction();
			}
		}, pageable);
		return page;
	}
	
	public AppVersionUpgrade isCurrentVersion(Integer deviceType, Long id){
		//将所有同一端的版本的isCurrentVersion设置为否
	    dao.isCurrentVersion(deviceType);
		
	    AppVersionUpgrade grade = dao.retrieve(id);
	    grade.setUpdateTime(new Date());
	    grade.setIsCurrentVersion(true);
	    AppVersionUpgrade result = dao.update(grade);
		return result;
	}

}
