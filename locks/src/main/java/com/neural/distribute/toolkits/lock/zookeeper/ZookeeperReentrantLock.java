package com.neural.distribute.toolkits.lock.zookeeper;

import com.neural.distribute.toolkits.lock.DistributeLock;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryForever;
import org.springframework.beans.factory.annotation.Value;
import sun.jvm.hotspot.debugger.proc.arm.ProcARMThread;

import java.util.concurrent.TimeUnit;

/**
 * 基于Zookeeper的可重入锁
 * Created by xavier on 2017/3/1.
 */
public class ZookeeperReentrantLock implements DistributeLock{

    /**
     * Zookeeper的根节点
     */
    private static final String ROOT_PATH = "/ROOT_LOCK/";

    final private CuratorFramework client;

    final InterProcessMutex lock;

    @Value("${zookeeper.interval.period}")
    private String intervalPeriod;

    public ZookeeperReentrantLock(String zkAddress) {
        RetryPolicy retryPolicy = new RetryForever(1000);
        client = CuratorFrameworkFactory.newClient(zkAddress, retryPolicy);
        lock = new InterProcessMutex(client, ROOT_PATH);
    }

    /**
     * 尝试在timeout时间内获取锁
     *
     * @param timeOut  - 超时时间
     * @param timeUnit - 时间单位
     * @return - 获取锁结果
     */
    @Override
    public boolean tryLock(Long timeOut, TimeUnit timeUnit) {

        return false;
    }

    /**
     * 释放锁
     */
    @Override
    public void unlock() {

    }
}
