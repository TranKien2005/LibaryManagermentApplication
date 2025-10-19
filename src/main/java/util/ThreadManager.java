package util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

public class ThreadManager {
    private static ExecutorService generalExecutorService;
    private static ExecutorService sqlExecutorService;

    private ThreadManager() {}

    public static void execute(Runnable task) {
        if (generalExecutorService == null || generalExecutorService.isShutdown()) {
            generalExecutorService = Executors.newFixedThreadPool(5, daemonFactory("general-pool-"));
        }
        generalExecutorService.execute(task);
        
    }

    public static Future<?> submitSqlTask(Runnable task) {
        if (sqlExecutorService == null || sqlExecutorService.isShutdown()) {
            sqlExecutorService = Executors.newSingleThreadExecutor(daemonFactory("sql-pool-"));
        }
        return sqlExecutorService.submit(task);
    }

    public static void shutdown() {
        if (generalExecutorService != null) {
            generalExecutorService.shutdown();
        }
        if (sqlExecutorService != null) {
            sqlExecutorService.shutdown();
        }
    }

    private static ThreadFactory daemonFactory(String prefix) {
        return new ThreadFactory() {
            private int count = 0;
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, prefix + (++count));
                t.setDaemon(true);
                return t;
            }
        };
    }
}