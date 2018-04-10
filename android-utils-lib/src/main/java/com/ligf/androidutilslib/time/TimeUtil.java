package com.ligf.androidutilslib.time;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 * Created by ligf on 2018/4/10.
 */

public class TimeUtil {

    /**
     * 获取当前时间字符串（年月日）
     * @return
     */
    public static String getCurrentTimeStr(){
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(calendar.get(Calendar.YEAR))
                .append(month < 10 ? "0" + month : month + "")
                .append(calendar.get(Calendar.DATE));
        return stringBuilder.toString();
    }

    /**
     * long 	单位是ms(毫秒)
     * format 	时间格式 (ex：yyyy/MM/dd hh:mm)
     * */
    public static String convertTime(long time, String format) {
        String convertTime = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = new Date(time);
        convertTime = simpleDateFormat.format(date);
        return convertTime;
    }
}
