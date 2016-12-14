package com.sms.mcube.smstrack;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.sms.mcube.smstrack.syncadapter.SyncUtils;


/**
 * Created by mukesh on 14/3/16.
 */
public class MyApplication extends Application {

    private static MDatabase mdatabase;
    //  private static MDatabase mDatabase;
    public String gcmKey;
    public static final String TAG = "VolleyPatterns";
    private RequestQueue mRequestQueue;
    private static MyApplication sInstance;

    public static Context getAplicationContext() {
        return sInstance.getApplicationContext();
    }
    
    
    
    
    public synchronized static MDatabase getWritabledatabase() {
        if (mdatabase == null) {
            mdatabase = new MDatabase(getAplicationContext());
        }
        return mdatabase;
    }

    /**
     * Log or request TAG
     */


    @Override
    public void onCreate() {
        super.onCreate();

        boolean sync = ContentResolver.getMasterSyncAutomatically();
        if (!sync)
            ContentResolver.setMasterSyncAutomatically(true);
        SyncUtils.CreateSyncAccount(getBaseContext());
        // initialize the singleton
        sInstance = this;

    }
    public static synchronized MyApplication getInstance() {
        return sInstance;
    }

      public RequestQueue getRequestQueue() {
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

        VolleyLog.d("Adding request to queue: %s", req.getUrl());

        getRequestQueue().add(req);
    }


    public <T> void addToRequestQueue(Request<T> req) {
        // set the default tag if tag is empty
        req.setTag(TAG);

        getRequestQueue().add(req);
    }

    /**
     * Cancels all pending requests by the specified TAG, it is important
     * to specify a TAG so that the pending/ongoing requests can be cancelled.
     *
     * @param tag
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

}
