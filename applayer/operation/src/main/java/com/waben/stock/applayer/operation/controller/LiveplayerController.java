package com.waben.stock.applayer.operation.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JavaType;
import com.waben.stock.applayer.operation.service.redis.RedisCache;
import com.waben.stock.interfaces.commonapi.netease.ChannelManageOverHttps;
import com.waben.stock.interfaces.commonapi.netease.config.NeteaseLiveConfig;
import com.waben.stock.interfaces.commonapi.netease.livebean.NeteaseAddressParam;
import com.waben.stock.interfaces.commonapi.netease.livebean.NeteaseAddressRet;
import com.waben.stock.interfaces.commonapi.netease.livebean.NeteaseChannellistParam;
import com.waben.stock.interfaces.commonapi.netease.livebean.NeteaseChannellistRet;
import com.waben.stock.interfaces.commonapi.netease.livebean.NeteaseChannelstatsParam;
import com.waben.stock.interfaces.commonapi.netease.livebean.NeteaseChannelstatsRet;
import com.waben.stock.interfaces.commonapi.netease.livebean.NeteaseCreateParam;
import com.waben.stock.interfaces.commonapi.netease.livebean.NeteaseCreateRet;
import com.waben.stock.interfaces.commonapi.netease.livebean.NeteaseDeleteParam;
import com.waben.stock.interfaces.commonapi.netease.livebean.NeteaseDeleteRet;
import com.waben.stock.interfaces.commonapi.netease.livebean.NeteaseDisableParam;
import com.waben.stock.interfaces.commonapi.netease.livebean.NeteaseDisableRet;
import com.waben.stock.interfaces.commonapi.netease.livebean.NeteaseLivePage;
import com.waben.stock.interfaces.commonapi.netease.livebean.NeteaseLiveResponse;
import com.waben.stock.interfaces.commonapi.netease.livebean.NeteaseUpdateParam;
import com.waben.stock.interfaces.commonapi.netease.livebean.NeteaseUpdateRet;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.util.JacksonUtil;

/**
 * 直播管理
 * 
 * @author lma
 *
 */
@Controller
@RequestMapping("/liveplayer")
public class LiveplayerController {

	@Autowired
	private RedisCache redisCache;

	@RequestMapping("/index")
	public String index() {
		return "manage/liveplayer/index";
	}

	@RequestMapping("/add")
	public String add() {
		return "manage/liveplayer/add";
	}

	@RequestMapping("/edit")
	public String edit() {
		return "manage/liveplayer/edit";
	}

	@RequestMapping("/address")
	public String address() {
		return "manage/liveplayer/address";
	}

	@GetMapping("/pages")
	@ResponseBody
	public Response<PageInfo<NeteaseChannellistRet>> livePlayerList(int page, int size) {
		NeteaseChannellistParam listParam = new NeteaseChannellistParam();
		listParam.setPnum(page + 1);
		listParam.setRecords(size);
		JavaType listJavaType = JacksonUtil.getGenericType(NeteaseLivePage.class, NeteaseChannellistRet.class);
		NeteaseLiveResponse<NeteaseLivePage<NeteaseChannellistRet>> listResponse = ChannelManageOverHttps
				.doAction(NeteaseLiveConfig.ChannellistUrl, listParam, listJavaType);
		if (listResponse.getCode() == 200) {
			String currentCid = redisCache.get(NeteaseLiveConfig.CurrentLivePlayerKey);
			PageInfo<NeteaseChannellistRet> result = new PageInfo<>();
			List<NeteaseChannellistRet> content = listResponse.getRet().getList();
			if (content != null && content.size() > 0) {
				boolean isMatch = false;
				if (currentCid != null) {
					for (NeteaseChannellistRet channel : content) {
						if (channel.getCid().equals(currentCid)) {
							channel.setCurrent(true);
							isMatch = true;
						}
					}
				}
				if (!isMatch) {
					content.get(content.size() - 1).setCurrent(true);
				}
			}
			result.setContent(content);
			result.setTotalElements(listResponse.getRet().getTotalRecords());
			return new Response<>(result);
		} else {
			return new Response<>(ExceptionConstant.UNKNOW_EXCEPTION, listResponse.getMsg());
		}
	}

