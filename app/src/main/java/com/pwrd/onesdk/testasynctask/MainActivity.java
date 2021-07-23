package com.pwrd.onesdk.testasynctask;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pwrd.onesdk.task.OneAsyncTask;
import com.pwrd.onesdk.task.OneTaskExecutor;

import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
//    private TaskExecutor mTaskExecutor = new TaskExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        if (Build.VERSION.SDK_INT >= 28) {
            WindowManager.LayoutParams attributes = getWindow().getAttributes();
            attributes.layoutInDisplayCutoutMode = 1;
            getWindow().setAttributes(attributes);
        }

    }

    private void initView() {
        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAsyncSleep();
            }

        });
        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAsyncToast();
            }


        });

//        setDefaultExecutor
//        try {
//            Class<?> asyncClass = Class.forName(AsyncTask.class.getName());
//            Method setDefaultExecutor = asyncClass.getDeclaredMethod("setDefaultExecutor", Executor.class);
//            setDefaultExecutor.setAccessible(true);
//            setDefaultExecutor.invoke(asyncClass, AsyncTask.THREAD_POOL_EXECUTOR);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }


    private void startAsyncSleep() {
        Log.d(TAG, "startAsyncSleep: ");
        OneTaskExecutor.printStackInfo(getClass(), new Exception());
//        new MyFirstAsyncTask().execute("test sleep");
        new MyFirstAsyncTask().executeNew("test sleep");
        if (Build.VERSION.SDK_INT >= 11) {
            new MyFirstAsyncTask().executeOnExecutor(new OneTaskExecutor(), ">=11");
        } else {
            new MyFirstAsyncTask().execute("<11");
        }


    }

    private void startAsyncToast() {


//        new MySecondAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "to do");
        new MySecondAsyncTask().execute("to do");
//        new MySecondAsyncTask().executeOnExecutor(mTaskExecutor, "to do");
//        new MySecondAsyncTask().executeOnExecutor(new OneTaskExecutor(), "to do");
//        if (Build.VERSION.SDK_INT >= 11) {
            new MyFirstAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ">=11");
//        } else {
//            new MyFirstAsyncTask().execute("<11");
//        }

    }

//    private void startAsyncToastPool() {
//
//        new MySecondAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "to do");
////        new MySecondAsyncTask().execute("to do");
//
//    }

    static class MyFirstAsyncTask extends OneAsyncTask<String, String, String> {
        private String TAG = "MyOneAsyncTask";
        private boolean flag = true;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute: ");
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: " + strings[0]);
            while (flag) {
//                CountDownTimer timer = new CountDownTimer(5_000, 1000) {
//                    @Override
//                    public void onTick(long millisUntilFinished) {
//                        Log.d(TAG, "onTick: " + millisUntilFinished);
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        Log.d(TAG, "onFinish: ");
//                        flag = false;
//                    }
//                };
//                timer.start();
                try {
//                    TimeUnit.SECONDS.sleep(5_000);
                    Thread.sleep(10_000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                flag = false;

            }
            return "test timer";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: " + s);

        }


    }

    class MySecondAsyncTask extends AsyncTask<String, String, String> {
        private String TAG = "MySecondAsyncTask";
        private boolean flag = true;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute: ");
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: " + strings[0]);
            return "test timer second";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: " + s);
            Toast.makeText(MainActivity.this, "show dialog", Toast.LENGTH_SHORT).show();

        }
    }
}