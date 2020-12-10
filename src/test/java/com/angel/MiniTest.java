package com.angel;

import com.AngelApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = {AngelApplication.class})
public class MiniTest {

    @Autowired
    MiniCode miniCode;

    /**
     * Write a program to convert the digits from 0 to 9 into letters
     */
    @Test
    public void testOne() {
        List<String> set = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            set.add(String.valueOf(i));
        }

        log.info("Answer to question 1 : {}", miniCode.numberToLetters(set));
    }

    /**
     *The program need to support converting the digits from 0 to 99 into letters
     */
    @Test
    public void testTwo (){
        log.info("Answer to question 2 : {}", miniCode.translationNumber(99));
    }

}





