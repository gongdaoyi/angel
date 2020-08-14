package com.angel;

import com.alibaba.fastjson.JSONObject;
import com.angel.model.User;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: ������
 * @create: 2020-07-08 17:48
 **/
public class JDK8to {

    public static void main(String[] args) {
        JSONObject object = new JSONObject();
        List<User> list = new ArrayList<>();
        for(int i = 0 ; i <3 ;i++){
            User user = new User ();
            user.setName("��"+i);
            list.add(user);
        }
        object.put("test",list);
        System.out.println(object.toJSONString());
//        String org = "null";
//        if (Optional.ofNullable(org).isPresent()) {
//            System.out.println("��Ϊ��");
//            return;
//        }
//        System.out.println("Ϊ��");

    }

    public static User getUser(User user) {//user.nameΪ�� ����user.name
        return Optional.ofNullable(user)
                .filter(u -> "gdy".equals(u.getName()))
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setName("isNull");
                    return newUser;
                });
    }

    public static List<Integer> getSquare(List<Integer> params) {//ԭԪ��ƽ��
        return params.stream().map(p -> p * p).distinct().collect(Collectors.toList());
    }

    public static long getNullNumber(List<String> params) {//ͳ��Ϊ�յ�����
        return params.stream().filter(str -> str.isEmpty()).count();
    }

    public static String getUserOrgName(User user) {//org_name Ϊ�� ��Ϊ���������
        return Optional.ofNullable(user).map(u -> u.getOrg()).map(org -> org.getOrg_name()).orElse("schoolIsNull");
    }

    public static boolean getYesOrNoNull(String params) {//�ַ���Ϊnull
        return Optional.ofNullable(params).isPresent();
    }

    public static int getLength(String str) {//�ַ����п�
//        if(str == null){
//            return 0 ;
//        }
//        return str.length();
        return Optional.ofNullable(str).map(String::length).orElse(0);
    }


}
