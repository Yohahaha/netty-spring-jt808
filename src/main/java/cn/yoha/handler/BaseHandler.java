package cn.yoha.handler;

import cn.yoha.vo.DataPackage;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseHandler<T> extends SimpleChannelInboundHandler<T> {
    private static final AttributeKey<Short> SERIAL_NUMBER = AttributeKey.newInstance("serialNumber");

    /**
     * 获取流水号
     */
    public short getSerialNumber(Channel channel) {
        Attribute<Short> attr = channel.attr(SERIAL_NUMBER);
        Short flowId = attr.get();
        // 基本数据类型的包装类默认值为0
        if (flowId == null || flowId == 0) {
            flowId = 1;
        } else {
            flowId++;
        }
        attr.set(flowId);
        return flowId;
    }

    public void write(ChannelHandlerContext context, DataPackage msg) {
        context.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                log.error("数据发送失败。", future.cause());
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exceptionCaught: " + cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            log.warn("客户端 {} 读取超时，自动断开。", ctx.channel().remoteAddress());
            ctx.close();
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
