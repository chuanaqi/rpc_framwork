package com.lai.rpc.framework.client;

import com.lai.rpc.framework.registry.common.RpcRequest;
import com.lai.rpc.framework.registry.common.RpcResponse;
import com.lai.rpc.framework.registry.service.IDiscoveryService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by laiweigeng on 2018/8/14.
 */
public class RpcInvocationHandler implements InvocationHandler {
    private String serverAddress;
    private IDiscoveryService discoveryService;
    public RpcInvocationHandler(IDiscoveryService discoveryService){
        this.discoveryService = discoveryService;
    }
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        //拿到声明这个方法的业务接口名称
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
        //查找服务
        if (discoveryService != null) {
            serverAddress = discoveryService.discover();
        }
        //随机获取服务的地址
        String[] array = serverAddress.split(":");
        String host = array[0];
        int port = Integer.parseInt(array[1]);
        //创建Netty实现的RpcClient，链接服务端
        RpcClient client = new RpcClient(host, port);
        //通过netty向服务端发送请求
        RpcResponse response = client.send(request);
        //返回信息
        if (response.isError()) {
            throw response.getError();
        } else {
            return response.getResult();
        }
    }
}
