package com.qearner.quiz.contest;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import com.qearner.quiz.Constant;
import com.qearner.quiz.R;

import com.qearner.quiz.UI.GradientProgress;
import com.qearner.quiz.activity.MainActivity;
import com.qearner.quiz.ads.AdUtils;
import com.qearner.quiz.vollyConfigs.ApiConfig;

import com.qearner.quiz.helper.AppController;
import com.qearner.quiz.UI.CircleImageView;
import com.qearner.quiz.helper.Session;
import com.qearner.quiz.helper.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ContestCompleteActivity extends AppCompatActivity {


    TextView txt_result_title, tvScore, tvCorrect, tvInCorrect, tvCoins;
    ScrollView scrollView;
    GradientProgress resultProgress;

    boolean isQuizCompleted;
    ProgressDialog mProgressDialog;
    RelativeLayout mainLayout;
    String contestId;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    ProgressBar progressbar;

    LinearLayout lyt_rank3, lyt_rank2, lyt_rank1;
    LinearLayout lytTop;
    AppCompatActivity activity;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tournament_complete);
        Utils.setStatusBarColor(ContestCompleteActivity.this, ContextCompat.getColor(getApplicationContext(), R.color.bg_color));
        mainLayout = findViewById(R.id.mainLayout);

        activity = ContestCompleteActivity.this;
        AdUtils.loadFacebookInterstitialAd(activity);
        contestId = getIntent().getStringExtra("qid");
        resultProgress = findViewById(R.id.resultProgress);
        resultProgress.setGradientAttributes(activity);
        scrollView = findViewById(R.id.scrollView);
        txt_result_title = findViewById(R.id.txt_result_title);
        tvCorrect = findViewById(R.id.right);
        tvInCorrect = findViewById(R.id.wrong);

        lytTop = findViewById(R.id.topLyt);
        lyt_rank1 = findViewById(R.id.lytRank1);
        lyt_rank2 = findViewById(R.id.lytRank2);
        lyt_rank3 = findViewById(R.id.lytRank3);
        tvScore = findViewById(R.id.tvScore);
        tvScore.setText(String.valueOf(Utils.quiz_score));
        tvCoins = findViewById(R.id.tvCoin);

        progressbar = findViewById(R.id.progressbar);
        isQuizCompleted = Session.isQuizCompleted(activity);
        txt_result_title.setText(getString(R.string.contest_complete));
        resultProgress.setResultAttributes(activity);
        resultProgress.setAudienceProgress(getPercentageCorrect(Utils.TotalQuestion, Utils.correctQuestion));
        tvCorrect.setText(String.valueOf(Utils.correctQuestion));
        tvInCorrect.setText(String.valueOf(Utils.wrongQuestion));

        GetUserData();
        prepareData(contestId);

    }

    public void getAllWidgets() {

    }

    private void prepareData(String contestId) {

        showProgressDialog();
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_LEADERBOARD, Constant.GET_DATA_KEY);
        params.put(Constant.CONTEST_ID, contestId);
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject1 = new JSONObject(response);
                    if (jsonObject1.getString(Constant.ERROR).equalsIgnoreCase(Constant.FALSE)) {

                        JSONArray jsonArray = jsonObject1.getJSONArray(Constant.DATA);
                        lytTop.setVisibility(View.VISIBLE);

                        for (int i = 1; i <= 3; i++) {
                            LinearLayout lyt = findViewById(getResources().getIdentifier("lytRank" + i, "id", getPackageName()));
                            lyt.setVisibility(View.INVISIBLE);
                        }

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            hideProgressDialog();

                            int rankNo = i + 1;
                            if (rankNo <= 3) {
                                LinearLayout lyt = findViewById(getResources().getIdentifier("lytRank" + rankNo, "id", getPackageName()));
                                lyt.setVisibility(View.VISIBLE);
                                TextView tvName = findViewById(getResources().getIdentifier("tvRank" + rankNo, "id", getPackageName()));
                                TextView tvScore = findViewById(getResources().getIdentifier("tvScore" + rankNo, "id", getPackageName()));
                                CircleImageView imgProfile = findViewById(getResources().getIdentifier("imgRank" + rankNo, "id", getPackageName()));

                                tvName.setText(jsonObject.getString(Constant.name));
                                tvScore.setText(jsonObject.getString(Constant.SCORE));
                                imgProfile.setImageUrl(jsonObject.getString(Constant.PROFILE), imageLoader);
                            }
                        }
                    } else {
                        hideProgressDialog();
                        lytTop.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params, activity);

    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    public static float getPercentageCorrect(int questions, int correct) {
        float proportionCorrect = ((float) correct) / ((float) questions);
        return proportionCorrect * 100;
    }


    public void ShareScore(View view) {
        String shareMsg = "I have finished " + Constant.CATE_NAME + "Quiz with " + Utils.quiz_score + " Score in " + getString(R.string.app_name);
        Utils.ShareInfo(scrollView, activity, shareMsg);
    }

    public void RateApp(View view) {
        AdUtils.showFacebookInterstitialAd(ContestCompleteActivity.this);
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
                        JSONObject jsonObj = obj.getJSONObject(Constant.DATA);
                        tvCoins.setText(jsonObj.getString(Constant.COINS));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params, activity);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}