package com.angel.utils;


//@Component
public class Task {

    private int i = 1;

    //    @Scheduled(cron = "0/15 * * * * ?")
    public void bTask() {

        System.out.println("执行第" + i + "次");
        i++;
    }
}
