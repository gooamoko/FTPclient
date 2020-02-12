package ru.gooamoko.ftpclient.asynctask;

import android.os.AsyncTask;

public abstract class FtpClientTask extends AsyncTask<String, Integer, String> {
    private FtpClientTaskCallback callback;

    public FtpClientTask(FtpClientTaskCallback callback) {
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(String message) {
        super.onPostExecute(message);
        if (callback != null) {
            callback.onFinishTask(message);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (callback != null) {
            callback.onFinishTask("Task cancelled");
        }
    }

}
