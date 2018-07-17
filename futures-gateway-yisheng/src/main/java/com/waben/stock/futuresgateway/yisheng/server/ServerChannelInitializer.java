package com.waben.stock.futuresgateway.yisheng.server;

import com.waben.stock.futuresgateway.yisheng.common.protobuf.Message;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Qualifier("serverChannelInitializer")
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
	private final static int READER_IDLE_TIME_SECONDS = 600;//读操作空闲20秒
	private final static int WRITER_IDLE_TIME_SECONDS = 600;//写操作空闲20秒
	private final static int ALL_IDLE_TIME_SECONDS = 1200;//读写全部空闲40秒

    
    @Autowired
    @Qualifier("logicServerHandler")
    private ChannelInboundHandlerAdapter logicServerHandler;

    
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
    	ChannelPipeline p = socketChannel.pipeline();
    	
    	//添加心跳机制,n秒查看一次在线的客户端channel是否空闲
        p.addLast("idleStateHandler", new IdleStateHandler(READER_IDLE_TIME_SECONDS
                , WRITER_IDLE_TIME_SECONDS, ALL_IDLE_TIME_SECONDS, TimeUnit.SECONDS));
        
        // 用于在序列化的字节数组前加上一个简单的包头，只包含序列化的字节长度。
        p.addLast("frameEncoder",
                new ProtobufVarint32LengthFieldPrepender());
        //配置Protobuf编码器，发送的消息会先经过编码
        p.addLast("protobufEncoder", new ProtobufEncoder());

        

        p.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());// 用于decode前解决半包和粘包问题（利用包头中的包含数组长度来识别半包粘包）
        //配置Protobuf解码处理器，消息接收到了就会自动解码，ProtobufDecoder是netty自带的，Message是自己定义的Protobuf类
        p.addLast("protobufDecoder",
                new ProtobufDecoder(Message.MessageBase.getDefaultInstance()));



        p.addLast("logicServerHandler", logicServerHandler);

    }
}
