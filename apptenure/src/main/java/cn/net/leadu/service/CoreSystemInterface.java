package cn.net.leadu.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by LEO on 16/10/10.
 */
@FeignClient(name = "coreSystemInterface", url = "${request.coreServerUrl}")
public interface CoreSystemInterface {

    @RequestMapping(value = "/lywxapi.htm!", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    String phoneCheckBy(@RequestParam(value = ".url", required = false) String url,
                         @RequestParam(value = "phoneNmun", required = false) String phoneNum);

    @RequestMapping(value = "/lywxapi.htm!", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    String identityCheckBy(@RequestParam(value = ".url", required = false) String url,
                        @RequestParam(value = "idNmun", required = false) String idNmun,
                        @RequestParam(value = "customName", required = false) String customName);

    @RequestMapping(value = "/lywxapi.htm!", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    String repayBy(@RequestParam(value = ".url", required = false) String url,
                           @RequestParam(value = "financingAmount", required = false) String financingAmount,
                           @RequestParam(value = "loanDeadtime", required = false) String loanDeadtime,
                           @RequestParam(value = "basqbh", required = false) String basqbh);

    @RequestMapping(value = "/lywxapi.htm!", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    String contractCreate(@RequestParam(value = ".url", required = false) String url,
                          @RequestParam(value = "lssqbh", required = false) String lssqbh,
                          @RequestParam(value = "financingAmount", required = false) String financingAmount,
                          @RequestParam(value = "loanDeadtime", required = false) String loanDeadtime);

    @RequestMapping(value = "/lywxapi.htm!", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    String pactSubmitBy(@RequestParam(value = ".url", required = false) String url,
                          @RequestParam(value = "lssqbh", required = false) String lssqbh,
                          @RequestParam(value = "contractUrl", required = false) String contractUrl,
                          @RequestParam(value = "confirmationUrl", required = false) String confirmationUrl,
                          @RequestParam(value = "loanDeadtime", required = false) String loanDeadtime,
                          @RequestParam(value = "financingAmount", required = false) String financingAmount,
                          @RequestParam(value = "basqbh", required = false) String basqbh,
                          @RequestParam(value = "baddbh", required = false) String baddbh,
                          @RequestParam(value = "xtczry", required = false) String xtczry);

    @RequestMapping(value = "/lywxapi.htm!", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    String contractStateSerch(@RequestParam(value = ".url", required = false) String url,
                        @RequestParam(value = "basqbh", required = false) String basqbh);

    @RequestMapping(value = "/lywxapi.htm!", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    String getMatchImage(@RequestParam(value = ".url", required = false) String url,
                              @RequestParam(value = "basqbh", required = false) String basqbh);
}
