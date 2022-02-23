package com.qearner.quiz.activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.qearner.quiz.R;
import com.qearner.quiz.UI.GradientProgress;
import com.qearner.quiz.ads.AdUtils;
import com.qearner.quiz.helper.AppController;
import com.qearner.quiz.Constant;
import com.qearner.quiz.helper.Session;
import com.qearner.quiz.helper.Utils;


public class CompleteActivity extends AppCompatActivity {

    //Toolbar toolbar;
    TextView tvResultMsg, tvQuizScore, tvCorrect, tvInCorrect, tvQuizCoins, tvVictoryMsg;
    ScrollView scrollView;
    GradientProgress resultProgress;
    RelativeLayout mainLayout;
    String fromQue;
    AppCompatActivity activity;
    LottieAnimationView celebrationAnim;
    ImageView imgVictory;
    MaterialButton btnPlayAgain;
    LinearLayout scoreLyt, coinLyt;
    RatingBar rb;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setStatusBarColor(CompleteActivity.this, ContextCompat.getColor(getApplicationContext(), R.color.bg_color));
        setContentView(R.layout.activity_complete);
        activity = CompleteActivity.this;
        getAllWidget();

        fromQue = getIntent().getStringExtra("fromQue");

        if (fromQue.equals("random") || fromQue.equals("true_false")) {
            scoreLyt.setVisibility(View.GONE);
            coinLyt.setVisibility(View.GONE);
        }
        if (Session.isQuizCompleted(activity)) {
            celebrationAnim.playAnimation();
            imgVictory.setBackgroundResource(R.drawable.victory);
            tvVictoryMsg.setText(getString(R.string.victory_));
            tvVictoryMsg.setTextColor(ContextCompat.getColor(activity, R.color.green));
            tvResultMsg.setText(getString(R.string.completed));


        } else {
            tvVictoryMsg.setText(getString(R.string.defeat));
            tvVictoryMsg.setTextColor(ContextCompat.getColor(activity, R.color.red));
            imgVictory.setBackgroundResource(R.drawable.defeat);
            //scrollView.setBackgroundResource(R.drawable.complete_not_bg);
            tvResultMsg.setText(getString(R.string.not_completed));
        }
        btnPlayAgain.setText(getResources().getString(R.string.play_again));
        tvQuizCoins.setText("" + Utils.quiz_coin);
        tvQuizScore.setText("" + Utils.quiz_score);
        tvCorrect.setText("" + Utils.correctQuestion);
        tvInCorrect.setText("" + Utils.wrongQuestion);

        resultProgress.setResultAttributes(activity);
        resultProgress.setAudienceProgress(getPercentageCorrect(Utils.TotalQuestion, Utils.correctQuestion));

        /*if (Session.isLogin(activity)) {
            GetUserData();
        }*/


        if(Constant.IN_APP_MODE.equals("1")){
            if (Constant.ADS_TYPE.equals("1")) {
                AdUtils.LoadNativeAds(activity);
            } else {
                AdUtils.loadNativeAd(activity);
            }
        }
    }

    public void getAllWidget() {
        mainLayout = findViewById(R.id.mainLayout);
        scoreLyt = findViewById(R.id.scoreLyt);
        coinLyt = findViewById(R.id.coinLyt);
        resultProgress = findViewById(R.id.resultProgress);
        scrollView = findViewById(R.id.scrollView);
        tvResultMsg = findViewById(R.id.tvResultMsg);
        tvCorrect = findViewById(R.id.right);
        tvInCorrect = findViewById(R.id.wrong);
        tvQuizScore = findViewById(R.id.tvScore);
        imgVictory = findViewById(R.id.imgVictory);
        tvQuizCoins = findViewById(R.id.tvCoin);
        tvVictoryMsg = findViewById(R.id.tvVictoryMsg);
        btnPlayAgain = findViewById(R.id.btnPlayAgain);
        celebrationAnim = findViewById(R.id.celebrationAnim);
        float d = (100 * 5) / 100;
        System.out.println("Values::=" + Utils.correctQuestion);
        rb = findViewById(R.id.ratingBar1);
      /*  rb.setRating(d);*/

        ObjectAnimator animation = ObjectAnimator.ofInt(rb, "progress", 250);
        animation.setDuration(2000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    public static float getPercentageCorrect(int questions, int correct) {
        float proportionCorrect = ((float) correct) / ((float) questions);
        return proportionCorrect * 100;
    }

    public void PlayAgain(View view) {

        Intent intent = new Intent(activity, PlayActivity.class);
        intent.putExtra("fromQue", fromQue);
        startActivity(intent);
        (activity).finish();
    }


    public void ShareScore(View view) {
        String shareMsg = "I have finished " + Constant.CATE_NAME + "Quiz  with " + Utils.quiz_score + " Score in " + getString(R.string.app_name);
        Utils.ShareInfo(scrollView, activity, shareMsg);
    }

    public void RateApp(View view) {
        AdUtils.showFacebookInterstitialAd(CompleteActivity.this);
        rateClicked();
    }

    public void Home(View view) {
        Intent intent1 = new Intent(activity, MainActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent1.putExtra("type", "default");
        startActivity(intent1);
        finish();
    }

    private void rateClicked() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.APP_LINK)));
        }
    }

/*    public void GetUserData() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_USER_BY_ID, "1");
        params.put(Constant.ID, Session.getUserData(Session.USER_ID, getApplicationContext()));
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject obj = new JSONObject(response);
                    boolean error = obj.getBoolean("error");
                    if (!error) {
                        JSONObject jsonObj = obj.getJSONObject("data");
                        Constant.TOTAL_COINS = Integer.parseInt(jsonObj.getString(Constant.COINS));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params);
    }*/

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}