package cn.net.leadu.util;

import cn.net.leadu.config.MessageProperties;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 发送短信util
 * Created by qiaohao on 2017/4/14.
 */
@Component
public class MessageUtil {

    @Autowired
    private MessageProperties messageProperties;

    public String senRadomCode(String phoneNum, String code) throws Exception{
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(20000)
                .setConnectionRequestTimeout(10000)
                .setSocketTimeout(20000).build();
        // 获取当前客户端对象
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost =  new HttpPost("http://101.251.214.153:8901/MWGate/wmgw.asmx/MongateSendSubmit");
        httpPost.setConfig(requestConfig);
        List<NameValuePair> formParams = new ArrayList<>();
        formParams.add(new BasicNameValuePair("userId", messageProperties.getUserId()));
        formParams.add(new BasicNameValuePair("password", messageProperties.getPassword()));
        formParams.add(new BasicNameValuePair("pszMobis", phoneNum));
        formParams.add(new BasicNameValuePair("pszMsg", messageProperties.getPszMsg().replace("xxxxxx",code)));
        formParams.add(new BasicNameValuePair("iMobiCount", messageProperties.getIMobiCount()));
        formParams.add(new BasicNameValuePair("pszSubPort", "*"));
        formParams.add(new BasicNameValuePair("MsgId", messageProperties.getMsgId()));
        HttpEntity entity = new UrlEncodedFormEntity(formParams, "UTF-8");
        httpPost.setEntity(entity);
        HttpEntity entityResult = httpClient.execute(httpPost).getEntity();
        String message = "";
        if(entityResult != null && entityResult.getContentLength() >0) {//返回值不为空，且长度大于0
            message= EntityUtils.toString(entityResult);//将返回值转换成字符串
        }
        String status = "";
        if(!"".equals(message)) {
            Document doc= DocumentHelper.parseText(message);
            Element el = doc.getRootElement();
            status = el.getText();//解析返回值
        } //处理返回结果
        return  status;
    }
}
