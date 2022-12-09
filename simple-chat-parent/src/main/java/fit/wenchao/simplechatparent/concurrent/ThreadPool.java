package fit.wenchao.simplechatparent.concurrent;

import java.util.concurrent.*;

public class ThreadPool {
    //private static final int poolSize = Runtime.getRuntime().availableProcessors() * 2;
    private static final int poolSize = Runtime.getRuntime().availableProcessors() * 4;
    //private static final int poolSize = Integer.MAX_VALUE;

    private static final int poolBlockingSize = 30;

    private static final int corePoolSize = poolSize;
    private static final int maxPoolSize = poolSize;
    private static final int keepAliveTime = 3;

    private static final ThreadPool singleton = new ThreadPool();

    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            corePoolSize,
            maxPoolSize,
            keepAliveTime,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(poolBlockingSize),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy()
    );

    public static ThreadPool getSingleton() {
        return singleton;
    }

    public void shutdown() {
        threadPool.shutdown();
    }

    public void shutdownNow() {
        threadPool.shutdownNow();
    }

    public void reStart() {
        this.shutdownNow();
        threadPool = new ThreadPoolExecutor( corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(poolBlockingSize),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    public void submit(Runnable task) {
        RejectedExecutionException rejected = null;
        while (true) {
            try {
                threadPool.execute(task);
                rejected = null;
            } catch (RejectedExecutionException e) {
                rejected = e;
            }
            if (rejected == null) {
                break;
            }
        }
    }


    public static void main(String[] args) {
        System.out.println(Runtime.getRuntime().availableProcessors());
        //ThreadPool singleton = getSingleton();
        //
        //for (int i = 0; i < 10; i++) {
        //    singleton.submit(() -> {
        //        System.out.println("hello");
        //        try {
        //            Thread.sleep(100);
        //        } catch (InterruptedException e) {
        //            e.printStackTrace();
        //        }
        //    });
        //}
        //
        //singleton.shutdown();
        //System.out.println("shutdown");
    }
}