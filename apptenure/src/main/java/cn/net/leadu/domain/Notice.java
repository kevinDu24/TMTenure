package cn.net.leadu.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户消息表
 * Created by zcHu on 2017/5/11.
 */
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Notice implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid",strategy = "uuid")
    private String id;

    private String phoneNum; //手机号

    private String type; //消息类型：1通过、2拒绝、3放款

    private String title; //消息标题

    private String info; //消息内容

    private String refuseReason; //拒绝原因

    private String status; //消息状态：0未读 、1已读

    @CreatedDate
    private Date createTime;

}
