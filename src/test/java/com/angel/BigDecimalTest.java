package com.angel;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @description: 计算 10：30 到 11：45 差了多少小时 并且四舍五入 保留一位小数 （1.25 ~= 1.3）
 * @create: 2020-08-11 17:16
 **/
public class BigDecimalTest {
    public static void main(String[] args) throws ParseException {
        // 1.3
        System.out.println(timeDifference("10:30", "11:45"));
    }

    public static String timeDifference(String startTime, String endTime) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");

        long startLong = df.parse(startTime).getTime();
        long endLong = df.parse(endTime).getTime();

        BigDecimal startBig = new BigDecimal(startLong);
        BigDecimal endBig = new BigDecimal(endLong);

        BigDecimal bigDecimal = (endBig.subtract(startBig))
                .divide(new BigDecimal(3600000), 1, BigDecimal.ROUND_HALF_UP);

        return bigDecimal.toString();
    }

}
