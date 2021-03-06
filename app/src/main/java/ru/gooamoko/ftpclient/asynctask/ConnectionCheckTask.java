package ru.gooamoko.ftpclient.asynctask;

import ru.gooamoko.ftpclient.FtpClient;
import ru.gooamoko.ftpclient.model.ConnectionParamsModel;

public class ConnectionCheckTask extends FtpClientTask {

    public ConnectionCheckTask(ConnectionParamsModel paramsModel, FtpClientTaskCallback callback) {
        super(paramsModel, callback);
    }

    @Override
    protected String doInBackground(String... arg) {
        try {
            FtpClient client = getClient();
            client.open();
            client.close();
            return FtpClient.SUCCESS;
        } catch (Exception e) {
            return FtpClient.ERROR;
        }
    }
}
