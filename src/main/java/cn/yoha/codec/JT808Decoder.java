package cn.yoha.codec;

import cn.yoha.util.JT808Util;
import cn.yoha.vo.DataPackage;
import cn.yoha.vo.req.HeartBeatMsg;
import cn.yoha.vo.req.LocationMsg;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static cn.yoha.config.JT808Constant.TERNIMAL_MSG_HEARTBEAT;
import static cn.yoha.config.JT808Constant.TERNIMAL_MSG_LOCATION;

/**
 * 解码器：1，将转义字符还原
 *         2，校验
 *         3，解码为具体的消息类型
 */
@Slf4j
public class JT808Decoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        log.debug(">>>> ip: {}, hex: {}",ctx.channel().remoteAddress(), ByteBufUtil.hexDump(in));
        DataPackage msg = decode(in);
        if (msg != null) {
            out.add(msg);
        }
    }

    private DataPackage decode(ByteBuf in) {
        // 转义，这里生成的ByteBuf在最终生成完具体消息实体后会被释放
        ByteBuf msg = revert(in);
        // 校验：获取最后一位效验码，根据规则计算出数据自身的校验码，对比；若不同，记录日志、释放资源、返回null；若相同，解码
        byte ori = msg.getByte(msg.writerIndex() - 1);
        msg.writerIndex(msg.writerIndex()-1);
        byte ans = JT808Util.checkMsg(msg);
        if (ori != ans) {
            log.warn("效验码错误，ori：{}，ans：{}",ori,ans);
            ReferenceCountUtil.release(msg);
            return null;
        }
        // 解码
        return parse(msg);
    }

    private ByteBuf revert(ByteBuf in) {
        byte[] bs = new byte[in.readableBytes()];
        in.readBytes(bs);
        ByteBuf msg = ByteBufAllocator.DEFAULT.heapBuffer();
        for (int i=0; i<bs.length; i++) {
            if (bs[i] == 0x7d && bs[i+1] == 0x01) {
                msg.writeByte(0x7d);
                i++;
            } else if (bs[i] == 0x7d && bs[i+1] == 0x02) {
                msg.writeByte(0x7e);
                i++;
            } else {
                msg.writeByte(bs[i]);
            }
        }
        return msg;
    }

    private DataPackage parse(ByteBuf in) {
        DataPackage msg = null;
        short msgId = in.getShort(in.readerIndex());
        switch (msgId) {
            case TERNIMAL_MSG_HEARTBEAT:
                msg = new HeartBeatMsg(in);
                break;
            case TERNIMAL_MSG_LOCATION:
                msg = new LocationMsg(in);
                break;
            default:
                msg = new DataPackage(in);
                break;
        }
        msg.parse();
        return msg;
    }
}
