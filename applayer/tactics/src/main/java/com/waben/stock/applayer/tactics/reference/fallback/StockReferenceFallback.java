package com.waben.stock.applayer.tactics.reference.fallback;

import org.springframework.stereotype.Component;

import com.waben.stock.applayer.tactics.reference.StockReference;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.stockcontent.StockDto;
import com.waben.stock.interfaces.exception.NetflixCircuitException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.StockQuery;

/**
 * 股票 reference服务接口fallback
 * 
 * @author luomengan
 *
 */
@Component
public class StockReferenceFallback implements StockReference {

	@Override
	public Response<StockDto> fetchById(Long id) {
		throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<PageInfo<StockDto>> pagesByQuery(StockQuery stockQuery) {
		throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<StockDto> fetchWithExponentByCode(String code) {
		throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<Integer> modify(StockDto stockDto) {
		throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public void delete(Long id) {

	}

	@Override
	public Response<StockDto> add(StockDto requestDto) {
		throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<StockDto> downline(String code, String stockOptionBlackRemark) {
		throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<StockDto> online(String code) {
		throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

}
