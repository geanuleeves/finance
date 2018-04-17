package com.waben.stock.applayer.tactics.business;

import com.waben.stock.applayer.tactics.reference.DrawActivityReference;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.activity.TicketAmountDto;
import com.waben.stock.interfaces.exception.NetflixCircuitException;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DrawActivityBusiness {

    @Autowired
    private DrawActivityReference drawActivityReference;

    public TicketAmountDto draw(long activityId,long publisherId) {
        Response<TicketAmountDto> response = drawActivityReference.draw(activityId, publisherId);
        String code = response.getCode();
        if ("200".equals(code)) {
            return response.getResult();
        }else if(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION.equals(code)){
            throw new NetflixCircuitException(code);
        }
        throw new ServiceException(response.getCode());
    }

}
