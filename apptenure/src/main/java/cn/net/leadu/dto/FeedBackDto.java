package cn.net.leadu.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by zcHu on 2017/5/18.
 */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class FeedBackDto implements Serializable{

        private String content;

        private String phoneNum;

}
