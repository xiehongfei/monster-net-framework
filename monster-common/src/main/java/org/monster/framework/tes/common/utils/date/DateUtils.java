package org.monster.framework.tes.common.utils.date;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Project :       Monster-frameWork
 * Author:         XIE-HONGFEI
 * Company:        hongfei tld.
 * Created Date:   2017/2/22 0022
 * Copyright @ 2017 Company hongfei tld. – Confidential and Proprietary
 * <p>
 * History:
 * ------------------------------------------------------------------------------
 *       Date       |     Author     |   Change Description
 * 2017/2/22 0022   |     谢洪飞      |   初版做成
 */

public class DateUtils {



    //private final static Logger logger = LoggerFactory.getLogger(DateUtils.class);

    public final static String DEFAULT_TIMEZONE = "GMT+8";

    public final static String ISO_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public final static String ISO_SHORT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    public final static String DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public final static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    public final static String SHORT_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    public final static String FULL_SEQ_FORMAT = "yyyyMMddHHmmssSSS";

    public final static String[] MULTI_FORMAT = { DEFAULT_DATE_FORMAT, DEFAULT_TIME_FORMAT, ISO_DATE_TIME_FORMAT, ISO_SHORT_DATE_TIME_FORMAT,
            SHORT_TIME_FORMAT, "yyyy-MM" };

    public final static String FORMAT_YYYY = "yyyy";

    public final static String FORMAT_YYYYMM = "yyyyMM";

    public final static String FORMAT_YYYYMMDD = "yyyyMMdd";

    public final static String FORMAT_YYYYMMDDHH = "yyyyMMddHH";

