package ru.gooamoko.ftpclient;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

public class UploadActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private TextView descriptionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        SharedPreferences sharedPreferences = getPreferences();
        String host = sharedPreferences.getString(FtpClient.HOST, "");
        String port = sharedPreferences.getString(FtpClient.PORT, "21");
        String user = sharedPreferences.getString(FtpClient.USER, "");
        String password = sharedPreferences.getString(FtpClient.PASSWORD, "");

        List<File> files = getFiles();

        Button acceptUploadBtn = findViewById(R.id.acceptUploadButton);
        acceptUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
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
        String message = String.format("host: %s\nport: %s\nFiles: %s", host, port, getFileNames(files));
        descriptionView.setText(message);
    }

    private String getFileNames(List<File> files) {
        if (files == null || files.isEmpty()) {
            return "No readable files";
        }
        StringBuilder builder = new StringBuilder();
        for (File file : files) {
            builder.append(file.getName()).append(", ");
        }
        return builder.toString();
    }


    private void uploadFile() {
        showToast("Данная функция пока не реализована.");
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

    public String getMimeType(Uri uri) {
        String type = null;
        if (uri != null) {
            String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }
        }
        return type;
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