	@GetMapping("/get/{cid}")
	@ResponseBody
	public Response<NeteaseChannelstatsRet> getByCid(@PathVariable("cid") String cid) {
		NeteaseChannelstatsParam param = new NeteaseChannelstatsParam();
		param.setCid(cid);
		NeteaseLiveResponse<NeteaseChannelstatsRet> response = ChannelManageOverHttps
				.doAction(NeteaseLiveConfig.ChannelstatsUrl, param, NeteaseChannelstatsRet.class);
		if (response.getCode() == 200) {
			return new Response<>(response.getRet());
		} else {
			return new Response<>(ExceptionConstant.UNKNOW_EXCEPTION, response.getMsg());
		}
	}

	@GetMapping("/address/{cid}")
	@ResponseBody
	public Response<NeteaseAddressRet> getAddressByCid(@PathVariable("cid") String cid) {
		NeteaseAddressParam param = new NeteaseAddressParam();
		param.setCid(cid);
		NeteaseLiveResponse<NeteaseAddressRet> response = ChannelManageOverHttps.doAction(NeteaseLiveConfig.AddressUrl,
				param, NeteaseAddressRet.class);
		if (response.getCode() == 200) {
			return new Response<>(response.getRet());
		} else {
			return new Response<>(ExceptionConstant.UNKNOW_EXCEPTION, response.getMsg());
		}
	}

	@PostMapping("/update/{cid}")
	@ResponseBody
	public Response<String> updateName(@PathVariable("cid") String cid, String name) {
		NeteaseUpdateParam param = new NeteaseUpdateParam();
		param.setCid(cid);
		param.setName(name);
		NeteaseLiveResponse<NeteaseUpdateRet> response = ChannelManageOverHttps.doAction(NeteaseLiveConfig.UpdateUrl,
				param, NeteaseUpdateRet.class);
		if (response.getCode() == 200) {
			Response<String> result = new Response<>();
			result.setResult("success");
			return result;
		} else {
			return new Response<>(ExceptionConstant.UNKNOW_EXCEPTION, response.getMsg());
		}
	}

	@PostMapping("/add")
	@ResponseBody
	public Response<String> add(String name) {
		NeteaseCreateParam param = new NeteaseCreateParam();
		param.setName(name);
		NeteaseLiveResponse<NeteaseCreateRet> response = ChannelManageOverHttps.doAction(NeteaseLiveConfig.CreateUrl,
				param, NeteaseCreateRet.class);
		if (response.getCode() == 200) {
			Response<String> result = new Response<>();
			result.setResult("success");
			return result;
		} else {
			return new Response<>(ExceptionConstant.UNKNOW_EXCEPTION, response.getMsg());
		}
	}

	@PostMapping("/delete/{cid}")
	@ResponseBody
	public Response<String> delete(@PathVariable("cid") String cid) {
		NeteaseDeleteParam param = new NeteaseDeleteParam();
		param.setCid(cid);
		NeteaseLiveResponse<NeteaseDeleteRet> response = ChannelManageOverHttps.doAction(NeteaseLiveConfig.DeleteUrl,
				param, NeteaseDeleteRet.class);
		if (response.getCode() == 200) {
			Response<String> result = new Response<>();
			result.setResult("success");
			return result;
		} else {
			return new Response<>(ExceptionConstant.UNKNOW_EXCEPTION, response.getMsg());
		}
	}

	@PostMapping("/disable/{cid}")
	@ResponseBody
	public Response<String> disable(@PathVariable("cid") String cid) {
		NeteaseDisableParam param = new NeteaseDisableParam();
		param.setCid(cid);
		NeteaseLiveResponse<NeteaseDisableRet> response = ChannelManageOverHttps.doAction(NeteaseLiveConfig.DisableUrl,
				param, NeteaseDisableRet.class);
		if (response.getCode() == 200) {
			Response<String> result = new Response<>();
			result.setResult("success");
			return result;
		} else {
			return new Response<>(ExceptionConstant.UNKNOW_EXCEPTION, response.getMsg());
		}
	}

	@PostMapping("/current/{cid}")
	@ResponseBody
	public Response<String> setCurrent(@PathVariable("cid") String cid) {
		redisCache.set(NeteaseLiveConfig.CurrentLivePlayerKey, cid);
		Response<String> result = new Response<>();
		result.setResult("success");
		return result;
	}

}
