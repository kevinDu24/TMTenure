package cn.net.leadu.dto.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;


/**
 * Created by LEO on 16/9/29.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoreResult {

    private CoreRes result;

    private String image;
}
