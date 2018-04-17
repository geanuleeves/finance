package com.waben.stock.applayer.operation.service.fallback;

import com.waben.stock.applayer.operation.service.activity.PublisherTicketService;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.activity.PublisherTicketDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PublisherTicketServiceFallback implements PublisherTicketService{
    @Override
    public Response<PublisherTicketDto> savePublisherTicket(PublisherTicketDto publisherTicketDto) {
        return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

    @Override
    public Response<PageInfo<PublisherTicketDto>> getPublisherTicketList(int pageno, Integer pagesize) {
        return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

    @Override
    public Response<PublisherTicketDto> getPublisherTicket(long publisherTicketId) {
        return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

    @Override
    public Response<List<PublisherTicketDto>> getPublisherTicketsByApId(long apId) {
        return new Response<>(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
    }

}
