package cn.net.leadu.service;

import cn.net.leadu.dto.SendShortMessageDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * Created by pengchao on 2018/7/24.
 */
@FeignClient(name = "coreApplySystemInterface", url = "http://happyleasing.cn/TMZL/")
public interface CoreApplySystemInterface {

    //发送短信接口
    @RequestMapping(value = "/HPLServletRequest", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    String SendShortMessage(@RequestParam(value = ".url", required = false) String url,
                            @RequestBody SendShortMessageDto sendShortMessageDto);
}
