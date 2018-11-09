package cn.net.leadu.controller;

import cn.net.leadu.message.Message;
import cn.net.leadu.service.CfsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by LEO on 16/9/29.
 */
@RestController
@RequestMapping(value = "/files")
public class FileController {
    @Autowired
    private CfsService cfsService;

    /**
     * 文件上传
     * @param type
     * @param file
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Message> uploadFile(String type, MultipartFile file){
        return cfsService.uploadFile(type, file);
    }

    @RequestMapping(value="/download/{type}/{loadDate}/{fileName}", method = RequestMethod.GET)
    public byte[] downloadFile(HttpServletResponse response, @PathVariable("type") String type, @PathVariable("fileName") String fileName, @PathVariable("loadDate") String loadDate) throws IOException {
        return cfsService.downloadFile(response, fileName, loadDate, type);
    }

    @RequestMapping(value="/download", method = RequestMethod.GET)
    public byte[] downloadFile(HttpServletResponse response, @RequestParam String path) throws IOException {
        return cfsService.download(response, path);
    }
}
