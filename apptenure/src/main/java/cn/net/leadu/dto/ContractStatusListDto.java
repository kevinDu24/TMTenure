package cn.net.leadu.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by zcHu on 2017/5/18.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContractStatusListDto implements Serializable{

    @JsonProperty("BASQZC")
    private String statusName; //申请状态名称

    @JsonProperty("BASQZT")
    private String statusCode; //申请状态代码

    @JsonProperty("BALCBZ")
    private String remark; //流程备注

    @JsonProperty("XTCZRQ")
    private String day; //操作日期

    @JsonProperty("XTCZSJ")
    private String time; //操作时间

    @JsonProperty("TIME")
    private String date; //操作日期时间

    @JsonProperty("XTCZRY")
    private String operator; //操作人员


}
