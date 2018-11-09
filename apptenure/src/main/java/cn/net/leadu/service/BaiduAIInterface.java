package cn.net.leadu.service;

import cn.net.leadu.dto.FaceMatchImageDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by pengchao on 2018/5/21.
 */


@FeignClient(name = "baiduAIInterface", url = "https://aip.baidubce.com")
public interface BaiduAIInterface {
    @RequestMapping(value = "/rest/2.0/face/v3/match", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    String imageMatch(@RequestParam(value = "access_token") String access_token,
                             @RequestBody List<FaceMatchImageDto> faceMatchImageDtoList);


    @RequestMapping(value = "/oauth/2.0/token?grant_type=client_credentials", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    String getAuth(@RequestParam(value = "client_id") String client_id,
                   @RequestParam(value = "client_secret") String client_secret);


    @RequestMapping(value = "/rest/2.0/face/v3/detect", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    String detect(@RequestParam(value = "access_token") String access_token,
                  @RequestBody FaceMatchImageDto faceMatchImageDto);


    //身份验证
    @RequestMapping(value = "/rest/2.0/face/v3/person/verify", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    String verify(@RequestParam(value = "access_token") String access_token,
                  @RequestBody FaceMatchImageDto faceMatchImageDto);


}
