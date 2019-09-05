package cn.yoha.server;

import cn.yoha.codec.JT808Decoder;
import cn.yoha.codec.JT808Encoder;
import cn.yoha.config.JT808Constant;
import cn.yoha.handler.HeartBeatHandler;
import cn.yoha.handler.LocationMsgHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.EventExecutorGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class JT808ChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    @Qualifier("businessGroup")
    EventExecutorGroup businessGroup;

    @Value("${netty.read-timeout}")
    private int readTimeout;

    @Autowired
    HeartBeatHandler heartBeatHandler;
    @Autowired
    LocationMsgHandler locationMsgHandler;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new IdleStateHandler(readTimeout, 0, 0, TimeUnit.MINUTES));
        pipeline.addLast(new DelimiterBasedFrameDecoder(1100,
                Unpooled.copiedBuffer(new byte[]{JT808Constant.PKG_DELIMITER}),
                Unpooled.copiedBuffer(new byte[]{JT808Constant.PKG_DELIMITER, JT808Constant.PKG_DELIMITER})));

        pipeline.addLast(new JT808Decoder());
        pipeline.addLast(new JT808Encoder());
        pipeline.addLast(heartBeatHandler);
        // 该handler需要将数据存入数据库，所以分配一个业务group去执行，避免阻塞io
        pipeline.addLast(businessGroup, locationMsgHandler);

    }
}
