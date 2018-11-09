package cn.net.leadu.service;

import cn.net.leadu.config.FileUploadProperties;
import cn.net.leadu.dao.NoticeRepository;
import cn.net.leadu.dao.PersonalInfoRepository;
import cn.net.leadu.domain.Notice;
import cn.net.leadu.domain.PersonalInfo;
import cn.net.leadu.dto.*;
import cn.net.leadu.message.Message;
import cn.net.leadu.message.MessageType;
import cn.net.leadu.util.CommonUtils;
import cn.net.leadu.util.esign.EviDocHelper;
import cn.net.leadu.util.esign.FileHelper;
import cn.net.leadu.util.esign.SignHelper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.timevale.esign.sdk.tech.bean.result.AddSealResult;
import com.timevale.esign.sdk.tech.bean.result.FileDigestSignResult;
import net.sf.json.JSONObject;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * app功能行service
 * Created by zcHu on 2017/8/28.
 */
@Service
public class FunctionService {

    private static final Logger logger = LoggerFactory.getLogger(FunctionService.class);

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private PersonalInfoRepository personalInfoRepository;

    @Autowired
    private CoreSystemInterface coreSystemInterface;

    @Autowired
    private FileUploadProperties fileUploadProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HttpServletRequest httpServletRequest;

    /**
     * 推送消息
     * @param phoneNum
     * @param type 1通过、2拒绝、3放款
     * @param reason 拒绝原因
     * @return
     */
    public ResponseEntity<Message> addNotice(String phoneNum, String type, String reason){
        Notice notice = new Notice();
        notice.setType(type);
        notice.setTitle("1".equals(type) ? "审核通过" :
                "2".equals(type) ? "审核未通过":
                        "3".equals(type) ? "放款啦": "无");
        String info = "1".equals(type) ? "恭喜您，您的申请已通过！" :
                "2".equals(type) ? "很遗憾，您的申请被拒绝了！拒绝原因：" +  reason + "。":
                "3".equals(type) ? "您的贷款已经发放啦！请注意查收。": "";
                notice.setInfo(info);
        notice.setInfo(info);
        if(reason != null){
            notice.setRefuseReason(reason);
        }
        notice.setStatus("0");
        notice.setPhoneNum(phoneNum);
        noticeRepository.save(notice);
        return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_SUCCESS), HttpStatus.OK);
    }

    /**
     * 查询未读消息数量
     * @param phoneNum
     * @return
     */
    public ResponseEntity<Message> getUnReadCount(String phoneNum){
        int count = 0;
        List<Notice> noticeList = noticeRepository.findByPhoneNumAndStatusOrderByCreateTimeDesc(phoneNum, "0");
        if(noticeList != null && !noticeList.isEmpty()){
            count = noticeList.size();
        }
        return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_SUCCESS, (Object)count), HttpStatus.OK);
    }

    /**
     * 查询消息列表
     * @param phoneNum
     * @return
     */
    public ResponseEntity<Message> getNoticeList(String phoneNum, String type){
        List<Notice> noticeList = noticeRepository.findByPhoneNumAndStatusOrderByCreateTimeDesc(phoneNum, type);
        return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_SUCCESS, noticeList), HttpStatus.OK);
    }

    /**
     * 更新消息为已读
     * @param id
     * @return
     */
    public ResponseEntity<Message> updateNotice(String id){
        Notice notice = noticeRepository.findById(id);
        if(notice == null){
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, "该消息不存在"), HttpStatus.OK);
        }
        notice.setStatus("1");
        noticeRepository.save(notice);
        return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_SUCCESS), HttpStatus.OK);
    }

    /**
     * 验证身份
     * @param idCard
     * @param name
     * @return
     */
    public ResponseEntity<Message> authentication(String idCard, String name, String phoneNum){
        // *************** AppStore审核用的测试账号分支，开始 **************
        if("12345678901".equals(phoneNum)){
            FinanceInfoDto dto = new FinanceInfoDto();
            dto.setPrice("50000");
            dto.setBankCard("6228001677778393888");
            dto.setColor("白色");
            dto.setVehicleIdentifyNum("SSWHISJOSJ123456");
            dto.setBasqbh("38440469");
            dto.setType("2016旗舰款");
            dto.setBrand("本田");
            PersonalInfo personalInfo = personalInfoRepository.findByPhoneNum(phoneNum);
            personalInfo.setIdCard(idCard);
            personalInfo.setName(name);
            personalInfo.setHistoryApplyNum(dto.getBasqbh());
            personalInfoRepository.save(personalInfo);
            FinanceInfoResultDto resultDto = new FinanceInfoResultDto(dto);
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_SUCCESS, resultDto), HttpStatus.OK);
        }
        // *************** AppStore审核用的测试账号分支，结束 **************
        PersonalInfo personalInfo = personalInfoRepository.findByPhoneNum(phoneNum);
        if(!name.equals(personalInfo.getName())){
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, "只可以签订本人的合同哦!"), HttpStatus.OK);
        }
        String result = coreSystemInterface.identityCheckBy("identityCheckBy", idCard, name);
        JSONObject jsonObject =  JSONObject.fromObject(result);
        if(!MessageType.MSG_TYPE_SUCCESS.equals(jsonObject.getString("status"))){
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, "身份信息有误!" + jsonObject.getString("error")), HttpStatus.OK);
        }
        FinanceInfoDto dto = new FinanceInfoDto();
        try {
            dto = objectMapper.readValue(jsonObject.getString("data"), FinanceInfoDto.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }
        personalInfo.setIdCard(idCard);
        personalInfo.setName(name);
        personalInfo.setHistoryApplyNum(dto.getBasqbh());
        personalInfoRepository.save(personalInfo);
        FinanceInfoResultDto resultDto = new FinanceInfoResultDto(dto);
        return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_SUCCESS, resultDto), HttpStatus.OK);
    }

    /**
     * 计算月还款额
     * @param financingAmount
     * @param month
     * @return
     */
    public ResponseEntity<Message> calculatorAmount(String financingAmount, String month){
        String uniqueMark = httpServletRequest.getHeader("uniqueMark");
        String [] values = uniqueMark.split(":");
        String phoneNum = values[0];
        PersonalInfo personalInfo = personalInfoRepository.findByPhoneNum(phoneNum);
        String result = coreSystemInterface.repayBy("RepayBy", financingAmount, month, personalInfo.getHistoryApplyNum());
        JSONObject jsonObject =  JSONObject.fromObject(result);
        String data = jsonObject.getString("data");
        if(data == null || data.isEmpty()){
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, "计算月还款额失败！"), HttpStatus.OK);
        }
        return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_SUCCESS, (Object)data), HttpStatus.OK);
    }

    /**
     * 验证身份页面点击下一步
     * @param phoneNum
     * @return
     */
    public ResponseEntity<Message> saveFinanceInfo(String month, String money, String pay, String phoneNum){
        PersonalInfo personalInfo = personalInfoRepository.findByPhoneNum(phoneNum);
        personalInfo.setMonth(month);
        personalInfo.setMoney(money);
        personalInfo.setPay(pay);
        personalInfoRepository.save(personalInfo);
        return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_SUCCESS), HttpStatus.OK);
    }

    /**
     * 核验身份证图片【下一步】
     * @param phoneNum
     * @return
     */
    public ResponseEntity<Message> checkIdCard(String idCard, String name, String phoneNum){
        PersonalInfo personalInfo = personalInfoRepository.findByPhoneNum(phoneNum);
        if(!(idCard.equals(personalInfo.getIdCard()) && name.equals(personalInfo.getName()))){
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, "身份证信息不一致，请确认"), HttpStatus.OK);
        }
