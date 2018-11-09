package cn.net.leadu.dao;

import cn.net.leadu.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by zcHu on 17/5/11.
 */
public interface NoticeRepository extends JpaRepository<Notice, String> {
    List<Notice> findByPhoneNumAndStatusOrderByCreateTimeDesc(String phoneNum, String status);
    Notice findById(String id);
}
