package com.test;

import java.util.Random;

public class RandomTest {

    /**
     * 生成从1到指定最大值的随机整数
     *
     * @param max 最大值（包含）
     * @return 1-max之间的随机数
     */
    public static int getRandomNumberFrom1(int max) {
        Random random = new Random();
        return random.nextInt(max) + 1;
    }

    public static void main(String[] args) {
        int max = 1;
        int randomNumber = getRandomNumberFrom1(max);
        System.out.println("从1到" + max + "中随机取出的数字: " + randomNumber);

        // 测试多次随机
        System.out.println("多次随机测试:");
        for (int i = 0; i < 10; i++) {
            System.out.println("第" + (i + 1) + "次: " + getRandomNumberFrom1(max));
        }
    }
}