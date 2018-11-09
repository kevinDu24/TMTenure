package cn.net.leadu.service;

import cn.net.leadu.message.Message;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by LEO on 16/10/10.
 */
@FeignClient(name = "informationInterface", url = "${request.adminServerUrl}")
public interface InformationInterface {

    @RequestMapping(value = "/informations", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    ResponseEntity<Message> getInfos(
            @RequestHeader("Header-Param") String headerParam,
            @RequestParam(value = "type") Integer type,
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "size") Integer size);

    @RequestMapping(value = "/informations/{infoId}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    String getInfoDetail(@PathVariable(value = "infoId") Long infoId);
}
