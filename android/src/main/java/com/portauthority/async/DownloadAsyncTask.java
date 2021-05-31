package com.portauthority.async;

import android.util.Log;

import com.portauthority.db.Database;
import com.portauthority.parser.Parser;
import com.portauthority.response.MainAsyncResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;

public abstract class DownloadAsyncTask extends Thread {

    protected Database db;
    protected WeakReference<MainAsyncResponse> delegate;
    MainAsyncResponse activity;
    Parser parser;
    private String TAG = "Log";

    public DownloadAsyncTask(String name) {
        super(name);

    }

    public void init(){
       activity  = delegate.get();
    }

    /**
     * Downloads and parses data based on the service URL and parser.
     *
     * @param service
     * @param parser
     */
    final void doInBackground(String service, Parser parser) {
        Log.i("Log", "database not exist download start: ");
        BufferedReader in = null;
        HttpsURLConnection connection = null;
        db.beginTransaction();
        try {
            URL url = new URL(service);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty("Accept-Encoding", "gzip");
            connection.connect();
            if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                Log.i(TAG, "failed to connect: "+connection.getResponseMessage());
                db.setIsDbUpdatedSuccessfully(false);
                return;
            }

            in = new BufferedReader(new InputStreamReader(new GZIPInputStream(connection.getInputStream()), "UTF-8"));
            String line;

            while ((line = in.readLine()) != null) {
                String[] data = parser.parseLine(line);
                if (data == null) {
                    continue;
                }

                if (parser.saveLine(db, data) == -1) {
                    Log.i(TAG, "Failed to insert data into the database. Please run this operation again");
                    db.setIsDbUpdatedSuccessfully(false);
                    return;
                }
            }
            db.setTransactionSuccessful();
            db.setIsDbUpdatedSuccessfully(true);

        } catch (Exception e) {
            db.setIsDbUpdatedSuccessfully(false);
            Log.i(TAG, "doInBackground: exception");
        } finally {
            db.endTransaction();
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ignored) {
            }

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

}
