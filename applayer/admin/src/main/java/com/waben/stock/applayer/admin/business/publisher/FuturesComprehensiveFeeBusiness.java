package com.waben.stock.applayer.admin.business.publisher;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.applayer.admin.paypi.WBConfig;
import com.waben.stock.applayer.admin.rabittmq.RabbitmqConfiguration;
import com.waben.stock.applayer.admin.rabittmq.RabbitmqProducer;
import com.waben.stock.applayer.admin.rabittmq.message.WithdrawQueryMessage;
import com.waben.stock.interfaces.commonapi.wabenpay.WabenPayOverHttp;
import com.waben.stock.interfaces.commonapi.wabenpay.bean.WithdrawParam;
import com.waben.stock.interfaces.commonapi.wabenpay.bean.WithdrawRet;
import com.waben.stock.interfaces.commonapi.wabenpay.common.WabenBankType;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.publisher.CapitalAccountDto;
import com.waben.stock.interfaces.dto.publisher.PublisherDto;
import com.waben.stock.interfaces.dto.publisher.RealNameDto;
import com.waben.stock.interfaces.dto.publisher.WithdrawalsOrderDto;
import com.waben.stock.interfaces.enums.BankType;
import com.waben.stock.interfaces.enums.WithdrawalsState;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.FuturesComprehensiveFeeQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.WithdrawalsOrderQuery;
import com.waben.stock.interfaces.service.publisher.CapitalAccountInterface;
import com.waben.stock.interfaces.service.publisher.FuturesComprehensiveFeeInterface;
import com.waben.stock.interfaces.service.publisher.PublisherInterface;
import com.waben.stock.interfaces.service.publisher.RealNameInterface;
import com.waben.stock.interfaces.service.publisher.WithdrawalsOrderInterface;
import com.waben.stock.interfaces.util.StringUtil;
import com.waben.stock.interfaces.util.UniqueCodeGenerator;

@Service
public class FuturesComprehensiveFeeBusiness {

	Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private FuturesComprehensiveFeeInterface reference;

	@Autowired
	@Qualifier("capitalAccountInterface")
	private CapitalAccountInterface service;
	
	@Autowired
    @Qualifier("withdrawalsOrderInterface")
    private WithdrawalsOrderInterface withdrawalsOrderReference;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	
	@Autowired
	private PublisherInterface publisherInterface;

	@Autowired
	private RealNameInterface realnameInterface;
	
	@Autowired
    private WBConfig wbConfig;
	
	@Autowired
	private RabbitmqProducer producer;
	
	private boolean isProd = true;
	

