package com.myaps.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mgajewski on 2017-05-02.
 */

@EBean(scope = EBean.Scope.Singleton)
public class ConnectivityHelper {

    public interface OnConnectionStatusChanged {
        void connectionStatusChanged(boolean isConnected);
    }

    @RootContext
    Context context;

    private static final Object syncRoot = new Object();

    private OnConnectionStatusChanged statusListener;
    ConnectivityManager connectionManager;
    private Timer repeater;

    private boolean isConnected = true;

    ConnectivityHelper(Context ctx) {
        connectionManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public void init(OnConnectionStatusChanged listener) {
        statusListener = listener;
        check();
        repeater = new Timer();
        repeater.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                check();
            }
        }, 1000, 1000);
    }

    public void dispose() {
        statusListener = null;
        repeater.cancel();
    }

    public boolean isConnected() {
        synchronized (syncRoot) {
            return isConnected;
        }
    }

    private void check() {
        boolean oldValue = isConnected;
        NetworkInfo netInfo = connectionManager.getActiveNetworkInfo();
        synchronized (syncRoot) {
            isConnected = netInfo != null && netInfo.isConnected();
        }
        if (oldValue != isConnected) {
            dispatchStatus(isConnected);
        }
    }

    @UiThread
    void dispatchStatus(boolean status) {
        if (statusListener != null) {
            statusListener.connectionStatusChanged(status);
        }
    }
}
