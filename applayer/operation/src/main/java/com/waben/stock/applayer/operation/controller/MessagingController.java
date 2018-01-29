package com.waben.stock.applayer.operation.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.waben.stock.applayer.operation.business.MessagingBusiness;
import com.waben.stock.interfaces.dto.message.MessagingDto;
import com.waben.stock.interfaces.enums.MessageType;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.MessagingQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import com.waben.stock.interfaces.vo.message.MessagingVo;

/**
 * 
 * @author Created by hujian on 2018年1月4日
 */
@Controller
@RequestMapping("messaging")
public class MessagingController {

	@Autowired
	private MessagingBusiness messagingBusiness;

	@RequestMapping("/index")
	public String index(ModelMap map) {
		map.addAttribute("types", MessageType.values());
		return "message/manage/index";
	}

	@GetMapping("/pages")
	@ResponseBody
	public Response<PageInfo<MessagingVo>> pages(MessagingQuery messagingQuery) {
		PageInfo<MessagingDto> pages = messagingBusiness.pages(messagingQuery);
		List<MessagingVo> messagingVoContent = CopyBeanUtils.copyListBeanPropertiesToList(pages.getContent(),
				MessagingVo.class);
		PageInfo<MessagingVo> result = new PageInfo<>(messagingVoContent, pages.getTotalPages(), pages.getLast(),
				pages.getTotalElements(), pages.getSize(), pages.getNumber(), pages.getFrist());
		return new Response<>(result);
	}

	@RequestMapping("/{messagingId}/view")
	public String view(@PathVariable Long messagingId, ModelMap map) {
		map.addAttribute("messaging", CopyBeanUtils.copyBeanProperties(MessagingVo.class,
				messagingBusiness.fetchMessagingById(messagingId), false));
		return "message/manage/view";
	}

	@RequestMapping("/{messagingId}/edit")
	public String edit(@PathVariable Long messagingId, ModelMap map) {
		map.addAttribute("types", MessageType.values());
		MessagingDto messageingDto = messagingBusiness.fetchMessagingById(messagingId);
		MessagingVo messagingVo = CopyBeanUtils.copyBeanProperties(MessagingVo.class, messageingDto, false);
		map.addAttribute("messaging", messagingVo);
		return "message/manage/edit";
	}

	@RequestMapping("/modify")
	@ResponseBody
	public Response<MessagingVo> modify(MessagingVo vo) {
		MessagingDto messagingDto = CopyBeanUtils.copyBeanProperties(MessagingDto.class, vo, false);
		MessagingDto result = messagingBusiness.modifyMessaging(messagingDto);
		return new Response<>(CopyBeanUtils.copyBeanProperties(MessagingVo.class, result, false));
	}

}
