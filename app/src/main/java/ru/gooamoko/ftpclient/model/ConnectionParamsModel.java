package ru.gooamoko.ftpclient.model;

import android.content.SharedPreferences;

import ru.gooamoko.ftpclient.FtpClient;

public class ConnectionParamsModel {
    private static final int DEFAULT_FTP_PORT = 21;
    private String host;
    private int port;
    private String user;
    private String password;

    public ConnectionParamsModel(SharedPreferences preferences) {
        host = preferences.getString(FtpClient.HOST, "");
        user = preferences.getString(FtpClient.USER, "");
        password = preferences.getString(FtpClient.PASSWORD, "");
        String portValue = preferences.getString(FtpClient.PORT, "21");
        try {
            port = Integer.parseInt(portValue);
        } catch (NumberFormatException e) {
            port = DEFAULT_FTP_PORT;
        }
    }

    public ConnectionParamsModel(String host, String port, String user, String password) {
        this.host = host;
        this.user = user;
        this.password = password;
        try {
            this.port = Integer.parseInt(port);
        } catch (NumberFormatException e) {
            this.port = 21;
        }
    }


    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }


    public boolean isError() {
        return isBlank(user) || isBlank(host);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
