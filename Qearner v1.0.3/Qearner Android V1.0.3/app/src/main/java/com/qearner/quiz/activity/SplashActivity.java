package com.qearner.quiz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.qearner.quiz.R;
import com.qearner.quiz.helper.Utils;
import com.qearner.quiz.login.LoginActivity;


public class SplashActivity extends AppCompatActivity {

    Handler handler;
    AppCompatActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setStatusBarColor(SplashActivity.this, ContextCompat.getColor(getApplicationContext(), R.color.bg_color));
        setContentView(R.layout.activity_splash);
        activity = SplashActivity.this;


        handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(activity, LoginActivity.class);
            startActivity(intent);
            finish();
        }, 6000);
    }

}