package cn.net.leadu.dto;

import lombok.Data;

/**
 * Created by pengchao on 2018/5/21.
 */
@Data
public class FaceMatchImageDto {

    private String image; //

    private String image_type; //BASE64,URL,FACE_TOKEN

    private String face_type; //LIVE,CERT

    private String quality_control; //图片质量控制

    private String liveness_control; //活体检测控制

    private String id_card_number; //身份证号

    private String name; //姓名

    private String face_field;

    public FaceMatchImageDto(String image, String image_type, String face_type, String quality_control, String liveness_control) {
        this.image = image;
        this.image_type = image_type;
        this.face_type = face_type;
        this.quality_control = quality_control;
        this.liveness_control = liveness_control;
    }

    public FaceMatchImageDto(String image, String image_type, String id_card_number, String name, String quality_control, String liveness_control) {
        this.image = image;
        this.image_type = image_type;
        this.id_card_number = id_card_number;
        this.name = name;
        this.quality_control = quality_control;
        this.liveness_control = liveness_control;
    }

    public FaceMatchImageDto(String image, String image_type, String face_field) {
        this.image = image;
        this.image_type = image_type;
        this.face_field = face_field;
    }

    public FaceMatchImageDto() {
    }
}
