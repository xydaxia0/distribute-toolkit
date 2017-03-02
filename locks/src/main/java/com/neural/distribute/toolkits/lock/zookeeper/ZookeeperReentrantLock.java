package com.neural.distribute.toolkits.lock.zookeeper;

import com.neural.distribute.toolkits.lock.DistributeLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryForever;
import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 基于Zookeeper的可重入锁
 * Created by xavier on 2017/3/1.
 */
@Slf4j
public class ZookeeperReentrantLock implements DistributeLock{

    /**
     * Zookeeper的根节点
     */
    private static final String ROOT_PATH = "/ROOT_LOCK/";

    private final InterProcessMutex lock;

    private final int intervalPeriod = 1000;

    public ZookeeperReentrantLock(CuratorFramework client, String applicationName) {
        lock = new InterProcessMutex(client, ROOT_PATH + applicationName);
    }

    /**
     * 尝试在timeout时间内获取锁
     *
     * @param timeOut  - 超时时间
     * @param timeUnit - 时间单位
     * @return - 获取锁结果
     */
    @Override
    public boolean tryLock(Long timeOut, TimeUnit timeUnit) throws Exception {
        try {
            log.info("lock start");
            return lock.acquire(timeOut, timeUnit);
        } catch (Exception e) {
            log.error("get zookeeper distribute lock error!", e);
            if (lock.isAcquiredInThisProcess()) lock.release();
            throw e;
        }
    }

    /**
     * 释放锁
     */
    @Override
    public void unlock() throws Exception {
        try {
            log.info("start to release lock");
            lock.release();
        } catch (Throwable e) {
            log.error("release zookeeper distribute lock error!", e);
            throw e;
        }
    }
}
