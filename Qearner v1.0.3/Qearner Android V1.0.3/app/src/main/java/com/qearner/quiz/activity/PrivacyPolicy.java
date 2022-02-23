package com.qearner.quiz.activity;

import android.annotation.SuppressLint;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.view.MenuItem;
import android.view.View;

import android.webkit.WebView;
import android.widget.ProgressBar;

import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.qearner.quiz.Constant;
import com.qearner.quiz.R;
import com.qearner.quiz.vollyConfigs.ApiConfig;
import com.qearner.quiz.helper.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PrivacyPolicy extends AppCompatActivity {


    public ProgressBar prgLoading;
    public WebView mWebView;
    public String type;
    public Toolbar toolbar;


    @SuppressLint({"SetJavaScriptEnabled", "NewApi"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        getAllWidgets();
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        type = getIntent().getStringExtra("type");
        try {
            mWebView.setClickable(true);
            mWebView.setFocusableInTouchMode(true);
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.setBackgroundColor(ContextCompat.getColor(PrivacyPolicy.this, R.color.transparent));
            getData(type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAllWidgets() {
        toolbar = findViewById(R.id.toolBar);
        prgLoading = findViewById(R.id.prgLoading);
        mWebView = findViewById(R.id.webView1);
    }

    public void GetPrivacyAndTerms(final String api) {
        if (Utils.isNetworkAvailable(this)) {
            if (!prgLoading.isShown()) {
                prgLoading.setVisibility(View.VISIBLE);
            }
            Map<String, String> params = new HashMap<>();
            params.put(api, "1");
            ApiConfig.RequestToVolley((result, response) -> {
                if (result) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getString(Constant.ERROR).equals(Constant.FALSE)) {
                            String privacyStr = obj.getString(Constant.DATA);
                            mWebView.setVerticalScrollBarEnabled(true);
                            String message = "<font color='white'>" + privacyStr + "</font>";
                            mWebView.loadDataWithBaseURL("", message, "text/html", "UTF-8", "");
                        } else {
                            Toast.makeText(getApplicationContext(), obj.getString(Constant.MESSAGE), Toast.LENGTH_LONG).show();
                        }
                        prgLoading.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, params, PrivacyPolicy.this);

        } else {
            prgLoading.setVisibility(View.GONE);
            setSnackBar();
        }
    }

    public void getData(String type) {
        switch (type) {
            case "privacy":
                Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.privacy_policy));
                GetPrivacyAndTerms(Constant.getPrivacy);
                break;
            case "terms":
                Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.terms));
                GetPrivacyAndTerms(Constant.getTerms);
                break;
            case "about":
                Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.about_us));
                GetPrivacyAndTerms(Constant.get_about_us);
                break;
            case "instruction":
                Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.instruction));
                GetPrivacyAndTerms(Constant.GET_INSTRUCTIONS);
                break;
        }
    }

    public void setSnackBar() {
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry), view -> getData(type));
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }


    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        mWebView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        finish();
        super.onBackPressed();

    }
}