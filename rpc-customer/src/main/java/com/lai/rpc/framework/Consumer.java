package com.lai.rpc.framework;



import com.lai.rpc.framework.client.RpcProxy;
import com.lai.rpc.framework.facade.HelloFacade;
import org.springframework.context.support.ClassPathXmlApplicationContext;
/**
 * Created by laiweigeng on 2018/8/10.
 */

public class Consumer {


    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext applicationContext =  new ClassPathXmlApplicationContext("spring.xml");
        RpcProxy rpcProxy = applicationContext.getBean(RpcProxy.class);
        HelloFacade helloFacade = rpcProxy.create(HelloFacade.class);
        System.out.println(helloFacade.sayHello("lai"));
    }
}
