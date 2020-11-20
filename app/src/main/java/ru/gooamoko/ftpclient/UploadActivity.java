package ru.gooamoko.ftpclient;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import ru.gooamoko.ftpclient.asynctask.FilesUploadTask;
import ru.gooamoko.ftpclient.asynctask.FtpClientTaskCallback;
import ru.gooamoko.ftpclient.model.ConnectionParamsModel;

public class UploadActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private TextView descriptionView;
    private Button acceptUploadBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        SharedPreferences sharedPreferences = getPreferences();
        final ConnectionParamsModel paramsModel = new ConnectionParamsModel(sharedPreferences);
        final List<File> files = getFiles();

        acceptUploadBtn = findViewById(R.id.acceptUploadButton);
        acceptUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload(paramsModel, files);
            }
        });

        Button cancelUploadBtn = findViewById(R.id.cancelUploadButton);
        cancelUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        descriptionView = findViewById(R.id.uploadDescriptionView);
        String message = String.format(Locale.US, "host: %s\nport: %d\nFiles: %s", paramsModel.getHost(), paramsModel.getPort(), getFileNames(files));
        descriptionView.setText(message);
    }

    private String getFileNames(List<File> files) {
        if (isEmpty(files)) {
            return "No readable files";
        }
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (File file : files) {
            if (!first) {
                builder.append(",\n");
            }
            builder.append(file.getName());
            first = false;
        }
        return builder.toString();
    }


    private void upload(ConnectionParamsModel paramsModel, List<File> files) {

        if (isEmpty(files)) {
            String message = getString(R.string.upload_error_msg);
            showToast(message);
        }

        acceptUploadBtn.setEnabled(false);
        final FtpClientTaskCallback callback = new FtpClientTaskCallback() {
            @Override
            public void onFinishTask(String result) {
                String message;
                if (FtpClient.SUCCESS.equalsIgnoreCase(result)) {
                    message = getString(R.string.upload_success_msg);
                } else {
                    message = getString(R.string.upload_error_msg);
                }

                acceptUploadBtn.setEnabled(true);
                finish();
                showToast(message);
            }
        };

        FilesUploadTask uploadTask = new FilesUploadTask(paramsModel, files, callback);
        uploadTask.execute();
    }

    private boolean isEmpty(List<?> collection) {
        return collection == null || collection.isEmpty();
    }

    private List<File> getFiles() {
        List<File> fileList = new LinkedList<>();
        Intent intent = getIntent();
        if (intent != null) {
            ClipData clipData = intent.getClipData();
            if (clipData != null) {
                int itemCount = clipData.getItemCount();

                if (itemCount > 0) {
                    ClipData.Item item = clipData.getItemAt(0);
                    File file = getFile(item.getUri());
                    if (file != null && file.canRead()) {
                        fileList.add(file);
                    }
                }
            }
            Uri intentData = intent.getData();
            if (intentData != null) {
                File file = getFile(intentData);
                if (file != null && file.canRead()) {
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }


    private File getFile(Uri uri) {
        try {
            if (uri != null) {
                URI fileUri = new URI(uri.toString());
                return new File(fileUri);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
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
