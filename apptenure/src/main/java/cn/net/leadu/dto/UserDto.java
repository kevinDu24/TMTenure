package cn.net.leadu.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by zcHu on 2017/5/18.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto implements Serializable{

    private String phoneNum;

    private String uniqueMark;

    private String name;

    public UserDto(String uniqueMark, String phoneNum, String name){
        this.uniqueMark = uniqueMark;
        this.phoneNum = phoneNum;
        this.name = name;
    }

}
