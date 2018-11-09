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
public class ContractCreateDto implements Serializable{

    @JsonProperty("basqbh")
    private String applyNum; //二次营销申请编号

    @JsonProperty("baddbh")
    private String contractNum; //合同编号

    @JsonProperty("contractUrl")
    private String contactPdf; //合同url

    @JsonProperty("confirmationUrl")
    private String confirmationUrl; //确认函url


}
