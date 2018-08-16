package com.lai.rpc.framework;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by laiweigeng on 2018/8/16.
 */
public class Application {
    public static void main(String[] args) {
        System.out.println("服务端返回开启");
        new ClassPathXmlApplicationContext("spring.xml");

    }
}
