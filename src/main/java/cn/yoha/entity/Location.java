package cn.yoha.entity;

import cn.yoha.vo.req.LocationMsg;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "location_log")
public class Location extends AbstractPersistable<Long> {
    private String terminalPhone; // 终端手机号
    private Integer alarm;
    private Integer statusField;
    private Float latitude;
    private Float longitude;
    private Short elevation;
    private Short speed;
    private Short direction;
    private String time;

    public static Location parseFromLocationMsg(LocationMsg msg) {
        Location location = new Location();
        location.setTerminalPhone(msg.getHeader().getTerminalPhone());
        BeanUtils.copyProperties(msg, location);
        return location;
    }
}
