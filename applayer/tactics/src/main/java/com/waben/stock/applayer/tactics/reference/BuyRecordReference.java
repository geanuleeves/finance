package com.waben.stock.applayer.tactics.reference;

import org.springframework.cloud.netflix.feign.FeignClient;

import com.waben.stock.applayer.tactics.reference.fallback.BuyRecordReferenceFallback;
import com.waben.stock.interfaces.service.buyrecord.BuyRecordInterface;

/**
 * 点买记录 reference服务接口
 *
 * @author luomengan
 */
@FeignClient(name = "buyrecord", path = "buyrecord", fallback = BuyRecordReferenceFallback.class, qualifier = "buyRecordReference")
public interface BuyRecordReference extends BuyRecordInterface {

}
