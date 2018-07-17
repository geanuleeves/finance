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

import com.waben.stock.interfaces.commonapi.wabenpay.WabenPayOverHttp;
import com.waben.stock.interfaces.commonapi.wabenpay.bean.WithdrawParam;
import com.waben.stock.interfaces.commonapi.wabenpay.bean.WithdrawRet;
import com.waben.stock.interfaces.dto.admin.futures.FutresOrderEntrustDto;
import com.waben.stock.interfaces.dto.admin.publisher.FuturesComprehensiveFeeDto;
import com.waben.stock.interfaces.dto.publisher.CapitalAccountDto;
import com.waben.stock.interfaces.dto.publisher.RealNameDto;
import com.waben.stock.interfaces.dto.publisher.WithdrawalsOrderDto;
import com.waben.stock.interfaces.enums.WithdrawalsState;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.FuturesComprehensiveFeeQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesTradeAdminQuery;
import com.waben.stock.interfaces.service.publisher.CapitalAccountInterface;
import com.waben.stock.interfaces.service.publisher.FuturesComprehensiveFeeInterface;
import com.waben.stock.interfaces.service.publisher.PublisherInterface;
import com.waben.stock.interfaces.service.publisher.RealNameInterface;
import com.waben.stock.interfaces.service.publisher.WithdrawalsOrderInterface;
import com.waben.stock.interfaces.util.StringUtil;

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
	
	private boolean isProd = true;
	

	public PageInfo<FuturesComprehensiveFeeDto> page(FuturesComprehensiveFeeQuery query) {
		List<Long> publisherIds = queryPublishIds(query);
		if(publisherIds==null){
			return new PageInfo<FuturesComprehensiveFeeDto>();
		}else{
			query.setPublisherId(publisherIds);
		}
		Response<PageInfo<FuturesComprehensiveFeeDto>> response = reference.page(query);
		if ("200".equals(response.getCode())) {
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
	
	public WithdrawalsOrderDto wbWithdrawalsAdmin(FuturesComprehensiveFeeDto compre){
		WithdrawalsOrderDto response = new WithdrawalsOrderDto();
		Response<FuturesComprehensiveFeeDto> result = reference.retrieve(compre.getId());
		FuturesComprehensiveFeeDto dto = result.getResult();
		if(dto.getWithdrawalsNo()!=null){
			Response<WithdrawalsOrderDto> withresult =  withdrawalsOrderReference.fetchByWithdrawalsNo(dto.getWithdrawalsNo());
			if(!"200".equals(withresult.getCode())){
				throw new ServiceException(withresult.getCode());
			}
			WithdrawalsOrderDto order = withresult.getResult();
			if(compre.getState()==1){
				
				WithdrawParam param = new WithdrawParam();
				param.setAppId(dto.getMerchantNo());
				param.setBankAcctName(order.getName());
				param.setBankNo(order.getBankCard());
				param.setBankCode(order.getBankCard());
				param.setBankName(dto.getBankName());
				param.setCardType("0");
				param.setOutOrderNo(order.getWithdrawalsNo());
				Date date = new Date();
				param.setTimestamp(sdf.format(date));
				param.setTotalAmt(isProd ? order.getAmount() : new BigDecimal("0.01"));
				param.setVersion("1.0");
				
				WithdrawRet withdrawRet = WabenPayOverHttp.withdraw(param, dto.getWebConfigKey());
				if(withdrawRet != null && !StringUtil.isEmpty(withdrawRet.getOrderNo())) {
					// 更新支付系统第三方订单状态
					order.setThirdWithdrawalsNo(withdrawRet.getOrderNo());
					order.setComprehensiveState(1);
					response = this.revisionWithdrawalsOrder(order);
					
					dto.setState(1);
					reference.modify(dto);
				}
			}else{
				order.setComprehensiveState(2);
				response = this.revisionWithdrawalsOrder(order);
				
				dto.setState(2);
				dto.setRemarke(compre.getRemarke());
				reference.modify(dto);
			}
		}else{
			dto.setState(2);
			dto.setRemarke(compre.getRemarke());
			reference.modify(dto);
		}
		return response;
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
}
