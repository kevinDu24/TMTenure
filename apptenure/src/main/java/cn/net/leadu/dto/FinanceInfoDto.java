package cn.net.leadu.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by zcHu on 2017/5/18.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FinanceInfoDto{

    @JsonProperty("usedCarPrice")
    private String price; //二手车评估价

    @JsonProperty("carBrand")
    private String brand; //车辆品牌

    @JsonProperty("carBrandType")
    private String type; //车型

    @JsonProperty("carNumber")
    private String vehicleIdentifyNum; //车架号

    @JsonProperty("carColor")
    private String color; //颜色

    @JsonProperty("carPlateNumber")
    private String vehicleNum; //车牌号

    @JsonProperty("bankNumber")
    private String bankCard; //还款卡号

    @JsonProperty("basqbh")
    @JsonIgnore
    private String basqbh; //历史申请编号

}
