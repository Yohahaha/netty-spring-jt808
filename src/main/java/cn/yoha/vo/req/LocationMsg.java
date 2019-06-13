package cn.yoha.vo.req;

import cn.yoha.util.BCD;
import cn.yoha.vo.DataPackage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class LocationMsg extends DataPackage {

    public LocationMsg(ByteBuf byteBuf) {
        super(byteBuf);
    }

    /*
            报警信息4字节
            状态字段4字节
            纬度    4字节
            精度    4字节
            高度    2字节
            速度    2字节
            方向    2字节
            时间    6字节BCD
    */
    private int alarm;
    private int statusField;
    private float latitude;
    private float longitude;
    private short elevation;
    private short speed;
    private short direction;
    private String time;

    @Override
    protected void parseBody() {
        ByteBuf body = this.byteBuf;
        this.setAlarm(body.readInt());
        this.setStatusField(body.readInt());
        this.setLatitude(body.readUnsignedInt() * 1.0F / 1000000);
        this.setLongitude(body.readUnsignedInt() * 1.0F / 1000000);
        this.setElevation(body.readShort());
        this.setSpeed(body.readShort());
        this.setDirection(body.readShort());
        this.setTime(BCD.toBcdTimeString(readLength(6)));
    }
}
