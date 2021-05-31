
package com.portauthority;

import android.util.Log;
import android.content.Context;


import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.portauthority.async.DownloadAsyncTask;
import com.portauthority.async.DownloadOuisAsyncTask;
import com.portauthority.async.GetDeviceDetailsAsync;
import com.portauthority.db.Database;
import com.portauthority.network.Host;
import com.portauthority.network.Wireless;
import com.portauthority.parser.OuiParser;
import com.portauthority.response.MainAsyncResponse;
import com.portauthority.Strings;

public class RNPortAuthModule extends ReactContextBaseJavaModule implements MainAsyncResponse {

    private String TAG = "PortAuthority";
    private Promise promise;
    private List<Host> hosts = Collections.synchronizedList(new ArrayList<>());
    private Wireless wifi;
    private WeakReference<MainAsyncResponse> delegate;
    private Database db;
    private DownloadAsyncTask ouiTask;
    private Context appContext;

    public PortAuthority(ReactApplicationContext reactContext) {
        super(reactContext);
        appContext=reactContext.getApplicationContext();
    }

    @Override
    public String getName() {
        return TAG;
    }

    @ReactMethod
    public void fetchNetworkDevices(final Promise promise) {
        this.promise = promise;
        delegate = new WeakReference<>(this);
        wifi = new Wireless(appContext);
        hosts.clear();
        try {
            if (!wifi.isEnabled()) {
                promise.reject("500", Strings.wifiDisabled, new Throwable());
                return;
            }

            if (!wifi.isConnectedWifi()) {
                promise.reject("500", Strings.notConnectedWifi, new Throwable());
                return;
            }
            checkDatabase();

        } catch (Exception e) {
            e.printStackTrace();
            promise.reject("500", "Fetch Exception", e.getCause());
        }
    }

    public void checkDatabase() {
        db = Database.getInstance(appContext);
        if (db.isIsDbUpdatedSuccessfully()) {
            Log.i(TAG, "database already updated : ");
            setupMac();
            return;
        }
        Log.i(TAG, "database does not exist : ");
        ouiTask = new DownloadOuisAsyncTask(db, new OuiParser(), this);
        ouiTask.start();
    }


    @Override
    public void processFinish(Host item, AtomicInteger i) {
        hosts.add(item);
    }


    @Override
    public void processFinish(boolean output) {
        Log.i(TAG, "processFinish: with int output" + output);
        try {
            Thread.sleep(3000);
            onSuccess();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public <T extends Throwable> void processFinish(T output) {
        promise.reject("500", "Process exit with exception", output);
    }

    public void onSuccess() {

        WritableArray arr = Arguments.createArray();
        Iterator it = hosts.iterator();
        while (it.hasNext()){
            Host item = (Host) it.next();
            WritableMap newResponse = Arguments.createMap();
            newResponse.putString("hostname", item.getHostname());
            newResponse.putString("name", item.getHostname());
            newResponse.putString("ip", item.getIp());
            newResponse.putString("mac", item.getMac());
            newResponse.putString("make", item.getVendor());
            arr.pushMap(newResponse);
        }

        Log.i(TAG, "onSuccess: "+arr.toString()+"   "+hosts.size());
        promise.resolve(arr);
    }

    public void setupMac() {
        try {
            if (!wifi.isEnabled()) {
                return;
            }
            Log.i(TAG, "setupMac: vendor ");
            new GetDeviceDetailsAsync(this, appContext).start();
        } catch (Exception e) {
            e.printStackTrace();
            promise.reject("500", "Process exit with exception", e.getCause());
        }
    }


}