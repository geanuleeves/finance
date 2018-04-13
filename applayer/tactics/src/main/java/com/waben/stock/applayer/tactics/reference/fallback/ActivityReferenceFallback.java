package com.waben.stock.applayer.tactics.reference.fallback;

import com.waben.stock.applayer.tactics.reference.ActivityReference;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.activity.ActivityDto;
import com.waben.stock.interfaces.exception.NetflixCircuitException;
import com.waben.stock.interfaces.pojo.Response;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ActivityReferenceFallback implements ActivityReference{
    @Override
    public Response<ActivityDto> saveActivity(ActivityDto adto) {
        throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

    @Override
    public Response<List<ActivityDto>> getActivityList(int pageno, Integer pagesize) {
        throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

    @Override
    public Response<Void> setValid(long activityId) {
        throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

    @Override
    public Response<ActivityDto> getActivity(long activityId) {
        throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

    @Override
    public Response<ActivityDto> getActivityByLocation(String location) {
        throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }
}
