package com.chenfan.finance.utils.pageinfo;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author: xuxianbei
 * Date: 2020/10/28
 * Time: 17:57
 * Version:V1.0
 */
public class TimeThreadSafeUtils {

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static final String YYYY_MM = "yyyy-MM";

    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    public static String dateFormatYYYY_MM_DD_HH_MM_SS(LocalDateTime dateTime) {
        return dateTimeFormat(dateTime, YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 时间格式
     *
     * @param dateTime
     * @param format
     * @return
     */
    public static String dateTimeFormat(LocalDateTime dateTime, String format) {
        return dateTime.format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * 当前时间
     * @return
     */
    public static LocalDateTime now() {
       return LocalDateTime.now();
    }

    /**
     * 当天最小时间例如  2020-10-12 00:00:00
     *
     * @return
     */
    public static LocalDateTime nowMin() {
        return LocalDateTime.parse(dateTimeFormat(LocalDateTime.now(), YYYY_MM_DD) + " 00:00:00",
                DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS));
    }

    /**
     * 当天最大时间例如  2020-10-12 23:59:59
     *
     * @return
     */
    public static LocalDateTime nowMax() {
        return LocalDateTime.parse(dateTimeFormat(LocalDateTime.now(), YYYY_MM_DD) + " 23:59:59",
                DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS));
    }

    /**
     * 两个时间差
     * @param dateA
     * @param dateB
     * @return
     */
    public static Long dateBetweenDay(Date dateA, Date dateB) {
        LocalDateTime ldtA = dateToLocalDateTime(dateA);
        LocalDateTime ldtB = dateToLocalDateTime(dateB);
        return Duration.between(ldtA, ldtB).toDays();
    }

    public static Integer dateCompare(Date dateA, Date dateB) {
        LocalDateTime ldtA = dateToLocalDateTime(dateA);
        LocalDateTime ldtB = dateToLocalDateTime(dateB);
        return ldtA.compareTo(ldtB);
    }

    public static LocalDateTime dateToLocalDateTime(Date dateA) {
        return dateA.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);//Combines this date-time with a time-zone to create a  ZonedDateTime.
        return Date.from(zdt.toInstant());
    }

}
