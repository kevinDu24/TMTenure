package cn.net.leadu.controller;

import cn.net.leadu.config.AccountProperties;
import cn.net.leadu.dto.FeedBackDto;
import cn.net.leadu.message.Message;
import cn.net.leadu.message.MessageType;
import cn.net.leadu.service.AuthService;
import cn.net.leadu.service.FeedbackInterface;
import cn.net.leadu.service.FunctionService;
import cn.net.leadu.service.InformationInterface;
import cn.net.leadu.util.CommonUtils;
import cn.net.leadu.util.HttpsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Created by LEO on 16/10/8.
 */
@RequestMapping("/tenure")
@RestController
public class FunctionController {

    @Autowired
    private FeedbackInterface feedbackInterface;

    @Autowired
    private AccountProperties accountProperties;

    @Autowired
    private InformationInterface informationInterface;

    @Autowired
    private FunctionService functionService;

    @Autowired
    private HttpsUtil httpsUtil;

    @Autowired
    private AuthService authService;

    private static final Logger logger = LoggerFactory.getLogger(SystemController.class);

    /**
     * 新增用户信息反馈
     * @param feedBackDto
     * @return
     */
    @RequestMapping(value = "/addFeedback", method = RequestMethod.POST)
    public  ResponseEntity<Message> addFeedback(@RequestHeader(value="Header-Param", defaultValue="{\"systemflag\":\"taimeng\"}") String headerParam, @RequestBody FeedBackDto feedBackDto){
        try {
            return feedbackInterface.addFeedback(accountProperties.getAuth(), headerParam, feedBackDto.getContent(), feedBackDto.getPhoneNum());
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("新增用户反馈异常error", ex);
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }
    }

    /**
     * 分页查询最新新闻列表
     * @param type
     * @param page
     * @param size
     * @return
     */
    @RequestMapping(value = "/getInfos",method = RequestMethod.GET)
    public ResponseEntity<Message> getInfos(@RequestHeader(value="Header-Param", defaultValue="{\"systemflag\":\"taimeng\"}") String headerParam,
                                            Integer type, Integer page, Integer size){
        try {
            return informationInterface.getInfos(headerParam, type, page, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("查询最新新闻列表异常error", ex);
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }
    }

    /**
     * 查询某条新闻
     * @param infoId
     * @return
     */
    @RequestMapping(value = "specificInfo/{infoId}",method = RequestMethod.GET)
    public ResponseEntity<Message> getInfoDetails(@PathVariable Long infoId){
        try {
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_SUCCESS, (Object)informationInterface.getInfoDetail(infoId)), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("查询新闻详情异常error", ex);
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }

    }

    /**
     * 查询未读消息数量
     * @param phoneNum
     * @return
     */
    @RequestMapping(value = "/getUnReadCount",method = RequestMethod.GET)
    public ResponseEntity<Message> getUnReadCount(String phoneNum){
        try {
            return functionService.getUnReadCount(phoneNum);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("查询未读消息数量异常error", ex);
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }
    }

    /**
     * 查询消息列表
     * @param phoneNum
     * @return
     */
    @RequestMapping(value = "/getNoticeList",method = RequestMethod.GET)
    public ResponseEntity<Message> getNoticeList(String phoneNum, String type){
        try {
            return functionService.getNoticeList(phoneNum, type);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("查询消息列表异常error", ex);
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }
    }

    /**
     * 更新消息为已读
     * @param id
     * @return
     */
    @RequestMapping(value = "/updateNotice",method = RequestMethod.GET)
    public ResponseEntity<Message> updateNotice(String id){
        try {
            return functionService.updateNotice(id);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("更新消息为已读异常error", ex);
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }
    }

    /**
     * 验证身份
     * @param idCard
     * @param name
     * @param phoneNum
     * @return
     */
    @RequestMapping(value = "/authentication",method = RequestMethod.GET)
    public ResponseEntity<Message> authentication(String idCard, String name, String phoneNum){
        try {
            return functionService.authentication(idCard, name, phoneNum);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("验证用户身份异常error", ex);
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }
    }

    /**
     * 计算月还款额
     * @param financingAmount
     * @param month
     * @return
     */
    @RequestMapping(value = "/calculatorAmount",method = RequestMethod.GET)
    public ResponseEntity<Message> calculatorAmount(String financingAmount, String month){
        try {
            return functionService.calculatorAmount(financingAmount, month);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("计算月还款额异常error", ex);
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }
    }

