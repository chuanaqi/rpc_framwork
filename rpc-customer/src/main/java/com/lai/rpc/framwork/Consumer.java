package com.lai.rpc.framwork;

import com.lai.rpc.framwork.facade.HelloFacade;
import com.lai.rpc.framwork.util.MyInvocationHandler;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;

/**
 * Created by laiweigeng on 2018/8/10.
 */
public class Consumer {

    public static <T> T refer(final Class<T> interfaceClass,
            final String host, final int port) throws Exception {
        if (interfaceClass == null)
            throw new IllegalArgumentException("Interface class == null");
        if (!interfaceClass.isInterface())
            throw new IllegalArgumentException("The " + interfaceClass.getName() + " must be interface class!");
        if (host == null || host.length() == 0)
            throw new IllegalArgumentException("Host == null!");
        if (port <= 0 || port > 65535)
            throw new IllegalArgumentException("Invalid port " + port);
        System.out.println("Get remote service " + interfaceClass.getName() + " from server " + host + ":" + port);
        //生成动态代理对象
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new MyInvocationHandler(host,port));
    }

    public static void main(String[] args) throws Exception {
        //此处返回的是动态代理对象
        HelloFacade service = refer(HelloFacade.class, "127.0.0.1", 1234);
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            //调用hello方法时会调用代理对象的invoke方法
            String hello = service.sayHello("World" + i);
            System.out.println(hello);
            Thread.sleep(1000);
        }
    }
}
