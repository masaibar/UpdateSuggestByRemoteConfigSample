package com.masaibar.updatesuggestbyremoteconfigsample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import static com.masaibar.updatesuggestbyremoteconfigsample.BuildConfig.DEBUG;

public class MainActivity extends AppCompatActivity {

    private FirebaseRemoteConfig mRemoteConfig;
    private TextView mTextVersion;

    private static final String KEY_VERSION = "version";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextVersion = (TextView) findViewById(R.id.text_version);
        findViewById(R.id.button_fetch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetch();
            }
        });

        initRemoteConfig();
    }

    private void initRemoteConfig() {
        mRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(DEBUG)
                .build();
        mRemoteConfig.setConfigSettings(configSettings);
        mRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        fetch();
    }

    private void fetch() {
        mTextVersion.setText(mRemoteConfig.getString(KEY_VERSION));

        long catchExpiration = 3600;
        if (mRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            catchExpiration = 0;
        }

        mRemoteConfig.fetch(catchExpiration)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "fetch succeeded",
                                    Toast.LENGTH_SHORT
                            ).show();
                            mRemoteConfig.activateFetched();
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "fetch failed",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                        setVersionText();
                    }
                });
    }

    private void setVersionText() {
        String version = mRemoteConfig.getString(KEY_VERSION);

        mTextVersion.setText(version);
    }
}
