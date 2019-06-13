package cn.yoha.handler;

import cn.yoha.dao.LocationDao;
import cn.yoha.entity.Location;
import cn.yoha.vo.req.LocationMsg;
import cn.yoha.vo.resp.CommonResp;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ChannelHandler.Sharable
public class LocationMsgHandler extends BaseHandler<LocationMsg> {
    @Autowired
    LocationDao locationDao;

    @Autowired
    @Qualifier("workerGroup")
    EventLoopGroup workerGroup;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LocationMsg msg) throws Exception {
        log.debug(msg.toString());
        locationDao.save(Location.parseFromLocationMsg(msg));
        CommonResp resp = CommonResp.success(msg, getSerialNumber(ctx.channel()));
        workerGroup.execute(() -> write(ctx, resp));
    }
}
