package com.waben.stock.applayer.tactics.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.admin.BroadcastDto;
import com.waben.stock.interfaces.dto.manage.CircularsDto;
import com.waben.stock.interfaces.exception.NetflixCircuitException;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.CircularsQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.BroadcastQuery;
import com.waben.stock.interfaces.service.manage.BroadcastInterface;
import com.waben.stock.interfaces.service.manage.CircularsInterface;

/**
 * 公告 Business
 * 
 * @author luomengan
 *
 */
@Service
public class CircularsBusiness {

	@Autowired
	@Qualifier("circularsInterface")
	private CircularsInterface circularsReference;
	
	@Autowired
	private BroadcastInterface broadReference;
	
	public List<BroadcastDto> findByType(BroadcastQuery query){
		Response<List<BroadcastDto>> response = broadReference.findBytype(query);
		if("200".equals(response.getCode())){
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public PageInfo<CircularsDto> pages(CircularsQuery query) {
		Response<PageInfo<CircularsDto>> response = circularsReference.pages(query);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public List<CircularsDto> fetchCirculars(Boolean enable) {
		Response<List<CircularsDto>> response = circularsReference.fetchCirculars(enable);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
	
    public CircularsDto findById(Long id) {
        Response<CircularsDto> response = circularsReference.fetchById(id);
        String code = response.getCode();
        if ("200".equals(code)) {
            return response.getResult();
        }else if(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION.equals(code)){
            throw new NetflixCircuitException(code);
        }
        throw new ServiceException(response.getCode());
    }

}
