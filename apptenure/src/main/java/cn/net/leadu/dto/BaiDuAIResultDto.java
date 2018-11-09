package cn.net.leadu.dto;

import lombok.Data;

/**
 * Created by pengchao on 2018/5/22.
 */
@Data
public class BaiDuAIResultDto {
    
    private String error_code; //
    
    private String error_msg; //
    
    private String log_id; //
    
    private String timestamp;
    
    private String cached;

    private FaceMatchResultDto result;
     
     
}
