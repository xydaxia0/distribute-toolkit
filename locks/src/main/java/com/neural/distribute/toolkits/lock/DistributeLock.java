package com.neural.distribute.toolkits.lock;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁接口
 * Created by xavier on 2017/3/1.
 */
public interface DistributeLock {

    /**
     * 尝试在timeout时间内获取锁
     * @param timeOut - 超时时间
     * @param timeUnit - 时间单位
     * @return - 获取锁结果
     */
    boolean tryLock(Long timeOut, TimeUnit timeUnit);

    /**
     * 释放锁
     */
    void unlock();

}
