package com.waben.stock.applayer.operation.service.fallback;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.waben.stock.applayer.operation.service.stockoption.StockOptionTradeService;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.admin.stockoption.StockOptionAdminDto;
import com.waben.stock.interfaces.dto.admin.stockoption.StockOptionBlacklistAdminDto;
import com.waben.stock.interfaces.dto.admin.stockoption.StockOptionRiskAdminDto;
import com.waben.stock.interfaces.dto.promotion.stockoption.StockOptionPromotionDto;
import com.waben.stock.interfaces.dto.promotion.stockoption.StockOptionStaDto;
import com.waben.stock.interfaces.dto.stockoption.StockOptionAmountLimitDto;
import com.waben.stock.interfaces.dto.stockoption.StockOptionQuoteDto;
import com.waben.stock.interfaces.dto.stockoption.StockOptionTradeDto;
import com.waben.stock.interfaces.exception.NetflixCircuitException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.StockOptionTradeQuery;
import com.waben.stock.interfaces.pojo.query.StockOptionTradeUserQuery;
import com.waben.stock.interfaces.pojo.query.admin.stockoption.StockOptionAdminQuery;
import com.waben.stock.interfaces.pojo.query.admin.stockoption.StockOptionRiskAdminQuery;
import com.waben.stock.interfaces.pojo.query.promotion.stockoption.StockOptionPromotionQuery;

@Component
public class StockOptionTradeServiceFallback implements StockOptionTradeService{
    @Override
    public Response<StockOptionTradeDto> add(StockOptionTradeDto stockOptionTradeDto) {
        return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

    @Override
    public Response<PageInfo<StockOptionTradeDto>> pagesByUserQuery(StockOptionTradeUserQuery query) {
        return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

    @Override
    public Response<PageInfo<StockOptionTradeDto>> pagesByQuery(StockOptionTradeQuery query) {
        return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

    @Override
    public Response<StockOptionTradeDto> fetchById(Long id) {
        return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

    @Override
    public Response<StockOptionTradeDto> modify(Long id) {
        return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

    @Override
    public Response<StockOptionTradeDto> settlement(Long id) {
        return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

	@Override
	public Response<StockOptionTradeDto> userRight(Long publisherId, Long id) {
		return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}
	
    @Override
    public Response<StockOptionTradeDto> success(Long id) {
        return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

    @Override
    public Response<StockOptionTradeDto> exercise(Long id) {
        return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

    @Override
    public Response<List<StockOptionTradeDto>> stockOptionsWithState(Integer state) {
        return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

    @Override
    public Response<StockOptionTradeDto> dueTreatmentExercise(Long id) {
        return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

    @Override
	public Response<StockOptionTradeDto> fail(Long id) {
		return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<PageInfo<StockOptionAdminDto>> adminPagesByQuery(StockOptionAdminQuery query) {
		return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<StockOptionTradeDto> turnover(Long id, Long orgId, BigDecimal orgRightMoneyRatio,
			BigDecimal buyingPrice) {
		return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<StockOptionTradeDto> mark(Long id, Boolean isMark) {
		return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<PageInfo<StockOptionRiskAdminDto>> adminNormalRiskPagesByQuery(StockOptionRiskAdminQuery query) {
		return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<PageInfo<StockOptionRiskAdminDto>> adminAbnormalRiskPagesByQuery(StockOptionRiskAdminQuery query) {
		return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<PageInfo<StockOptionBlacklistAdminDto>> adminBlackRiskPagesByQuery(
			StockOptionRiskAdminQuery query) {
		return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<StockOptionAmountLimitDto> modifyStockOptionLimit(String stockCode, String stockName,
			Boolean isGlobal, BigDecimal amountLimit) {
		return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<StockOptionQuoteDto> modifyStockOptionQuote(String stockCode, String stockName, Integer cycle,
			BigDecimal rightMoneyRatio) {
		return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<String> deleteStockOptionLimit(String stockCode) {
		return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<StockOptionAmountLimitDto> fetchGlobalStockOptionLimit() {
		return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<StockOptionTradeDto> insettlement(Long id, BigDecimal sellingPrice) {
		return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<StockOptionTradeDto> dosettlement(Long id) {
		return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<PageInfo<StockOptionPromotionDto>> promotionPagesByQuery(StockOptionPromotionQuery query) {
		return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<Integer> countStockOptionTradeState(Long publisherId) {
		throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<StockOptionTradeDto> update(StockOptionTradeDto dto) {
		throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<StockOptionStaDto> promotionSta(StockOptionPromotionQuery query) {
		throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}
    
}
