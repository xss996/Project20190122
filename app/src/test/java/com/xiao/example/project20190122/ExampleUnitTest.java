package com.xiao.example.project20190122;

import com.xiao.example.project20190122.util.CheckCodeUtil;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test1(){
        int angle = 60;    //假设角度为60度
        double radian = angle * Math.PI / 180;    //计算出弧度
        System.out.println(Math.cos(radian) );    //输出cos 60度的值
        System.out.println(CheckCodeUtil.getCommand(2047,45));

    }
}