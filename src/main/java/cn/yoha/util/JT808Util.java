package cn.yoha.util;

import io.netty.buffer.ByteBuf;

public class JT808Util {
    /**
     * 校验规则：从消息头开始直到校验码之前，每一个字节都和下一个字节求异或
     * 由于传入时已经去掉原数据的校验码字段，直接从readerIndex计算到writerIndex即可
     */
    public static byte checkMsg(ByteBuf msg) {
        byte ans = msg.getByte(msg.readerIndex());
        for (int i = msg.readerIndex() + 1; i < msg.writerIndex(); i++) {
            ans = (byte) (ans ^ msg.getByte(i));
        }
        return ans;
    }
}
