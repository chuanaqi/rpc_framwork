package com.lai.rpc.framework.server;

import com.lai.rpc.framework.common.RpcDecoder;
import com.lai.rpc.framework.common.RpcEncoder;
import com.lai.rpc.framework.common.RpcRequest;
import com.lai.rpc.framework.common.RpcResponse;
import com.lai.rpc.framework.service.IRegistryService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by laiweigeng on 2018/8/14.
 */
public class RpcServer implements ApplicationContextAware, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);
    private String serverAddress;
    private IRegistryService registryService;

    private Map<String, Object> handlerMap = new HashMap<String, Object>();

    public RpcServer(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    /**服务器绑定的地址和端口由spring在构造本类时从配置文件中传入**/
    public RpcServer(String serverAddress, IRegistryService registryService) {
        this.serverAddress = serverAddress;
        this.registryService = registryService;
    }

    /**
     * 通过注解，获取标注了RpcService的业务类的接口及实现类，放入handlerMap
     * @param applicationContext
     * @throws BeansException
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if(MapUtils.isNotEmpty(serviceBeanMap)){

            for (Object serviceBean : serviceBeanMap.values()){

                String interfaceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
                handlerMap.put(interfaceName,serviceBean);
            }
        }
    }

    /**
     * 启动netty
     * @throws Exception
     */
    public void afterPropertiesSet() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel)
                                throws Exception {
                            channel.pipeline()
                                    .addLast(new RpcDecoder(RpcRequest.class))// 注册解码 IN-1
                                    .addLast(new RpcEncoder(RpcResponse.class))// 注册编码 OUT
                                    .addLast(new RpcHandler(handlerMap));//注册RpcHandler IN-2
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);


            String[] array = serverAddress.split(":");
            String host = array[0];
            int port = Integer.parseInt(array[1]);

            ChannelFuture future = bootstrap.bind(host, port).sync();
            logger.debug("rpc server started on port {}", port);

            if (registryService != null) {
                registryService.register(serverAddress);
            }

            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}
