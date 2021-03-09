package com.example.position.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtils {

    public final static String YYYY_MM_DD_HH_MM_SS_S = "yyyy-MM-dd HH:mm:ss.S";
    public final static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public final static String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public final static String YYYY_MM_DD_HH = "yyyy-MM-dd HH";
    public final static String YYYY_MM_DD = "yyyy-MM-dd";
    public final static String HH_MM_SS = "HH:mm:ss";
    public final static String YYYY_MM = "yyyy-MM";
    public final static String YYYY = "yyyy";
    public final static String MM = "MM";
    public final static String DD = "dd";
    public final static String YYYYMMDD = "yyyyMMdd";
    public final static String YYYYMMDDHHMMSSS = "yyyyMMddHHmmssS";
    public final static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public final static String TODAY_FIRST = "TODAY_FIRST";
    public final static String TODAY_LAST = "TODAY_LAST";

    /**
     * 获取一个简单的日期格式化对象
     * @param parttern 日期格式
     * @return 一个简单的日期格式化对象
     */
    private static SimpleDateFormat getFormatter(String parttern) {
        return new SimpleDateFormat(parttern);
    }


    public static void main(String[] args) {
        System.out.println(getToDayFirstOrLast(TODAY_FIRST));//今天零点零分零秒
        System.out.println(getToDayFirstOrLast(TODAY_LAST));//今天零点零分零秒
    }

    public static Date getToDayFirstOrLast(String type){
        long current=System.currentTimeMillis();
        //今天零点零分零秒的毫秒数
        long zero=current/(1000*3600*24)*(1000*3600*24)- TimeZone.getDefault().getRawOffset();
        if(StringUtils.isBlank(type)){
            return new Date(zero);
        }
        if(StringUtils.equals(DateUtils.TODAY_FIRST,type)){
            return new Date(zero);
        }
        if(StringUtils.equals(DateUtils.TODAY_LAST,type)){
            long twelve=zero+24*60*60*1000-1;//今天23点59分59秒的毫秒数
            return new Date(twelve);
        }
        return new Date(zero);
    }

    /**
     * 获取两个日期之间的日期集合
     * @param start
     * @param end
     * @return
     * @auto aaron
     */
    public static List<Date> getBetweenDates(Date start, Date end) {
        List<Date> result = new ArrayList<Date>();
        try {
            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(start);
            tempStart.add(Calendar.DAY_OF_YEAR, 1);

            Calendar tempEnd = Calendar.getInstance();
            tempEnd.setTime(end);
            while (tempStart.before(tempEnd)) {
                result.add(tempStart.getTime());
                tempStart.add(Calendar.DAY_OF_YEAR, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获得N天的日期
     *
     * @return
     */
    public static Date getNDate(Integer n) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,n);
        return calendar.getTime();
    }

    /**
     * 获得指定日志N天的日期
     *
     * @return
     */
    public static Date getNDate(Date date,Integer n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE,n);
        return calendar.getTime();
    }


    /**
     * 获得N天的日期
     *
     * @return 一个包含年月日的String型日期。
     */
    public static String getNDateStr(Integer n,String format) {
        return format(getNDate(n), format);
    }

    /**
     * 获得指定日期 N天的日期
     *
     * @return 一个包含年月日的String型日期。
     */
    public static String getNDateStr(Date date,Integer n,String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE,n);
        return format(calendar.getTime(), format);
    }


    /**
     * 获取两个日期之间的日期集合
     * @param start
     * @param end
     * @return
     * @auto aaron
     */
    public static List<String> getBetweenDatesStr(Date start, Date end) {
        List<String> result = new ArrayList<String>();
        getBetweenDates( start,  end).forEach(s ->{
            result.add(format(s));
        });
        return result;
    }



    /**
     * 日期格式化－将<code>Date</code>类型的日期格式化为<code>String</code>型
     * @param date 待格式化的日期
     * @param pattern 时间样式
     * @return 一个被格式化了的<code>String</code>日期
     */
    public static String format(Date date, String pattern) {
        if (date == null) {
            return "";
        } else {
            return getFormatter(pattern).format(date);
        }
    }

    /**
     * 日期解析－将String类型的日期解析为Date型
     * @param strDate 待格式化的日期
     * @param pattern 日期样式
     * @exception ParseException 如果所给的字符串不能被解析成一个日期
     * @return 一个被格式化了的Date日期
     */
    public static Date parse(String strDate, String pattern) throws ParseException {
        try {
            return getFormatter(pattern).parse(strDate);
        } catch (ParseException pe) {
            throw new ParseException("Method parse in Class DateUtils  err: parse strDate fail.", pe.getErrorOffset());
        }
    }

    /**
     * 默认把日期格式化成yyyy-MM-dd格式
     * @param date
     * @return
     */
    public static String format(Date date) {
        if (date == null) {
            return "";
        } else {
            return getFormatter(YYYY_MM_DD).format(date);
        }
    }

    /**
     * 把字符串日期默认转换为yyyy-MM-dd格式的Data对象
     * @param strDate
     * @return
     */
    public static Date format(String strDate) {
        Date d = null;
        if (StringUtils.isNotEmpty(strDate)) {
            try {
                d = getFormatter(YYYY_MM_DD).parse(strDate);
            } catch (ParseException pex) {
                return d;
            }
        }
        return d;
    }

    /**
     * 获取当前日期
     * @return Date型日期
     */
    public static synchronized Date getCurrDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    /**
     * 获取当前完整时间
     * @return 一个包含年月日时分秒的String型日期。YYYYMMDDHHMMSSS
     */
    public static String getCurrDateStr(String format) {
        return format(getCurrDate(), format);
    }

    /**
     * 获取当前完整时间
     * @return 一个包含年月日时分秒的String型日期。YYYYMMDDHHMMSSS
     */
    public static String getCurrDateTimeStrS() {
        return format(getCurrDate(), YYYYMMDDHHMMSSS);
    }

    /**
     * 获取当前完整时间
     * @return 一个包含年月日时分秒的String型日期。yyyy-MM-dd hh:mm:ss
     */
    public static String getCurrDateTimeStr() {
        return format(getCurrDate(), YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 获得当前日期
     * @return 一个包含年月日的String型日期。YYYY_MM_DD_HH_MM_SS
     */
    public static String getCurrDateStr() {
        return format(getCurrDate(), YYYY_MM_DD_HH_MM_SS);
    }


    /**
     * 获取当前年分 样式：yyyy
     * @return 当前年分
     */
    public static String getYear() {
        return format(getCurrDate(), YYYY);
    }

    /**
     * 获取当前月分 样式：MM
     * @return 当前月分
     */
    public static String getMonth() {
        return format(getCurrDate(), MM);
    }

    /**
     * 获取当前日期号 样式：dd
     * @return 当前日期号
     */
    public static String getDay() {
        return format(getCurrDate(), DD);
    }

    /**
     * 按给定日期样式判断给定字符串是否为合法日期数据
     * @param strDate 要判断的日期
     * @param pattern 日期样式
     * @return true 如果是，否则返回false
     */
    public static boolean isDate(String strDate, String pattern) {
        try {
            parse(strDate, pattern);
            return true;
        } catch (ParseException pe) {
            return false;
        }
    }

    /**
     * 判断给定字符串是否为特定格式年月日时分秒（格式：yyyy-MM-dd HH:mm:ss）数据
     * @param strDate 要判断的日期
     * @return true 如果是，否则返回false
     */
    public static boolean isYYYY_MM_DD_HH_MM_SS(String strDate) {
        try {
            parse(strDate, YYYY_MM_DD_HH_MM_SS);
            return true;
        } catch (ParseException pe) {
            return false;
        }
    }

    /**
     * 判断给定字符串是否为特定格式的年月日（格式：yyyy-MM-dd）数据
     * @param strDate 要判断的日期
     * @return true 如果是，否则返回false
     */
    public static boolean isYYYY_MM_DD(String strDate) {
        try {
            parse(strDate, YYYY_MM_DD);
            return true;
        } catch (ParseException pe) {
            return false;
        }
    }

    /**
     * 判断给定字符串是否为特定格式时分秒（格式：HH:mm:ss）数据
     * @param strDate 要判断的日期
     * @return true 如果是，否则返回false
     */
    public static boolean isHH_MM_SS(String strDate) {
        try {
            parse(strDate, HH_MM_SS);
            return true;
        } catch (ParseException pe) {
            return false;
        }
    }

}
