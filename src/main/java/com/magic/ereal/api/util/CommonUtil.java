package com.magic.ereal.api.util;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 *
 * 通用工具
 * Created by Eric Xie on 2017/2/13 0013.
 */
public class CommonUtil {



    /**
     *  判断 String集合是否为空
     * @param args
     * @return
     */
    public static boolean isEmpty(String... args){
        for (String string : args) {
            if(null == string || string.trim().length() == 0 || "null".equals(string)){
                return true;
            }
        }
        return false;
    }

    public static boolean isEmpty(Integer... args){
        for (Integer arg : args){
            if(null == arg){
                return true;
            }
        }
        return false;
    }

    public static boolean isEmpty(Long... args){
        for (Long arg : args){
            if(null == arg){
                return true;
            }
        }
        return false;
    }
    /**
     *  获取手机号的后四位
     * @return
     */
    public static String subMobile(String mobile){
        if(null == mobile || mobile.length() <= 5){
            return "0000";
        }
        return mobile.substring(mobile.length() - 4);
    }

    /**
     *  订单号生成规则
     * @return
     */
    public static String buildOrderNumber(){
        SimpleDateFormat format = new SimpleDateFormat("yyMMddhhmmssSSSS");
        return format.format(new Date());
    }


    /**
     *  获取客户端的IP地址
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    public static String get32UUID(){
        return UUID.randomUUID().toString().trim().replaceAll("-", "");
    }

}
