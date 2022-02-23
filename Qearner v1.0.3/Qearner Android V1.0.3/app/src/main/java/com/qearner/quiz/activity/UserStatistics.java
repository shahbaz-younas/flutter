package com.qearner.quiz.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.android.material.snackbar.Snackbar;
import com.qearner.quiz.Constant;
import com.qearner.quiz.R;
import com.qearner.quiz.UI.GradientProgress;
import com.qearner.quiz.ads.AdUtils;
import com.qearner.quiz.vollyConfigs.ApiConfig;
import com.qearner.quiz.helper.AppController;
import com.qearner.quiz.UI.CircleImageView;
import com.qearner.quiz.helper.Session;
import com.qearner.quiz.helper.Utils;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserStatistics extends AppCompatActivity {

    Toolbar toolbar;
    TextView tvName, tvRank, tvScore, tvCoin, tvTotalQue, tvCorrect, tvInCorrect, tvCorrectP, tvInCorrectP;
    CircleImageView imgProfile;
    GradientProgress progress;
    String totalQues, correctQues, inCorrectQues, strongCate, weakCate, strongRatio, weakRatio;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    AppCompatActivity activity;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_statistics);
        activity = UserStatistics.this;
        getAllWidgets();
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.user_statistics));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgProfile.setDefaultImageResId(R.drawable.ic_account);
        imgProfile.setImageUrl(Session.getUserData(Session.PROFILE, activity), imageLoader);
        tvName.setText(getString(R.string.hello) + Session.getUserData(Session.NAME, activity));
        GetUserData();
        GetUserStatistics();
        if (Constant.IN_APP_MODE.equals("1")) {
            if (Constant.ADS_TYPE.equals("1")) {
                AdUtils.LoadNativeAds(activity);
            } else {
                AdUtils.loadNativeAd(activity);
            }
        }
        AdUtils.loadFacebookBannerAds(activity);
    }

    public void getAllWidgets() {
        toolbar = findViewById(R.id.toolBar);
        imgProfile = findViewById(R.id.imgProfile);
        tvName = findViewById(R.id.tvName);
        tvRank = findViewById(R.id.tvRank);
        tvScore = findViewById(R.id.tvScore);
        tvCoin = findViewById(R.id.tvCoin);

        tvTotalQue = findViewById(R.id.tvAttended);
        tvCorrect = findViewById(R.id.tvCorrect);
        tvInCorrect = findViewById(R.id.tvInCorrect);
        tvCorrectP = findViewById(R.id.tvCorrectP);
        tvInCorrectP = findViewById(R.id.tvInCorrectP);
        progress = findViewById(R.id.progress);

    }

    public void setSnackBar() {
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry), view -> {
                    GetUserData();
                    GetUserStatistics();
                });

        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }


    @SuppressLint("SetTextI18n")
    public void GetUserStatistics() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_USER_STATISTICS, "1");
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, activity));
        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {
                try {
                    JSONObject obj = new JSONObject(response);
                    boolean error = obj.getBoolean(Constant.ERROR);
                    if (!error) {
                        JSONObject object = obj.getJSONObject(Constant.DATA);
                        totalQues = object.getString(Constant.QUESTION_ANSWERED);
                        correctQues = object.getString(Constant.CORRECT_ANSWERS);
                        inCorrectQues = String.valueOf(Integer.parseInt(totalQues) - Integer.parseInt(correctQues));
                        strongCate = object.getString(Constant.STRONG_CATE);
                        weakCate = object.getString(Constant.WEAK_CATE);
                        strongRatio = object.getString(Constant.RATIO_1);
                        weakRatio = object.getString(Constant.RATIO_2);
                        tvTotalQue.setText(totalQues);
                        tvCorrect.setText(correctQues);
                        tvInCorrect.setText(inCorrectQues);

                        int percentCorrect = Math.round((Float.parseFloat(correctQues) * 100) / Float.parseFloat(totalQues));
                        int percentInCorrect = Math.round((Float.parseFloat(inCorrectQues) * 100) / Float.parseFloat(totalQues));

                        tvCorrectP.setText(percentCorrect + getString(R.string.modulo_sign));
                        tvInCorrectP.setText(percentInCorrect + getString(R.string.modulo_sign));
                        progress.setStatisticAttributes(getApplicationContext());
                        progress.setMaxProgress(Integer.parseInt(totalQues));
                        progress.setCurrentProgress(Integer.parseInt(correctQues));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params, activity);


    }

    @SuppressLint("SetTextI18n")
    public void GetUserData() {
        if (Utils.isNetworkAvailable(activity)) {
            Map<String, String> params = new HashMap<>();
            params.put(Constant.GET_USER_BY_ID, "1");
            params.put("device_id","1234");
            params.put(Constant.ID, Session.getUserData(Session.USER_ID, getApplicationContext()));
            ApiConfig.RequestToVolley((result, response) -> {

                if (result) {
                    try {

                        JSONObject obj = new JSONObject(response);
                        boolean error = obj.getBoolean(Constant.ERROR);
                        if (!error) {
                            JSONObject jsonObject = obj.getJSONObject(Constant.DATA);
                            Constant.TOTAL_COINS = Integer.parseInt(jsonObject.getString(Constant.COINS));
                            tvCoin.setText("" + Constant.TOTAL_COINS);
                            tvRank.setText("" + jsonObject.getString(Constant.GLOBAL_RANK));
                            tvScore.setText(jsonObject.getString(Constant.GLOBAL_SCORE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, params, activity);

        } else {
            setSnackBar();
        }
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
    protected void onDestroy() {
        if (AdUtils.mAdView != null) {
            AdUtils.mAdView.destroy();
        }
        super.onDestroy();
    }
}
