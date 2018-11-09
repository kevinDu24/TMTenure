package cn.net.leadu.service;

import cn.net.leadu.config.MessageProperties;
import cn.net.leadu.dao.MessageLogsRepository;
import cn.net.leadu.dao.PersonalInfoRepository;
import cn.net.leadu.dao.RedisRepository;
import cn.net.leadu.domain.MessageLogs;
import cn.net.leadu.domain.PersonalInfo;
import cn.net.leadu.dto.SendShortMessageDto;
import cn.net.leadu.dto.UserDto;
import cn.net.leadu.dto.result.CoreResult;
import cn.net.leadu.message.Message;
import cn.net.leadu.message.MessageType;
import cn.net.leadu.util.CommonUtils;
import cn.net.leadu.util.MessageUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * app系统service
 * Created by zcHu on 2017/8/28.
 */
@Service
public class SystemService{

    @Autowired
    private RedisRepository redisRepository;

    @Autowired
    private PersonalInfoRepository personalInfoRepository;

    @Autowired
    private CoreSystemInterface coreSystemInterface;

    @Autowired
    private MessageUtil messageUtil;

    @Autowired
    private MessageLogsRepository messageLogsRepository;

    @Autowired
    private MessageProperties messageProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CoreApplySystemInterface coreApplySystemInterface;

    private static final Logger logger = LoggerFactory.getLogger(SystemService.class);

