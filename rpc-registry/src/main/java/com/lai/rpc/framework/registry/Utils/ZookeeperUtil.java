package com.lai.rpc.framework.registry.Utils;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * Created by laiweigeng on 2018/8/13.
 */
public class ZookeeperUtil {

    /**
     * 创建zookeeper链接，监听
     * @return
     */
    public static ZooKeeper connectServer(String registryAddress,int timeout,final CountDownLatch latch) throws Exception {
        ZooKeeper zk = null;

        zk = new ZooKeeper(registryAddress, timeout,
                new Watcher() {
                    public void process(WatchedEvent event) {
                        if (event.getState() == Event.KeeperState.SyncConnected) {
                            latch.countDown();
                        }
                    }
                });
        latch.await();

        return zk;
    }
}
