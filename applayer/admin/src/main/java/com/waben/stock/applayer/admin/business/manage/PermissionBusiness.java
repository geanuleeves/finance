package com.waben.stock.applayer.admin.business.manage;

import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.manage.PermissionDto;
import com.waben.stock.interfaces.exception.NetflixCircuitException;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.service.manage.OperationLogInterface;
import com.waben.stock.interfaces.service.manage.PermissionInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: zengzhiwei
 * @date: 2018/7/30 11:11
 * @desc：
 */
@Service
public class PermissionBusiness {

    @Autowired
    @Qualifier("permissionInterface")
    private PermissionInterface permissionInterface;


    public List<PermissionDto> getPermissionsByVariety() {
        Response<List<PermissionDto>> response = permissionInterface.fetchPermissionsByVariety(0L);
        String code = response.getCode();
        if(code.equals("200")) {
            return response.getResult();
        }else if(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION.equals(code)){
            throw new NetflixCircuitException(code);
        }
        throw new ServiceException(code);
    }
}
