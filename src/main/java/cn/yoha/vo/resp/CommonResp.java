package cn.yoha.vo.resp;

import cn.yoha.config.JT808Constant;
import cn.yoha.vo.DataPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

/**
 * 公共的默认响应，消息头由父类构造，自己重写消息体
 */
@Data
public class CommonResp extends DataPackage {

    public static final byte SUCCESS = 0;
    public static final byte FAILURE = 1;
    public static final byte MSG_ERROR = 2;
    public static final byte UNSUPPORTED = 3;
    public static final byte ALARM_PROCESS_ACK = 4;

    private short replyFlowId;
    private short replyId;
    private byte result;

    private CommonResp() {
    }

    @Override
    public ByteBuf toByteBufMsg() {
        ByteBuf body = super.toByteBufMsg();
        body.writeShort(replyFlowId);
        body.writeShort(replyId);
        body.writeByte(result);
        return body;
    }

    public static CommonResp success(DataPackage msg, short flowId) {
        CommonResp resp = new CommonResp();
        resp.getHeader().setMsgId(JT808Constant.SERVER_RESP_COMMON);
        resp.getHeader().setTerminalPhone(msg.getHeader().getTerminalPhone());
        resp.getHeader().setFlowId(flowId);
        resp.setReplyFlowId(msg.getHeader().getFlowId());
        resp.setReplyId(msg.getHeader().getMsgId());
        resp.setResult(SUCCESS);
        return resp;
    }
}
