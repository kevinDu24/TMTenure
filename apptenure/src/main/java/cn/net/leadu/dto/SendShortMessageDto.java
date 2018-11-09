package cn.net.leadu.dto;

import lombok.Data;

/**
 * Created by pengchao on 2018/7/23.
 */
@Data
public class SendShortMessageDto {

    private String phoneNum; //手机号

    private String text; //短信内容

}
