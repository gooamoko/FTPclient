package ru.gooamoko.ftpclient;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import ru.gooamoko.ftpclient.asynctask.FilesUploadTask;
import ru.gooamoko.ftpclient.asynctask.FtpClientTaskCallback;
import ru.gooamoko.ftpclient.model.ConnectionParamsModel;
import ru.gooamoko.ftpclient.model.FileInfo;

public class UploadActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private Button acceptUploadBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        SharedPreferences sharedPreferences = getPreferences();
        final ConnectionParamsModel paramsModel = new ConnectionParamsModel(sharedPreferences);
        final List<FileInfo> files = getFiles();

        acceptUploadBtn = findViewById(R.id.acceptUploadButton);
        acceptUploadBtn.setOnClickListener(v -> upload(paramsModel, files));

        Button cancelUploadBtn = findViewById(R.id.cancelUploadButton);
        cancelUploadBtn.setOnClickListener(v -> finish());

        TextView descriptionView = findViewById(R.id.uploadDescriptionView);
        String message = String.format(Locale.US, "host: %s\nport: %d\nFiles: %s", paramsModel.getHost(), paramsModel.getPort(), getFileNames(files));
        descriptionView.setText(message);
    }

    private String getFileNames(List<FileInfo> files) {
        if (isEmpty(files)) {
            return "No readable files";
        }
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (FileInfo file : files) {
            if (!first) {
                builder.append(",\n");
            }
            builder.append(file.getName());
            first = false;
        }
        return builder.toString();
    }


    private void upload(ConnectionParamsModel paramsModel, List<FileInfo> files) {

        if (isEmpty(files)) {
            String message = getString(R.string.upload_error_msg);
            showToast(message);
        }

        acceptUploadBtn.setEnabled(false);
        final FtpClientTaskCallback callback = result -> {
            String message;
            if (FtpClient.SUCCESS.equalsIgnoreCase(result)) {
                message = getString(R.string.upload_success_msg);
            } else {
                message = getString(R.string.upload_error_msg);
            }

            acceptUploadBtn.setEnabled(true);
            finish();
            showToast(message);
        };

        FilesUploadTask uploadTask = new FilesUploadTask(paramsModel, files, callback);
        uploadTask.execute();
    }

    private boolean isEmpty(List<?> collection) {
        return collection == null || collection.isEmpty();
    }

    private List<FileInfo> getFiles() {
        List<FileInfo> fileList = new LinkedList<>();
        Intent intent = getIntent();
        if (intent != null) {
            ClipData clipData = intent.getClipData();
            ContentResolver contentResolver = getContentResolver();

            if (clipData != null) {
                int itemCount = clipData.getItemCount();
                if (itemCount > 0) {
                    for (int index = 0; index < itemCount; index++) {
                        ClipData.Item item = clipData.getItemAt(index);
                        FileInfo file = new FileInfo(contentResolver, item.getUri());
                        if (file.exists()) {
                            fileList.add(file);
                        }
                    }
                }
            }

            FileInfo dataInfo = new FileInfo(contentResolver, intent.getData());
            if (dataInfo.exists()) {
                fileList.add(dataInfo);
            }
        }
        return fileList;
    }


    private SharedPreferences getPreferences() {
        if (preferences == null) {
            preferences = getSharedPreferences(FtpClient.PREFERENCES_NAME, MODE_PRIVATE);
        }
        return preferences;
    }

    private void showToast(String message) {
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.show();
    }
}
