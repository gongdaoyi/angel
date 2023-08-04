package com.angel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ShirleyTest {

    private static final Logger log = LoggerFactory.getLogger(ShirleyTest.class);

    public static void main(String[] args) throws Exception {
        String str = " d; ";
        str = str.replace(",", ";").trim();
        System.out.println(str);
        List<String> split = Arrays.asList(str.split(";", -1));
        System.out.println("[" + split.size() + "]");

        str = split.stream().filter(v -> !v.contains("a")).collect(Collectors.joining(";"));
        System.out.println("[str=" + str + "]");
    }

    /**
     * 获取当前月第一天
     */
    public static String getFirstDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        // 获取某月最小天数
        int firstDay = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        // 设置日历中月份的最小天数
        calendar.set(Calendar.DAY_OF_MONTH, firstDay);
        // 格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        return sdf.format(calendar.getTime());
    }


    /**
     * 获取当前月最后一天
     */
    public static String getLastDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        // 获取某月最大天数
        int lastDay;
        //2月的平年瑞年天数
        int month = calendar.get(Calendar.MONTH) + 1;
        if (month == 2) {
            lastDay = calendar.getLeastMaximum(Calendar.DAY_OF_MONTH);
        } else {
            lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        // 设置日历中月份的最大天数
        calendar.set(Calendar.DAY_OF_MONTH, lastDay);
        // 格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        return sdf.format(calendar.getTime());
    }

    private static BigDecimal getBigDecimal() {
        return new BigDecimal("9.111111111");
    }

    public static Object toUpperCase(Object obj) {
        return obj.toString().toUpperCase();
    }

    public static void testBean(String vipId, Integer userAge) {
        System.out.println(vipId + userAge);
    }

    /**
     * @param src 目标串
     * @param len 处理后长度
     * @param ch  填充字符
     * @return 处理后串
     */
    public static String padLeft(String src, int len, char ch) {
        int diff = len - src.length();
        if (diff <= 0) {
            return src;
        }

        char[] charr = new char[len];
        System.arraycopy(src.toCharArray(), 0, charr, diff, src.length());
        for (int i = 0; i < diff; i++) {
            charr[i] = ch;
        }
        return new String(charr);
    }

    private static JSONArray testJSON() {

        JSONObject three = new JSONObject();
        three.put("data", "20210102");
        three.put("time", "91050");

        JSONObject four = new JSONObject();
        four.put("data", "20210102");
        four.put("time", "101050");

        JSONArray ary = new JSONArray();
        ary.add(four);
        ary.add(three);

        // 对jsonarray排序
        ary.sort(Comparator.comparing(obj -> ((JSONObject) obj).getString("age")));

        return ary;
    }

    /**
     * 检查幂等性
     */
    private static void checkResult() {
        JSONObject one = new JSONObject();
        one.put("111", "aaaa");
        one.put("aaa", "bbbb");

        JSONObject two = new JSONObject();
        two.put("aaa", "bbbb");
        two.put("111", "aaaa");

        JSONObject oneJson = (JSONObject) JSON.parse(JSON.toJSONString(one));
        JSONObject twoJson = (JSONObject) JSON.parse(JSON.toJSONString(two));

        System.out.println(oneJson.toString());
        System.out.println(twoJson.toString());

        if (!oneJson.equals(twoJson)) {
            System.out.println("查询结果不一致");
            return;
        }

        System.out.println("一致");
    }

    private static void checkContains(String str) {
        try {
            String[] split = str.split("/");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new NullPointerException("空指针异常");
        } finally {
            System.out.println("str:" + str);
        }
    }


    /**
     * 截取倒数第三个/之后的字符
     * PS：index + 1之后不含斜杠
     */
    private static String parse(String shirley) {
        int index = shirley.lastIndexOf("/");
        index = shirley.lastIndexOf("/", index - 1);
        index = shirley.lastIndexOf("/", index - 1);

        return shirley.substring(index + 1);
    }

    /**
     * JSON转换
     *
     * @param subDataObj {"时间":"2021-08-12 08:50:39","操作":"完成签署广发证券专业投资者风险告知确认书（A4）","客户":"向季歼"}
     * @return {时间:'2021-08-12 08:50:39',操作:'完成签署广发证券专业投资者风险告知确认书（A4）',客户:'向季歼'}
     */
    private static String handleSubData(JSONObject subDataObj) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        List<String> lis = new ArrayList<>();
        for (Map.Entry<String, Object> entry : subDataObj.entrySet()) {
            String property = entry.getKey() + ":'" + entry.getValue() + "'";
            lis.add(property);
        }
        String data = lis.stream().collect(Collectors.joining(","));
        sb.append(data);
        sb.append("}");

        return sb.toString();
    }
}
