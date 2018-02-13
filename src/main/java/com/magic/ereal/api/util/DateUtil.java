package com.magic.ereal.api.util;

import org.omg.CORBA.PUBLIC_MEMBER;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 时间格式化工具
 * Created by Eric Xie on 2017/4/24 0024.
 */
public class DateUtil {

    /**
     * 将 时间戳 格式化成 指定格式 时间
     * @return
     */
    public static String dateFortimestamp(long timestamp,String format) throws Exception{
        if(0 == timestamp){
            timestamp = System.currentTimeMillis();
        }
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(new Date(timestamp));
    }


    public static Date strToDate(String dataStr,String format){
        DateFormat dateFormat = new SimpleDateFormat(format);
        try {
            return dateFormat.parse(dataStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public static Date dateFortimestamp(long timestamp,String format,Integer flag){
        if(0 == timestamp){
            timestamp = System.currentTimeMillis();
        }
        DateFormat dateFormat = new SimpleDateFormat(format);
        return strToDate(dateFormat.format(new Date(timestamp)),format);
    }



    public static Date setTime(Date date,Integer days){
        date = null == date ? new Date() :  date;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH,days == null ? 0 : days);
        return calendar.getTime();
    }

    public static Date setTime(Long timestamp){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(null == timestamp ? new Date() : new Date(timestamp));
        calendar.add(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        return calendar.getTime();
    }

    public static void main(String[] args) {
        System.out.println(setTime(new Date(),-2));
    }



}
