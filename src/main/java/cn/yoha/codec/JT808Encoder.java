package cn.yoha.codec;

import cn.yoha.config.JT808Constant;
import cn.yoha.util.JT808Util;
import cn.yoha.vo.DataPackage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * JT808协议编码器
 * 1，获取消息的ByteBuf流
 * 2，分割出消息体和消息头
 * 3，填充之前占位的msgId和msgProps
 * 4，添加校验码
 * 5，转义
 */
@Slf4j
public class JT808Encoder extends MessageToByteEncoder<DataPackage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, DataPackage msg, ByteBuf out) throws Exception {
        log.debug(msg.toString());
        ByteBuf bb = msg.toByteBufMsg();
        bb.markWriterIndex(); // 记录写标记，为了写之前占位的字节
        short bodyLen = (short) (bb.readableBytes() - 12);
        if (msg.getHeader().hasSubPackage()) {
            bodyLen -= 4;
        }
        short bodyProps = createDefaultMsgBodyProps(bodyLen);
        bb.writerIndex(0);
        bb.writeShort(msg.getHeader().getMsgId());
        bb.writeShort(bodyProps);
        bb.resetWriterIndex(); // 恢复之前的写标记
        bb.writeByte(JT808Util.checkMsg(bb));
        log.debug("<<<< ip: {}, hex: {}\n", ctx.channel().remoteAddress(), ByteBufUtil.hexDump(bb));
        ByteBuf escape = escape(bb);
        out.writeBytes(escape);
        ReferenceCountUtil.release(escape);
    }

    private ByteBuf escape(ByteBuf in) {
        // 0x7d -> 0x7d 0x01; 0x7e -> 0x7d 0x02
        int len = in.readableBytes();
        ByteBuf msg = ByteBufAllocator.DEFAULT.heapBuffer(len + 12);
        msg.writeByte(JT808Constant.PKG_DELIMITER);
        while (in.isReadable()) {
            byte b = in.readByte();
            if (b == 0x7d) {
                msg.writeByte(0x7d);
                msg.writeByte(0x01);
            } else if (b == 0x7e) {
                msg.writeByte(0x7d);
                msg.writeByte(0x02);
            } else {
                msg.writeByte(b);
            }
        }
        ReferenceCountUtil.release(in);
        msg.writeByte(JT808Constant.PKG_DELIMITER);
        return msg;
    }

    private short createDefaultMsgBodyProps(short len) {
        return createMsgBodyProps(len, (byte) 0, false, (byte) 0);
    }

    private short createMsgBodyProps(short len, byte encType, boolean isSubPackage, byte reversed) {
        int subPack = isSubPackage ? 1 : 0;
        int ret = ((reversed << 14) & 0xc000) | ((subPack << 13) & 0x2000) | ((encType << 10) & 0x1c00) | (len & 0x3ff);
        return (short) (ret & 0xffff);
    }
}
