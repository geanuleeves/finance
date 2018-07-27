package com.waben.stock.interfaces.service.manage;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.waben.stock.interfaces.dto.manage.BannerForwardDto;
import com.waben.stock.interfaces.pojo.Response;

/**
 * 轮播 reference服务接口
 *
 * @author lma
 */
@FeignClient(name = "manage", path = "bannerforward", qualifier = "bannerForwardInterface")
public interface BannerForwardInterface {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	Response<List<BannerForwardDto>> fetchBannerForwards();
}
