package ru.gooamoko.ftpclient;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ru.gooamoko.ftpclient.asynctask.ConnectionCheckTask;
import ru.gooamoko.ftpclient.asynctask.FtpClientTaskCallback;

public class SettingsActivity extends AppCompatActivity {
    private Button checkButton;
    private Button saveButton;
    private EditText hostEdit;
    private EditText portEdit;
    private EditText userEdit;
    private EditText passwordEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        hostEdit = findViewById(R.id.setingsHostEdit);
        portEdit = findViewById(R.id.settingsPortEdit);
        userEdit = findViewById(R.id.settingsUserEdit);
        passwordEdit = findViewById(R.id.settingsPasswordEdit);

        checkButton = findViewById(R.id.checkButton);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String host = hostEdit.getText().toString();
                String user = userEdit.getText().toString();
                String password = passwordEdit.getText().toString();

                checkButton.setEnabled(false);

                final FtpClientTaskCallback callback = new FtpClientTaskCallback() {
                    @Override
                    public void onFinishTask(String message) {
                        showToast(message);
                        checkButton.setEnabled(true);
                    }
                };

                String successMsg = getString(R.string.check_success_msg);
                String errorMsg = getString(R.string.check_error_msg);

                ConnectionCheckTask checkTask = new ConnectionCheckTask(callback, successMsg, errorMsg);
                checkTask.execute(host, user, password);
            }
        });

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeProperties();
            }
        });
    }

    private void storeProperties() {
        SharedPreferences preferences = getSharedPreferences(FtpClient.PREFERENCES_NAME, MODE_PRIVATE);
        Editor ed = preferences.edit();
        ed.putString(FtpClient.HOST, hostEdit.getText().toString());
        ed.putString(FtpClient.PORT, portEdit.getText().toString());
        ed.putString(FtpClient.USER, userEdit.getText().toString());
        ed.putString(FtpClient.PASSWORD, passwordEdit.getText().toString());
        ed.apply();
        String message = getString(R.string.settings_save_message);
        showToast(message);
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
    }
}
