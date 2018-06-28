package com.waben.stock.futuresgateway.yisheng.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;

/**
 * Channel Manager
 *
 */

public class ChannelRepository {
	public final static Map<String, Channel> channelCache = new ConcurrentHashMap<String, Channel>();

	public void put(String key, Channel value) {
		channelCache.put(key, value);
	}

	public Channel get(String key) {
		return channelCache.get(key);
	}

	public void remove(String key) { 
		channelCache.remove(key);
	}

	public int size() {
		return channelCache.size();
	}
}
