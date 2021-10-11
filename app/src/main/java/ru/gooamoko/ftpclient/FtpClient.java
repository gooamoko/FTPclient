package ru.gooamoko.ftpclient;

import android.webkit.MimeTypeMap;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import ru.gooamoko.ftpclient.model.ConnectionParamsModel;
import ru.gooamoko.ftpclient.model.FileInfo;

public class FtpClient {
    public static final String PREFERENCES_NAME = FtpClient.class.getName();
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String USER = "user";
    public static final String PASSWORD = "password";
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
    public static final String OTHER_FOLDER = "other/";
    public static final String VIDEO_FOLDER = "video/";
    public static final String AUDIO_FOLDER = "audio/";
    public static final String IMAGES_FOLDER = "images/";
    public static final String PDF_FOLDER = "pdf/";

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

    public void upload(List<FileInfo> source) {
        if (!connected) {
            throw new FtpException("Can't connect to FTP server");
        }

        if (source == null || source.isEmpty()) {
            return;
        }

        try {
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            for (FileInfo file : source) {
                // Угадаем имя каалога по расширению файла.
                String folder = guessPathByMimeType(file.getName());
                boolean exists = false;

                // Проверим, есть ли у нас такой каталог?
                FTPFile[] ftpFiles = ftp.listDirectories();
                for (FTPFile ftpFile : ftpFiles) {
                    String existingFolder = ftpFile.getName() + "/";
                    if (folder.equalsIgnoreCase(existingFolder)) {
                        folder = existingFolder;
                        exists = true;
                        break;
                    }
                }

                // Если каталога нет, создадим его.
                if (!exists) {
                    boolean created = ftp.makeDirectory(folder);
                    if (!created) {
                        throw new FtpException("Directory creation error");
                    }
                }

                InputStream input = file.getData();
                ftp.enterLocalPassiveMode();
                boolean uploaded = ftp.storeFile(folder + file.getName(), input);
                if (!uploaded) {
                    throw new FtpException("File upload error");
                }
                input.close();
                ftp.enterLocalActiveMode();
            }
        } catch (IOException e) {
            throw new FtpException("File upload error", e);
        }
    }

    public void close() throws IOException {
        ftp.logout();
        ftp.disconnect();
    }


    private String guessPathByMimeType(String fileName) {
        String type = null;
        if (fileName != null) {
            String extension = MimeTypeMap.getFileExtensionFromUrl(fileName);
            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }
        }
        if (type == null || type.trim().isEmpty()) {
            return OTHER_FOLDER;
        }
        String trimmedType = type.toLowerCase().trim();
        if (trimmedType.startsWith("video")) {
            return VIDEO_FOLDER;
        }
        if (trimmedType.startsWith("audio")) {
            return AUDIO_FOLDER;
        }
        if (trimmedType.startsWith("image")) {
            return IMAGES_FOLDER;
        }
        if ("application/pdf".equalsIgnoreCase(trimmedType)) {
            return PDF_FOLDER;
        }
        return OTHER_FOLDER;
    }
}
