package com.waben.stock.datalayer.futures.quote.nettyclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.waben.stock.datalayer.futures.quote.QuoteContainer;
import com.waben.stock.datalayer.futures.quote.protobuf.Command.CommandType;
import com.waben.stock.datalayer.futures.quote.protobuf.FuturesQuoteFullData.FuturesQuoteFullDataBase;
import com.waben.stock.datalayer.futures.quote.protobuf.Message.MessageBase;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Component
public class LogicClientHandler extends SimpleChannelInboundHandler<MessageBase> {
	
	@Autowired
	private QuoteContainer quoteContainer;

	// 连接成功后，向server发送消息
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		MessageBase.Builder authMsg = MessageBase.newBuilder();
		authMsg.setClientId(NettyClient.CLIENTID);
		authMsg.setCmd(CommandType.PUSH_DATA);
		authMsg.setRequestType(3L);
		ctx.writeAndFlush(authMsg.build());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("连接断开 ");
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, MessageBase msg) throws Exception {
		if (msg.getCmd().equals(CommandType.AUTH_BACK)) {
			System.out.println("验证成功");
			ctx.writeAndFlush(MessageBase.newBuilder().setClientId(NettyClient.CLIENTID).setCmd(CommandType.PUSH_DATA)
					.setData("This is upload data").build());

		} else if (msg.getCmd().equals(CommandType.PING)) {
			// 接收到server发送的ping指令
			System.out.println(msg.getData());

		} else if (msg.getCmd().equals(CommandType.PONG)) {
			// 接收到server发送的pong指令
			System.out.println(msg.getData());

		} else if (msg.getCmd().equals(CommandType.PUSH_DATA)) {
			// 接收到server推送数据
			long requestType = msg.getRequestType();
			if (requestType == 3) {
				FuturesQuoteFullDataBase fullData = msg.getFullFq();
				quoteContainer.pushQuote(fullData);
			}
		} else if (msg.getCmd().equals(CommandType.PUSH_DATA_BACK)) {
			// 接收到server返回数据
			System.out.println(msg.getData());

		} else {
			System.out.println(msg.getData());
		}
	}
}