    public final static String FORMAT_YYYYMMDDHHMM = "yyyyMMddHHmm";

    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat(DEFAULT_DATE_FORMAT).format(date);
    }

    public static String formatDate(Date date, String format) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(format).format(date);
    }

    public static Integer formatDateToInt(Date date, String format) {
        if (date == null) {
            return null;
        }
        return Integer.valueOf(new SimpleDateFormat(format).format(date));
    }

    public static Long formatDateToLong(Date date, String format) {
        if (date == null) {
            return null;
        }
        return Long.valueOf(new SimpleDateFormat(format).format(date));
    }

    public static String formatTime(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(DEFAULT_TIME_FORMAT).format(date);
    }

    public static String formatShortTime(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(SHORT_TIME_FORMAT).format(date);
    }

    public static String formatDateNow() {
        return formatDate(DateUtils.currentDate());
    }

    public static String formatTimeNow() {
        return formatTime(DateUtils.currentDate());
    }

    public static Date parseDate(String date, String format) {
        if (date == null) {
            return null;
        }
        try {
            return new SimpleDateFormat(format).parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date parseTime(String date, String format) {
        if (date == null) {
            return null;
        }
        try {
            return new SimpleDateFormat(format).parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date parseDate(String date) {
        return parseDate(date, DEFAULT_DATE_FORMAT);
    }

    public static Date parseTime(String date) {
        return parseTime(date, DEFAULT_TIME_FORMAT);
    }

    public static String plusOneDay(String date) {
        DateTime dateTime = new DateTime(parseDate(date).getTime());
        return formatDate(dateTime.plusDays(1).toDate());
    }

    public static String plusOneDay(Date date) {
        DateTime dateTime = new DateTime(date.getTime());
        return formatDate(dateTime.plusDays(1).toDate());
    }

    public static String getHumanDisplayForTimediff(Long diffMillis) {
        if (diffMillis == null) {
            return "";
        }
        long day = diffMillis / (24 * 60 * 60 * 1000);
        long hour = (diffMillis / (60 * 60 * 1000) - day * 24);
        long min = ((diffMillis / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long se = (diffMillis / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        StringBuilder sb = new StringBuilder();
        if (day > 0) {
            sb.append(day + "D");
        }
        DecimalFormat df = new DecimalFormat("00");
        sb.append(df.format(hour) + ":");
        sb.append(df.format(min) + ":");
        sb.append(df.format(se));
        return sb.toString();
    }

    /**
     * 把类似2014-01-01 ~ 2014-01-30格式的单一字符串转换为两个元素数组
     */
    public static Date[] parseBetweenDates(String date) {
        if (StringUtils.isBlank(date)) {
            return null;
        }
        date = date.replace("～", "~");
        Date[] dates = new Date[2];
        String[] values = date.split("~");
        dates[0] = parseMultiFormatDate(values[0].trim());
        dates[1] = parseMultiFormatDate(values[1].trim());
        return dates;
    }

    public static Date parseMultiFormatDate(String date) {
        try {
            return org.apache.commons.lang3.time.DateUtils.parseDate(date, MULTI_FORMAT);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *@Title:getDiffDay
     *@Description:获取日期相差天数
     *@param:@param beginDate  字符串类型开始日期
     *@param:@param endDate    字符串类型结束日期
     *@param:@return
     *@return:Long             日期相差天数
     *@author:谢
     *@thorws:
     */
    public static Long getDiffDay(String beginDate, String endDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Long checkday = 0l;
        //开始结束相差天数
        try {
            checkday = (formatter.parse(endDate).getTime() - formatter.parse(beginDate).getTime()) / (1000 * 24 * 60 * 60);
        } catch (ParseException e) {

            e.printStackTrace();
            checkday = null;
        }
        return checkday;
    }

    /**
     *@Title:getDiffDay
     *@Description:获取日期相差天数
     *@param:@param beginDate Date类型开始日期
     *@param:@param endDate   Date类型结束日期
     *@param:@return
     *@return:Long            相差天数
     *@author: 谢
     *@thorws:
     */
    public static Long getDiffDay(Date beginDate, Date endDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strBeginDate = format.format(beginDate);

        String strEndDate = format.format(endDate);
        return getDiffDay(strBeginDate, strEndDate);
    }

    /**
     * N天之后
     * @param n
     * @param date
     * @return
     */
    public static Date nDaysAfter(Integer n, Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + n);
        return cal.getTime();
    }

    /**
     * N年之后
     * @param n
     * @param date
     * @return
     */
    public static Date nYearsAfter(Integer n, Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + n);
        return cal.getTime();
    }

    /**
     * N天之前
     * @param n
     * @param date
     * @return
     */
    public static Date nDaysAgo(Integer n, Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - n);
        return cal.getTime();
    }

    public static Date nSecsAgo(Integer n, Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.SECOND, cal.get(Calendar.SECOND) - n);
        return cal.getTime();
    }

    private static Date currentDate;

    public static Date setCurrentDate(Date date) {
       // Validation.isTrue(DynamicConfigService.isDevMode(), "当前操作只能在开发测试运行模式才可用");
        if (date == null) {
            currentDate = null;
        } else {
            currentDate = date;
        }
        return currentDate;
    }

    /**
     * 为了便于在模拟数据程序中控制业务数据获取到的当前时间
     * 提供一个帮助类处理当前时间，为了避免误操作，只有在devMode开发模式才允许“篡改”当前时间
     * @return
     */
    public static Date currentDate() {
        if (currentDate == null) {
            return new Date();
        }
        /*if (DynamicConfigService.isDevMode()) {
            return currentDate;
        } else {*/
            return new Date();
        //}
    }

    public static Integer getWeekOfYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        String yearString = formatDate(date, FORMAT_YYYY);
        int weekNum = c.get(Calendar.WEEK_OF_YEAR);
        if (weekNum < 10) {
            yearString = StringUtils.rightPad(yearString, 5, "0");
        }
        return Integer.valueOf(yearString + weekNum);
    }

    /**
     *  比较两时间相差
     *
     * @param fromDate
     * @param now
     * @param diffType
     * @return
     */
    public static long getDiffDayHourMinSeconds(Date fromDate, Date now, DiffType diffType) {

        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        long ns = 1000;
        long diff = now.getTime() - fromDate.getTime();
        long result = -1;
        switch (diffType) {
            case Day:
                result = diff / nd;
                break;
            case Hour:
                result = diff / nh;
                break;
            case Minute:
                result = diff / nm;
                break;

            case Seconds:
                result = diff / ns;
                break;

            default:
                //throw new ServiceException("日期比较类型错误");
                break;
        }
        return result;
    }

    public static enum DiffType {
        Day,

        Hour,

        Minute,

        Seconds
    }

    /**
     *   n分钟后
     *
     * @param n
     * @param date
     * @return
     */
    public static Date nMinsAfter(Integer n, Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, n);

        return cal.getTime();
    }

    /**
     *   n分钟前
     *
     * @param n
     * @param date
     * @return
     */
    public static Date nMinsAgo(Integer n, Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, (0 - n));

        return cal.getTime();
    }

    public static void main(String[] args) {

        String authorization = UUID.nameUUIDFromBytes("aqbx_api_gfagent_auth_prd".getBytes()).toString();
        System.out.println("UUID-  " + authorization);

        Map<String, Object> sortedParams = Maps.newTreeMap();
        sortedParams.put("zero", "what r u doing now ?");
        sortedParams.put("arr", "how do u do ?");
        sortedParams.put("beer", "hehe , bear ?");
        sortedParams.put("about", "what's the fuck ?");

        for (Map.Entry<String, Object> entry : sortedParams.entrySet()) {
            System.out.println(entry.getKey() + entry.getValue());
        }

        String policyUrl = "<![CDATA[https://antx11.answern.com/com.isoftstone.iics.www/downloadChannelPolicys?sign=84edfb82f7ac783e85abae849a2bf79d&policyNo=1010008080017170000000009]]>";

        System.out.println(StringUtils.substringBetween(policyUrl, "<![CDATA[", "]]>"));

        Date currDate = new Date();

        System.out.println(DateUtils.formatDate(currDate, DateUtils.DEFAULT_TIME_FORMAT));
        System.out.println(DateUtils.formatDate(DateUtils.nSecsAgo(1, currDate), DateUtils.DEFAULT_TIME_FORMAT));

        int a = 10;
        int b = 10;
        method2(a, b);
        System.out.println("a=" + a);
        System.out.println("b=" + b);

    }

    private static void method2(int a, int b) {
        System.setOut(new java.io.PrintStream(System.out, true) {
            @Override
            public void println(String x) {
                if ("a=10".equals(x)) {
                    super.println("a=100");
                } else if ("b=10".equals(x)) {
                    super.println("b=200");
                }
            }
        });
    }

    private static void method(int a, int b) {

        System.out.println("a=100");
        System.out.println("b=200");
        System.exit(0);
    }

    private static void method1(int a, int b) {
        System.setOut(new java.io.PrintStream(System.out, true) {
            @Override
            public void println(String x) {
                super.println(x + '0');
            }
        });
    }


}
