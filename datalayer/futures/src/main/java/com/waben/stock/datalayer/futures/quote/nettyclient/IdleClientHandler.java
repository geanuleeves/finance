package com.waben.stock.datalayer.futures.quote.nettyclient;

import org.springframework.stereotype.Component;

import com.google.protobuf.Message;
import com.waben.stock.datalayer.futures.quote.protobuf.Command.CommandType;
import com.waben.stock.datalayer.futures.quote.protobuf.Message.MessageBase;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

@Component
@ChannelHandler.Sharable
public class IdleClientHandler extends SimpleChannelInboundHandler<Message> {

	private NettyClient nettyClient;
	private int heartbeatCount = 0;

	public IdleClientHandler() {
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			String type = "";
			if (event.state() == IdleState.READER_IDLE) {
				type = "read idle";
			} else if (event.state() == IdleState.WRITER_IDLE) {
				type = "write idle";
				ctx.close();
			} else if (event.state() == IdleState.ALL_IDLE) {
				type = "all idle";
			}
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}

	/**
	 * 发送ping消息
	 * 
	 * @param context
	 */
	protected void sendPingMsg(ChannelHandlerContext context) {
		System.out.println("heartbeat:" + NettyClient.CLIENTID);
		context.channel().writeAndFlush(MessageBase.newBuilder().setClientId(NettyClient.CLIENTID).setCmd(CommandType.PING)
				.setData("This is a ping msg").build());
		heartbeatCount++;
	}

	/**
	 * 处理断开重连
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		nettyClient.doConnect();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

	}

	public NettyClient getNettyClient() {
		return nettyClient;
	}

	public void setNettyClient(NettyClient nettyClient) {
		this.nettyClient = nettyClient;
	}

}
