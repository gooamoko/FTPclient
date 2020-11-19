package ru.gooamoko.ftpclient.asynctask;

import android.os.AsyncTask;

import ru.gooamoko.ftpclient.FtpClient;
import ru.gooamoko.ftpclient.model.ConnectionParamsModel;

public abstract class FtpClientTask extends AsyncTask<String, Integer, String> {
    private FtpClientTaskCallback callback;
    protected ConnectionParamsModel connectionParams;

    public FtpClientTask(ConnectionParamsModel paramsModel, FtpClientTaskCallback callback) {
        this.connectionParams = paramsModel;
        this.callback = callback;
    }

    protected FtpClient getClient() {
        return new FtpClient(connectionParams);
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
