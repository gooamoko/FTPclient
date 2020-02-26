package ru.gooamoko.ftpclient.asynctask;

import ru.gooamoko.ftpclient.FtpClient;

public class ConnectionCheckTask extends FtpClientTask {

    public ConnectionCheckTask(FtpClientTaskCallback callback) {
        super(callback);
    }

    @Override
    protected String doInBackground(String... arg) {
        try {
            String host = arg[0];
            String port = arg[1];
            String user = arg[2];
            String password = arg[3];

            FtpClient client = new FtpClient(host, port, user, password);
            client.open();
            client.close();

            return FtpClient.SUCCESS;
        } catch (Exception e) {
            return FtpClient.ERROR;
        }
    }
}
