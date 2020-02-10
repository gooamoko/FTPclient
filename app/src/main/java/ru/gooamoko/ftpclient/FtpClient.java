package ru.gooamoko.ftpclient;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;

public class FtpClient {
    private String server;
    private int port = 21;
    private String user;
    private String password;
    private FTPClient ftp;

    public FtpClient(String server, String user, String password) {
        this.server = server;
        this.user = user;
        this.password = password;
    }

    void open() {
        try {
            ftp = new FTPClient();

            ftp.addProtocolCommandListener(new CustomProtocolCommandListener());

            ftp.connect(server, port);
            int reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                throw new FtpException("Can't connect to FTP server");
            }

            ftp.login(user, password);
        } catch (IOException e) {
            throw new FtpException(e.getMessage(), e);
        }
    }

    void close() throws IOException {
        ftp.disconnect();
    }
}
