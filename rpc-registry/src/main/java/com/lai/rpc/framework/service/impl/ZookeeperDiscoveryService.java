package com.lai.rpc.framework.service.impl;

import com.lai.rpc.framework.Utils.ZookeeperUtil;
import com.lai.rpc.framework.common.Constant;
import com.lai.rpc.framework.service.IDiscoveryService;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by laiweigeng on 2018/8/13.
 */
public class ZookeeperDiscoveryService implements IDiscoveryService {

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperDiscoveryService.class);

    private CountDownLatch latch = new CountDownLatch(1);

    private volatile List<String> dataList = new ArrayList<String>();

    private String registryAddress;

    public ZookeeperDiscoveryService(String registryAddress) {
        this.registryAddress = registryAddress;

        try {
            ZooKeeper zk = ZookeeperUtil.connectServer(registryAddress,Constant.ZK_SESSION_TIMEOUT,latch);
            if (zk != null) {
                watchNode(zk);
            }
        }catch (Exception e){
            logger.error("ZookeeperDiscoveryService ZookeeperDiscoveryService exception", e);
        }

    }
    public String discover() {
        String data = null;
        int size = dataList.size();
        /**存在新节点，随机选取使用**/
        if (size > 0) {
            if (size == 1) {
                data = dataList.get(0);
                logger.debug("ZookeeperDiscoveryService using only data: {}", data);
            } else {
                data = dataList.get(ThreadLocalRandom.current().nextInt(size));
                logger.debug("ZookeeperDiscoveryService using random data: {}", data);
            }
        }
        return data;
    }


    /**
     * 监控节点
     * @param zk
     */

    private void watchNode(final ZooKeeper zk) {
        try {
            /**获取所有子节点**/
            List<String> nodeList = zk.getChildren(Constant.ZK_REGISTRY_PATH,
                    new Watcher() {
                        public void process(WatchedEvent event) {
                            /**节点改变**/
                            if (event.getType() == Event.EventType.NodeChildrenChanged) {
                                watchNode(zk);
                            }
                        }
                    });
            List<String> dataList = new ArrayList<String>();
            for (String node : nodeList) {
                byte[] bytes = zk.getData(Constant.ZK_REGISTRY_PATH + "/" + node, false, null);
                dataList.add(new String(bytes));
            }
            logger.debug("zookeeper node dataList: {}", dataList);
            this.dataList = dataList;
        } catch (Exception e) {
            logger.error("ZookeeperDiscoveryService watchNode exception", e);
        }
    }
}
