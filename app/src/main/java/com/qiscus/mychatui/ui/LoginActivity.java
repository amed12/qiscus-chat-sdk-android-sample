package com.qiscus.mychatui.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.qiscus.mychatui.MyApplication;
import com.qiscus.mychatui.R;
import com.qiscus.mychatui.data.model.User;
import com.qiscus.mychatui.presenter.LoginPresenter;
import com.qiscus.sdk.chat.core.QiscusCore;
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom;
import com.qiscus.sdk.chat.core.data.remote.QiscusPusherApi;

/**
 * Created on : January 30, 2018
 * Author     : zetbaitsu
 * Name       : Zetra
 * GitHub     : https://github.com/zetbaitsu
 */
public class LoginActivity extends AppCompatActivity implements LoginPresenter.View {

    private LinearLayout login1Button;
    private LinearLayout login2Button;
    private ProgressDialog progressDialog;
    private String cross;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login1Button = findViewById(R.id.login1);
        login2Button = findViewById(R.id.login2);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait!");

        LoginPresenter loginPresenter = new LoginPresenter(this,
                MyApplication.getInstance().getComponent().getUserRepository());

        loginPresenter.start();

        login1Button.setOnClickListener(v -> {
            loginPresenter.login(
                    "guest-1001",
                    "passkey",
                    "user1"
            );
            cross = "guest-1002";
        });
        login2Button.setOnClickListener(v -> {
            cross = "guest-1001";
            loginPresenter.login(
                    "guest-1002",
                    "passkey",
                    "user2"
            );
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
                    finish();
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
}
