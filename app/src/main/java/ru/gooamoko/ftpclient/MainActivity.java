package ru.gooamoko.ftpclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "FTPCLNT";

    private Button exitButton;
    private Button checkButton;
    private Button uploadButton;
    private EditText hostEdit;
    private EditText userEdit;
    private EditText passwordEdit;
    private int notificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        hostEdit = findViewById(R.id.hostEdit);
        userEdit = findViewById(R.id.userEdit);
        passwordEdit = findViewById(R.id.passwordEdit);

        checkButton = findViewById(R.id.checkButton);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String host = hostEdit.getText().toString();
                String user = userEdit.getText().toString();
                String password = passwordEdit.getText().toString();

                checkButton.setEnabled(false);
                AsyncCheckRequest checkRequest = new AsyncCheckRequest();
                checkRequest.execute(host, user, password);
            }
        });

        exitButton = findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

    class AsyncCheckRequest extends AsyncTask<String, Integer, String> {
        String title;

        AsyncCheckRequest() {
            this.title = getString(R.string.check_msg_title);
        }

        @Override
        protected String doInBackground(String... arg) {
            String host = arg[0];
            String user = arg[1];
            String password = arg[2];

            String message;
            try {
                FtpClient client = new FtpClient(host, user, password);
                client.open();
                client.close();

                message = String.format(getString(R.string.check_success_msg), host, user);
            } catch (Exception e) {
                message = String.format(getString(R.string.check_error_msg), host, user);
            }
            return message;
        }

        @Override
        protected void onPostExecute(String message) {
            super.onPostExecute(message);
            sendNotification(title, message);
        }
    }
}
