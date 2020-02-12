package ru.gooamoko.ftpclient;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;

public class FtpClient {
    private final static int DEFAULT_FTP_PORT = 21;
    private String server;
    private int port;
    private String user;
    private String password;
    private FTPClient ftp;

    public FtpClient(String server, String port, String user, String password) {
        this.server = server;
        this.user = user;
        this.password = password;

        try {
            this.port = Integer.parseInt(port);
        } catch (NumberFormatException e) {
            this.port = DEFAULT_FTP_PORT;
        }
    }

    public void open() {
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
        } catch (Exception e) {
            throw new FtpException(e.getMessage(), e);
        }
    }

    public void close() throws IOException {
        ftp.disconnect();
    }
}
