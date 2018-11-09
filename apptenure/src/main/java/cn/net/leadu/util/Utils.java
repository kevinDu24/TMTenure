package cn.net.leadu.util;

/**
 * Created by LEO on 16/9/1.
 */
public class Utils {
    public static Boolean isCardId(String str){
        return str.matches("(\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)");
    }

    public static Boolean isNumber(String str){
        return str.matches("[0-9]+");
    }

    public static String getFileSuffix(String fileName){
        return fileName.substring(fileName.lastIndexOf("."));
    }

}
