package cn.net.leadu.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.util.Date;

/**
 * Created by PengChao on 16/9/22.
 */
public class CommonUtils {
    private static final Logger logger = LoggerFactory.getLogger(CommonUtils.class);

    public static String loginkey = "token_";
    public static String errorCode = "9500";
    public static String errorInfo = "系统异常，请重试";

    /**
     * sql语句like查询值构造
     *
     * @param param
     * @return
     */
    public static String likePartten(String param) {
        return "%" + param + "%";
    }

    /**
     * 获取文件后缀名(包含.)
     *
     * @param fileName
     * @return
     */
    public static String getFileSuffix(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public static String inPartten(Object[] param) {
        StringBuffer stringBuffer = new StringBuffer();
        for (Object object : param) {
            stringBuffer.append("'" + object + "',");
        }
        if (stringBuffer.length() > 0) {
            stringBuffer = stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        }
        return stringBuffer.toString();
    }


    /**
     * 返回是否为空
     *
     * @param param
     * @return
     */
    public static boolean isNull(Object param) {
        if (param == null)
            return true;
        else if (param.toString().trim().equals(""))
            return true;
        else
            return false;
    }

    /**
     * URL解码
     *
     * @param param
     * @return
     */
    public static String urlDecoder(String param) {
        if (param == null) {
            return null;
        }
        try {
            return URLDecoder.decode(param, "utf-8");
        } catch (Exception ex) {
            logger.error("URL解码error",ex);
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 转string字符串
     *
     * @param param
     * @return
     */
    public static String getStr(Object param) {
        try {
            if (param != null)
                return param.toString();
            else
                return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 转date
     *
     * @param param
     * @return
     */
    public static Date getDate(Object param) {
        try {
            if (param != null)
                return (Date)param;
            else
                return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
