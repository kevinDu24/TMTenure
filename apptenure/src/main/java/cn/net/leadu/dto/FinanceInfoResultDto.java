package cn.net.leadu.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Created by zcHu on 2017/5/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class FinanceInfoResultDto {

    private String price; //二手车评估价

    private String brand; //车辆品牌

    private String type; //车型

    private String vehicleIdentifyNum; //车架号

    private String color; //颜色

    private String vehicleNum; //车牌号

    private String bankCard; //还款卡号

    private String basqbh; //历史申请编号


    public FinanceInfoResultDto(FinanceInfoDto dto) {
        this.price = dto.getPrice();
        this.brand = dto.getBrand();
        this.type = dto.getType();
        this.vehicleIdentifyNum = dto.getVehicleIdentifyNum();
        this.color = dto.getColor();
        this.vehicleNum = dto.getVehicleNum();
        this.bankCard = dto.getBankCard();
        this.basqbh = dto.getBasqbh();
    }
}
