package com.lai.rpc.framework.client;

import com.lai.rpc.framework.registry.service.IDiscoveryService;

import java.lang.reflect.Proxy;

/**
 * Created by laiweigeng on 2018/8/14.
 */
public class RpcProxy {
    private IDiscoveryService discoveryService;


    public RpcProxy(IDiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
    }

    public <T> T create(Class<?> interfaceClass) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] {interfaceClass}, new RpcInvocationHandler(discoveryService));
    }

}
