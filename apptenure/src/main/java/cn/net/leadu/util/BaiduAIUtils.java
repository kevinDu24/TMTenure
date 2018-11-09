package cn.net.leadu.util;

import com.baidu.aip.face.AipFace;
import com.baidu.aip.face.MatchRequest;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class BaiduAIUtils {

    //设置APPID/AK/SK
    public static final String APP_ID = "11270645";
    public static final String API_KEY = "EaVXj7WeDTRMuChtmdgFkZ2K";
    public static final String SECRET_KEY = "u93bhXvyzGPPUUXd3k2bOG4zs2boBL9b";

//    public static void main(String[] args) {
//        // 初始化一个AipFace
//        AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);
//
//        // 可选：设置网络连接参数
//        client.setConnectionTimeoutInMillis(2000);
//        client.setSocketTimeoutInMillis(60000);
//
//        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
////        client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
////        client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理
//
//        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
//        // 也可以直接通过jvm启动参数设置此环境变量
////        System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");
//
//        // 调用接口
//        String path = "http://222.73.56.22:89/cmdOtherImg/20180521/c0bd09d5-ee88-46bd-b8d0-5565c0428ab1.jpg";
//        JSONObject res = client.detect(path, "URL", new HashMap<String, String>());
//        System.out.println(res.toString(2));
////        sample(client);
//    }


    public static void sample(AipFace client) {
        String image1 = "http://222.73.56.22:89/cmdOtherImg/20180521/c0bd09d5-ee88-46bd-b8d0-5565c0428ab1.jpg";
        String image2 = "http://222.73.56.22:89/cmdOtherImg/20180521/da6a4857-d796-49b0-8f88-d3f276fd9f76.jpg";
        String image3 = "http://222.73.56.22:89/approvalIdCard/20180521/0f0022a5-2705-4ca5-901e-48356a4a0def.jpg";

        // image1/image2也可以为url或facetoken, 相应的imageType参数需要与之对应。
        MatchRequest req1 = new MatchRequest(image1, "URL", "LIVE", "NORMAL", "NORMAL");
        MatchRequest req2 = new MatchRequest(image2, "URL", "LIVE", "NORMAL", "NORMAL");
        MatchRequest req3 = new MatchRequest(image3, "URL", "CERT", "NORMAL", "NORMAL");
        ArrayList<MatchRequest> requests = new ArrayList<MatchRequest>();
        requests.add(req1);
        requests.add(req2);
        requests.add(req3);

        JSONObject res = client.match(requests);
        System.out.println(res.toString(2));

    }

}