package cn.net.leadu.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by qiaohao on 2017/2/19.
 */
public class DateUtils {
    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

    //日期类型只做示例  不可使用共通日期类型 单独创建
//    public static SimpleDateFormat simpleDateFormatyyyyMMdd = new SimpleDateFormat("yyyyMMdd");
//    public static SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    public static SimpleDateFormat simpleDateFormat_14 =new SimpleDateFormat("yyyyMMddHHmmss");
//    public static SimpleDateFormat simpleDateFormat_8 =new SimpleDateFormat("yyyyMMdd");
//    public static SimpleDateFormat simpleDateFormat_10 =new SimpleDateFormat("yyyy-MM-dd");
    // 毫秒分钟转换用
    private static final long nm = 1000 * 60;
    /**
     * 将字符时间转换为日期
     * @param time
     * @param simpleDateFormat
     * @return
     */
    public static Date getDate(String time,SimpleDateFormat simpleDateFormat){
        try{
            return simpleDateFormat.parse(time);
        }catch (Exception ex){
            logger.error("将字符时间转换为日期error",ex);
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 返回两个时间相差的分钟
     * @param beginTime
     * @param endTime
     * @return
     */
    public static Long getMinute(Date beginTime,Date endTime){
        Long s = (endTime.getTime() - beginTime.getTime()) / (1000 * 60);
        return s;
    }

    /**
     * 给日期加上指定的年，并返回
     * @param time
     * @param year
     * @return
     */
    public static Date getAddYearDate(Date time,Integer year){
        try{
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(time);
            calendar.add(Calendar.YEAR,year);
            return calendar.getTime();
        }catch (Exception ex){
            logger.error("给日期加上指定的年error",ex);
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 将日期转换为字符串显示
     * @param time
     * @param simpleDateFormat
     * @return
     */
    public static String getStrDate(Date time,SimpleDateFormat simpleDateFormat){
        if (time == null) {
            return null;
        }
        try{
            return simpleDateFormat.format(time);
        }catch (Exception ex){
            logger.error("将日期转换为字符串显示error",ex);
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 将分钟转为天小时分钟表示
     * @param min Double
     * @return String
     */
    public static String minConvertDayHourMin(Double min){

        String html="0分";
        if(min != null){
            Double m = (Double) min;
            String format;
            Object[] array;
            Integer days = (int)(m / (60 * 24));
            Integer hours = (int) (m/(60) - days*24);
            Integer minutes = (int) (m - hours*60 - days*24*60);
            if(days>0){
                format = "%1$,d天%2$,d时%3$,d分";
                array = new Object[]{days,hours,minutes};
            }else if(hours>0){
                format = "%1$,d时%2$,d分";
                array = new Object[]{hours,minutes};
            }else{
                format = "%1$,d分";
                array = new Object[]{minutes};
            }
            html = String.format(format, array);
        }
        return html;
    }

    /**
     * 将期间转为天小时分钟表示
     * @param startTime
     * @param endTime
     * @return String
     */
    public static String convertDisplayNameByTime(Date startTime, Date endTime){
        // 获得两个时间的毫秒时间差异
        long diff  = endTime.getTime() - startTime.getTime();
        // 计算差多少分钟
        double hour = diff / nm;
        return minConvertDayHourMin(hour);
    }

    /**
     * 将时分秒转换为int
     * @param hhmmss
     * @return
     */
    public static Integer convertHHmmssToInt(String hhmmss){
        Integer result = Integer.parseInt(hhmmss.replace(":",""));
        return result;
    }

    /**
     * 根据日期、天数d。返回日期d天之前的日期
     * 比如：2017/3/16 12：22：00 天数 2  返回 2017/3/14 12：22：00
     * @param date 日期
     * @param days 天数
     * @return
     */
    public static Date getBeforeDateByDay(Date date, int days){
        return new Date(date.getTime() - days * 24 * 60 * 60 * 1000);
    }

    /**
     * 判断两日期是否在一天
     * @param date1
     * @param Date2
     * @return
     */
    public static boolean isSameDay(Date date1, Date Date2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        int year1 = calendar.get(Calendar.YEAR);
        int day1 = calendar.get(Calendar.DAY_OF_YEAR);

        calendar.setTime(Date2);
        int year2 = calendar.get(Calendar.YEAR);
        int day2 = calendar.get(Calendar.DAY_OF_YEAR);

        if ((year1 == year2) && (day1 == day2)) {
            return true;
        }
        return false;
    }
}
