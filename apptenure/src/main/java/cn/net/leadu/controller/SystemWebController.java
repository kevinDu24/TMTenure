package cn.net.leadu.controller;

import cn.net.leadu.dto.WebSearchDto;
import cn.net.leadu.message.Message;
import cn.net.leadu.message.MessageType;
import cn.net.leadu.service.SystemWebService;
import cn.net.leadu.util.CommonUtils;
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
 * Web端Controller
 *
 * Created by PengChao on 16/9/1.
 */
@RestController
@RequestMapping("systemWeb")
public class SystemWebController {

    @Autowired
    private SystemWebService systemWebService;

    private static final Logger logger = LoggerFactory.getLogger(SystemWebController.class);

    /**
     * 登录接口
     * @return
     */
    @RequestMapping(value = "/loginWeb", method = RequestMethod.GET)
    public ResponseEntity<Message> login(@RequestParam(required = true, value = "timeStamp") String timeStamp,
                                         @RequestParam(required = true, value = "code") String code){
        try {
            return systemWebService.loginWeb(timeStamp, code);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Web端登录异常error", ex);
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }
    }


    /**
     * 获取短信验证码（web端）
     * @return
     */
    @RequestMapping(value = "/getRadomCodeWeb", method = RequestMethod.GET)
    public ResponseEntity<Message> getRadomCodeWeb(@RequestParam(required = true, value = "phoneNum") String phoneNum){
        try {
            return systemWebService.getRadomCodeWeb(phoneNum);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Web端登录时获取短信验证码异常error", ex);
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
        }
    }

    /**
     * 分页返回申请列表
     * @param webSearchDto
     * @param page
     * @param size
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity<Message> search(WebSearchDto webSearchDto, int page , int size){
        try{
            return systemWebService.search(webSearchDto,page,size);
        }catch (Exception ex){
            ex.printStackTrace();
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR , CommonUtils.errorInfo), HttpStatus.OK);
        }
    }

}
