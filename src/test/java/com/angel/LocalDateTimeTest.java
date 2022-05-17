package com.angel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author HP
 **/
public class LocalDateTimeTest {

    private static final Logger logger = LoggerFactory.getLogger(LocalDateTimeTest.class);

    public static void main(String[] args) {
        List<String> dayByMonth = getDayByMonth(2020, 2);

        logger.info(dayByMonth.toString());
    }

    /**
     * 获取指定年 月的每一天
     */
    private static List<String> getDayByMonth(int yearParam, int monthParam) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, yearParam);
        calendar.set(Calendar.MONTH, monthParam - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<String> dateList = new ArrayList<>();

        int currentMonth = calendar.get(Calendar.MONTH);

        while (currentMonth == calendar.get(Calendar.MONTH)) {
            dateList.add(sdf.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return dateList;
    }
}
