package com.qearner.quiz.activity;

import static com.qearner.quiz.helper.AppController.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import android.widget.ImageView;

import android.widget.LinearLayout;

import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;

import com.android.volley.toolbox.NetworkImageView;
import com.facebook.ads.Ad;


import com.facebook.ads.AdError;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.RewardedVideoAdListener;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.resources.TextAppearance;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.qearner.quiz.R;
import com.qearner.quiz.UI.GradientProgress;

import com.qearner.quiz.contest.ContestCompleteActivity;
import com.qearner.quiz.login.LoginActivity;
import com.qearner.quiz.vollyConfigs.ApiConfig;
import com.qearner.quiz.helper.AppController;

import com.qearner.quiz.Constant;
import com.qearner.quiz.helper.Session;
import com.qearner.quiz.helper.Utils;
import com.qearner.quiz.model.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayActivity extends AppCompatActivity implements OnClickListener {
    Toolbar toolbar;
    Question question;
    int questionIndex = 0, btnPosition = 0,
            score = 0,
            count = 0,
            queAttend = 0,
            quiz_coin = 0,
            correctQuestion = 0,
            inCorrectQuestion = 0;
    TextView tvScore, coin_count, tvBack;
    public TextView tvA, tvB, btnOpt1, btnOpt2, btnOpt3, btnOpt4, btnOpt5, txtQuestion, tvImgQues, tvIndex;
    ImageView imgNote, fifty_fifty, skip_question, resetTimer, audience_poll;
    RelativeLayout layout_A, layout_B, layout_C, layout_D, layout_E, relayCount;
    CoordinatorLayout innerLayout;
    Animation animation;
    Handler mHandler = new Handler();
    Handler noteHandler = new Handler();
    Animation RightSwipe_A, RightSwipe_B, RightSwipe_C, RightSwipe_D, RightSwipe_E, Fade_in, fifty_fifty_anim, TimerCount;
    GradientProgress progressBarTwo_A, progressBarTwo_B, progressBarTwo_C, progressBarTwo_D, progressBarTwo_E;
    GradientProgress progressTimer;
    Timer timer;

    ArrayList<String> options;
    long leftTime = 0;
    boolean isQuizStart;
    public static ArrayList<Question> questionList;
    NetworkImageView imgQuestion;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    ImageView imgZoom;
    LinearLayout lifelineLyt, alertLyt;
    String qid, qTitle, entrypoint, type, fromQue;
    ScrollView queScroll;
    NestedScrollView mainScroll;
    AppCompatActivity activity;
    Menu myMenu;
    private RewardedVideoAd rewardedVideoAd;
    public boolean isOnBg = false;
    public static AdRequest adRequest;
    public RewardedAd rewardedVideoAds;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_play);
        activity = PlayActivity.this;
        fromQue = getIntent().getStringExtra("fromQue");
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        resetAllValue();
        final Handler handler = new Handler();

        relayCount = findViewById(R.id.relayCount);
        relayCount.setVisibility(View.VISIBLE);
        if (Utils.isNetworkAvailable(activity)) {
            getQuestionsFromJson();
            final Runnable counter = () -> {
                Utils.setleftLayoutAnimation(activity, innerLayout);
                nextQuizQuestion();
                relayCount.setVisibility(View.GONE);
                innerLayout.setVisibility(View.VISIBLE);


            };
            handler.postDelayed(counter, 3000);
        } else {
            alertLyt.setVisibility(View.VISIBLE);
            Utils.setAlertMsg(activity, "internet");
            innerLayout.setVisibility(View.GONE);
        }

        final int[] CLICKABLE = new int[]{R.id.a_layout, R.id.b_layout, R.id.c_layout, R.id.d_layout, R.id.e_layout};
        for (int i : CLICKABLE) {
            findViewById(i).setOnClickListener(this);
        }

        Session.removeSharedPreferencesData(activity);
        RightSwipe_A = AnimationUtils.loadAnimation(activity, R.anim.anim_right_a);
        RightSwipe_B = AnimationUtils.loadAnimation(activity, R.anim.anim_right_b);
        RightSwipe_C = AnimationUtils.loadAnimation(activity, R.anim.anim_right_c);
        RightSwipe_D = AnimationUtils.loadAnimation(activity, R.anim.anim_right_d);
        RightSwipe_E = AnimationUtils.loadAnimation(activity, R.anim.anim_right_e);
        TimerCount = AnimationUtils.loadAnimation(activity, R.anim.timer_counter);
        Fade_in = AnimationUtils.loadAnimation(activity, R.anim.fade_out);
        fifty_fifty_anim = AnimationUtils.loadAnimation(activity, R.anim.fifty_fifty);


        switch (fromQue) {
            case "cate":
                getSupportActionBar().setTitle(Constant.CATE_NAME);
                break;
            case "subCate":
                getSupportActionBar().setTitle(Constant.SUB_CATE_NAME);
                break;
            case "daily":
                getSupportActionBar().setTitle(getString(R.string.daily_quiz));
                break;
            case "random":
                getSupportActionBar().setTitle(getString(R.string.random_quiz));
                break;
            case "true_false":
                getSupportActionBar().setTitle(getString(R.string.true_false));
                break;
            case "contest":
                qid = getIntent().getStringExtra("id");
                qTitle = getIntent().getStringExtra("title");
                entrypoint = getIntent().getStringExtra("entrypoint");
                type = getString(R.string.contest_quiz);
                getSupportActionBar().setTitle(qTitle);
                break;
        }

        mainScroll.setOnTouchListener((v, event) -> {
            v.findViewById(R.id.queScroll).getParent().requestDisallowInterceptTouchEvent(false);
            return false;
        });
        queScroll.setOnTouchListener((v, event) -> {
            // Disallow the touch request for parent scroll on touch of child view
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });


        tvBack.setOnClickListener(v -> BackButtonMethod());
        progressTimer.setMaxProgress(Constant.CIRCULAR_MAX_PROGRESS);
        progressTimer.setCurrentProgress(Constant.CIRCULAR_MAX_PROGRESS);

        RewardAdsLoad();
        RewardsAdsLoads();
    }

    public void RewardsAdsLoads() {
        adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, Constant.ADMOB_REWARDS_ADS,
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        // Log.d(TAG, loadAdError.getMessage());
                        rewardedVideoAds = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        rewardedVideoAds = rewardedAd;

                    }
                });
    }


    public void RewardAdsLoad() {
        // Instantiate a RewardedVideoAd object.
        // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
        // now, while you are testing and replace it later when you have signed up.
        // While you are using this temporary code you will only get test ads and if you release
        // your code like this to the Google Play your users will not receive ads (you will get
        // a no fill error).
        rewardedVideoAd = new RewardedVideoAd(this, Constant.FB_REWARDS_ADS);
        RewardedVideoAdListener rewardedVideoAdListener = new RewardedVideoAdListener() {
            @Override
            public void onError(Ad ad, AdError error) {
                // Rewarded video ad failed to load
                Log.e(TAG, "Rewarded video ad failed to load: " + error.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Rewarded video ad is loaded and ready to be displayed
                Log.d(TAG, "Rewarded video ad is loaded and ready to be displayed!");
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Rewarded video ad clicked
                Log.d(TAG, "Rewarded video ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Rewarded Video ad impression - the event will fire when the
                // video starts playing
                Log.d(TAG, "Rewarded video ad impression logged!");
            }

            @Override
            public void onRewardedVideoCompleted() {
                // Rewarded Video View Complete - the video has been played to the end.
                // You can use this event to initialize your reward
                Log.d(TAG, "Rewarded video completed!");

                // Call method to give reward
                Constant.TOTAL_COINS = (Constant.TOTAL_COINS + Integer.parseInt(Constant.REWARD_COIN_VALUE));
                Utils.AddCoins(activity, Constant.REWARD_COIN_VALUE, "Rewards Ads", "PlayQuiz", "0");
                RewardAdsLoad();
            }

            @Override
            public void onRewardedVideoClosed() {
                // The Rewarded Video ad was closed - this can occur during the video
                // by closing the app, or closing the end card.
                Log.d(TAG, "Rewarded video ad closed!");
            }
        };
        rewardedVideoAd.loadAd(
                rewardedVideoAd.buildLoadAdConfig()
                        .withAdListener(rewardedVideoAdListener)
                        .build());
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


    @SuppressLint("SetTextI18n")
    public void resetAllValue() {
        tvA = findViewById(R.id.tvA);
        tvB = findViewById(R.id.tvB);
        imgNote = findViewById(R.id.imgNote);
        progressTimer = findViewById(R.id.progressTimer);
        innerLayout = findViewById(R.id.innerLayout);
        tvIndex = findViewById(R.id.tvIndex);
        tvBack = findViewById(R.id.tvBack);
        alertLyt = findViewById(R.id.lyt_alert);
        lifelineLyt = findViewById(R.id.lifelineLyt);
        mainScroll = findViewById(R.id.mainScroll);
        queScroll = findViewById(R.id.queScroll);
        coin_count = findViewById(R.id.coin_count);
        imgQuestion = findViewById(R.id.imgQuestion);
        btnOpt1 = findViewById(R.id.btnOpt1);
        btnOpt2 = findViewById(R.id.btnOpt2);
        btnOpt3 = findViewById(R.id.btnOpt3);
        btnOpt4 = findViewById(R.id.btnOpt4);
        btnOpt5 = findViewById(R.id.btnOpt5);
        imgZoom = findViewById(R.id.imgZoom);
        fifty_fifty = findViewById(R.id.fifty_fifty);
        skip_question = findViewById(R.id.skip_question);
        resetTimer = findViewById(R.id.reset_timer);
        audience_poll = findViewById(R.id.audience_poll);
        txtQuestion = findViewById(R.id.txtQuestion);
        tvImgQues = findViewById(R.id.tvImgQues);

        layout_A = findViewById(R.id.a_layout);
        layout_B = findViewById(R.id.b_layout);
        layout_C = findViewById(R.id.c_layout);
        layout_D = findViewById(R.id.d_layout);
        layout_E = findViewById(R.id.e_layout);

        progressBarTwo_A = findViewById(R.id.progress_A);
        progressBarTwo_B = findViewById(R.id.progress_B);
        progressBarTwo_C = findViewById(R.id.progress_C);
        progressBarTwo_D = findViewById(R.id.progress_D);
        progressBarTwo_E = findViewById(R.id.progress_E);


        animation = AnimationUtils.loadAnimation(activity, R.anim.right_ans_anim); // Change alpha from fully visible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the
        tvScore = findViewById(R.id.txtScore);
        tvScore.setText(String.valueOf(score));
        coin_count.setText(String.valueOf(Constant.TOTAL_COINS));

    }

    @SuppressLint("SetTextI18n")
    private void nextQuizQuestion() {
        setAgain();

        if (questionIndex < questionList.size()) {
            starTimer();
            queAttend = queAttend + 1;
            count = 0;
            question = questionList.get(questionIndex);
            int temp = (questionIndex + 1);

            //imgQuestion.resetZoom();
            tvIndex.setText(temp + "/" + questionList.size());
            if (!question.getImage().isEmpty()) {
                imgQuestion.setImageUrl(question.getImage(), imageLoader);
                tvImgQues.startAnimation(Fade_in);
                tvImgQues.setText(question.getQuestion());
                txtQuestion.setVisibility(View.GONE);
                tvImgQues.setVisibility(View.VISIBLE);
                // imgZoom.setVisibility(View.VISIBLE);
                imgQuestion.setVisibility(View.VISIBLE);
                /*imgZoom.setOnClickListener(view -> {
                    click++;
                    if (click == 1)
                        imgQuestion.setZoom(1.25f);
                    else if (click == 2)
                        imgQuestion.setZoom(1.50f);
                    else if (click == 3)
                        imgQuestion.setZoom(1.75f);
                    else if (click == 4) {
                        imgQuestion.setZoom(2.00f);
                        click = 0;
                    }
                });*/
            } else {
                txtQuestion.startAnimation(Fade_in);
                txtQuestion.setText(question.getQuestion());
                txtQuestion.setVisibility(View.VISIBLE);
                tvImgQues.setVisibility(View.GONE);
                // imgZoom.setVisibility(View.GONE);
                imgQuestion.setVisibility(View.GONE);
            }

            options = new ArrayList<>();
            options.addAll(question.getOptions());
            if (question.getQueType().equals(Constant.TRUE_FALSE)) {
                tvA.setVisibility(View.GONE);
                tvB.setVisibility(View.GONE);
                layout_C.setVisibility(View.GONE);
                layout_D.setVisibility(View.GONE);
                btnOpt1.setGravity(Gravity.CENTER);
                btnOpt2.setGravity(Gravity.CENTER);
                lifelineLyt.setVisibility(View.GONE);
            } else {
                Collections.shuffle(options);
                layout_C.setVisibility(View.VISIBLE);
                layout_D.setVisibility(View.VISIBLE);
                btnOpt1.setGravity(Gravity.NO_GRAVITY);
                btnOpt2.setGravity(Gravity.NO_GRAVITY);
                lifelineLyt.setVisibility(View.VISIBLE);
            }
            if (Session.getBoolean(Session.E_MODE, getApplicationContext())) {
                if (options.size() == 4)
                    layout_E.setVisibility(View.GONE);
                else
                    layout_E.setVisibility(View.VISIBLE);
            }
            btnOpt1.setText(options.get(0));
            btnOpt2.setText(options.get(1));
            btnOpt3.setText(options.get(2));
            btnOpt4.setText(options.get(3));
            if (Session.getBoolean(Session.E_MODE, getApplicationContext())) {
                if (options.size() == 5)
                    btnOpt5.setText(options.get(4));
            }
        } else {
            quizCompleted();
        }
    }

    public void quizCompleted() {
        checkUserStatus();
        Utils.TotalQuestion = questionList.size();
        Utils.correctQuestion = correctQuestion;
        Utils.wrongQuestion = inCorrectQuestion;

        stopTimer();
        int total = questionList.size();
        int percent = (correctQuestion * 100) / total;

        Utils.quiz_coin = quiz_coin;
        Utils.quiz_score = score;

        coin_count.setText(String.valueOf(Constant.TOTAL_COINS));
        Session.setQuizComplete(getApplicationContext(), percent >= Constant.PASSING_PER);
        switch (fromQue) {
            case "cate":
            case "subCate":
                if (!Constant.isPlayed) {
                    if (score >= 0)
                        UpdateScore(String.valueOf(score));
                    if (quiz_coin != 0)
                        Utils.AddCoins(activity, "" + quiz_coin, "Play Quiz", "Play Zone", (quiz_coin < 0) ? "1" : "0");
                    SetUserStatistics(String.valueOf(questionList.size()), String.valueOf(correctQuestion), String.valueOf(percent));
                    ApiConfig.setPlayedStatus(activity, Constant.CATE_ID, Constant.SUB_CAT_ID);
                }
                callCompleteResult();

                break;
            case "daily":
                Utils.AddCoins(activity, "" + quiz_coin, "Daily Quiz Coin", "Daily Quiz", (quiz_coin < 0) ? "1" : "0");
                Utils.updateCoinOrDailyQuizStatus(activity, Constant.DailyPlayed);
                callCompleteResult();
                break;
            case "random":
            case "true_false":
                callCompleteResult();
                break;
            case "contest":
                updateContestScore(String.valueOf(score), String.valueOf(queAttend));
                Utils.AddCoins(activity, "" + quiz_coin, "Contest Quiz", "Contest Zone", (quiz_coin < 0) ? "1" : "0");
                Intent contest = new Intent(activity, ContestCompleteActivity.class);
                contest.putExtra("qid", qid);
                startActivity(contest);
                break;
        }
        finish();
        blankAllValue();

    }

    public void callCompleteResult() {
        Intent randomIntent = new Intent(activity, CompleteActivity.class);
        randomIntent.putExtra("fromQue", fromQue);
        startActivity(randomIntent);
    }

    public void AddReview(Question question, TextView tvBtnOpt, RelativeLayout layout) {
        layout_A.setClickable(false);
        layout_B.setClickable(false);
        layout_C.setClickable(false);
        layout_D.setClickable(false);
        layout_E.setClickable(false);

        if (tvBtnOpt.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            rightSound();

            layout.setBackgroundResource(R.drawable.right_gradient);
            layout.startAnimation(animation);
            score = score + Constant.FOR_CORRECT_ANS;
            switch (fromQue) {
                case "cate":
                case "subCate":
                case "daily":
                case "contest":
                    if (!Constant.isPlayed) {
                        quiz_coin = quiz_coin + Integer.parseInt(Constant.FOR_CORRECT_ANS_COIN);
                        Constant.TOTAL_COINS = (Constant.TOTAL_COINS + Integer.parseInt(Constant.FOR_CORRECT_ANS_COIN));
                    }
                    break;
            }
            correctQuestion = correctQuestion + 1;
        } else {
            playWrongSound();
            layout.setBackgroundResource(R.drawable.wrong_gradient);
            score = score - Constant.PENALTY;
            switch (fromQue) {
                case "cate":
                case "subCate":
                case "daily":
                case "contest":

                    if (!Constant.isPlayed) {
                        quiz_coin = quiz_coin - Integer.parseInt(Constant.PENALTY_COIN);
                        Constant.TOTAL_COINS = (Constant.TOTAL_COINS - Integer.parseInt(Constant.PENALTY_COIN));
                    }
                    break;
            }
            inCorrectQuestion = inCorrectQuestion + 1;
        }
        coin_count.setText(String.valueOf(Constant.TOTAL_COINS));
        tvScore.setText(String.valueOf(score));
        question.setSelectedAns(tvBtnOpt.getText().toString());
        if (Constant.QUICK_ANSWER_ENABLE.equals("1"))
            RightAnswerBackgroundSet();
        question.setAttended(true);
        stopTimer();

        if (question.getNote().isEmpty()) {
            questionIndex++;
            mHandler.postDelayed(mUpdateUITimerTask, 1000);
        } else {
            noteHandler.postDelayed(mNoteDialog, 500);

        }

    }

    public void RightAnswerBackgroundSet() {
        if (btnOpt1.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            layout_A.setBackgroundResource(R.drawable.right_gradient);
            layout_A.startAnimation(animation);

        } else if (btnOpt2.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            layout_B.setBackgroundResource(R.drawable.right_gradient);
            layout_B.startAnimation(animation);

        } else if (btnOpt3.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            layout_C.setBackgroundResource(R.drawable.right_gradient);
            layout_C.startAnimation(animation);

        } else if (btnOpt4.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            layout_D.setBackgroundResource(R.drawable.right_gradient);
            layout_D.startAnimation(animation);

        } else if (btnOpt5.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            layout_E.setBackgroundResource(R.drawable.right_gradient);
            layout_E.startAnimation(animation);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        if (questionIndex < questionList.size()) {
            question = questionList.get(questionIndex);
            layout_A.setClickable(false);
            layout_B.setClickable(false);
            layout_C.setClickable(false);
            layout_D.setClickable(false);
            layout_E.setClickable(false);
            if (progressBarTwo_A.getVisibility() == (View.VISIBLE)) {
                progressBarTwo_A.setVisibility(View.INVISIBLE);
                progressBarTwo_B.setVisibility(View.INVISIBLE);
                progressBarTwo_C.setVisibility(View.INVISIBLE);
                progressBarTwo_D.setVisibility(View.INVISIBLE);
                progressBarTwo_E.setVisibility(View.INVISIBLE);
            }
            switch (v.getId()) {
                case R.id.a_layout:
                    AddReview(question, btnOpt1, layout_A);
                    break;
                case R.id.b_layout:
                    AddReview(question, btnOpt2, layout_B);
                    break;
                case R.id.c_layout:
                    AddReview(question, btnOpt3, layout_C);
                    break;
                case R.id.d_layout:
                    AddReview(question, btnOpt4, layout_D);
                    break;
                case R.id.e_layout:
                    AddReview(question, btnOpt5, layout_E);
                    break;
            }

        }
    }


    private final Runnable mUpdateUITimerTask = () -> {
        if (getApplicationContext() != null) {
            nextQuizQuestion();
        }
    };
    private final Runnable mNoteDialog = () -> {
        if (getApplicationContext() != null) {
            noteDialog(question.getNote());
        }
    };

    public void PlayAreaLeaveDialog() {
        if (isQuizStart) {
            stopTimer();
            final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View dialogView = inflater.inflate(R.layout.dialog_leave_test, null);
            dialog.setView(dialogView);
            TextView btnLeave = dialogView.findViewById(R.id.btnLeave);
            TextView btnResume = dialogView.findViewById(R.id.btnResume);
            TextView txtFirstMsg = dialogView.findViewById(R.id.txtFirstmsg);
            TextView text = dialogView.findViewById(R.id.text);
            txtFirstMsg.setText(getString(R.string.exit_msg_quiz));
            text.setVisibility(View.GONE);
            AlertDialog alertDialog = dialog.create();
            Utils.setDialogBg(alertDialog);
            alertDialog.show();
            alertDialog.setCancelable(false);
            btnLeave.setOnClickListener(view -> {
                stopTimer();
                leftTime = 0;
                Constant.LeftTime = 0;
                if (quiz_coin != 0) {
                    if (fromQue.equals("cate") || fromQue.equals("subCate")) {
                        if (!Constant.isPlayed)
                            Utils.AddCoins(activity, "" + quiz_coin, "Play Quiz", "Play Zone", (quiz_coin < 0) ? "1" : "0");
                    } else if (fromQue.equals("contest")) {
                        Utils.AddCoins(activity, "" + quiz_coin, "Contest Quiz", " Contest Zone", (quiz_coin < 0) ? "1" : "0");
                        updateContestScore(String.valueOf(score), String.valueOf(queAttend));
                    }
                }
                alertDialog.dismiss();
                finish();
            });


            btnResume.setOnClickListener(view -> {
                alertDialog.dismiss();
                Constant.LeftTime = leftTime;
                if (Constant.LeftTime != 0) {
                    timer = new Timer(leftTime, 1000);
                    timer.start();
                }
            });
            alertDialog.show();
        } else {
            stopTimer();
            finish();
        }

    }

    public void updateContestScore(final String score, final String queAttend) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.CONTEST_UPDATE_SCORE, Constant.GET_DATA_KEY);
        params.put(Constant.CONTEST_ID, qid);
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, activity));
        params.put(Constant.QUESTION_ATTEND, queAttend);
        params.put(Constant.CORRECT_ANSWERS, "" + Utils.correctQuestion);
        params.put(Constant.SCORE, "" + score);
        ApiConfig.RequestToVolley((result, response) -> {

        }, params, activity);

    }


    //play sound when answer is correct
    public void rightSound() {
        if (Session.getSoundEnableDisable(activity))
            Utils.setRightAnsSound(activity);

        if (Session.getVibration(activity))
            Utils.vibrate(activity, Utils.VIBRATION_DURATION);

    }

    //play sound when answer is incorrect
    private void playWrongSound() {
        if (Session.getSoundEnableDisable(activity))
            Utils.setWrongAnsSound(activity);

        if (Session.getVibration(activity))
            Utils.vibrate(activity, Utils.VIBRATION_DURATION);

    }

    public void noteDialog(String note) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_default, null);
        dialog.setView(dialogView);
        ImageView image = dialogView.findViewById(R.id.image);
        TextView ok = dialogView.findViewById(R.id.ok);
        TextView tvTitle = dialogView.findViewById(R.id.title);
        tvTitle.setTextSize(16);
        tvTitle.setText(note);
        ok.setText(activity.getResources().getString(R.string.exit));
        image.setImageResource(R.drawable.ic_note);
        image.setMinimumHeight(50);
        image.setMinimumWidth(50);
        final AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        alertDialog.setCancelable(false);
        ok.setOnClickListener(view -> {
            alertDialog.dismiss();
            questionIndex++;
            mHandler.postDelayed(mUpdateUITimerTask, 1000);
        });
    }

    //set progress again after next question
    private void setAgain() {
        stopTimer();
        Constant.LeftTime = 0;
        leftTime = 0;
        queScroll.scrollTo(0, 0);

        if (progressBarTwo_A.getVisibility() == (View.VISIBLE)) {
            progressBarTwo_A.setVisibility(View.INVISIBLE);
            progressBarTwo_B.setVisibility(View.INVISIBLE);
            progressBarTwo_C.setVisibility(View.INVISIBLE);
            progressBarTwo_D.setVisibility(View.INVISIBLE);
            progressBarTwo_E.setVisibility(View.INVISIBLE);
        }

        tvA.setVisibility(View.VISIBLE);
        tvB.setVisibility(View.VISIBLE);
        layout_A.setBackgroundResource(R.drawable.card_bg_radius_5);
        layout_B.setBackgroundResource(R.drawable.card_bg_radius_5);
        layout_C.setBackgroundResource(R.drawable.card_bg_radius_5);
        layout_D.setBackgroundResource(R.drawable.card_bg_radius_5);
        layout_E.setBackgroundResource(R.drawable.card_bg_radius_5);
        layout_A.clearAnimation();
        layout_B.clearAnimation();
        layout_C.clearAnimation();
        layout_D.clearAnimation();
        layout_E.clearAnimation();
        txtQuestion.clearAnimation();
        tvImgQues.clearAnimation();
        layout_A.setClickable(true);
        layout_B.setClickable(true);
        layout_C.setClickable(true);
        layout_D.setClickable(true);
        layout_E.setClickable(true);
        layout_A.startAnimation(RightSwipe_A);
        layout_B.startAnimation(RightSwipe_B);
        layout_C.startAnimation(RightSwipe_C);
        layout_D.startAnimation(RightSwipe_D);
        layout_E.startAnimation(RightSwipe_E);
        progressBarTwo_A.setAudiencePollAttributes(activity);
        progressBarTwo_B.setAudiencePollAttributes(activity);
        progressBarTwo_C.setAudiencePollAttributes(activity);
        progressBarTwo_D.setAudiencePollAttributes(activity);
        progressBarTwo_E.setAudiencePollAttributes(activity);
    }

    public void FiftyFifty(View view) {
        Utils.btnClick(view, activity);
        if (!Session.isFiftyFiftyUsed(activity)) {

            if (Constant.TOTAL_COINS >= Constant.FIFTY_AUD_COINS) {
                btnPosition = 0;
                Constant.TOTAL_COINS = Constant.TOTAL_COINS - Constant.FIFTY_AUD_COINS;
                quiz_coin = quiz_coin - Constant.FIFTY_AUD_COINS;

                //Utils.UpdateCoin(activity, "-" + Constant.FIFTY_AUD_COINS);
                Utils.AddCoins(activity, "-" + Constant.FIFTY_AUD_COINS, "50-50 Lifeline use", "PlayQuiz", "1");
                coin_count.setText(String.valueOf(Constant.TOTAL_COINS));
                if (btnOpt1.getText().toString().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns()))
                    btnPosition = 1;
                if (btnOpt2.getText().toString().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns()))
                    btnPosition = 2;
                if (btnOpt3.getText().toString().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns()))
                    btnPosition = 3;
                if (btnOpt4.getText().toString().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns()))
                    btnPosition = 4;

                if (Session.getBoolean(Session.E_MODE, getApplicationContext()))
                    FiftyFiftyWith_E();
                else
                    FiftyFiftyWithout_E();

                Session.setFifty_Fifty(activity);
            } else
                ShowRewarded(activity, Constant.FIFTY_AUD_COINS);

        } else
            AlreadyUsed();
    }

    public void FiftyFiftyWithout_E() {

        if (btnPosition == 1) {
            layout_B.startAnimation(fifty_fifty_anim);
            layout_C.startAnimation(fifty_fifty_anim);
            layout_B.setClickable(false);
            layout_C.setClickable(false);

        } else if (btnPosition == 2) {
            layout_C.startAnimation(fifty_fifty_anim);
            layout_D.startAnimation(fifty_fifty_anim);
            layout_C.setClickable(false);
            layout_D.setClickable(false);

        } else if (btnPosition == 3) {
            layout_D.startAnimation(fifty_fifty_anim);
            layout_A.startAnimation(fifty_fifty_anim);
            layout_D.setClickable(false);
            layout_A.setClickable(false);

        } else if (btnPosition == 4) {
            layout_A.startAnimation(fifty_fifty_anim);
            layout_B.startAnimation(fifty_fifty_anim);
            layout_A.setClickable(false);
            layout_B.setClickable(false);
        }
    }

    public void FiftyFiftyWith_E() {
        if (btnOpt5.getText().toString().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns())) {
            btnPosition = 5;
        }

        if (btnPosition == 1) {
            layout_B.startAnimation(fifty_fifty_anim);
            layout_C.startAnimation(fifty_fifty_anim);
            layout_B.setClickable(false);
            layout_C.setClickable(false);

        } else if (btnPosition == 2) {
            layout_C.startAnimation(fifty_fifty_anim);
            layout_D.startAnimation(fifty_fifty_anim);
            layout_C.setClickable(false);
            layout_D.setClickable(false);

        } else if (btnPosition == 3) {
            layout_D.startAnimation(fifty_fifty_anim);
            layout_E.startAnimation(fifty_fifty_anim);
            layout_D.setClickable(false);
            layout_E.setClickable(false);

        } else if (btnPosition == 4) {

            layout_E.startAnimation(fifty_fifty_anim);
            layout_A.startAnimation(fifty_fifty_anim);
            layout_E.setClickable(false);
            layout_A.setClickable(false);

        } else if (btnPosition == 5) {
            layout_A.startAnimation(fifty_fifty_anim);
            layout_B.startAnimation(fifty_fifty_anim);
            layout_A.setClickable(false);
            layout_B.setClickable(false);
        }
    }

    public void SkipQuestion(View view) {
        Utils.btnClick(view, activity);
        if (!Session.isSkipUsed(activity)) {
            if (Constant.TOTAL_COINS >= Constant.RESET_SKIP_COINS) {
                stopTimer();
                leftTime = 0;
                Constant.LeftTime = 0;

                Constant.TOTAL_COINS = Constant.TOTAL_COINS - Constant.RESET_SKIP_COINS;
                quiz_coin = quiz_coin - Constant.RESET_SKIP_COINS;
                //  Utils.UpdateCoin(activity, "-" + Constant.RESET_SKIP_COINS);
                Utils.AddCoins(activity, "-" + Constant.RESET_SKIP_COINS, "Skip Lifeline use", "PlayQuiz", "1");
                coin_count.setText(String.valueOf(Constant.TOTAL_COINS));
                questionIndex++;
                nextQuizQuestion();
                Session.setSkip(activity);
            } else
                ShowRewarded(activity, Constant.RESET_SKIP_COINS);
        } else
            AlreadyUsed();
    }

    public void AudiencePoll(View view) {
        Utils.btnClick(view, activity);
        if (!Session.isAudiencePollUsed(activity)) {
            if (Constant.TOTAL_COINS >= Constant.FIFTY_AUD_COINS) {
                btnPosition = 0;
                Constant.TOTAL_COINS = Constant.TOTAL_COINS - Constant.FIFTY_AUD_COINS;
                quiz_coin = quiz_coin - Constant.FIFTY_AUD_COINS;
                // Utils.UpdateCoin(activity, "-" + Constant.FIFTY_AUD_COINS);
                Utils.AddCoins(activity, "-" + Constant.FIFTY_AUD_COINS, "AudiencePoll Lifeline use", "PlayQuiz", "1");
                coin_count.setText(String.valueOf(Constant.TOTAL_COINS));
                if (Session.getBoolean(Session.E_MODE, getApplicationContext()))
                    AudienceWith_E();
                else
                    AudienceWithout_E();

                Session.setAudiencePoll(activity);
            } else
                ShowRewarded(activity, Constant.FIFTY_AUD_COINS);
        } else
            AlreadyUsed();
    }

    public void AudienceWithout_E() {
        int min = 45;
        int max = 70;
        Random r = new Random();
        int A = r.nextInt(max - min + 1) + min;
        int remain1 = 100 - A;
        int B = r.nextInt(((remain1 - 10)) + 1);
        int remain2 = remain1 - B;
        int C = r.nextInt(((remain2 - 5)) + 1);
        int D = remain2 - C;
        progressBarTwo_A.setVisibility(View.VISIBLE);
        progressBarTwo_B.setVisibility(View.VISIBLE);
        progressBarTwo_C.setVisibility(View.VISIBLE);
        progressBarTwo_D.setVisibility(View.VISIBLE);

        if (btnOpt1.getText().toString().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns()))
            btnPosition = 1;
        if (btnOpt2.getText().toString().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns()))
            btnPosition = 2;
        if (btnOpt3.getText().toString().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns()))
            btnPosition = 3;
        if (btnOpt4.getText().toString().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns()))
            btnPosition = 4;

        if (btnPosition == 1) {
            progressBarTwo_A.setAudienceProgress(A);
            progressBarTwo_B.setAudienceProgress(B);
            progressBarTwo_C.setAudienceProgress(C);
            progressBarTwo_D.setAudienceProgress(D);

        } else if (btnPosition == 2) {
            progressBarTwo_B.setAudienceProgress(A);
            progressBarTwo_C.setAudienceProgress(C);
            progressBarTwo_D.setAudienceProgress(D);
            progressBarTwo_A.setAudienceProgress(B);

        } else if (btnPosition == 3) {
            progressBarTwo_C.setAudienceProgress(A);
            progressBarTwo_B.setAudienceProgress(C);
            progressBarTwo_D.setAudienceProgress(D);
            progressBarTwo_A.setAudienceProgress(B);

        } else if (btnPosition == 4) {
            progressBarTwo_D.setAudienceProgress(A);
            progressBarTwo_B.setAudienceProgress(C);
            progressBarTwo_C.setAudienceProgress(D);
            progressBarTwo_A.setAudienceProgress(B);

        }
    }

    public void AudienceWith_E() {
        int min = 45;
        int max = 70;
        Random r = new Random();
        int A = r.nextInt(max - min + 1) + min;
        int remain1 = 100 - A;
        int B = r.nextInt(((remain1 - 8)) + 1);
        int remain2 = remain1 - B;
        int C = r.nextInt(((remain2 - 4)) + 1);
        int remain3 = remain2 - C;
        int D = r.nextInt(((remain3 - 2)) + 1);
        int E = remain3 - D;
        progressBarTwo_A.setVisibility(View.VISIBLE);
        progressBarTwo_B.setVisibility(View.VISIBLE);
        progressBarTwo_C.setVisibility(View.VISIBLE);
        progressBarTwo_D.setVisibility(View.VISIBLE);
        progressBarTwo_E.setVisibility(View.VISIBLE);

        if (btnOpt1.getText().toString().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns()))
            btnPosition = 1;
        if (btnOpt2.getText().toString().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns()))
            btnPosition = 2;
        if (btnOpt3.getText().toString().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns()))
            btnPosition = 3;
        if (btnOpt4.getText().toString().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns()))
            btnPosition = 4;
        if (btnOpt5.getText().toString().equalsIgnoreCase(questionList.get(questionIndex).getTrueAns()))
            btnPosition = 5;

        if (btnPosition == 1) {
            progressBarTwo_A.setAudienceProgress(A);
            progressBarTwo_B.setAudienceProgress(B);
            progressBarTwo_C.setAudienceProgress(C);
            progressBarTwo_D.setAudienceProgress(D);
            progressBarTwo_E.setAudienceProgress(E);

        } else if (btnPosition == 2) {

            progressBarTwo_B.setAudienceProgress(A);
            progressBarTwo_C.setAudienceProgress(B);
            progressBarTwo_D.setAudienceProgress(C);
            progressBarTwo_E.setAudienceProgress(D);
            progressBarTwo_A.setAudienceProgress(E);

        } else if (btnPosition == 3) {

            progressBarTwo_C.setAudienceProgress(A);
            progressBarTwo_D.setAudienceProgress(B);
            progressBarTwo_E.setAudienceProgress(C);
            progressBarTwo_A.setAudienceProgress(D);
            progressBarTwo_B.setAudienceProgress(E);

        } else if (btnPosition == 4) {

            progressBarTwo_D.setAudienceProgress(A);
            progressBarTwo_E.setAudienceProgress(B);
            progressBarTwo_A.setAudienceProgress(C);
            progressBarTwo_B.setAudienceProgress(D);
            progressBarTwo_C.setAudienceProgress(E);

        } else if (btnPosition == 5) {
            progressBarTwo_E.setAudienceProgress(A);
            progressBarTwo_A.setAudienceProgress(B);
            progressBarTwo_B.setAudienceProgress(C);
            progressBarTwo_C.setAudienceProgress(D);
            progressBarTwo_D.setAudienceProgress(E);
        }
    }

    public void ResetTimer(View view) {
        Utils.btnClick(view, activity);
        if (!Session.isResetUsed(activity)) {
            if (Constant.TOTAL_COINS >= Constant.RESET_SKIP_COINS) {

                Constant.TOTAL_COINS = Constant.TOTAL_COINS - Constant.RESET_SKIP_COINS;
                quiz_coin = quiz_coin - Constant.RESET_SKIP_COINS;

                Utils.AddCoins(activity, "-" + Constant.RESET_SKIP_COINS, "ResetTimer Lifeline use", "PlayQuiz", "1");
                coin_count.setText(String.valueOf(Constant.TOTAL_COINS));
                Constant.LeftTime = 0;
                leftTime = 0;
                stopTimer();
                starTimer();
                Session.setReset(activity);
            } else
                ShowRewarded(activity, Constant.RESET_SKIP_COINS);
        } else
            AlreadyUsed();
    }

    //Show alert dialog when lifeline already used in current quiz
    public void AlreadyUsed() {
        stopTimer();

        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.lifeline_dialog, null);
        dialog.setView(dialogView);
        TextView ok = dialogView.findViewById(R.id.ok);
        final AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        alertDialog.setCancelable(false);
        ok.setOnClickListener(view -> {
            alertDialog.dismiss();
            if (leftTime != 0) {
                timer = new Timer(leftTime, 1000);
                timer.start();
            }
        });
    }

    public void BackButtonMethod() {

        CheckSound();
        PlayAreaLeaveDialog();

    }

    public void CheckSound() {

        if (Session.getSoundEnableDisable(activity))
            Utils.backSoundOnclick(activity);
        if (Session.getVibration(activity))
            Utils.vibrate(activity, Utils.VIBRATION_DURATION);
    }

    public void SettingButtonMethod() {
        CheckSound();
        stopTimer();

        Intent intent = new Intent(activity, SettingActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.open_next, R.anim.close_next);
    }

    public void getQuestionsFromJson() {
        Map<String, String> params = new HashMap<>();

        switch (fromQue) {
            case "cate":
                params.put(Constant.GET_QUES_BY_CATE, "1");
                params.put(Constant.category, "" + Constant.CATE_ID);
                break;
            case "subCate":
                params.put(Constant.GET_QUES_BY_SUB_CATE, "1");
                params.put(Constant.subCategoryId, "" + Constant.SUB_CAT_ID);
                break;
            case "daily":
                params.put(Constant.getDailyQuiz, "1");
                break;
            case "random":
                params.put(Constant.get_questions_by_type, "1");
                params.put(Constant.type, "1");
                params.put(Constant.limit, String.valueOf(Constant.RANDOM_QUE_LIMIT));
                break;
            case "true_false":
                params.put(Constant.get_questions_by_type, "1");
                params.put(Constant.type, "2");
                params.put(Constant.limit, String.valueOf(Constant.RANDOM_QUE_LIMIT));
                break;
            case "contest":
                params.put(Constant.GET_QUESTION_BY_CONTEST, Constant.GET_DATA_KEY);
                params.put(Constant.CONTEST_ID, qid);
                break;

        }
        if (Session.getBoolean(Session.LANG_MODE, getApplicationContext()))
            params.put(Constant.LANGUAGE_ID, Session.getCurrentLanguage(getApplicationContext()));

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    System.out.println("=== ques " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean(Constant.ERROR);
                    if (!error) {
                        isQuizStart = true;
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        questionList = new ArrayList<>();
                        questionList.addAll(Utils.getQuestions(jsonArray, activity));

                        if (fromQue.equals("contest"))
                            Utils.AddCoins(activity, "-" + entrypoint, "Contest", "Contest Entry Coins", "1");
                    } else {
                        isQuizStart = false;
                        if (jsonObject.getString(Constant.LOGIN).equalsIgnoreCase(Constant.TRUE)) {
                            Utils.userLoggedOutDialog(activity);
                        } else {
                            NotEnoughQuestion();
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params, activity);

    }


    public void NotEnoughQuestion() {
        invalidateOptionsMenu();
        alertLyt.setVisibility(View.VISIBLE);

        Utils.setAlertMsg(activity, "question");
        innerLayout.setVisibility(View.GONE);

    }

    public void blankAllValue() {
        questionIndex = 0;
        score = 0;
        correctQuestion = 0;
        inCorrectQuestion = 0;
    }

    public void UpdateScore(final String score) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.setMonthlyLeaderboard, "1");
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, activity));
        params.put(Constant.SCORE, score);
        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {
                try {
                    JSONObject obj = new JSONObject(response);
                    boolean error = obj.getBoolean(Constant.ERROR);
                    String message = obj.getString(Constant.MESSAGE);
                    if (error) {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params, activity);
    }

    public void SetUserStatistics(final String ttlQue, final String correct,
                                  final String percent) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.SET_USER_STATISTICS, "1");
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, activity));
        params.put(Constant.QUESTION_ANSWERED, ttlQue);
        params.put(Constant.CORRECT_ANSWERS, correct);
        params.put(Constant.COINS, String.valueOf(Constant.TOTAL_COINS));
        params.put(Constant.RATIO, percent);
        params.put(Constant.cate_id, String.valueOf(Constant.CATE_ID));
        ApiConfig.RequestToVolley((result, response) -> {

        }, params, activity);

    }

    //Show dialog for rewarded ad
    //if user has not enough coin to use lifeline
    @SuppressLint("SetTextI18n")
    public void ShowRewarded(final Activity activity, int coin) {
        stopTimer();

        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_alert_coin, null);
        dialog.setView(dialogView);
        TextView tvCoinMsg = dialogView.findViewById(R.id.tvCoinMsg);
        TextView skip = dialogView.findViewById(R.id.skip);
        TextView watchNow = dialogView.findViewById(R.id.watch_now);
        tvCoinMsg.setText(activity.getResources().getString(R.string.coin_message1) + coin + activity.getResources().getString(R.string.coin_message2));
        final AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        alertDialog.setCancelable(false);
        skip.setOnClickListener(view -> {
            alertDialog.dismiss();
            if (leftTime != 0) {
                timer = new Timer(leftTime, 1000);
                timer.start();
            }
        });
        watchNow.setOnClickListener(view -> {
            if (Constant.ADS_TYPE.equals("1")) {
                showRewardedVideos();
            } else {
                showRewardedVideo();
            }
            alertDialog.dismiss();
        });

    }

    public void showRewardedVideo() {
        if (rewardedVideoAd != null) {
            Activity activityContext = activity;

            // Check if rewardedVideoAd has been loaded successfully
            if (!rewardedVideoAd.isAdLoaded()) {
                return;
            }
            // Check if ad is already expired or invalidated, and do not show ad if that is the case. You will not get paid to show an invalidated ad.
            if (rewardedVideoAd.isAdInvalidated()) {
                return;
            }
            rewardedVideoAd.show();
        } else {
            reWardsNotLoad();
        }
    }

    public void showRewardedVideos() {
        if (rewardedVideoAds != null) {
            Activity activityContext = activity;
            rewardedVideoAds.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    rewardedVideoAds = null;
                }

                @Override
                public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                    // Called when ad fails to show.

                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    RewardsAdsLoads();
                    // Called when ad is dismissed.
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.

                }
            });
            rewardedVideoAds.show(activityContext, rewardItem -> {
                Constant.TOTAL_COINS = (Constant.TOTAL_COINS + Integer.parseInt(Constant.REWARD_COIN_VALUE));
                Utils.AddCoins(activity, Constant.REWARD_COIN_VALUE, "Rewards Ads", "PlayQuiz", "0");

                RewardsAdsLoads();
            });


        } else {
            reWardsNotLoad();
        }
    }


    public void reWardsNotLoad() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.lifeline_dialog, null);
        dialog.setView(dialogView);
        TextView ok = dialogView.findViewById(R.id.ok);
        TextView title = dialogView.findViewById(R.id.title);
        TextView message = dialogView.findViewById(R.id.message);
        message.setText(getString(R.string.rewards_message));
        title.setText(getString(R.string.reward_ads_title));
        final AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        alertDialog.setCancelable(false);
        ok.setOnClickListener(view -> {
            if (Constant.ADS_TYPE.equals("1")) {
                RewardsAdsLoads();
            } else {
                RewardAdsLoad();
            }

            alertDialog.dismiss();

        });
    }


    public class Timer extends CountDownTimer {
        private Timer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            leftTime = millisUntilFinished;
            count = (int) (millisUntilFinished / 1000);
            int progress = (int) (millisUntilFinished / 1000);
            if (progressTimer == null)
                progressTimer = findViewById(R.id.circleTimer);
            else
                progressTimer.setAudienceProgress(progress);

            //when left last 5 second we show progress color red
            if (millisUntilFinished <= 6000)
                progressTimer.SetTimerAttributes(Color.RED, Color.RED);
            else
                progressTimer.setGradientAttributes(activity);

        }

        @Override
        public void onFinish() {
            if (questionIndex >= questionList.size()) {
                quizCompleted();
            } else {
                if (isOnBg) {
                    playWrongSound();
                }
                score = score - Constant.PENALTY;
                inCorrectQuestion = inCorrectQuestion + 1;
                tvScore.setText(String.valueOf(score));
                mHandler.postDelayed(mUpdateUITimerTask, 100);
                questionIndex++;
            }
            stopTimer();
        }
    }

    public void starTimer() {
        timer = new Timer(Constant.TIME_PER_QUESTION, Constant.COUNT_DOWN_TIMER);
        timer.start();
    }

    public void stopTimer() {
        if (timer != null)
            timer.cancel();
    }
    public void checkUserStatus() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_USER_BY_ID, "1");
        params.put(Constant.ID, Session.getUserData(Session.USER_ID, activity));
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    System.out.println("=== user status " + response);
                    JSONObject obj = new JSONObject(response);
                    String error = obj.getString(Constant.ERROR);
                    if (error.equalsIgnoreCase(Constant.FALSE)) {


                    } else {
                        if (obj.getString(Constant.LOGIN).equalsIgnoreCase(Constant.TRUE)) {
                           Utils.userLoggedOutDialog(activity);

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params, activity);
    }


    @Override
    public void onResume() {
        super.onResume();
        isOnBg = true;
        if (Constant.LeftTime != 0) {
            timer = new Timer(leftTime, 1000);
            timer.start();
        }
        coin_count.setText(String.valueOf(Constant.TOTAL_COINS));
    }

    @Override
    public void onPause() {
        super.onPause();
        isOnBg = false;
        Constant.LeftTime = leftTime;
        stopTimer();
    }


    @Override
    public void onDestroy() {
        if (rewardedVideoAd != null) {
            rewardedVideoAd.destroy();
            rewardedVideoAd = null;
        }
        leftTime = 0;
        stopTimer();

        finish();
        blankAllValue();
        super.onDestroy();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        myMenu = menu;
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.report).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.setting) {
            SettingButtonMethod();
            return true;
        } else if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onBackPressed() {

        PlayAreaLeaveDialog();

    }
}