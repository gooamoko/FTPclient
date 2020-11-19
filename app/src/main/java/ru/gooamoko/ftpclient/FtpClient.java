package ru.gooamoko.ftpclient;

import android.webkit.MimeTypeMap;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import ru.gooamoko.ftpclient.model.ConnectionParamsModel;

public class FtpClient {
    public static final String PREFERENCES_NAME = FtpClient.class.getName();
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

    private static final String UPLOAD_DIR = "video/";

    private final ConnectionParamsModel connectionParams;
    private FTPClient ftp;
    private boolean connected;

    public FtpClient(ConnectionParamsModel connectionParams) {
        if (connectionParams == null || connectionParams.isError()) {
            throw new FtpException("Incorrect connection parameters!");
        }
        this.connectionParams = connectionParams;
    }

    public void open() {
        try {
            ftp = new FTPClient();
            ftp.addProtocolCommandListener(new CustomProtocolCommandListener());
            ftp.connect(connectionParams.getHost(), connectionParams.getPort());

            int reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                throw new FtpException("Can't connect to FTP server");
            }

            ftp.login(connectionParams.getUser(), connectionParams.getPassword());
            connected = true;
        } catch (Exception e) {
            throw new FtpException(e.getMessage(), e);
        }
    }

    public void upload(List<File> source) {
        if (!connected) {
            throw new FtpException("Can't connect to FTP server");
        }

        if (source == null || source.isEmpty()) {
            return;
        }

        try {
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.enterLocalPassiveMode();

            for (File file : source) {
                // TODO: 20.11.20 Возможно, стоит загружать разные типы файлов в разные папки
                InputStream input = new FileInputStream(file);
                ftp.storeFile(UPLOAD_DIR + file.getName(), input);
            }
        } catch (Exception e) {
            throw new FtpException("File upload error", e);
        }
    }

    public void close() throws IOException {
        ftp.logout();
        ftp.disconnect();
    }


    private String getMimeType(File file) {
        String type = null;
        if (file != null) {
            String extension = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }
        }
        return type;
    }
}
