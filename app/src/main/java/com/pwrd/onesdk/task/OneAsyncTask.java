package com.pwrd.onesdk.task;

import android.os.AsyncTask;
import android.util.Log;

public abstract class OneAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result>{
    private String TAG = "OneAsyncTask";

    OneTaskExecutor mTaskExecutor = new OneTaskExecutor();

    public OneAsyncTask() {
        super();
    }

    public AsyncTask<Params, Progress, Result> executeNew(Params... params){
        Log.d(TAG, "executeNew: name: " + getClass().getName() + " params: " + params[0]);
        return executeOnExecutor(mTaskExecutor, params);
    }

    public void executeNew(Runnable runnable){
        Log.d(TAG, "executeNew: name: " + getClass().getName() + " runnable: " + runnable);
        mTaskExecutor.execute(runnable);
    }


}
