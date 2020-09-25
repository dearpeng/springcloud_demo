package com.alimama.springcloud_demo;

import java.util.concurrent.*;

/**
 * Created by PengWX on 2020/8/26.
 */
public class Test {

    public static void main(String[] args) {
        Integer size = 5;
        ExecutorService executor = new ThreadPoolExecutor(size, 10, 1, TimeUnit.DAYS, new LinkedBlockingQueue<Runnable>(1024), new ThreadPoolExecutor.CallerRunsPolicy());
        final Semaphore sp = new Semaphore(1);//创建Semaphore信号量，初始化许可大小为3
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
            Runnable runnable = new Runnable() {
                public void run() {
                    try {
                        sp.acquire();//请求获得许可，如果有可获得的许可则继续往下执行，许可数减1。否则进入阻塞状态
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    System.out.println("线程" + Thread.currentThread().getName() + "进入，当前已有" + (size - sp.availablePermits()) + "个并发");
                    try {
                        Thread.sleep((long) (Math.random() * 10000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("线程" + Thread.currentThread().getName() +"即将离开");
                    sp.release();//释放许可，许可数加1
                    //下面代码有时候执行不准确，因为其没有和上面的代码合成原子单元
                    System.out.println("线程" + Thread.currentThread().getName() + "已离开，当前已有" + (size - sp.availablePermits()) + "个并发");
                }
            };
            executor.execute(runnable);
        }
    }
}
