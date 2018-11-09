package cn.net.leadu.dto;

import cn.net.leadu.util.CommonUtils;
import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * Created by qiaohao on 2017/5/6.
 */
@Data
public class WebSearchDto {

    private String name; // 申请人姓名

    private String phoneNum; // 手机号

    private String applyNum; // 申请编号

    private String idCard; // 身份证号

    private String money;//融资金额

    private String month;//贷款期限:12/24

    private String faceImageUrl; // 活体检测截取照片

    private String idCardUrl; // 身份证照片

    private String contactSignedPdf; // 合同url（已签署）

    private String confirmationSignedPdf; // 确认函url（已签署）

    private Date createTime;

    private Date updateTime;

    private String searchType;


    public WebSearchDto(){


    }

    /**
     * 有参构造方法
     *
     * @param map
     * @param submitFlag 是否已提交的flag
     */
    public WebSearchDto(Map<String,Object> map, boolean submitFlag) {
        this.name = CommonUtils.getStr(map.get("name"));
        this.phoneNum = CommonUtils.getStr(map.get("phonenum"));
        this.idCard = CommonUtils.getStr(map.get("idcard"));
        if(submitFlag){
            this.applyNum = CommonUtils.getStr(map.get("applynum"));
            this.money = CommonUtils.getStr(map.get("money"));
            this.month = CommonUtils.getStr(map.get("month"));
            this.faceImageUrl = CommonUtils.getStr(map.get("faceimageurl"));
            this.idCardUrl = CommonUtils.getStr(map.get("idcardurl"));
            this.updateTime = CommonUtils.getDate(map.get("updatetime"));
            this.contactSignedPdf = CommonUtils.getStr(map.get("contactsignedpdf"));
            this.confirmationSignedPdf = CommonUtils.getStr(map.get("confirmationsignedpdf"));
        } else {
            this.createTime = CommonUtils.getDate(map.get("createtime"));
        }
    }

}
