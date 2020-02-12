package ru.gooamoko.ftpclient.asynctask;

import ru.gooamoko.ftpclient.FtpClient;

public class ConnectionCheckTask extends FtpClientTask {
    private String successMessage;
    private String errorMessage;

    public ConnectionCheckTask(FtpClientTaskCallback callback, String successMessage, String errorMessage) {
        super(callback);
        this.successMessage = successMessage;
        this.errorMessage = errorMessage;
    }

    @Override
    protected String doInBackground(String... arg) {
        String host = arg[0];
        String port = arg[1];
        String user = arg[2];
        String password = arg[3];

        String message;
        try {
            FtpClient client = new FtpClient(host, port, user, password);
            client.open();
            client.close();

            message = String.format(successMessage, host, user);
        } catch (Exception e) {
            // TODO: 13.02.20 Выводить хост, порт логин и ошибку только при неудачном подключении
            message = String.format(errorMessage, host, user);
        }
        return message;
    }
}
