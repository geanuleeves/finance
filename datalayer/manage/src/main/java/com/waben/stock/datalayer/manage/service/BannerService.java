package com.waben.stock.datalayer.manage.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.waben.stock.datalayer.manage.entity.Banner;
import com.waben.stock.datalayer.manage.repository.BannerDao;
import com.waben.stock.interfaces.dto.manage.BannerDto;

/**
 * 轮播 Service
 * 
 * @author luomengan
 *
 */
@Service
public class BannerService {

	@Autowired
	private BannerDao bannerDao;


}
