package com.portauthority.async;

import android.util.Log;

import com.portauthority.db.Database;
import com.portauthority.parser.Parser;
import com.portauthority.response.MainAsyncResponse;

import java.lang.ref.WeakReference;

public class DownloadPortDataAsyncTask extends DownloadAsyncTask {

    private static final String SERVICE = "https://www.iana.org/assignments/service-names-port-numbers/service-names-port-numbers.csv";

    /**
     * Creates a new asynchronous task that takes care of downloading port data.
     *
     * @param database
     * @param parser
     * @param activity
     */
    public DownloadPortDataAsyncTask(Database database, Parser parser, MainAsyncResponse activity) {
        super("");
        db = database;
        delegate = new WeakReference<>(activity);
        this.parser = parser;
        init();
    }

    @Override
    public void run() {
        super.run();
        Log.i("Log", "DownloadPortDataAsyncTask run: ");
        db.clearPorts();
        doInBackground(SERVICE, parser);
    }


}
