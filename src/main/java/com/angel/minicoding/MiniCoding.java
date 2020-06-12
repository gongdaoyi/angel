package com.angel;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: Conor-McGregor
 * @description:
 * @author: Mr.Gong
 * @create: 2020-06-11 21:35
 **/
public class MiniCoding {
    public static void main(String[] args) {
        String[] dictionary = {"i", "like", "sam", "sung", "samsung", "mobile", "ice", "cream", "man go"};
        String other = "ilikesamsungmobile";
        System.out.println("Output:" + parsing(dictionary, other));
    }

    private static String parsing(String[] dictionary, String other) {
        Pattern pattern = Pattern.compile(String.join("|", dictionary));
        Matcher matcher = pattern.matcher(other);
        List<String> list = new LinkedList<>();
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return String.join(" ", list);
    }
}