	public PageInfo<WithdrawalsOrderDto> page(FuturesComprehensiveFeeQuery query) {
		List<Long> publisherIds = queryPublishIds(query);
		if(publisherIds==null){
			return new PageInfo<WithdrawalsOrderDto>();
		}else{
			query.setPublisherId(publisherIds);
		}
		WithdrawalsOrderQuery withquery = new WithdrawalsOrderQuery();
		if(publisherIds.size()>0){
			withquery.setPublisherId(publisherIds.get(0));
		}
		withquery.setState(query.getState());
		Response<PageInfo<WithdrawalsOrderDto>> response = withdrawalsOrderReference.pagesByQuery(withquery);
		if ("200".equals(response.getCode())) {
			List<WithdrawalsOrderDto> list = response.getResult().getContent();
			for (int i=0;i<list.size();i++) {
				PublisherDto pu = publisherInterface.recover(list.get(i).getPublisherId()).getResult();
				if(pu!=null){
					response.getResult().getContent().get(i).setPublisherPhone(pu.getPhone());
				}
				RealNameDto real = realnameInterface.fetchByResourceId(list.get(i).getPublisherId()).getResult();
				if(real != null){
					response.getResult().getContent().get(i).setName(real.getName());
				}
			}
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public CapitalAccountDto findByPublisherId(Long publisherId) {
		Response<CapitalAccountDto> response = service.fetchByPublisherId(publisherId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
	
	private List<Long> queryPublishIds(FuturesComprehensiveFeeQuery query) {
		List<Long> publisherIds = new ArrayList<Long>();
		if (query.getPhone() != null && !"".equals(query.getPhone())) {
			if (publisherInterface.fetchByPhone(query.getPhone()).getResult() != null) {
				String publisherId = publisherInterface.fetchByPhone(query.getPhone()).getResult().getId()
						.toString();
				if (publisherId != null && !"".equals(publisherId)) {
					publisherIds.add(Long.valueOf(publisherId));
				} else {
					return null;
				}
			} else {
				return null;
			}
			;
		} else if (query.getName() != null && !"".equals(query.getName())) {
			List<RealNameDto> real = realnameInterface.findByName(query.getName()).getResult();
			if (real == null || real.size() == 0) {
				return null;
			} else {
				for (RealNameDto realNameDto : real) {
					publisherIds.add(Long.valueOf(realNameDto.getResourceId().toString()));
				}
			}

		}
		return publisherIds;
	}
	
	public WithdrawalsOrderDto wbWithdrawalsAdminCancle(WithdrawalsOrderDto compre){
		Response<WithdrawalsOrderDto> response = withdrawalsOrderReference.refuse(compre.getId(), compre.getRemark());
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
	
	public WithdrawalsOrderDto wbWithdrawalsAdmin(WithdrawalsOrderDto compre){
		WithdrawalsOrderDto order = withdrawalsOrderReference.fetchById(compre.getId()).getResult();
		if(order!=null){
			if(order.getComprehensiveState()!=null && order.getComprehensiveState()==1){
				throw new ServiceException(ExceptionConstant.THE_STATE_ISNOT_AUDITED_EXCEPTION);
			}
			String withdrawalsNo = UniqueCodeGenerator.generateWithdrawalsNo();
			order.setWithdrawalsNo(withdrawalsNo);
			order.setState(WithdrawalsState.PROCESSING);
			Date date = new Date();
			order.setUpdateTime(date);
			order.setComprehensiveState(1);
			order = revisionWithdrawalsOrder(order);
			
			WabenBankType bankType = WabenBankType.getByPlateformBankType(BankType.getByCode(order.getBankCode()));
			if(bankType==null){
				throw new ServiceException(ExceptionConstant.DATANOTFOUND_EXCEPTION);
			}
			logger.info("发起提现申请:{}_{}_{}_{}", order.getName(), order.getIdCard(), order.getPublisherPhone(), order.getBankCard());
			WithdrawParam param = new WithdrawParam();
			param.setAppId(wbConfig.getMerchantNo());
			param.setBankAcctName(order.getName());
			param.setBankNo(order.getBankCard());
			param.setBankCode(bankType.getCode());
			param.setBankName(bankType.getBank());
			param.setCardType("0");
			param.setOutOrderNo(withdrawalsNo);
			param.setTimestamp(sdf.format(date));
			param.setTotalAmt(isProd ? order.getAmount() : new BigDecimal("0.01"));
			param.setVersion("1.0");
			
			// 发起提现请求前，预使用队列查询
			WithdrawQueryMessage message = new WithdrawQueryMessage();
			message.setAppId(wbConfig.getMerchantNo());
			message.setOutOrderNo(withdrawalsNo);
			producer.sendMessage(RabbitmqConfiguration.withdrawQueryQueueName, message);
			
			// 发起提现请求
			WithdrawRet withdrawRet = WabenPayOverHttp.withdraw(param, wbConfig.getKey());
			if(withdrawRet != null && !StringUtil.isEmpty(withdrawRet.getOrderNo())) {
				// 更新支付系统第三方订单状态
				order.setThirdWithdrawalsNo(withdrawRet.getOrderNo());
				order = this.revisionWithdrawalsOrder(order);
			}
			return order;
		}else{
			throw new ServiceException(ExceptionConstant.DATANOTFOUND_EXCEPTION);
		}
	}

	
	public WithdrawalsOrderDto saveWithdrawalsOrders(WithdrawalsOrderDto withdrawalsOrderDto) {
        Response<WithdrawalsOrderDto> orderResp = withdrawalsOrderReference.addWithdrawalsOrder(withdrawalsOrderDto);
        if ("200".equals(orderResp.getCode())) {
            return orderResp.getResult();
        }
        throw new ServiceException(orderResp.getCode());
    }
	
	public WithdrawalsOrderDto revisionWithdrawalsOrder(WithdrawalsOrderDto withdrawalsOrderDto) {
        Response<WithdrawalsOrderDto> orderResp = withdrawalsOrderReference.modifyWithdrawalsOrder(withdrawalsOrderDto);
        if ("200".equals(orderResp.getCode())) {
            return orderResp.getResult();
        }
        throw new ServiceException(orderResp.getCode());
    }
	
	public WithdrawalsOrderDto findByWithdrawalsNo(String withdrawalsNo) {
        Response<WithdrawalsOrderDto> orderResp = withdrawalsOrderReference.fetchByWithdrawalsNo(withdrawalsNo);
        if ("200".equals(orderResp.getCode())) {
            return orderResp.getResult();
        }
        throw new ServiceException(orderResp.getCode());
    }
}
