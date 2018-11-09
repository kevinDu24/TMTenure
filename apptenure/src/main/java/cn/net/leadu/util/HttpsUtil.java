package cn.net.leadu.util;

import cn.net.leadu.config.FaceIdProperties;
import net.sf.json.JSONObject;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by huzongcheng on 2017/11/3.
 */

@Component
public class HttpsUtil {

    @Autowired
    private FaceIdProperties faceIdProperties;

    public static String ocrUrl = "https://api.megvii.com/faceid/v1/ocridcard";

    public static String verifyUrl = "https://api.megvii.com/faceid/v2/verify";

    /**
     * 身份证ocr识别
     *
     * @param image 文件
     */
    public JSONObject ocrIdCard(MultipartFile image) throws IOException {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"), image.getBytes());
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", image.getName(), fileBody)
                .addFormDataPart("api_key", faceIdProperties.getApiKey())
                .addFormDataPart("api_secret", faceIdProperties.getApiSecret())
                .build();
        Request request = new Request.Builder()
                .url(ocrUrl)
                .post(requestBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        Response response = call.execute();
        return JSONObject.fromObject(response.body().string());
    }

    /**
     * 验证接口
     *
     * @param image 文件
     * @param type 1
     */
    public JSONObject verify(MultipartFile image, String type, Map<String,String> map) throws IOException {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"), image.getBytes());
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        builder.addFormDataPart("api_key", faceIdProperties.getApiKey());
        builder.addFormDataPart("api_secret", faceIdProperties.getApiSecret());
        if("1".equals(type)){
            builder.addFormDataPart("image", image.getName(), fileBody);
        } else if("2".equals(type)){
            builder.addFormDataPart("image_best", image.getName(), fileBody);
        }
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            builder.addFormDataPart(entry.getKey(), entry.getValue());
        }
        MultipartBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(verifyUrl)
                .post(requestBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        Response response = call.execute();
        return JSONObject.fromObject(response.body().string());
    }
}

