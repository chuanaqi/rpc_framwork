package com.lai.rpc.framwork.facade.impl;

import com.lai.rpc.framwork.facade.HelloFacade;

import java.util.logging.Logger;

/**
 * Created by laiweigeng on 2018/8/10.
 */
public class HelloFacadeImpl implements HelloFacade {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public String sayHello(String info) {
        logger.info(info);
        return "Hello:"+info;
    }
    public static void main(String[] args){
        new HelloFacadeImpl().sayHello("lai");
    }
}
