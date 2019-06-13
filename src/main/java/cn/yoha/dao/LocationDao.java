package cn.yoha.dao;

import cn.yoha.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationDao extends JpaRepository<Location, Long> {
}
