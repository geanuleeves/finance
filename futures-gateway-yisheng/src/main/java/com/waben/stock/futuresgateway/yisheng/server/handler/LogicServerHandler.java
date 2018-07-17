package com.waben.stock.futuresgateway.yisheng.server.handler;

import com.waben.stock.futuresgateway.yisheng.common.protobuf.Command;
import com.waben.stock.futuresgateway.yisheng.common.protobuf.Message;
import com.waben.stock.futuresgateway.yisheng.server.ChannelRepository;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
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
	private final AttributeKey<String> hyInfo = AttributeKey.valueOf("hyInfo");
	private final AttributeKey<String> pzInfo = AttributeKey.valueOf("pzInfo");

	private final AttributeKey<String> test1 = AttributeKey.valueOf("test1");
	private final AttributeKey<String> test2 = AttributeKey.valueOf("test2");

	private final AttributeKey<Long> requestTypeInfo = AttributeKey.valueOf("requestTypeInfo");



	@Autowired
	@Qualifier("channelRepository")
	ChannelRepository channelRepository;

	@Override
	public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
		final Message.MessageBase msgBase = (Message.MessageBase)msg;
		// log.info("服务器端收到消息:" + msgBase.getClientId());
		String clientId = msgBase.getClientId();
		if(StringUtils.isEmpty(clientId)){
			return;
		}
		Attribute<String> attr = ctx.channel().attr(clientInfo);
		attr.set(clientId);

		Channel ch = channelRepository.get(clientId);
		if(null == ch){
			ch = ctx.channel();

			channelRepository.put(clientId, ch);
		}

		if(msgBase.getCmd().equals(Command.CommandType.PING)) {
			//处理ping消息
			// log.info("服务端接受到ping");
			ctx.writeAndFlush(createData(clientId, Command.CommandType.PING, "你的请求类型是" + ctx.channel().attr(requestTypeInfo)).build());
		}else if(msgBase.getCmd().equals(Command.CommandType.AUTH)){

		}else if(msgBase.getCmd().equals(Command.CommandType.PUSH_DATA)){
			Long requestType = msgBase.getRequestType();
			Attribute<Long> a = ctx.channel().attr(requestTypeInfo);
			a.set(requestType);

			String data = msgBase.getData();
			if(requestType != null && requestType == 1) {
				// 设置合约编号和品种编号
				if(!StringUtils.isEmpty(data)){
					String[] datas = data.split("&");
					if(datas.length == 2){
						Attribute<String> hyattr = ctx.channel().attr(hyInfo);
						Attribute<String> pzattr = ctx.channel().attr(pzInfo);
						hyattr.set(datas[0]);
						pzattr.set(datas[1]);
					}
				}
			}
		}

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
		log.error("exception, client is " + clientId);
		cause.printStackTrace();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		//channel失效处理,客户端下线或者强制退出等任何情况都触发这个方法
		if(ctx.channel().isOpen()){
			ctx.channel().close();
		}
		channelRepository.remove(ctx.channel().attr(clientInfo).get());
	}

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

	private Message.MessageBase.Builder createData(String clientId, Command.CommandType cmd, String data){
		Message.MessageBase.Builder msg = Message.MessageBase.newBuilder();
		msg.setClientId(clientId);
		msg.setCmd(cmd);
		msg.setData(data);
		return msg;
	}
}
