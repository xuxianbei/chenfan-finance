package com.chenfan.finance.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author liran
 */
public class TimeFormatUtil {


    public static SimpleDateFormat yyyyMMDate = new SimpleDateFormat("yyyyMM");
    public static SimpleDateFormat allDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat yyyyMMddDate = new SimpleDateFormat("yyyyMMdd");
    public static String localDateTimeToString(LocalDateTime oldTime) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String localTime = null;
        if(null != oldTime){
            localTime = df.format(oldTime);
        }
        return localTime;
    }
    public static String localDateTimeToString(LocalDateTime oldTime,String format) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        String localTime = null;
        if(null != oldTime){
            localTime = df.format(oldTime);
        }
        return localTime;
    }
    public static String localDateTimeToStringAll(LocalDateTime oldTime) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String localTime = null;
        if(null != oldTime){
            localTime = df.format(oldTime);
        }
        return localTime;
    }



    public static LocalDateTime StringToLocalDateTime(String timeParam){
        DateTimeFormatter dtm = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime parse = LocalDateTime.parse(timeParam, dtm);
        return parse;
    }
    public static LocalDateTime stringToLocalDateTime(String timeParam){
        if(StringUtils.isBlank(timeParam)){
            return null;
        }
        DateTimeFormatter dtm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime parse = LocalDateTime.parse(timeParam, dtm);
        return parse;
    }

}
