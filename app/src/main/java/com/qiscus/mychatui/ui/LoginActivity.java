package com.qiscus.mychatui.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qiscus.mychatui.MyApplication;
import com.qiscus.mychatui.R;
import com.qiscus.mychatui.data.model.User;
import com.qiscus.mychatui.presenter.LoginPresenter;
import com.qiscus.sdk.chat.core.QiscusCore;
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom;
import com.qiscus.sdk.chat.core.data.remote.QiscusApi;
import com.qiscus.sdk.chat.core.data.remote.QiscusPusherApi;
import com.qiscus.sdk.chat.core.util.QiscusAndroidUtil;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created on : January 30, 2018
 * Author     : zetbaitsu
 * Name       : Zetra
 * GitHub     : https://github.com/zetbaitsu
 */
public class LoginActivity extends AppCompatActivity implements LoginPresenter.View {

    private LinearLayout login1Button;
    private LinearLayout login2Button;
    private TextView loginTv;
    private TextView login2Tv;
    private ProgressDialog progressDialog;
    private String cross;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login1Button = findViewById(R.id.login1);
        login2Button = findViewById(R.id.login2);
        loginTv = findViewById(R.id.tv_start);
        login2Tv = findViewById(R.id.tv_start2);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait!");

        LoginPresenter loginPresenter = new LoginPresenter(this,
                MyApplication.getInstance().getComponent().getUserRepository());


        loginPresenter.start();

        if (QiscusCore.hasSetupUser()) {
            if (QiscusCore.getQiscusAccount().getEmail().equals("guest-1001")){
                cross = "guest-1002";
                loginTv.setText("Log out");
                login2Button.setVisibility(View.GONE);
            }else {
                cross = "guest-1001";
                login2Tv.setText("Log out");
                login1Button.setVisibility(View.GONE);
            }
        }else {
            loginTv.setText("LOGIN USER 1");
            login2Tv.setText("LOGIN USER 2");
        }
        login1Button.setOnClickListener(v -> {
            if (QiscusCore.hasSetupUser()) {
                logoutUser();
                login2Button.setVisibility(View.VISIBLE);
                loginTv.setText("LOGIN USER 1");
            }else {
                loginPresenter.login(
                        "guest-1001",
                        "passkey",
                        "user1"
                );
                cross = "guest-1002";
                loginTv.setText("LOG OUT");
                login2Button.setVisibility(View.GONE);
            }
        });
        login2Button.setOnClickListener(v -> {
            if (QiscusCore.hasSetupUser()) {
                logoutUser();
                login2Tv.setText("LOGIN USER 2");
                login1Button.setVisibility(View.VISIBLE);
            }else {
                cross = "guest-1001";
                loginPresenter.login(
                        "guest-1002",
                        "passkey",
                        "user2"
                );
                login2Tv.setText("LOG OUT");
                login1Button.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void showHomePage() {
        User user = new User();
        user.setId(cross);
        user.setName(cross);
        MyApplication.getInstance().getComponent().getChatRoomRepository().createChatRoom(
                user,
                qiscusChatRoom -> {
                    startActivity(ChatRoomActivity.generateIntent(this, qiscusChatRoom));
                },
                error -> {

                }
        );
    }

    @Override
    public void showLoading() {
        progressDialog.show();
    }

    @Override
    public void dismissLoading() {
        progressDialog.dismiss();
    }

    @Override
    public void showErrorMessage(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void logoutUser() {
        if (QiscusCore.hasSetupUser()) {
            QiscusCore.clearUser();
        }
    }
}
