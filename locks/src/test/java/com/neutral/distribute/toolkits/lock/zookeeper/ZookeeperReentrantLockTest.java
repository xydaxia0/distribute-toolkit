package com.neutral.distribute.toolkits.lock.zookeeper;

/**
 * Created by xavier on 2017/3/1.
 */

import com.neural.distribute.toolkits.lock.DistributeLock;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Created by xavier on 2017/1/17.
 */
@RunWith(SpringJUnit4ClassRunner.class) // 整合
@ContextConfiguration(locations="classpath:applicationContext.xml")
@Slf4j
public class ZookeeperReentrantLockTest {

    @Resource
    private ExecutorService threadPool;

    @Resource
    private DistributeLock lock;

    @Test
    public void testLock() throws InterruptedException {
        IntStream.rangeClosed(1, 10).forEach(i -> CompletableFuture.supplyAsync(this::doSomthing, threadPool));
        Thread.sleep(10000L);
    }

    private boolean doSomthing() {
        try {
            System.out.println(Thread.currentThread() + "try to get lock");
            lock.tryLock(1L, TimeUnit.SECONDS);
            System.out.println(Thread.currentThread() + "received the lock");

            Thread.sleep(1000);

            System.out.println(Thread.currentThread() + "try to release lock");
            lock.unlock();
            System.out.println(Thread.currentThread() + "unlocked the lock");
        } catch (Exception e) {
            log.error("EROOR ",  e);
            return false;
        }
        return true;
    }

}
