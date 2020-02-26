package ru.gooamoko.ftpclient;

import android.annotation.SuppressLint;
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
    private EditText hostEdit;
    private EditText portEdit;
    private EditText userEdit;
    private EditText passwordEdit;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        preferences = getPreferences();

        hostEdit = findViewById(R.id.setingsHostEdit);
        portEdit = findViewById(R.id.settingsPortEdit);
        userEdit = findViewById(R.id.settingsUserEdit);
        passwordEdit = findViewById(R.id.settingsPasswordEdit);

        hostEdit.setText(preferences.getString(FtpClient.HOST, ""));
        portEdit.setText(preferences.getString(FtpClient.PORT, "21"));
        userEdit.setText(preferences.getString(FtpClient.USER, ""));
        passwordEdit.setText(preferences.getString(FtpClient.PASSWORD, ""));

        checkButton = findViewById(R.id.checkButton);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String host = hostEdit.getText().toString();
                final String port = portEdit.getText().toString();
                final String user = userEdit.getText().toString();
                String password = passwordEdit.getText().toString();

                checkButton.setEnabled(false);
                final FtpClientTaskCallback callback = new FtpClientTaskCallback() {
                    @Override
                    public void onFinishTask(String result) {
                        checkButton.setEnabled(true);
                        if (FtpClient.SUCCESS.equalsIgnoreCase(result)) {
                            storeProperties();
                            String successMsg = getString(R.string.check_success_msg);
                            String savedMessage = getString(R.string.settings_save_message);
                            showToast(successMsg + "\n" + savedMessage);
                        } else {
                            final String errorMsg = getString(R.string.check_error_msg);
                            showToast(String.format(errorMsg, host, port, user));
                        }
                    }
                };

                ConnectionCheckTask checkTask = new ConnectionCheckTask(callback);
                checkTask.execute(host, port, user, password);
            }
        });
    }

    private SharedPreferences getPreferences() {
        if (preferences == null) {
            preferences = getSharedPreferences(FtpClient.PREFERENCES_NAME, MODE_PRIVATE);
        }
        return preferences;
    }

    @SuppressLint("ApplySharedPref")
    private void storeProperties() {
        SharedPreferences preferences = getPreferences();
        Editor ed = preferences.edit();
        ed.putString(FtpClient.HOST, hostEdit.getText().toString());
        ed.putString(FtpClient.PORT, portEdit.getText().toString());
        ed.putString(FtpClient.USER, userEdit.getText().toString());
        ed.putString(FtpClient.PASSWORD, passwordEdit.getText().toString());
        ed.commit();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
