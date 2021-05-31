package com.portauthority.async;

import android.util.Log;

import com.portauthority.db.Database;
import com.portauthority.parser.Parser;
import com.portauthority.response.MainAsyncResponse;

import java.lang.ref.WeakReference;

public class DownloadOuisAsyncTask extends DownloadAsyncTask {

    private static final String SERVICE = "https://code.wireshark.org/review/gitweb?p=wireshark.git;a=blob_plain;f=manuf";

    /**
     * Creates a new asynchronous task to handle downloading OUI data.
     *
     * @param database
     * @param parser
     * @param activity
     */
    public DownloadOuisAsyncTask(Database database, Parser parser, MainAsyncResponse activity) {
        super("DownloadOuisAsyncTask");
        db = database;
        delegate = new WeakReference<>(activity);
        this.parser = parser;
        init();
    }

    @Override
    public void run() {
        super.run();
        db.clearOuis();
        doInBackground(SERVICE, parser);
        onPostExecute();
    }

    private void onPostExecute() {
        Log.i("Log", "post DB Update: "+activity);
        if (activity != null) {
            activity.setupMac();
        }
    }

}