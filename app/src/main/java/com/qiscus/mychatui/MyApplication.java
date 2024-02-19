package com.qiscus.mychatui;


import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDexApplication;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.qiscus.jupuk.Jupuk;
import com.qiscus.mychatui.data.model.AppCreds;
import com.qiscus.mychatui.util.PushNotificationUtil;
import com.qiscus.nirmana.Nirmana;
import com.qiscus.sdk.chat.core.QiscusCore;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.one.EmojiOneProvider;

/**
 * Created on : January 30, 2018
 * Author     : zetbaitsu
 * Name       : Zetra
 * GitHub     : https://github.com/zetbaitsu
 */
public class MyApplication extends MultiDexApplication {
    private static MyApplication instance;

    private AppComponent component;
    private AppCreds appCreds;

    public static MyApplication getInstance() {
        return instance;
    }

    private static void initEmoji() {
        EmojiManager.install(new EmojiOneProvider());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        component = new AppComponent(this);

        Nirmana.init(this);

        appCreds = MyApplication.getInstance().getComponent().getUserRepository().
                getAppCreds();
        if (appCreds == null) {
            FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(3600)
                    .build();
            firebaseRemoteConfig.setConfigSettingsAsync(configSettings);
            firebaseRemoteConfig.fetchAndActivate()
                    .addOnCompleteListener(new OnCompleteListener<Boolean>() {
                        @Override
                        public void onComplete(@NonNull Task<Boolean> task) {
                            if (task.isSuccessful()) {
                                boolean updated = task.getResult();
                                Log.d("remoteConfig", "Config params updated: " + updated);
                                Toast.makeText(getApplicationContext(), "Fetch and activate succeeded",
                                        Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getApplicationContext(), "Fetch failed",
                                        Toast.LENGTH_SHORT).show();
                            }
                            String appId = firebaseRemoteConfig.getString("app_id");
                            Boolean isCustomServer = firebaseRemoteConfig.getBoolean("is_custom_server");
                            String customServerUrl = firebaseRemoteConfig.getString("custom_server");
                            appCreds = new AppCreds();
                            appCreds.setAppId(appId);
                            appCreds.setCustomServerUrl(customServerUrl);
                            appCreds.setCustomServer(isCustomServer);
                            MyApplication.getInstance().getComponent().getUserRepository().
                                    setAppCreds(appCreds);
                            if (isCustomServer) {
                                QiscusCore.setupWithCustomServer(getInstance(),
                                        appId,
                                        customServerUrl,
                                        null,
                                        null);
                            } else {
                                QiscusCore.setup(getInstance(), appId);
                                Toast.makeText(getApplicationContext(), "App id Current -> " + appId,
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        } else {
            if (appCreds.getCustomServer()){
                QiscusCore.setupWithCustomServer(getInstance(),
                        appCreds.getAppId(),
                        appCreds.getCustomServerUrl(),
                        null,
                        null);
            }else {
                QiscusCore.setup(getInstance(), appCreds.getAppId());
                Toast.makeText(getApplicationContext(), "App id Current -> " + appCreds.getAppId(),
                        Toast.LENGTH_SHORT).show();
            }
        }
        QiscusCore.getChatConfig()
                .enableDebugMode(true)
//                .setNotificationListener(PushNotificationUtil::showNotification)
                .setEnableFcmPushNotification(false);
        initEmoji();
        Jupuk.init(this);
    }

    public AppComponent getComponent() {
        return component;
    }
}
