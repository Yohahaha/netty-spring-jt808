package cn.yoha.handler;

import cn.yoha.vo.req.HeartBeatMsg;
import cn.yoha.vo.resp.CommonResp;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ChannelHandler.Sharable
public class HeartBeatHandler extends BaseHandler<HeartBeatMsg> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HeartBeatMsg msg) throws Exception {
        log.debug(msg.toString());
        CommonResp resp = CommonResp.success(msg, getSerialNumber(ctx.channel()));
        write(ctx, resp);
    }
}
