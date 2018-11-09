package cn.net.leadu.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by LEO on 16/10/10.
 */
@ConfigurationProperties(prefix = "admin")
@Data
public class AccountProperties {
    private String auth;
}
