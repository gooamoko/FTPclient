package ru.gooamoko.ftpclient;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "FTPCLNT";

    private EditText folderEdit;
    private TextView errorView;
    private int notificationId;

    @Override
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        folderEdit = findViewById(R.id.userEdit);
        errorView = findViewById(R.id.errorView);

        Intent intent = getIntent();
        if (intent != null) {
            ClipData clipData = intent.getClipData();
            if (clipData == null) {
                errorView.setText("No URI received");
            } else {
                int itemCount = clipData.getItemCount();

                if (itemCount > 0) {
                    ClipData.Item item = clipData.getItemAt(0);
                    File file = getFile(item.getUri());
                    if (file != null && file.canRead()) {
                        String message = String.format("%s (%s)", file.getAbsolutePath(), getMimeType(item.getUri()));
                        errorView.setText(message);
                    } else {
                        errorView.setText("Can't read " + file.getAbsolutePath());
                    }
                } else {
                    errorView.setText("No items found");
                }
            }
        } else {
            errorView.setText("Intent is null!");
        }

        Button exitButton = findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button uploadButton = findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });

//        createNotificationChannel();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
        return super.onOptionsItemSelected(item);
    }


    private void showToast(String message) {
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.show();
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

    private void uploadFile() {
        SharedPreferences preferences = getSharedPreferences(FtpClient.PREFERENCES_NAME, MODE_PRIVATE);
        String host = preferences.getString(FtpClient.HOST, "");
        String port = preferences.getString(FtpClient.PORT, "21");
        String user = preferences.getString(FtpClient.USER, "");
        String password = preferences.getString(FtpClient.PASSWORD, "");
        String message = String.format("host: %s\nport: %s\nuser: %s\npassword: %s", host, port, user, password);
        showToast(message);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                CharSequence name = getString(R.string.channel_name);
                String description = getString(R.string.channel_description);
                int importance = NotificationManager.IMPORTANCE_DEFAULT;

                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);

                notificationManager.createNotificationChannel(channel);
            }
        }
    }


    private void sendNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(notificationId++, builder.build());
    }
}
