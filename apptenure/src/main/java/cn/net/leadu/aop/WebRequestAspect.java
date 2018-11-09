package cn.net.leadu.aop;

import cn.net.leadu.dao.ErrorlogsRepository;
import cn.net.leadu.domain.ErrorLogs;
import cn.net.leadu.message.Message;
import cn.net.leadu.message.MessageType;
import cn.net.leadu.service.SystemService;
import cn.net.leadu.util.CommonUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by huzongcheng on 2017/4/6.
 */
@Aspect
@Component
public class WebRequestAspect {

    @Autowired
    private SystemService systemService;

    @Autowired
    private ErrorlogsRepository errorlogsRepository;

    // 定义切点Pointcut
    @Pointcut("execution(public * cn.net.leadu.controller.FunctionController.*(..))")
    public void excuteService() {
    }

    @Around("excuteService()")
    public Object doAround(ProceedingJoinPoint joinPoint) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            // 获取用户标识
            String uniqueMark = request.getHeader("uniqueMark");
            // 验证有效性
            ResponseEntity<Message> result1 = systemService.verify(uniqueMark);
            if(CommonUtils.errorCode.equals(result1.getBody().getError())){
                return result1;
            } else {
                String path = request.getServletPath();
                if(path.contains("/tenure/ocrIdCard") || path.contains("/tenure/verify")){
                    return joinPoint.proceed();
                }
                ResponseEntity<Message> result = (ResponseEntity<Message>) joinPoint.proceed();
                Message message = result.getBody();
                if(!MessageType.MSG_TYPE_SUCCESS.equals(message.getStatus())){
                    saveError(request, uniqueMark, message);
                }
                return result;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            Message message = new Message(MessageType.MSG_TYPE_ERROR, CommonUtils.errorInfo);
            return new ResponseEntity<Message>(message, HttpStatus.OK);
        }
    }

    private void saveError(HttpServletRequest request, String uniqueMark, Message message) {
        ErrorLogs errorlogs = new ErrorLogs();
        errorlogs.setPath(request.getServletPath());
        String [] values = uniqueMark.split(":");
        String phoneNum = values[0];
        errorlogs.setPhoneNum(phoneNum);
        errorlogs.setErrorInfo(message.getError());
        errorlogs.setDeviceType(request.getHeader("deviceType"));
        errorlogsRepository.save(errorlogs);
    }
}