    /**
     * 验证身份页面点击下一步
     * @param month
     * @param money
     * @param pay
     * @param phoneNum
     * @return
     */
    @RequestMapping(value = "/saveFinanceInfo",method = RequestMethod.GET)
    public ResponseEntity<Message> saveFinanceInfo(String month, String money, String pay, String phoneNum){
        try {
            return functionService.saveFinanceInfo(month, money, pay, phoneNum);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("保存融资信息异常error", ex);
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }
    }

    /**
     * 核验身份证图片【下一步】
     * @param idCard
     * @param name
     * @param phoneNum
     * @return
     */
    @RequestMapping(value = "/checkIdCard",method = RequestMethod.GET)
    public ResponseEntity<Message> saveIdCard(String idCard, String name, String phoneNum){
        try {
            return functionService.checkIdCard(idCard, name, phoneNum);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("核验身份证信息异常error", ex);
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }
    }

    /**
     * 合同申请入口接口
     * @param phoneNum
     * @return
     */
    @RequestMapping(value = "/getApplyState",method = RequestMethod.GET)
    public ResponseEntity<Message> getApplyState(String phoneNum){
        try {
            return functionService.getApplyState(phoneNum);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("合同申请入口判断异常error", ex);
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }
    }

    /**
     * 获取合同
     * @param phoneNum
     * @return
     */
    @RequestMapping(value = "/getOriginContract",method = RequestMethod.GET)
    public ResponseEntity<Message> getOriginContract(String phoneNum){
        try {
            return functionService.getOriginContract(phoneNum);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("生成合同模板异常error", ex);
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }
    }

    /**
     * 发送短信（签订合同）
     * @return
     */
    @RequestMapping(value = "/sendMessage",method = RequestMethod.GET)
    public ResponseEntity<Message> sendMessage(String phoneNum){
        try {
            return functionService.sendMessage(phoneNum);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("发送短信(合同签订)异常error", ex);
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }
    }

    /**
     * 签署确认函
     * @return
     */
    @RequestMapping(value = "/signConfirmation",method = RequestMethod.GET)
    public ResponseEntity<Message> signConfirmation(String phoneNum, String code){
        try {
            return functionService.signConfirmation(phoneNum, code);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("签署确认函异常error", ex);
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }
    }

    /**
     * 签署合同
     * @return
     */
    @RequestMapping(value = "/sign",method = RequestMethod.GET)
    public ResponseEntity<Message> sign(String phoneNum, String code){
        try {
            return functionService.sign(phoneNum, code);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("签署合同异常error", ex);
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }
    }

    /**
     * 合同提交接口
     * @return
     */
    @RequestMapping(value = "/submitContract",method = RequestMethod.GET)
    public ResponseEntity<Message> submitContract(String phoneNum, String faceImageUrl, String idCardUrl){
        try {
            return functionService.submitContract(phoneNum, faceImageUrl, idCardUrl);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("合同提交异常error", ex);
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }
    }

    /**
     * 查询合同审批状态
     * @return
     */
    @RequestMapping(value = "/contractStateSerch",method = RequestMethod.GET)
    public ResponseEntity<Message> contractStateSerch(String phoneNum){
        try {
            return functionService.contractStateSerch(phoneNum);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("查询合同审批状态异常error", ex);
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }
    }

    /**
     * 身份证ocr识别
     * @return
     */
    @RequestMapping(value = "/ocrIdCard",method = RequestMethod.POST)
    public Object ocrIdCard(MultipartFile image){
        try {
            return httpsUtil.ocrIdCard(image);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * face++验证接口
     * @return
     */
    @RequestMapping(value = "/verify",method = RequestMethod.POST)
    public Object verify(MultipartFile image, MultipartFile image_best, @RequestParam Map<String,String> map){
        try {
            if(image == null && image_best != null){
                return httpsUtil.verify(image_best, "2", map);
            } else if(image_best == null && image != null){
                return httpsUtil.verify(image, "1", map);
            } else {
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    /**
     * 百度人脸对比
     * face++人脸识别失败可调用百度再识别一次
     * @return
     */
//    @RequestMapping(value = "/match",method = RequestMethod.POST)
//    public ResponseEntity<Message> match(String image,String phoneNum){
//        try {
//            return authService.match(image, phoneNum);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            logger.error("百度人脸对比异常error", ex);
//            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
//        }
//    }

}
