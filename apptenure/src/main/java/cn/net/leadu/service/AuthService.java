package cn.net.leadu.service;

/**
 * Created by pengchao on 2018/5/21.
 */
import cn.net.leadu.controller.SystemController;
import cn.net.leadu.dao.PersonalInfoRepository;
import cn.net.leadu.domain.PersonalInfo;
import cn.net.leadu.dto.BaiDuAIResultDto;
import cn.net.leadu.dto.FaceMatchImageDto;
import cn.net.leadu.dto.result.CoreResult;
import cn.net.leadu.message.Message;
import cn.net.leadu.message.MessageType;
import cn.net.leadu.util.BaiduAIUtils;
import cn.net.leadu.util.CommonUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取token类
 */
@Service
public class AuthService {

    @Autowired
    BaiduAIInterface baiduAIInterface;

    @Autowired
    private Gson gson;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersonalInfoRepository personalInfoRepository;

    @Autowired
    private CoreSystemInterface coreSystemInterface;


    private static final Logger logger = LoggerFactory.getLogger(SystemController.class);


    /**
     * 获取权限token
     * @return 返回示例：
     * {
     * "access_token": "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567",
     * "expires_in": 2592000
     * }
     */
    public String getAuth() {
        // 官网获取的 API Key 更新为你注册的
        String clientId = BaiduAIUtils.API_KEY;
        // 官网获取的 Secret Key 更新为你注册的
        String clientSecret = BaiduAIUtils.SECRET_KEY;
        try {
            String message = baiduAIInterface.getAuth(clientId, clientSecret);
            JSONObject jsonObject = new JSONObject(message);
            String access_token = jsonObject.getString("access_token");
            return access_token;
        } catch (Exception e) {
            System.err.printf("获取token失败！");
            e.printStackTrace(System.err);
        }
        return null;
    }


    /**
     * 百度人脸对比 返回人脸对比分数
     * @param idCardImage 身份证照片
     * @param phoneNum 手机号
     * @return
     */
    public ResponseEntity<Message> match(String idCardImage, String phoneNum) {
        try {
            PersonalInfo personalInfo = personalInfoRepository.findByPhoneNum(phoneNum);
            String historyApplyNum = personalInfo.getHistoryApplyNum();
            String coreImages = "";
            //获取主系统公安部图片,主系统暂未提供 todo
            String coreResult = coreSystemInterface.getMatchImage("getMatchImage", historyApplyNum);
            CoreResult codeResult = new CoreResult();
            try {
                codeResult = objectMapper.readValue(coreResult, CoreResult.class);
            } catch (IOException e) {
                e.printStackTrace();
                return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo), HttpStatus.OK);
            }
            if("true".equals(codeResult.getResult().getIsSuccess())){
                coreImages = codeResult.getImage();
                if(coreImages.isEmpty()){
                    return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, "未查询到数据"), HttpStatus.OK);
                }
            }else {
                return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, codeResult.getResult().getResultMsg()), HttpStatus.OK);
            }
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            FaceMatchImageDto faceMatchImageDto1 = new FaceMatchImageDto(coreImages, "URL", "LIVE", "NORMAL", "NONE");
            FaceMatchImageDto matchImageDto3 = new FaceMatchImageDto(idCardImage, "URL", "CERT", "NORMAL", "NONE");
            List<FaceMatchImageDto> faceMatchImageDtoList = new ArrayList<>();
            faceMatchImageDtoList.add(faceMatchImageDto1);
            faceMatchImageDtoList.add(matchImageDto3);
            String accessToken = getAuth();
            if(accessToken == null || "".equals(accessToken)){
                return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, "获取token失败!"), HttpStatus.OK);
            }
            BaiDuAIResultDto baiDuAIResultDto = new BaiDuAIResultDto();
            String result = baiduAIInterface.imageMatch(accessToken, faceMatchImageDtoList);
            baiDuAIResultDto = objectMapper.readValue(result, BaiDuAIResultDto.class);
            logger.info("baiduMatchResult={}", result);
            if("0".equals(baiDuAIResultDto.getError_code()) && "SUCCESS".equals(baiDuAIResultDto.getError_msg())){
                String score = baiDuAIResultDto.getResult().getScore();
                return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_SUCCESS, null, baiDuAIResultDto.getResult()), HttpStatus.OK);
            }else {
                return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, baiDuAIResultDto.getError_code() + ":" + baiDuAIResultDto.getError_msg()), HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, "人脸对比系统异常!"), HttpStatus.OK);
        }
    }

}
