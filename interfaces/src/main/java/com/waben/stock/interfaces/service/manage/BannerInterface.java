package com.waben.stock.interfaces.service.manage;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.waben.stock.interfaces.dto.manage.BannerDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.BannerQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;

/**
 * 轮播 公共接口
 * 
 * @author lma
 *
 */
@FeignClient(name = "manage", path = "banner", qualifier = "bannerInterface")
public interface BannerInterface {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	Response<List<BannerDto>> fetchBanners(@RequestParam(value = "enable", required = false) Boolean enable);

	@RequestMapping(value = "/pages", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<PageInfo<BannerDto>> pages(@RequestBody BannerQuery query);

	@RequestMapping(value = "/id/{id}", method = RequestMethod.GET)
	Response<BannerDto> fetchById(@PathVariable("id") Long id);

	@RequestMapping(value = "/modify", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<BannerDto> modify(@RequestBody BannerDto bannerDto);

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	void delete(@PathVariable("id") Long id);

	@RequestMapping(value = "/save", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<BannerDto> add(BannerDto requestDto);
}
