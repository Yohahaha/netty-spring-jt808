package cn.yoha.vo;

import cn.yoha.util.BCD;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.ReferenceCountUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class DataPackage {

    protected Header header = new Header();
    protected ByteBuf byteBuf;

    public DataPackage() {
    }

    public DataPackage(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    /**
     * 解析请求报文
     */
    public void parse() {
        try {
            this.parseHead();
            if (this.header.getMsgBodyLength() != this.byteBuf.readableBytes()) {
                throw new IllegalArgumentException("包体长度有误");
            }
            this.parseBody();
        } finally {
            ReferenceCountUtil.release(this.byteBuf);
        }
    }

    private void parseHead() {
        header.setMsgId(byteBuf.readShort());
        header.setMsgBodyProps(byteBuf.readShort());
        header.setTerminalPhone(BCD.BCDtoString(readLength(6)));
        header.setFlowId(byteBuf.readShort());
        if (header.hasSubPackage()) {
            // todo 处理分包问题
            byteBuf.readInt();
        }
    }

    /**
     * 留给请求报文重写，或许可以定义为抽象方法？
     */
    protected void parseBody() {

    }

    public byte[] readLength(int length) {
        byte[] bs = new byte[length];
        this.byteBuf.readBytes(bs);
        return bs;
    }

    /**
     * 响应报文生成消息头
     * 这里先大致写了响应头，由子类继续完成需要的字段，注意最后释放ByteBuf
     */
    public ByteBuf toByteBufMsg() {
        ByteBuf msg = ByteBufAllocator.DEFAULT.heapBuffer();
        msg.writeInt(0); // 占位msgId和msgBodyProps
        msg.writeBytes(BCD.toBcdBytes(StringUtils.leftPad(this.header.getTerminalPhone(), 12, "0")));
        msg.writeShort(this.header.getFlowId());
        // todo 处理响应报文的分包
        return msg;
    }

    @Data
    public static class Header {
        private short msgId;
        private short msgBodyProps;
        private String terminalPhone;
        private short flowId;

        public short getMsgBodyLength() {
            return (short) (msgBodyProps & 0x3ff);
        }

        public short getEncryptionType() {
            return (short) ((msgBodyProps & 0x1c00) >> 10);
        }

        public boolean hasSubPackage() {
            return ((msgBodyProps & 0x2000) >> 13) == 1;
        }
    }
}
