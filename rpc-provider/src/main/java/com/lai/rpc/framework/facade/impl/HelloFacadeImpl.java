package com.lai.rpc.framework.facade.impl;

import com.lai.rpc.framework.server.RpcService;
import com.lai.rpc.framework.facade.HelloFacade;

import java.util.logging.Logger;

/**
 * Created by laiweigeng on 2018/8/10.
 */
@RpcService(HelloFacade.class)
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
