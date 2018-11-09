package cn.net.leadu.dao;

import cn.net.leadu.domain.PersonalInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by zcHu on 17/5/11.
 */
public interface PersonalInfoRepository extends JpaRepository<PersonalInfo, String> {
    PersonalInfo findByPhoneNum(String phoneNum);
    PersonalInfo findByApplyNum(String applyNum);
}
