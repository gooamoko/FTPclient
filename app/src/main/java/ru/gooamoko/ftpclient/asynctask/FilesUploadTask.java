package ru.gooamoko.ftpclient.asynctask;

import java.io.File;
import java.util.List;

import ru.gooamoko.ftpclient.FtpClient;
import ru.gooamoko.ftpclient.model.ConnectionParamsModel;

public class FilesUploadTask extends FtpClientTask {
    private final List<File> files;

    public FilesUploadTask(ConnectionParamsModel paramsModel, List<File> files, FtpClientTaskCallback callback) {
        super(paramsModel, callback);
        this.files = files;
    }

    @Override
    protected String doInBackground(String... arg) {
        try {
            FtpClient client = getClient();
            client.open();
            client.upload(files);
            client.close();
            return FtpClient.SUCCESS;
        } catch (Exception e) {
            return FtpClient.ERROR;
        }
    }
}
