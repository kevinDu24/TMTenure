package cn.net.leadu.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zcHu on 2017/5/18.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContractStatusDto implements Serializable{

    private String name;

    private String applyNum;

    private List<ContractStatusListDto> data;


}
