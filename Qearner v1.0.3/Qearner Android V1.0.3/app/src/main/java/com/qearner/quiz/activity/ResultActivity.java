package com.qearner.quiz.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

import com.qearner.quiz.Constant;
import com.qearner.quiz.R;
import com.qearner.quiz.UI.GradientProgress;
import com.qearner.quiz.ads.AdUtils;
import com.qearner.quiz.battle.SearchPlayerActivity;
import com.qearner.quiz.vollyConfigs.ApiConfig;
import com.qearner.quiz.helper.AppController;
import com.qearner.quiz.helper.Session;
import com.qearner.quiz.helper.Utils;
import com.qearner.quiz.model.Question;
import com.qearner.quiz.selfchallenge.SelfChallengeQuestion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {

    TextView tvResultMsg, tvCorrect, tvInCorrect, tvTime, tvChallengeTime;
    ScrollView scrollView;
    GradientProgress progressBar;
    boolean isQuizCompleted;
    String fromQue;
    AppCompatActivity activity;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        getAllWidgets();
        activity = ResultActivity.this;
        fromQue = getIntent().getStringExtra("fromQue");
        AdUtils.loadFacebookInterstitialAd(activity);
        isQuizCompleted = Session.isQuizCompleted(activity);
        ArrayList<String> correctList = new ArrayList<>();
        ArrayList<String> inCorrectList = new ArrayList<>();

        for (Question q : SelfChallengeQuestion.questionList) {
            if (q.isCorrect())
                correctList.add(getString(R.string.correct));
            else {
                if (q.isAttended())
                    inCorrectList.add(getString(R.string.incorrect));
            }
        }

        progressBar.setSelfResultAttributes(activity);
        progressBar.setMaxProgress((int) Constant.CHALLENGE_TIME);
        progressBar.setCurrentProgress((int) (Constant.TAKE_TIME));
        tvTime.setText("" + getMinuteSeconds(Constant.TAKE_TIME));
        tvResultMsg.setText(getString(R.string.time_challenge_msg) + getMinuteSeconds(Constant.TAKE_TIME) + getString(R.string.sec));
        tvChallengeTime.setText(getString(R.string.challenge_time) + getMinuteSeconds(Constant.CHALLENGE_TIME));
        tvCorrect.setText("" + correctList.size());
        tvInCorrect.setText("" + inCorrectList.size());

        GetUserData();

        if(Constant.IN_APP_MODE.equals("1")){
            if (Constant.ADS_TYPE.equals("1")) {
                AdUtils.LoadNativeAds(activity);
            } else {
                AdUtils.loadNativeAd(activity);
            }
        }
    }

    public void getAllWidgets() {
        progressBar = findViewById(R.id.progressBar);
        tvTime = findViewById(R.id.tvTime);
        tvChallengeTime = findViewById(R.id.tvChallengeTime);
        scrollView = findViewById(R.id.scrollView);
        tvResultMsg = findViewById(R.id.tvResultMsg);
        tvCorrect = findViewById(R.id.right);
        tvInCorrect = findViewById(R.id.wrong);
    }

    public String getMinuteSeconds(long milliSeconds) {
        long totalSecs = (long) (milliSeconds / 1000.0);
        long minutes = (totalSecs / 60);
        long seconds = totalSecs % 60;
        @SuppressLint("DefaultLocale") String str = String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
        return str;
    }

    public void ShareScore(View view) {
        String shareMsg = "I have finished   " + getMinuteSeconds(Constant.CHALLENGE_TIME) + " minute self challenge in " + getMinuteSeconds(Constant.TAKE_TIME) + " minute in " + getString(R.string.app_name);
        Utils.ShareInfo(scrollView, activity, shareMsg);
    }

    public void RateApp(View view) {
        AdUtils.showFacebookInterstitialAd(ResultActivity.this);
        rateClicked();
    }

    public void Home(View view) {
        Intent intent1 = new Intent(activity, MainActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent1.putExtra("type", "default");
        startActivity(intent1);
    }

    private void rateClicked() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.APP_LINK)));
        }
    }

    public void GetUserData() {
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
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params, activity);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void PlayQuiz(View view) {
        Intent intent = new Intent(activity, CategoryActivity.class);
        startActivity(intent);
    }

    public void BattleQuiz(View view) {
        searchPlayerCall();
    }

    public void searchPlayerCall() {
        if (Constant.isCateEnable)
            openCategoryPage(Constant.BATTLE);
        else
            startActivity(new Intent(activity, SearchPlayerActivity.class));
    }


    public void openCategoryPage(String type) {
        startActivity(new Intent(activity, CategoryActivity.class)
                .putExtra(Constant.QUIZ_TYPE, type));
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
    public void onResume() {
        super.onResume();


    }

}