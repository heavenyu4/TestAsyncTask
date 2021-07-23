package com.pwrd.onesdk.task;

import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class OneTaskExecutor implements Executor {
    private String TAG = "OneTaskExecutor";
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
//    private static final int CORE_POOL_SIZE = 1;

    private static final int KEEP_ALIVE_SECONDS = 30;
    private static final int MAXIMUM_POOL_SIZE = ((CPU_COUNT * 2) + 1);
    private static final Executor THREAD_POOL_EXECUTOR;
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "OneTaskExecutor #" + this.mCount.getAndIncrement());
        }
    };

    public OneTaskExecutor(){
        Log.d(TAG, "OneTaskExecutor: CPU_COUNT: " + CPU_COUNT + " CORE_POOL_SIZE: " + CORE_POOL_SIZE + " MAXIMUM_POOL_SIZE： " + MAXIMUM_POOL_SIZE);
        Log.d(TAG, "OneTaskExecutor: " + this);
    }

    public static void printStackInfo(Class<?> clazz, Exception exception){
        String TAG = "OneTaskExecutor";
        if (clazz == null){
            return;
        }
        Log.d("OneTaskExecutor", "printStackInfo: class: " + clazz.getName());

        //调用者上级类名
        Log.i(TAG, "printStackInfo: Class0———>：" + exception.getStackTrace()[0].getClassName());
        //调用者上级的上级类名
        Log.i(TAG, "printStackInfo: Class1———>：" + exception.getStackTrace()[1].getClassName());
        //调用者上级的方法名
        Log.i(TAG, "printStackInfo: MethodName0———>：" + exception.getStackTrace()[0].getMethodName());
        //调用者上级的上级方法名
        Log.i(TAG, "printStackInfo: MethodName1———>：" + exception.getStackTrace()[1].getMethodName());
        //当前方法行号
        Log.i(TAG, "printStackInfo: line0———>：" + exception.getStackTrace()[0].getLineNumber());
        //调用方法行号
        Log.i(TAG, "printStackInfo: line1———>：" + exception.getStackTrace()[1].getLineNumber());

    }

    Runnable mActive;
    private final ArrayDeque<Runnable> mTasks = new ArrayDeque<>();

    static {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS,
                TimeUnit.SECONDS, new LinkedBlockingQueue(128), sThreadFactory);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        THREAD_POOL_EXECUTOR = threadPoolExecutor;
    }

    @Override
    public synchronized void execute(final Runnable r) {
        Log.d(TAG, "execute: ");
        this.mTasks.offer(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "run: ");
                    r.run();
                } finally {
                    OneTaskExecutor.this.scheduleNext();
                }
            }
        });
        if (this.mActive == null) {
            scheduleNext();
        }
    }

    public synchronized void scheduleNext() {
        Log.d(TAG, "scheduleNext: ");
        Runnable poll = this.mTasks.poll();
        this.mActive = poll;
        if (poll != null) {
            THREAD_POOL_EXECUTOR.execute(this.mActive);
        }
    }
}