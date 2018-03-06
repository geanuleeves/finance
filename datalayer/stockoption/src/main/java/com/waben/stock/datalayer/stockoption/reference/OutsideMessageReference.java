package com.waben.stock.datalayer.stockoption.reference;

import org.springframework.cloud.netflix.feign.FeignClient;

import com.waben.stock.datalayer.stockoption.reference.fallback.OutsideMessageReferenceFallback;
import com.waben.stock.interfaces.service.message.OutsideMessageInterface;

@FeignClient(name = "message", path = "outsidemsg", fallback = OutsideMessageReferenceFallback.class, qualifier = "outsideMessageReference")
public interface OutsideMessageReference extends OutsideMessageInterface {
}
