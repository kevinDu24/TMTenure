package cn.net.leadu.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息表
 * Created by zcHu on 2017/5/11.
 */
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonalInfo implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid",strategy = "uuid")
    private String id;

    private String phoneNum; //手机号

    private String idCard; //身份证号

    private String name; //姓名

    private String money;//融资金额

    private String month;//贷款期限:12/24

    private String pay; //月还款额

    private String state; //订单状态:0未提交、1已提交

    private String historyApplyNum; //历史申请编号

    private String applyNum; //申请编号

    private String contractNum; //合同号

    private String contactPdf; // 合同url（未签署，主系统）

    private String contactSignedPdf; // 合同url（已签署）

    private String confirmationPdf; // 确认函url（未签署，主系统）

    private String confirmationSignedPdf; // 确认函url（已签署）

    private String accountId; // e签宝账户标识

    private String sealData; // 个人电子印章图片base64数据

    private String signServiceId; // 合同签署记录id

    private String signConfirmationServiceId; // q确认函签署记录id

    private String faceImageUrl; // 活体检测截取照片

    private String idCardUrl; // 身份证照片

    @CreatedDate
    private Date createTime;

    @LastModifiedDate
    private Date updateTime;

}
