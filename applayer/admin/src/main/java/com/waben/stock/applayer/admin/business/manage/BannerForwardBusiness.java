package com.waben.stock.applayer.admin.business.manage;


import com.waben.stock.applayer.admin.reference.BannerForwardReference;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.manage.BannerForwardDto;
import com.waben.stock.interfaces.exception.NetflixCircuitException;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BannerForwardBusiness {

    @Autowired
    @Qualifier("bannerForwardReference")
    private BannerForwardReference reference;


    public List<BannerForwardDto> findAll() {
        Response<List<BannerForwardDto>> response = reference.fetchBannerForwards();
        String code = response.getCode();
        if ("200".equals(code)) {
            return response.getResult();
        }else if(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION.equals(code)){
            throw new NetflixCircuitException(code);
        }
        throw new ServiceException(response.getCode());
    }

}
