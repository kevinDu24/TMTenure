package cn.net.leadu.controller;

import cn.net.leadu.message.Message;
import cn.net.leadu.message.MessageType;
import cn.net.leadu.service.AuthService;
import cn.net.leadu.service.FunctionService;
import cn.net.leadu.service.SystemService;
import cn.net.leadu.util.CommonUtils;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by PengChao on 16/9/1.
 */
@RestController
@RequestMapping("system")
public class SystemController {

    @Autowired
    private SystemService systemService;

    @Autowired
    private FunctionService functionService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private AuthService authService;

    private static final Logger logger = LoggerFactory.getLogger(SystemController.class);

    /**
     * 登录接口
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ResponseEntity<Message> login(@RequestParam(required = true, value = "phoneNum") String phoneNum,
                                         @RequestParam(required = true, value = "timeStamp") String timeStamp,
                                         @RequestParam(required = true, value = "code") String code){
        try {
            return systemService.login(phoneNum, timeStamp, code);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("登录异常error", ex);
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }
    }

    /**
     * 验证接口
     * @return
     */
    @RequestMapping(value = "/verify", method = RequestMethod.GET)
    public ResponseEntity<Message> verify(){
        String uniqueMark = httpServletRequest.getHeader("uniqueMark");
        if (uniqueMark == null){
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorCode), HttpStatus.OK);
        }
        try {
            return systemService.verify(uniqueMark);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }
    }

    /**
     * 退出登录
     * @return
     */
    @RequestMapping(value = "/loginout", method = RequestMethod.GET)
    public ResponseEntity<Message> loginout(){
        String uniqueMark = httpServletRequest.getHeader("uniqueMark");
        if (uniqueMark == null){
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorCode), HttpStatus.OK);
        }
        try {
            return systemService.loginout(uniqueMark);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }
    }

    /**
     * 获取短信验证码
     * @return
     */
    @RequestMapping(value = "/getRadomCode", method = RequestMethod.GET)
    public ResponseEntity<Message> getRadomCode(@RequestParam(required = true, value = "phoneNum") String phoneNum){
        try {
            return systemService.getRadomCode(phoneNum);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("登录时获取短信验证码异常error", ex);
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }
    }

    /**
     * 推送消息（供主系统调用）
     * @param phoneNum
     * @param type 1通过、2拒绝、3放款
     * @param reason 拒绝原因
     * @return
     */
    @RequestMapping(value = "/pushNotice",method = RequestMethod.GET)
    public ResponseEntity<Message> pushNotice(@RequestParam(required = true, value = "phoneNum")String phoneNum,
                                              @RequestParam(required = true, value = "type")String type,
                                              @RequestParam(required = false, value = "reason")String reason){
        try {
            return functionService.addNotice(phoneNum, type, reason);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("主系统推送消息异常error", ex);
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }

    }

    /**
     * 清除二次营销提交信息
     * @return
     */
    @RequestMapping(value = "/cleanContract", method = RequestMethod.GET)
    public ResponseEntity<Message> cleanContract(@RequestParam(required = true, value = "applyNum") String applyNum){
        try {
            return systemService.cleanContract(applyNum);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("清除二次营销提交信息异常error", ex);
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }
    }


    @RequestMapping(value = "/redisSave", method = RequestMethod.POST)
    public ResponseEntity<Message> redisSave(String key, String value, int time){
        return systemService.redisSave(key,value,time);
    }

    @RequestMapping(value = "/getRedisValue", method = RequestMethod.POST)
    public ResponseEntity<Message> getRedisValue(String key){
        return systemService.getRedisValue(key);
    }

}
