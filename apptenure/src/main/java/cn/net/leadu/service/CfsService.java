package cn.net.leadu.service;

import cn.net.leadu.config.FileUploadProperties;
import cn.net.leadu.message.Message;
import cn.net.leadu.message.MessageType;
import cn.net.leadu.util.Utils;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by pengchao on 2016/3/3.
 */
@Service
public class CfsService {

    @Autowired
    private FileUploadProperties fileUploadProperties;

    /**
     * 文件上传转发器
     * @param type
     * @param file
     * @return
     */
    public ResponseEntity<Message> uploadFile(String type, MultipartFile file){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String uploadDate = sdf.format(new Date());
        if("idCard".equals(type)){//身份证照片
            return saveFile(fileUploadProperties.getIdCardPath() + uploadDate + "/", file, fileUploadProperties.getRequestIdCardPath() + uploadDate + "/");
        } else if("faceImage".equals(type)){//人脸照片
            return saveFile(fileUploadProperties.getFaceImagePath() + uploadDate + "/", file, fileUploadProperties.getRequestFaceImagePath() + uploadDate + "/");
        }
        return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, "未指定上传类型"), HttpStatus.OK);
    }

    /**
     * 保存文件
     * @param savePath
     * @param file
     * @param serverPath
     * @return
     */
    public ResponseEntity<Message> saveFile(String savePath, MultipartFile file, String serverPath){
        if (file != null && !file.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + Utils.getFileSuffix(file.getOriginalFilename());
            try {
                System.out.println(serverPath + fileName);
                System.out.println(savePath + fileName);
                FileUtils.writeByteArrayToFile(new File(savePath + fileName), file.getBytes());
                Map map = Maps.newHashMap();
                map.put("url", serverPath + fileName);
                return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_SUCCESS, map), HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, "文件上传失败"), HttpStatus.OK);
            }
        }
        return new ResponseEntity<Message>(new Message(MessageType.MSG_TYPE_ERROR, "文件为空,上传失败"), HttpStatus.OK);
    }



    /**
     * 文件下载
     * @param response
     * @param fileName 文件名
     * @return
     * @throws IOException
     */
    public byte[] downloadFile(HttpServletResponse response, String fileName, String loadDate, String type) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        String path = null;
        if("signedPdf".equals(type)){
            path = fileUploadProperties.getSignedPdfPath() +  loadDate + "/" + fileName;
        } else if("idCard".equals(type)){
            path = fileUploadProperties.getIdCardPath() +  loadDate + "/" + fileName;
        } else if("faceImage".equals(type)){
            path = fileUploadProperties.getFaceImagePath() +  loadDate + "/" + fileName;
        } else {
            String errorMessage = "抱歉. 你访问的文件不存在！";
            System.out.println(errorMessage);
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
            outputStream.close();
            return null;
        }

        File file = new File(path);
        if(!file.exists()){
            String errorMessage = "抱歉. 你访问的文件不存在！";
            System.out.println(errorMessage);
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
            outputStream.close();
            return null;
        }

        String mimeType= URLConnection.guessContentTypeFromName(file.getName());
        if(mimeType==null){
            mimeType = "application/octet-stream";
        }
        response.setContentType(mimeType);
        response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() +"\""));
        response.setContentLength((int)file.length());
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        FileCopyUtils.copy(inputStream, response.getOutputStream());
        return null;
    }

    /**
     * 附件下载
     * @param response
     * @return
     * @throws IOException
     */
    public byte[] download(HttpServletResponse response, String path) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        File file = new File(path);
        if(!file.exists()){
            String errorMessage = "抱歉. 你访问的文件不存在！";
            System.out.println(errorMessage);
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
            outputStream.close();
            return null;
        }

        String mimeType= URLConnection.guessContentTypeFromName(file.getName());
        if(mimeType==null){
            mimeType = "application/octet-stream";
        }
        response.setContentType(mimeType);
        response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() +"\""));
        response.setContentLength((int)file.length());
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        FileCopyUtils.copy(inputStream, response.getOutputStream());
        return null;
    }

}
