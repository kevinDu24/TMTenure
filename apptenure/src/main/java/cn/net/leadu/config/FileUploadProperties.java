package cn.net.leadu.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by wisely on 2015/10/23.
 */
@ConfigurationProperties(prefix = "file")
@Data
public class FileUploadProperties {

    private String idCardPath;
    private String requestIdCardPath;
    private String faceImagePath;
    private String requestFaceImagePath;
    private String pdfPath;
    private String requestPdfPath;
    private String signedPdfPath;
    private String requestSignedPdfPath;

}
