package com.lai.rpc.framework.service.impl;


import com.lai.rpc.framework.Utils.ZookeeperUtil;
import com.lai.rpc.framework.common.Constant;
import com.lai.rpc.framework.service.IRegistryService;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * Created by laiweigeng on 2018/8/13.
 * zk注册服务
 */
public class ZookeeperRegistryService implements IRegistryService {
    private  static final Logger logger = LoggerFactory.getLogger(ZookeeperRegistryService.class);

    private CountDownLatch latch = new CountDownLatch(1);

    private String registryAddress;

    public ZookeeperRegistryService(String registryAddress) {
        /**zookeeper的地址**/
        this.registryAddress = registryAddress;
    }

    /**
     * 创建zk链接
     * @param data
     */
    public void register(String data) {
        try {
            if (data != null) {
                ZooKeeper zk = ZookeeperUtil.connectServer(registryAddress,Constant.ZK_SESSION_TIMEOUT,latch);
                if (zk != null) {
                    createNode(zk, data);
                }
            }
        }catch (Exception e){
            logger.error("ZookeeperRegistryService register exception", e);
        }

    }


    /**
     * 创建节点
     * @param zk
     * @param data
     */
    private void createNode(ZooKeeper zk, String data) {
        try {
            byte[] bytes = data.getBytes();
            if (zk.exists(Constant.ZK_REGISTRY_PATH, null) == null) {
                zk.create(Constant.ZK_REGISTRY_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                        CreateMode.PERSISTENT);
            }

            String path = zk.create(Constant.ZK_DATA_PATH, bytes,
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            logger.debug("create zookeeper node path:{},data:{}", path, data);
        } catch (Exception e) {
            logger.error("ZookeeperRegistryService createNode exception", e);
        }
    }
}
