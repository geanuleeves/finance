package com.waben.stock.datalayer.futures.quote.nettyclient;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.waben.stock.datalayer.futures.quote.protobuf.Message.MessageBase;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * netty客户端
 */
@Component
public class NettyClient {

	public static String CLIENTID = "FuturesBack-" + UUID.randomUUID().toString();
	private final static String HOST = "47.106.253.130";
	private final static int PORT = 9094;
	private final static int READER_IDLE_TIME_SECONDS = 10;// 读操作空闲20秒
	private final static int WRITER_IDLE_TIME_SECONDS = 10;// 写操作空闲20秒
	private final static int ALL_IDLE_TIME_SECONDS = 20;// 读写全部空闲40秒

	@Autowired
	private IdleClientHandler idleHandler;

	@Autowired
	private LogicClientHandler logicHandler;

	@PostConstruct
	public void init() throws Exception {
		NettyClient _this = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				_this.connect();
			}
		}).start();
	}

	public void connect() {
		try {
			doConnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * netty client 连接，连接失败10秒后重试连接
	 */
	public Bootstrap doConnect() {
		idleHandler.setNettyClient(this);
		logicHandler.setNettyClient(this);
		Bootstrap bootstrap = new Bootstrap();
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
		try {
			if (bootstrap != null) {
				bootstrap.group(eventLoopGroup);
				bootstrap.channel(NioSocketChannel.class);
				bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
				bootstrap.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline p = ch.pipeline();

						p.addLast("idleStateHandler", new IdleStateHandler(READER_IDLE_TIME_SECONDS,
								WRITER_IDLE_TIME_SECONDS, ALL_IDLE_TIME_SECONDS, TimeUnit.SECONDS));
						p.addLast("idleTimeoutHandler", idleHandler);

						p.addLast(new ProtobufVarint32FrameDecoder());
						p.addLast(new ProtobufDecoder(MessageBase.getDefaultInstance()));

						p.addLast(new ProtobufVarint32LengthFieldPrepender());
						p.addLast(new ProtobufEncoder());

						p.addLast("clientHandler", logicHandler);
					}
				});
				bootstrap.remoteAddress(HOST, PORT);
				ChannelFuture f = bootstrap.connect().addListener((ChannelFuture futureListener) -> {
					final EventLoop eventLoop = futureListener.channel().eventLoop();
					if (!futureListener.isSuccess()) {
						System.out.println("Failed to connect to server, try connect after 10s");
						futureListener.channel().eventLoop().schedule(() -> doConnect(), 10,
								TimeUnit.SECONDS);
					}
				});
				f.channel().closeFuture().sync();
				eventLoopGroup.shutdownGracefully();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return bootstrap;
	}
}
