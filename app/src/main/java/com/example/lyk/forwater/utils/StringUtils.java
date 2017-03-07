package com.example.lyk.forwater.utils;
import java.util.Calendar;
/**
 * Created by lyk on 2016/12/2.
 */

public class StringUtils {
    public static String getYear(String src)
    {
        return src.split("-")[0].trim();
    }
    public static String getMonth(String src)
    {
        return src.split("-")[1].trim();
    }
    public static String getDay(String src)
    {
        return src.split(" ")[0].split("-")[2].trim();
    }
    public static String getHour(String src)
    {
        return src.split(" ")[1].split(":")[0].trim();
    }
    public static String getMinute(String src)
    {
        return src.split(" ")[1].split(":")[1].trim();
    }
    public static String getSecond(String src)
    {
        return src.split(" ")[1].split(":")[2].trim();
    }
    public static String convertCalendar(Calendar calendar)
    {
        String month= (calendar.get(Calendar.MONTH) + 1)+"";
        if(month.trim().length()==1)
            month="0"+month.trim();
        String day=calendar.get(Calendar.DAY_OF_MONTH)+"";
        if(day.trim().length()==1)
            day="0"+day.trim();
        String time = calendar.get(Calendar.YEAR) + "-" + month + "-"
                + day + "%";
        return time;
    }
    public static boolean isDateEq(String c1,String c2)
    {
        if(c1.split(" ")[0].equals(c2.split(" ")[0]))
            return true;
        return false;
    }
    public static String realconvertCalendar(Calendar calendar)
    {
        String month= (calendar.get(Calendar.MONTH) + 1)+"";
        if(month.trim().length()==1)
            month="0"+month.trim();
        String day=calendar.get(Calendar.DAY_OF_MONTH)+"";
        if(day.trim().length()==1)
            day="0"+day.trim();

        String time = calendar.get(Calendar.YEAR) + "-" + month + "-"
                + day + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":"
                + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
        return time;
    }
}
