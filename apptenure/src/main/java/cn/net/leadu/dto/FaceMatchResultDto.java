package cn.net.leadu.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by pengchao on 2018/5/22.
 */
@Data
public class FaceMatchResultDto {

    private String score; //对比得分
    
    private List<FaceListDto> face_list; //
     

}
