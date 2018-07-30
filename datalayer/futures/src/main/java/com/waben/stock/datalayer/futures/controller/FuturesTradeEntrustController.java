package com.waben.stock.datalayer.futures.controller;

import com.waben.stock.datalayer.futures.entity.FuturesTradeEntrust;
import com.waben.stock.datalayer.futures.service.FuturesTradeEntrustService;
import com.waben.stock.interfaces.dto.futures.FuturesTradeEntrustDto;
import com.waben.stock.interfaces.enums.FuturesTradeActionType;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.futures.FuturesTradeEntrustQuery;
import com.waben.stock.interfaces.service.futures.FuturesTradeEntrustInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import com.waben.stock.interfaces.util.PageToPageInfo;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 交易委托
 *
 * @author chenk 2018/7/26
 */
@RestController
@RequestMapping("/trade_entrust")
@Api(description = "交易委托接口列表")
public class FuturesTradeEntrustController implements FuturesTradeEntrustInterface {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FuturesTradeEntrustService futuresTradeEntrustService;

	@Override
	public Response<FuturesTradeEntrustDto> fetchById(@PathVariable Long id) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(FuturesTradeEntrustDto.class,
				futuresTradeEntrustService.findById(id), false));
	}

	@Override
	public Response<FuturesTradeEntrustDto> save(@RequestBody FuturesTradeEntrustDto dto) {
		FuturesTradeEntrust futuresTradeEntrust = CopyBeanUtils.copyBeanProperties(FuturesTradeEntrust.class, dto,
				false);
		FuturesTradeEntrust result = futuresTradeEntrustService.save(futuresTradeEntrust);
		FuturesTradeEntrustDto response = CopyBeanUtils.copyBeanProperties(result, new FuturesTradeEntrustDto(), false);
		return new Response<>(response);
	}

	@Override
	public Response<FuturesTradeEntrustDto> modify(FuturesTradeEntrustDto dto) {
		FuturesTradeEntrust futuresTradeEntrust = CopyBeanUtils.copyBeanProperties(FuturesTradeEntrust.class, dto,
				false);
		FuturesTradeEntrust result = futuresTradeEntrustService.modify(futuresTradeEntrust);
		FuturesTradeEntrustDto resultDto = CopyBeanUtils.copyBeanProperties(result, new FuturesTradeEntrustDto(),
				false);
		return new Response<>(resultDto);
	}

	@Override
	public Response<FuturesTradeEntrustDto> cancelEntrust(@PathVariable Long id, Long publisherId) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(FuturesTradeEntrustDto.class,
				futuresTradeEntrustService.cancelEntrust(id, publisherId), false));
	}

    @Override
    public Response<String> delete(Long id) {
        futuresTradeEntrustService.delete(id);
        Response<String> res = new Response<String>();
        res.setMessage("响应成功");
        return res;
    }

    @Override
    public Response<PageInfo<FuturesTradeEntrustDto>> pagesAdmin(@RequestBody FuturesTradeEntrustQuery query) {
        Page<FuturesTradeEntrust> page = futuresTradeEntrustService.pages(query);
        PageInfo<FuturesTradeEntrustDto> result = PageToPageInfo.pageToPageInfo(page, FuturesTradeEntrustDto.class);
        if (result != null && result.getContent() != null) {
            for (int i = 0; i < result.getContent().size(); i++) {
                FuturesTradeEntrust futuresTradeEntrust = page.getContent().get(i);
                //合约名称
                result.getContent().get(i).setContractName(futuresTradeEntrust.getContract().getContractName());
                // 1:买涨,2:买跌
                String orderType = futuresTradeEntrust.getOrderType().getIndex();
                //1:开仓,2:平仓
                String tradeActionType = futuresTradeEntrust.getTradeActionType().getIndex();
                if (("1".equals(orderType) && "1".equals(tradeActionType))
                        || ("2".equals(orderType) && "2".equals(tradeActionType))) {
                    //委托类型（买）
                    result.getContent().get(i).setTradeType(FuturesTradeActionType.OPEN);
                } else {
                    //委托类型（卖）
                    result.getContent().get(i).setTradeType(FuturesTradeActionType.CLOSE);
                }
            }
        }
        return new Response<>(result);
    }

}
