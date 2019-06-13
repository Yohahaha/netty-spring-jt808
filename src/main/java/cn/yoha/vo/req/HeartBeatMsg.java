package cn.yoha.vo.req;

import cn.yoha.vo.DataPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class HeartBeatMsg extends DataPackage {
    public HeartBeatMsg(ByteBuf byteBuf) {
        super(byteBuf);
    }
}
