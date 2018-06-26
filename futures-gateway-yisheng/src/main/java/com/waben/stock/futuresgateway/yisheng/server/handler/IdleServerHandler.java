package com.waben.stock.futuresgateway.yisheng.server.handler;

import com.waben.stock.futuresgateway.yisheng.server.ChannelRepository;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 连接空闲Handler
 */
@Component
@Qualifier("idleServerHandler")
@ChannelHandler.Sharable
public class IdleServerHandler extends ChannelInboundHandlerAdapter {
	public Logger log = Logger.getLogger(this.getClass());
	private final AttributeKey<String> clientInfo = AttributeKey.valueOf("clientInfo");

	@Autowired
		@Qualifier("channelRepository")
		ChannelRepository channelRepository;

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			String type = "";
			if (event.state() == IdleState.READER_IDLE) {
				type = "read idle";
				log.info(ctx.channel().remoteAddress()+"超时类型：" + type);
				if(ctx.channel().attr(clientInfo) != null
						&& !StringUtils.isEmpty(ctx.channel().attr(clientInfo).get())
						&& channelRepository != null
						){
					log.info("超时client:"+ ctx.channel().attr(clientInfo).get());
					channelRepository.remove(ctx.channel().attr(clientInfo).get());
				}
				ctx.channel().close();
			} else if (event.state() == IdleState.WRITER_IDLE) {
				type = "write idle";
			} else if (event.state() == IdleState.ALL_IDLE) {
				type = "all idle";
			}

		} else {
			super.userEventTriggered(ctx, evt);
		}
	}
}
