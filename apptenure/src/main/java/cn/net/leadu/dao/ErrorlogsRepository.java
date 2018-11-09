package cn.net.leadu.dao;

import cn.net.leadu.domain.ErrorLogs;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by zcHu on 17/5/11.
 */
public interface ErrorlogsRepository extends JpaRepository<ErrorLogs, String> {
}
