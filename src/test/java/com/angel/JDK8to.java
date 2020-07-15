package com.angel;

import com.angel.model.User;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 测试类
 * @create: 2020-07-08 17:48
 **/
public class JDK8to {

    public static void main(String[] args) {
//        String org = "null";
//        if (getYesOrNoNull(org)) {
//            System.out.println("不为空");
//            return;
//        }
//        System.out.println("为空");
        String str = "null";
        System.out.println(getLength(str));
    }

    public static User getUser(User user) {//user.name为空 设置user.name
        return Optional.ofNullable(user)
                .filter(u -> "gdy".equals(u.getName()))
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setName("isNull");
                    return newUser;
                });
    }

    public static List<Integer> getSquare(List<Integer> params) {//原元素平方
        return params.stream().map(p -> p * p).distinct().collect(Collectors.toList());
    }

    public static long getNullNumber(List<String> params) {//统计为空得数量
        return params.stream().filter(str -> str.isEmpty()).count();
    }

    public static String getUserOrgName(User user) {//org_name 为空 不为空两种输出
        return Optional.ofNullable(user).map(u -> u.getOrg()).map(org -> org.getOrg_name()).orElse("schoolIsNull");
    }

    public static boolean getYesOrNoNull(String params) {//字符串为null
        return Optional.ofNullable(params).isPresent();
    }

    public static int getLength(String str) {//字符串判空
//        if(str == null){
//            return 0 ;
//        }
//        return str.length();
        return Optional.ofNullable(str).map(String::length).orElse(0);
    }


}
