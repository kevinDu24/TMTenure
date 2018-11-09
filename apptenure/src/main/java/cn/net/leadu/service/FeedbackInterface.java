package cn.net.leadu.service;

import cn.net.leadu.message.Message;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by pengchao on 2016/11/21.
 */

@FeignClient(name = "customerInterface", url = "${request.adminServerUrl}")
public interface FeedbackInterface {
    @RequestMapping(value = "/baoyouFeedback/feedback", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    ResponseEntity<Message> addFeedback(@RequestHeader("authorization") String auth, @RequestHeader("Header-Param") String headerParam, @RequestParam(value = "content") String content, @RequestParam(value = "phoneNum") String phoneNum);

}
