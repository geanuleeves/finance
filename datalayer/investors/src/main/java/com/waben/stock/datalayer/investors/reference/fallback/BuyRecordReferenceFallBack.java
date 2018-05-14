package com.waben.stock.datalayer.investors.reference.fallback;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.waben.stock.datalayer.investors.reference.BuyRecordReference;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.buyrecord.BuyRecordDto;
import com.waben.stock.interfaces.exception.NetflixCircuitException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.BuyRecordQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.StrategyHoldingQuery;
import com.waben.stock.interfaces.pojo.query.StrategyPostedQuery;
import com.waben.stock.interfaces.pojo.query.StrategyUnwindQuery;
import com.waben.stock.interfaces.pojo.query.admin.buyrecord.BuyRecordAdminQuery;

/**
 * @author Created by yuyidi on 2017/12/2.
 * @desc
 */
@Component
public class BuyRecordReferenceFallBack implements BuyRecordReference {

    @Override
    public Response<BuyRecordDto> addBuyRecord(BuyRecordDto buyRecordDto) {
        return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

    @Override
    public Response<Void> dropBuyRecord(Long id) {
        return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }
    
    @Override
	public Response<BuyRecordDto> sellLock(Long investorId, Long id, String delegateNumber,
			String windControlTypeIndex) {
        return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

    @Override
    public Response<BuyRecordDto> withdrawLock(String entrustNo, Long id) {
        return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

    @Override
    public Response<BuyRecordDto> sellOut(Long investorId, Long id, BigDecimal sellingPrice) {
        return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

    @Override
    public Response<PageInfo<BuyRecordDto>> pagesByQuery(BuyRecordQuery buyRecordQuery) {
        return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

    @Override
    public Response<BuyRecordDto> fetchBuyRecord(Long buyrecord) {
        return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

    @Override
    public Response<BuyRecordDto> buyLock(Long investorId, Long id, String delegateNumber) {
        return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

    @Override
    public Response<BuyRecordDto> buyInto(Long investorId, Long id, BigDecimal buyingPrice) {
        return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

	@Override
	public Response<BuyRecordDto> sellApply(Long publisherId, Long id) {
        return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

    @Override
    public Response<PageInfo<BuyRecordDto>> pagesByPostedQuery(StrategyPostedQuery strategyPostedQuery) {
        return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

    @Override
    public Response<PageInfo<BuyRecordDto>> pagesByHoldingQuery(StrategyHoldingQuery strategyHoldingQuery) {
        throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

    @Override
    public Response<PageInfo<BuyRecordDto>> pagesByUnwindQuery(StrategyUnwindQuery trategyUnwindQuery) {
        return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

    @Override
    public Response<PageInfo<BuyRecordDto>> pagesByWithdrawQuery(StrategyUnwindQuery trategyUnwindQuery) {
        return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public Response<BuyRecordDto> updateState(BuyRecordDto buyRecordDto) {
        return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

    @Override
    public Response<List<BuyRecordDto>> fetchMonthsProfit(String year) {
        return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

    @Override
    public Response<List<BuyRecordDto>> buyRecordsWithStatus(Integer buyRecordState) {
        return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

	@Override
	public Response<BuyRecordDto> deferred(Long id) {
		return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<Integer> strategyJoinCount(Long publisherId, Long strategyTypeId) {
		return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<BuyRecordDto> revoke(Long id) {
		throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

    @Override
    public Response<Boolean> echo() {
        throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

	@Override
	public Response<PageInfo<BuyRecordDto>> adminPagesByQuery(BuyRecordAdminQuery query) {
		throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}
}
