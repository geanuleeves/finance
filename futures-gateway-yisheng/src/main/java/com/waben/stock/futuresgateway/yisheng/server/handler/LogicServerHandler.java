package com.waben.stock.futuresgateway.yisheng.server.handler;

import com.waben.stock.futuresgateway.yisheng.common.protobuf.Command;
import com.waben.stock.futuresgateway.yisheng.common.protobuf.Message;
import com.waben.stock.futuresgateway.yisheng.server.ChannelRepository;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 业务逻辑handler
 *
 */
@Component
@Qualifier("logicServerHandler")
@ChannelHandler.Sharable
public class LogicServerHandler extends ChannelInboundHandlerAdapter{
	public Logger log = Logger.getLogger(this.getClass());
	private final AttributeKey<String> clientInfo = AttributeKey.valueOf("clientInfo");
	private final AttributeKey<Long> requestTypeInfo = AttributeKey.valueOf("requestType");
	private final AttributeKey<String> hyInfo = AttributeKey.valueOf("hyInfo");
	private final AttributeKey<String> pzInfo = AttributeKey.valueOf("pzInfo");

	@Autowired
	@Qualifier("channelRepository")
	ChannelRepository channelRepository;

	@Override
	public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
		final Message.MessageBase msgBase = (Message.MessageBase)msg;

		log.info(msgBase.getData());


		String clientId = msgBase.getClientId();
		Channel ch = channelRepository.get(clientId);
		if(null == ch){
			ch = ctx.channel();
			Attribute<String> attr = ctx.attr(clientInfo);
			attr.set(clientId);
			channelRepository.put(clientId, ch);
		}



		if(msgBase.getCmd().equals(Command.CommandType.PING)){
			//处理ping消息
			log.info("服务端接受到ping");
			ctx.writeAndFlush(createData(clientId, Command.CommandType.PING, "This is ping data").build());
		}else if(msgBase.getCmd().equals(Command.CommandType.PUSH_DATA)){
			Long requestType = msgBase.getRequestType();
			String data = msgBase.getData();
			// 设置请求类型
			Attribute<Long> rtattr = ctx.attr(requestTypeInfo);
			rtattr.set(requestType);
			if(requestType != null && requestType == 1) {
				// 设置合约编号和品种编号
				if(!StringUtils.isEmpty(data)){
					String[] datas = data.split("&");
					if(datas.length == 2){
						Attribute<String> hyattr = ctx.attr(hyInfo);
						Attribute<String> pzattr = ctx.attr(pzInfo);
						hyattr.set(datas[0]);
						pzattr.set(datas[1]);
					}
					channelRepository.put(clientId, ctx.channel());
				}
			} else if(requestType != null && requestType == 2) {
				channelRepository.put(clientId, ctx.channel());
			}
		}

		/* 上一条消息发送成功后，立马推送一条消息 */
//		cf.addListener(new GenericFutureListener<Future<? super Void>>() {
//			@Override
//			public void operationComplete(Future<? super Void> future) throws Exception {
//				if (future.isSuccess()){
//					ctx.writeAndFlush(
//							Message.MessageBase.newBuilder()
//							.setClientId(msgBase.getClientId())
//							.setCmd(Command.CommandType.PUSH_DATA)
//							.setData("This is a push msg")
//							.build()
//							);
//				}
//			}
//		});
		ReferenceCountUtil.release(msg);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

	}

	@SuppressWarnings("deprecation")
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		Attribute<String> attr = ctx.attr(clientInfo);
		String clientId = attr.get();
		log.error("Connection closed, client is " + clientId);
		cause.printStackTrace();
	}

	private Message.MessageBase.Builder createData(String clientId, Command.CommandType cmd, String data){
		Message.MessageBase.Builder msg = Message.MessageBase.newBuilder();
		msg.setClientId(clientId);
		msg.setCmd(cmd);
		msg.setData(data);
		return msg;
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		//channel失效处理,客户端下线或者强制退出等任何情况都触发这个方法
		ctx.channel().close();
		channelRepository.remove(ctx.channel().attr(clientInfo).get());
		super.channelInactive(ctx);
	}
}
