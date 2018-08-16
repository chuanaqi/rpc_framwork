package com.lai.rpc.framework.server;

import com.lai.rpc.framework.registry.common.RpcRequest;
import com.lai.rpc.framework.registry.common.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by laiweigeng on 2018/8/14.
 */
public class RpcHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(RpcHandler.class);

    private final Map<String, Object> handlerMap;

    public RpcHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    /**
     * 接收消息，处理消息，返回结果
     */
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest)
            throws Exception {
        RpcResponse response = new RpcResponse();
        response.setRequestId(rpcRequest.getRequestId());
        try {
            //根据request来处理具体的业务调用
            Object result = handle(rpcRequest);
            response.setResult(result);
        } catch (Throwable t) {
            response.setError(t);
        }
        //写入 outbundle（即RpcEncoder）进行下一步处理（即编码）后发送到channel中给客户端
        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

    }

    /**
     * 根据request来处理具体的业务调用
     * 调用通过反射完成
     * @param request
     * @return
     * @throws Throwable
     */
    private Object handle(RpcRequest request) throws Throwable {
        String className = request.getClassName();

        Object serviceBean = handlerMap.get(className);

        Class<?>[] parameterTypes = request.getParameterTypes();

        String methodName = request.getMethodName();
        Object[] parameters = request.getParameters();
        Class<?> forName = Class.forName(className);
        Method method = forName.getMethod(methodName, parameterTypes);

        return method.invoke(serviceBean, parameters);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) {
        logger.error("server caught exception", cause);
        channelHandlerContext.close();
    }
}
