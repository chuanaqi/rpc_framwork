package com.lai.rpc.framwork;

import com.lai.rpc.framwork.facade.HelloFacade;
import com.lai.rpc.framwork.facade.impl.HelloFacadeImpl;
import com.lai.rpc.framwork.server.ServerThread;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by laiweigeng on 2018/8/10.
 */
public class Provider {
    public static final ExecutorService executorService = Executors.newCachedThreadPool();
    public static void export(final Object service, int port) throws Exception {
        //服务校验
        if (service == null) {
            throw new IllegalArgumentException("service must not be null");
        }
        //端口校验
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Invalid port:" + port + "a valid port must between 0 and 65535");
        }
        //向操作系统注册服务
        ServerSocket serverSocket = new ServerSocket(port);
        //循环启动监听
        while (true) {
            Socket socket = serverSocket.accept();
            //开启独立的线程处理服务调用
            executorService.submit(new ServerThread(socket,service));
//            executorService.shutdown();

        }
    }

    public static void main(String[] args) throws Exception {
        HelloFacade service = new HelloFacadeImpl();
        System.out.println("rpc provider started");
        export(service, 1234);
    }
}
