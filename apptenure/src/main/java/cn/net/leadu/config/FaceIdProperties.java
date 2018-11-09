package cn.net.leadu.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by LEO on 16/10/10.
 */
@ConfigurationProperties(prefix = "faceid")
@Data
public class FaceIdProperties {
    private String apiKey;
    private String apiSecret;
}
