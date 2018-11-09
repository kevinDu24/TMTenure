package cn.net.leadu.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by LEO on 16/10/10.
 */
@ConfigurationProperties(prefix = "message")
@Data
public class MessageProperties {
    private String userId;
    private String password;
    private String pszMsg;
    private String iMobiCount;
    private String MsgId;
}
