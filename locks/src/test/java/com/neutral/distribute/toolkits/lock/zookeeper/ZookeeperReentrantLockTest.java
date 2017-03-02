package com.neutral.distribute.toolkits.lock.zookeeper;

/**
 * Created by xavier on 2017/3/1.
 */

import com.neural.distribute.toolkits.lock.DistributeLock;
import com.neural.distribute.toolkits.lock.zookeeper.ZookeeperReentrantLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryForever;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
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

    private static final String zkAddress = "10.185.31.4:2181,10.185.31.4:2182,10.185.31.4:2183";


    private CountDownLatch start = new CountDownLatch(1);
    private CountDownLatch end = new CountDownLatch(10);
    RetryPolicy retryPolicy = new RetryForever(1000);
    CuratorFramework client = CuratorFrameworkFactory.newClient(zkAddress, retryPolicy);

    @Test
    public void testLock() throws InterruptedException {
        client.start();
        IntStream.rangeClosed(1, 10).forEach(i -> CompletableFuture.supplyAsync(this::doSomthing, threadPool));
        start.countDown();
        end.await();
    }

    private boolean doSomthing() {
        try {
            start.await();
            log.info(Thread.currentThread() + "try to get lock");


            DistributeLock distributeLock = new ZookeeperReentrantLock(client, "paymentTest");
            boolean locked = distributeLock.tryLock(1L, TimeUnit.SECONDS);
            if (locked) {
                log.info(Thread.currentThread() + "received the lock");
                Thread.sleep(1000);

                log.info(Thread.currentThread() + "try to release lock");
                distributeLock.unlock();
                log.info(Thread.currentThread() + "unlocked the lock");
                end.countDown();
            } else {
                log.info("TIME OUT!");
                end.countDown();
                return false;
            }
        } catch (Exception e) {
            log.error("EROOR ",  e);
            end.countDown();
            return false;
        }
        return true;
    }

}