//        personalInfo.setIdCardUrl(url);
//        personalInfoRepository.save(personalInfo);
        return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_SUCCESS), HttpStatus.OK);
    }

    /**
     * 合同申请入口接口
     * @param phoneNum
     * @return
     */
    public ResponseEntity<Message> getApplyState(String phoneNum){
        PersonalInfo personalInfo = personalInfoRepository.findByPhoneNum(phoneNum);
        return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_SUCCESS, (Object)personalInfo.getState()), HttpStatus.OK);
    }

    /**
     * 生成并获取合同
     * @param phoneNum
     * @return
     */
    public ResponseEntity<Message> getOriginContract(String phoneNum){
        // *************** AppStore审核用的测试账号分支，开始 **************
        if("12345678901".equals(phoneNum)){
            FileUrlDto resultDto = new FileUrlDto();
            resultDto.setContactPdfUrl("http://happyleasing.cn/aa/20171011/7355692-140220-(HZLZHT).pdf");
            resultDto.setConfirmationPdfUrl("http://happyleasing.cn/aa/20171011/7355692-140219-(RZQRH).pdf");
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_SUCCESS, resultDto), HttpStatus.OK);
        }
        // *************** AppStore审核用的测试账号分支，结束 **************
        PersonalInfo personalInfo = personalInfoRepository.findByPhoneNum(phoneNum);
        String result = coreSystemInterface.contractCreate("ContractCreate", personalInfo.getHistoryApplyNum(),
                personalInfo.getMoney(), personalInfo.getMonth());
        JSONObject jsonObject =  JSONObject.fromObject(result);
        logger.info("调用主系统合同生成接口开始******************************************");
        if(!MessageType.MSG_TYPE_SUCCESS.equals(jsonObject.getString("status"))){
            logger.error("调用主系统合同生成失败error", jsonObject.getString("error"));
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, "合同生成失败!" + jsonObject.getString("error")), HttpStatus.OK);
        }
        ContractCreateDto dto = new ContractCreateDto();
        try {
            dto = objectMapper.readValue(jsonObject.getString("data"), ContractCreateDto.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }
        personalInfo.setContactPdf(dto.getContactPdf());
        personalInfo.setConfirmationPdf(dto.getConfirmationUrl());
        personalInfo.setApplyNum(dto.getApplyNum());
        personalInfo.setContractNum(dto.getContractNum());
        personalInfoRepository.save(personalInfo);
        String contract = personalInfo.getContactPdf();
        String confirmation = personalInfo.getConfirmationPdf();
        if(contract == null || confirmation == null || confirmation.isEmpty() || contract.isEmpty()){
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, "未获取到合同模板，请联系客服"), HttpStatus.OK);
        }
        FileUrlDto resultDto = new FileUrlDto();
        resultDto.setContactPdfUrl(contract);
        resultDto.setConfirmationPdfUrl(confirmation);
        return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_SUCCESS, resultDto), HttpStatus.OK);
    }

    /**
     * 发送短信（签订合同）
     * @param phoneNum
     * @return
     */
    public ResponseEntity<Message> sendMessage(String phoneNum){
        PersonalInfo personalInfo = personalInfoRepository.findByPhoneNum(phoneNum);
        String accountId = personalInfo.getAccountId();
        if(accountId == null || accountId.isEmpty()){
            accountId = SignHelper.addPersonAccount(personalInfo.getIdCard(), personalInfo.getName());
            if(accountId == null || accountId.isEmpty()){
                return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, "短信发送失败，创建个人账户失败"), HttpStatus.OK);
            }
        }
        boolean flag = SignHelper.sendMessage(accountId, phoneNum);
        if(!flag){
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, "短信发送失败!"), HttpStatus.OK);
        }
        personalInfo.setAccountId(accountId);
        personalInfoRepository.save(personalInfo);
        return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_SUCCESS), HttpStatus.OK);
    }

    /**
     * 签署确认函
     * @return
     */
    public ResponseEntity<Message> signConfirmation(String phoneNum, String code){
        String timeStamp = String.valueOf(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String uploadDate = sdf.format(new Date());
        PersonalInfo personalInfo = personalInfoRepository.findByPhoneNum(phoneNum);
        // 文件源
        String srcPdfUrl = personalInfo.getConfirmationPdf();
        URL url1 = null;
        byte[] byt = null;
        try {
            url1 = new URL(srcPdfUrl);
            HttpURLConnection conn = (HttpURLConnection)url1.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3*1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            DataInputStream in = new DataInputStream(conn.getInputStream());
            File goalFile = new File(fileUploadProperties.getPdfPath()
                    + uploadDate + "/" + personalInfo.getName() + "_确认函_"  + timeStamp + ".pdf");
            // 获取父级文件夹
            File fileParent = goalFile.getParentFile();
            if(!fileParent.exists()){
                // 不存在就创建文件夹
                fileParent.mkdirs();
            }
            DataOutputStream out = new DataOutputStream(new FileOutputStream(fileUploadProperties.getPdfPath()
                    + uploadDate + "/" + personalInfo.getName() + "_确认函_"  + timeStamp + ".pdf"));
            byte[] buffer = new byte[10485760];
            int count = 0;
            while ((count = in.read(buffer)) > 0) {
                out.write(buffer, 0, count);
            }
            out.close();
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, "合同模板下载失败"), HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, "合同模板下载失败"), HttpStatus.OK);
        }
        // 待签署的PDF文件路径
        String srcPdfFile = fileUploadProperties.getPdfPath() + uploadDate + "/" + personalInfo.getName() + "_确认函_"  + timeStamp + ".pdf";

        // 最终签署后的PDF文件路径
        String signedFolder = fileUploadProperties.getSignedPdfPath() + uploadDate + "/";
        // 最终签署后PDF文件名称
        String signedFileName = personalInfo.getName() + "_确认函_"  + timeStamp + ".pdf";
        String url = fileUploadProperties.getSignedPdfPath() + uploadDate + "/" + signedFileName;
        File goalFile = new File(url);
        // 获取父级文件夹
        File fileParent = goalFile.getParentFile();
        if(!fileParent.exists()){
            // 不存在就创建
            fileParent.mkdirs();
        }
        String downLoadUrl = fileUploadProperties.getRequestSignedPdfPath() + uploadDate + "/" + signedFileName;
        FileDigestSignResult result = doSignWithTemplateSealByStream(personalInfo, downLoadUrl, srcPdfFile, signedFolder, signedFileName, phoneNum, code, "0");
        if(0 == result.getErrCode()){
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_SUCCESS, (Object)downLoadUrl), HttpStatus.OK);
        } else {
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, "签署失败！" + result.getMsg()), HttpStatus.OK);
        }
    }

    /**
     * 签署合同
     * @return
     */
    public ResponseEntity<Message> sign(String phoneNum, String code){
        String timeStamp = String.valueOf(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String uploadDate = sdf.format(new Date());
        PersonalInfo personalInfo = personalInfoRepository.findByPhoneNum(phoneNum);
        // 文件源
        String srcPdfUrl = personalInfo.getContactPdf();
        URL url1 = null;
        byte[] byt = null;
        try {
            url1 = new URL(srcPdfUrl);
            HttpURLConnection conn = (HttpURLConnection)url1.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3*1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            DataInputStream in = new DataInputStream(conn.getInputStream());
            File goalFile = new File(fileUploadProperties.getPdfPath()
                    + uploadDate + "/" + personalInfo.getName() + "_合同_"  + timeStamp + ".pdf");
            // 获取父级文件夹
            File fileParent = goalFile.getParentFile();
            if(!fileParent.exists()){
                // 不存在就创建文件夹
                fileParent.mkdirs();
            }
            DataOutputStream out = new DataOutputStream(new FileOutputStream(fileUploadProperties.getPdfPath()
                    + uploadDate + "/" + personalInfo.getName() + "_合同_"  + timeStamp + ".pdf"));
            byte[] buffer = new byte[10485760];
            int count = 0;
            while ((count = in.read(buffer)) > 0) {
                out.write(buffer, 0, count);
            }
            out.close();
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, "合同模板下载失败"), HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, "合同模板下载失败"), HttpStatus.OK);
        }
        // 待签署的PDF文件路径
        String srcPdfFile = fileUploadProperties.getPdfPath() + uploadDate + "/" + personalInfo.getName() + "_合同_"  + timeStamp + ".pdf";

        // 最终签署后的PDF文件路径
        String signedFolder = fileUploadProperties.getSignedPdfPath() + uploadDate + "/";
        // 最终签署后PDF文件名称
        String signedFileName = personalInfo.getName() + "_合同_"  + timeStamp + ".pdf";
        String url = fileUploadProperties.getSignedPdfPath() + uploadDate + "/" + signedFileName;
        File goalFile = new File(url);
        // 获取父级文件夹
        File fileParent = goalFile.getParentFile();
        if(!fileParent.exists()){
            // 不存在就创建
            fileParent.mkdirs();
        }
        String downLoadUrl = fileUploadProperties.getRequestSignedPdfPath() + uploadDate + "/" + signedFileName;
        FileDigestSignResult result = doSignWithTemplateSealByStream(personalInfo, downLoadUrl, srcPdfFile, signedFolder, signedFileName, phoneNum, code, "1");
        if(0 == result.getErrCode()){
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_SUCCESS, (Object)downLoadUrl), HttpStatus.OK);
        } else {
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, "签署失败！" + result.getMsg()), HttpStatus.OK);
        }
    }

    /**
     * 合同提交接口
     * @param phoneNum
     * @return
     */
    public ResponseEntity<Message> submitContract(String phoneNum,String faceImageUrl, String idCardUrl){
        PersonalInfo personalInfo = personalInfoRepository.findByPhoneNum(phoneNum);
        //调用确认函文档保全服务
        boolean confirmationFlag = EviDocHelper.eviDoc(personalInfo, false);
        if(!confirmationFlag){
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, "合同提交失败，确认函文档保全错误"), HttpStatus.OK);
        }
        //调用合同文档保全服务
        boolean contractFlag = EviDocHelper.eviDoc(personalInfo, true);
        if(!contractFlag){
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, "合同提交失败，合同文档保全错误"), HttpStatus.OK);
        }
        String result = coreSystemInterface.pactSubmitBy("pactSubmitBy", personalInfo.getHistoryApplyNum(), personalInfo.getContactSignedPdf(),
                personalInfo.getConfirmationSignedPdf(), personalInfo.getMonth(), personalInfo.getMoney(),
                personalInfo.getApplyNum(), personalInfo.getContractNum(), phoneNum);
        JSONObject jsonObject =  JSONObject.fromObject(result);
        logger.info("调用主系统合同提交接口开始******************************************");
        if(!MessageType.MSG_TYPE_SUCCESS.equals(jsonObject.getString("status"))){
            logger.error("调用主系统合同提交接口失败error", jsonObject.getString("error"));
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, "合同提交失败!" + jsonObject.getString("error")), HttpStatus.OK);
        }
        personalInfo.setState("1");
        personalInfo.setFaceImageUrl(faceImageUrl);
        personalInfo.setIdCardUrl(idCardUrl);
        personalInfoRepository.save(personalInfo);
        Notice notice = new Notice();
        notice.setStatus("0");
        notice.setTitle("申请已提交");
        notice.setInfo("您的申请已提交，我们会尽快处理，请耐心等待。");
        notice.setPhoneNum(phoneNum);
        noticeRepository.save(notice);
        return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_SUCCESS), HttpStatus.OK);
    }

    /**
     * 查询合同审批状态
     * @param phoneNum
     * @return
     */
    public ResponseEntity<Message> contractStateSerch(String phoneNum){
        PersonalInfo personalInfo = personalInfoRepository.findByPhoneNum(phoneNum);
        String result = coreSystemInterface.contractStateSerch("ContractStateSerch", personalInfo.getApplyNum());
        JSONObject jsonObject =  JSONObject.fromObject(result);
        if(!MessageType.MSG_TYPE_SUCCESS.equals(jsonObject.getString("status"))){
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, "合同状态查询失败!" + jsonObject.getString("error")), HttpStatus.OK);
        }
        ContractStatusDto dto = new ContractStatusDto();
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            dto = objectMapper.readValue(result, ContractStatusDto.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }
        List<ContractStatusListDto> dtolist = dto.getData();
        List<ContractStatusListDto> resultList = new ArrayList();
        if(dtolist != null){
            // 日期格式化
            for(ContractStatusListDto item : dtolist){
                String day = item.getDay();
                String time = item.getTime();
                if(day == null || day == null || time.isEmpty() || time.isEmpty()){
                    item.setDate("");
                } else {
                    day = day.substring(0,4) + "-" + day.substring(4,6) + "-" + day.substring(6,8);
                    time = time.substring(0,2) + ":" + time.substring(2,4) + ":" + time.substring(4,6);
                    item.setDate(day + " " + time);
                }
                resultList.add(item);
            }
        }
        dto.setData(resultList);
        dto.setApplyNum(personalInfo.getApplyNum());
        dto.setName(personalInfo.getName());
        return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_SUCCESS, dto), HttpStatus.OK);
    }

    /***
     * 签署人之间用文件二进制流传递,标准模板印章签署，所用印章SealData为addTemplateSeal接口创建的模板印章返回的SealData
     *
     * @param srcPdfFile
     * @param signedFolder
     * @param signedFileName
     * @param type 0:确认函 1:合同
     */
    private FileDigestSignResult doSignWithTemplateSealByStream(PersonalInfo personalInfo, String downLoadUrl,
                                                  String srcPdfFile, String signedFolder,
                                                  String signedFileName, String mobile, String code, String type) {
        // 创建个人客户账户
        String userPersonAccountId = personalInfo.getAccountId();
        if(userPersonAccountId == null || userPersonAccountId.isEmpty()){
            userPersonAccountId = SignHelper.addPersonAccount(personalInfo.getIdCard(), personalInfo.getName());
        }
        // 创建个人印章
        AddSealResult userPersonSealData = new AddSealResult();
        String sealData = personalInfo.getSealData();
        if(sealData == null || sealData.isEmpty()){
            userPersonSealData = SignHelper.addPersonTemplateSeal(userPersonAccountId);
        } else {
            userPersonSealData.setSealData(sealData);
        }
        // 签署合同
        if("1".equals(type)){
            // 贵公司签署，签署方式,以文件流的方式传递pdf文档
//            FileDigestSignResult platformSignResult = SignHelper.platformSignByStreamm(srcPdfFile);

            // 个人客户签署，签署方式：关键字定位,以文件流的方式传递pdf文档,签章1
            FileDigestSignResult userPersonSignResult = SignHelper.userPersonSignByStream(FileHelper.getBytes(srcPdfFile),
                    userPersonAccountId, userPersonSealData.getSealData(), mobile, code, "1");
            if (0 != userPersonSignResult.getErrCode()) {
                return userPersonSignResult;
            }
            // 个人客户签署，签署方式：关键字定位,以文件流的方式传递pdf文档,签章1
            FileDigestSignResult userPersonSignResult1 = SignHelper.userPersonSignByStream(userPersonSignResult.getStream(),
                    userPersonAccountId, userPersonSealData.getSealData(), mobile, code, "2");
            // 所有签署完成,将最终签署后的文件流保存到本地
            if (0 == userPersonSignResult1.getErrCode()) {
                // 首次创建个人账号，保存个人账号信息
                personalInfo.setAccountId(userPersonAccountId);
                personalInfo.setSealData(userPersonSealData.getSealData());
                // 保存签署记录
                personalInfo.setSignServiceId(userPersonSignResult1.getSignServiceId());
                personalInfo.setContactSignedPdf(downLoadUrl);
                personalInfoRepository.save(personalInfo);
                SignHelper.saveSignedByStream(userPersonSignResult1.getStream(), signedFolder, signedFileName);
            }
            return userPersonSignResult1;
            //签署确认函
        } else if("0".equals(type)){
            // 贵公司签署，签署方式,以文件流的方式传递pdf文档
//            FileDigestSignResult platformSignResult = SignHelper.platformSignByStreammConfirm(srcPdfFile);

            // 个人客户签署，签署方式：关键字定位,以文件流的方式传递pdf文档
            FileDigestSignResult userPersonSignResult = SignHelper.userPersonSignByStreamConfirm(FileHelper.getBytes(srcPdfFile),
                    userPersonAccountId, userPersonSealData.getSealData(), mobile, code);
            // 所有签署完成,将最终签署后的文件流保存到本地
            if (0 == userPersonSignResult.getErrCode()) {
                // 首次创建个人账号，保存个人账号信息
                personalInfo.setAccountId(userPersonAccountId);
                personalInfo.setSealData(userPersonSealData.getSealData());
                // 保存签署记录
                personalInfo.setSignConfirmationServiceId(userPersonSignResult.getSignServiceId());
                personalInfo.setConfirmationSignedPdf(downLoadUrl);
                personalInfoRepository.save(personalInfo);
                SignHelper.saveSignedByStream(userPersonSignResult.getStream(), signedFolder, signedFileName);
            }
            return userPersonSignResult;
        }
        return null;
    }
}