    /**
     * 登录接口
     * @param phoneNum
     * @param timeStamp
     * @param code
     * @return
     */
    public ResponseEntity<Message> login(String phoneNum, String timeStamp, String code){
        if(redisRepository.get(timeStamp) == null){
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR,"验证码已过期,请重新获取验证码"), HttpStatus.OK);
        }
        if(!redisRepository.get(timeStamp).equals(code)){
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR,"验证码错误"), HttpStatus.OK);
        }
        String uuid = UUID.randomUUID().toString().replace("-", "");
        uuid = phoneNum +  ":" +  uuid;
        String key = CommonUtils.loginkey + phoneNum;
        String name = redisRepository.get("name_" + phoneNum) == null ? "" : (String)redisRepository.get("name_" + phoneNum);
        UserDto userDto = new UserDto(uuid, phoneNum, name);
        redisRepository.save(key, userDto, 7 * 24 * 3600);
        PersonalInfo personalInfo = personalInfoRepository.findByPhoneNum(phoneNum);
        if(personalInfo == null){
            PersonalInfo dto = new PersonalInfo();
            dto.setPhoneNum(phoneNum);
            dto.setName(name);
            dto.setState("0");
            personalInfoRepository.save(dto);
        }
        return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_SUCCESS, userDto), HttpStatus.OK);
    }

    /**
     * 验证接口
     * @param uniqueMark
     * @return
     */
    public ResponseEntity<Message> verify(String uniqueMark){
        String [] values = uniqueMark.split(":");
        if(values.length != 2){
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorCode), HttpStatus.OK);
        }
        String phoneNum = values[0];
        String uuid = values[1];
        if("".equals(uuid)){
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorCode), HttpStatus.OK);
        }
        String key = CommonUtils.loginkey + phoneNum;
        Object result = redisRepository.get(key);
        if(result == null){
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorCode), HttpStatus.OK);
        }
        UserDto appUserDto = (UserDto)redisRepository.get(key);
        if (appUserDto.getUniqueMark().equals(uniqueMark)) {
            redisRepository.save(key, appUserDto, 7 * 24 * 3600);
        } else {
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorCode), HttpStatus.OK);
        }
        return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_SUCCESS, appUserDto), HttpStatus.OK);
    }


    /**
     * 退出登录
     * @param uniqueMark
     * @return
     */
    public ResponseEntity<Message> loginout(String uniqueMark){
        String [] values = uniqueMark.split(":");
        if(values.length != 2){
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorCode), HttpStatus.OK);
        }
        String phoneNum = values[0];
        String uuid = values[1];
        if("".equals(uuid)){
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorCode), HttpStatus.OK);
        }
        String key = CommonUtils.loginkey + phoneNum;
        Object result = redisRepository.get(key);
        if(result == null){
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorCode), HttpStatus.OK);
        } else {
            redisRepository.delete(key);
        }
        return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_SUCCESS), HttpStatus.OK);
    }

    /**
     * 获取短信验证码
     * @param phoneNum
     * @return
     */
    public ResponseEntity<Message> getRadomCode(String phoneNum) throws Exception{
        // *************** AppStore审核用的测试账号分支，开始 **************
        if("12345678901".equals(phoneNum)){
            String code = "123456";
            String timeStamp = String.valueOf(System.currentTimeMillis());
            redisRepository.save(timeStamp, code, 300);
            redisRepository.save("name_" + phoneNum, "测试账号", 300);
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_SUCCESS, (Object)timeStamp), HttpStatus.OK);
        }
        // *************** AppStore审核用的测试账号分支，结束 **************
        String result = coreSystemInterface.phoneCheckBy("phoneCheckBy", phoneNum);
        JSONObject jsonObject =  JSONObject.fromObject(result);
        String status = jsonObject.getString("status");
        String phoneCheck = jsonObject.getString("phoneCheck");
        String name = "";
        if(MessageType.MSG_TYPE_SUCCESS.equals(status) &&
                "1".equals(JSONObject.fromObject(phoneCheck).getString("isHave"))){
            name = JSONObject.fromObject(phoneCheck).getString("customName");
        } else {
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, "该手机号不在用户名单内"), HttpStatus.OK);
        }
        String code = "" + (int)(Math.random()*900000+100000);
        String timeStamp = String.valueOf(System.currentTimeMillis());
        redisRepository.save(timeStamp, code, 300);
        redisRepository.save("name_" + phoneNum, name, 300);
        //调用外部的发送短信接口

        String origin = "1";
        if(redisRepository.get("tenure_shortMessageOrigin") != null){
            origin = (String) redisRepository.get("tenure_shortMessageOrigin");
        }
        SendShortMessageDto sendShortMessageDto = new SendShortMessageDto();
        sendShortMessageDto.setPhoneNum(phoneNum);
        sendShortMessageDto.setText(messageProperties.getPszMsg().replace("xxxxxx",code));
        String send = "";
        try {
            //梦网科技发送
            if("0".equals(origin)){
                send = messageUtil.senRadomCode(phoneNum, code);
                //解析后的返回值不为空且长度不大于10，则提交失败，交给主系统发送
                if(send == null || "".equals(send) || send.length()< 10){
                    ResponseEntity<Message> responseEntity =sendShortMessage(sendShortMessageDto);
                    send = responseEntity.getBody().getStatus();
                }
            } else {
                //主系统发送
                ResponseEntity<Message> responseEntity =sendShortMessage(sendShortMessageDto);
                send = responseEntity.getBody().getStatus();
                //主系统发送失败，再由梦网科技发送
                if("ERROR".equals(send)){
                    send = messageUtil.senRadomCode(phoneNum, code);
                }
            }
            //保存发送短信log及梦网流水号
            saveMessageLog(phoneNum, messageProperties.getPszMsg().replace("xxxxxx",code), send);
        } catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }
        if(send != null && !"".equals(send) && ("SUCCESS".equals(send) || send.length()>10)){
            //解析后的返回值不为空且长度大于10，则是提交成功
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_SUCCESS, (Object)timeStamp), HttpStatus.OK);
        }else{//解析后的返回值不为空且长度不大于10，则提交失败，返回错误码
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }
    }


    /**
     * 发送短信接口
     *
     * @return
     */
    public ResponseEntity<Message> sendShortMessage(SendShortMessageDto sendShortMessageDto) {
        if (CommonUtils.isNull(sendShortMessageDto.getPhoneNum()) || CommonUtils.isNull(sendShortMessageDto.getText())) {
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, "缺少必要参数"), HttpStatus.OK);
        }
        //提交到主系统
        CoreResult codeResult = new CoreResult();
        try {
            logger.info("sendShortMessageDto={}", JSONObject.fromObject(sendShortMessageDto).toString(2));
            String coreResult = coreApplySystemInterface.SendShortMessage("SendShortMessage", sendShortMessageDto);
            logger.info("SendShortMessage={}", coreResult);
            codeResult = objectMapper.readValue(coreResult, CoreResult.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }
        if ("true".equals(codeResult.getResult().getIsSuccess())) {
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_SUCCESS), HttpStatus.OK);
        }
        return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, codeResult.getResult().getResultMsg()), HttpStatus.OK);
    }


    public ResponseEntity<Message> redisSave(String key, String value, int time){
        redisRepository.save(key, value, time);
        return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_SUCCESS,  redisRepository.get(key)), HttpStatus.OK);
    }

    public ResponseEntity<Message> getRedisValue(String key){
        return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_SUCCESS,  redisRepository.get(key)), HttpStatus.OK);
    }

    /**
     * 清除二次营销提交信息
     * @param applyNum
     * @return
     */
    public ResponseEntity<Message> cleanContract(String applyNum){
        PersonalInfo personalInfo = personalInfoRepository.findByApplyNum(applyNum);
        if(personalInfo != null){
            personalInfoRepository.delete(personalInfo);
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_SUCCESS), HttpStatus.OK);
        }
        return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, "未找到新申请编号"), HttpStatus.OK);
    }



    /**
     * 保存发送短信log
     * @param phoneNum
     * @param content
     */
    public void saveMessageLog(String phoneNum, String content, String sendStatus){
        Date nowDate = new Date();
        MessageLogs messageLog = new MessageLogs();
        messageLog.setPhone(phoneNum);
        messageLog.setContent(content);
        messageLog.setSendTime(nowDate);
        messageLog.setUpdateTime(nowDate);
        messageLog.setStatus(sendStatus);
        messageLogsRepository.save(messageLog);
    }
}
