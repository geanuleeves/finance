package com.waben.stock.interfaces.service.stockcontent;

import com.waben.stock.interfaces.dto.stockcontent.LossDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.LossQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface LossInterface {

    @RequestMapping(value = "/pages", method = RequestMethod.GET, consumes = "application/json")
    Response<PageInfo<LossDto>> pagesByQuery(@RequestBody LossQuery lossQuery);
}
