package com.qearner.quiz.battle;

import android.annotation.SuppressLint;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.android.volley.toolbox.ImageLoader;

import com.android.volley.toolbox.NetworkImageView;

import com.google.android.material.snackbar.Snackbar;
import com.qearner.quiz.Constant;
import com.qearner.quiz.R;
import com.qearner.quiz.UI.GradientProgress;
import com.qearner.quiz.activity.SettingActivity;
import com.qearner.quiz.vollyConfigs.ApiConfig;
import com.qearner.quiz.helper.AppController;
import com.qearner.quiz.UI.CircleImageView;
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


public class RobotPlayActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tvA, tvB, btnOpt1, btnOpt2, btnOpt3, btnOpt4, btnOpt5, txtQuestion, tvImgQues, tvPlayer1Name, tvPlayer2Name, tvIndex;
    ArrayList<String> options;
    Boolean virtual_play = false;
    Question question;
    Toolbar toolbar;

    Animation RightSwipe_A, RightSwipe_B, RightSwipe_C, RightSwipe_D, RightSwipe_E, Fade_in;
    CircleImageView imgPlayer1, imgPlayer2;
    ImageView imgZoom;
    Handler mHandler = new Handler();

    RelativeLayout layout_A, layout_B, layout_C, layout_D, layout_E;
    ArrayList<Question> questionList;
    NetworkImageView imgQuestion;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    AppCompatActivity activity;
    Animation animation;

    long leftTime;
    MyCountDownTimer timer;
    AlertDialog quitAlertDialog;
    TextSwitcher right_p1, right_p2, right_p01, right_p02;
    Animation in, out;
    ProgressBar progressBar;

    ScrollView mainScroll, queScroll;
    int questionIndex = 0, correctQuestion = 0, inCorrectQuestion = 0,
            qIndex_vPlayer = 0, correctQuestion_vPlayer = 0, incorrectQuestion_vPlayer = 0,
            textSize;
    String Player1Name, Player2Name, winner, winnerMessage,
            optionClicked = "false",
            profilePlayer1, winDialogTitle;
    GradientProgress progressTimer;


    @SuppressLint({"NewApi", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);
        activity = RobotPlayActivity.this;
        getAllWidgets();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.battle_quiz));
        imgPlayer1.setDefaultImageResId(R.drawable.ic_account);
        imgPlayer2.setDefaultImageResId(R.drawable.ic_android);
        textSize = Integer.parseInt(Session.getSavedTextSize(activity));
        setAnimationMethod();

        if (Utils.isNetworkAvailable(activity)) {
            init();
        } else {
            View parentLayout = findViewById(android.R.id.content);
            Snackbar snackbar = Snackbar
                    .make(parentLayout, getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(R.string.retry), view -> {

                    });
            snackbar.show();

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

    }

    public void getAllWidgets() {
        int[] CLICKABLE = new int[]{R.id.a_layout, R.id.b_layout, R.id.c_layout, R.id.d_layout, R.id.e_layout};
        for (int i : CLICKABLE) {
            findViewById(i).setOnClickListener(this);
        }
        toolbar = findViewById(R.id.toolBar);

        tvA = findViewById(R.id.tvA);
        tvB = findViewById(R.id.tvB);

        progressTimer = findViewById(R.id.circleTimer);
        progressBar = findViewById(R.id.progressBar);

        right_p1 = findViewById(R.id.right_p1);
        right_p2 = findViewById(R.id.right_p2);
        right_p01 = findViewById(R.id.right_p01);
        right_p02 = findViewById(R.id.right_p02);

        mainScroll = findViewById(R.id.mainScroll);
        queScroll = findViewById(R.id.queScroll);
        imgQuestion = findViewById(R.id.imgQuestion);
        tvImgQues = findViewById(R.id.tvImgQues);

        tvIndex = findViewById(R.id.tvIndex);
        btnOpt1 = findViewById(R.id.btnOpt1);
        btnOpt2 = findViewById(R.id.btnOpt2);
        btnOpt3 = findViewById(R.id.btnOpt3);
        btnOpt4 = findViewById(R.id.btnOpt4);
        btnOpt5 = findViewById(R.id.btnOpt5);

        imgZoom = findViewById(R.id.imgZoom);
        tvPlayer1Name = findViewById(R.id.tv_player1_name);
        tvPlayer2Name = findViewById(R.id.tv_player2_name);
        imgPlayer1 = findViewById(R.id.iv_player1_pic);
        imgPlayer2 = findViewById(R.id.iv_player2_pic);


        txtQuestion = findViewById(R.id.txtQuestion);
        layout_A = findViewById(R.id.a_layout);
        layout_B = findViewById(R.id.b_layout);
        layout_C = findViewById(R.id.c_layout);
        layout_D = findViewById(R.id.d_layout);
        layout_E = findViewById(R.id.e_layout);
    }


    public void setAnimationMethod() {


        RightSwipe_A = AnimationUtils.loadAnimation(activity, R.anim.anim_right_a);
        RightSwipe_B = AnimationUtils.loadAnimation(activity, R.anim.anim_right_b);
        RightSwipe_C = AnimationUtils.loadAnimation(activity, R.anim.anim_right_c);
        RightSwipe_D = AnimationUtils.loadAnimation(activity, R.anim.anim_right_d);
        RightSwipe_E = AnimationUtils.loadAnimation(activity, R.anim.anim_right_e);
        Fade_in = AnimationUtils.loadAnimation(activity, R.anim.fade_out);


        animation = AnimationUtils.loadAnimation(activity, R.anim.right_ans_anim); // Change alpha from fully visible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the
//circle timer
        progressTimer.setMaxProgress(Constant.CIRCULAR_MAX_PROGRESS);
        progressTimer.setCurrentProgress(Constant.CIRCULAR_MAX_PROGRESS);


//Text Watcher anim
        in = AnimationUtils.loadAnimation(this, R.anim.slide_up1);
        out = AnimationUtils.loadAnimation(this, R.anim.slide_up);


        right_p1.setFactory(mFactory);
        right_p01.setFactory(mFactory);
        right_p2.setFactory(mFactory);
        right_p02.setFactory(mFactory);

        right_p1.setCurrentText(String.valueOf(correctQuestion));
        right_p01.setCurrentText(String.valueOf(inCorrectQuestion));
        right_p2.setCurrentText(String.valueOf(correctQuestion));
        right_p02.setCurrentText(String.valueOf(inCorrectQuestion));

        right_p1.setInAnimation(in);
        right_p1.setOutAnimation(out);

        right_p01.setOutAnimation(out);
        right_p2.setInAnimation(in);
        right_p2.setOutAnimation(out);

        right_p02.setOutAnimation(out);

    }

    private final Runnable mUpdateUITimerTask = new Runnable() {
        public void run() {
            progressBar.setVisibility(View.GONE);
            nextQuizQuestion();
        }
    };

    private void init() {
        Player1Name = Session.getUserData(Session.NAME, getApplicationContext());
        tvPlayer1Name.setText(Player1Name);
        profilePlayer1 = Session.getUserData(Session.PROFILE, getApplicationContext());
        imgPlayer1.setImageUrl(profilePlayer1, imageLoader);

        virtual_play = true;
        Player2Name = getString(R.string.robot);
        tvPlayer2Name.setText(Player2Name);
        imgPlayer2.setDefaultImageResId(R.drawable.ic_android);
        getQuestionForComputer();
    }

    public void getQuestionForComputer() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.getQuestionForRobot, "1");
        if (Session.getBoolean(Session.LANG_MODE, getApplicationContext()))
            params.put(Constant.LANGUAGE_ID, Session.getCurrentLanguage(getApplicationContext()));
        if (Constant.isCateEnable)
            params.put(Constant.category, Constant.CATE_ID);
        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String error = jsonObject.getString(Constant.ERROR);
                    if (error.equalsIgnoreCase(Constant.FALSE)) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        questionList = new ArrayList<>();
                        questionList.addAll(Utils.getQuestions(jsonArray, activity));
                        Constant.MAX_QUESTION_PER_BATTLE = questionList.size();

                        mHandler.postDelayed(mUpdateUITimerTask, 1000);
                        mainScroll.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params, activity);

    }

    @Override
    public void onClick(View v) {
        if (questionIndex < questionList.size()) {
            question = questionList.get(questionIndex);
            layout_A.setClickable(false);
            layout_B.setClickable(false);
            layout_C.setClickable(false);
            layout_D.setClickable(false);
            layout_E.setClickable(false);


            int id = v.getId();
            if (id == R.id.a_layout) {
                AddReview(question, btnOpt1, layout_A);
            } else if (id == R.id.b_layout) {
                AddReview(question, btnOpt2, layout_B);
            } else if (id == R.id.c_layout) {
                AddReview(question, btnOpt3, layout_C);
            } else if (id == R.id.d_layout) {
                AddReview(question, btnOpt4, layout_D);
            } else if (id == R.id.e_layout) {
                AddReview(question, btnOpt5, layout_E);
            }

            if (virtual_play) {
                PerformVirtualClick();
            }

            optionClicked = "true";

        }
    }

    public void AddReview(Question question, TextView tvBtnOpt, RelativeLayout layout) {
        layout_A.setClickable(false);
        layout_B.setClickable(false);
        layout_C.setClickable(false);
        layout_D.setClickable(false);
        layout_E.setClickable(false);

        if (tvBtnOpt.getText().toString().equalsIgnoreCase(question.getTrueAns())) {
            layout.setBackgroundResource(R.drawable.right_gradient);
            imgPlayer1.setBorderColor(ContextCompat.getColor(activity, R.color.green));
            correctQuestion = correctQuestion + 1;
            addScore();

        } else {
            layout.setBackgroundResource(R.drawable.wrong_gradient);
            imgPlayer1.setBorderColor(ContextCompat.getColor(activity, R.color.red));
            inCorrectQuestion = inCorrectQuestion + 1;
            WrongQuestion();
        }

        question.setSelectedAns(tvBtnOpt.getText().toString());
        if (Constant.QUICK_ANSWER_ENABLE.equals("1"))
            RightAnswerBackgroundSet();
        question.setAttended(true);
        questionIndex++;
        mHandler.postDelayed(mUpdateUITimerTask, 1000);
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

    private void showQuitGameAlertDialog() {
        try {
            stopTimer();
            final AlertDialog.Builder dialog1 = new AlertDialog.Builder(activity);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View dialogView = inflater.inflate(R.layout.dialog_leave_battle, null);
            dialog1.setView(dialogView);
            dialog1.setCancelable(true);

            final AlertDialog alertDialog = dialog1.create();
            TextView tvMessage = dialogView.findViewById(R.id.tv_message);
            TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
            tvTitle.setText(Player1Name);
            TextView tvOk = dialogView.findViewById(R.id.btn_ok);
            TextView btnNo = dialogView.findViewById(R.id.btnNo);
            tvMessage.setText(getString(R.string.msg_alert_leave));
            tvOk.setOnClickListener(v -> {
                alertDialog.dismiss();
                finish();
            });
            btnNo.setOnClickListener(view -> {
                timer = new MyCountDownTimer(leftTime, 1000);
                timer.start();
                alertDialog.dismiss();
            });
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.setCancelable(false);
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showWinnerDialog() {
        try {

            stopTimer();


            final AlertDialog.Builder dialog1 = new AlertDialog.Builder(activity);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View sheetView = inflater.inflate(R.layout.bottom_complete, null);
            dialog1.setView(sheetView);
            dialog1.setCancelable(false);

            final AlertDialog alertDialog = dialog1.create();
            final CircleImageView player1, player2;
            final TextView wrong, tvScore, winnerStatus, looserStatus, txt_result_title, victoryMsg;
            final ImageView imgVictory;
            TextView btnReBattle, btnExit;
            player1 = sheetView.findViewById(R.id.winnerImg);
            player2 = sheetView.findViewById(R.id.losserimage);
            wrong = sheetView.findViewById(R.id.wrong);
            tvScore = sheetView.findViewById(R.id.tvScore);
            winnerStatus = sheetView.findViewById(R.id.winnerstatus);
            looserStatus = sheetView.findViewById(R.id.looserstatus);
            txt_result_title = sheetView.findViewById(R.id.txt_result_title);
            victoryMsg = sheetView.findViewById(R.id.victorymsg);
            imgVictory = sheetView.findViewById(R.id.imgVictory);
            btnReBattle = sheetView.findViewById(R.id.btnReBattle);
            btnExit = sheetView.findViewById(R.id.btnExit);
            if (winner.equals("you")) {
                txt_result_title.setText(getString(R.string.congrats));
                winnerStatus.setText(getString(R.string.winner));
                wrong.setText(Player1Name);
                tvScore.setText(Player2Name);
                victoryMsg.setText(getString(R.string.victory_));
                imgVictory.setBackgroundResource(R.drawable.victory);
                player1.setImageUrl(profilePlayer1, imageLoader);
                player2.setDefaultImageResId(R.drawable.ic_android);
                looserStatus.setText(getString(R.string.you_loss));
            } else {
                txt_result_title.setText(getString(R.string.next_time));
                winnerStatus.setText(getString(R.string.you_loss));
                wrong.setText(Player1Name);
                tvScore.setText(Player2Name);
                victoryMsg.setText(getString(R.string.defeat));
                imgVictory.setBackgroundResource(R.drawable.defeat);
                player1.setImageUrl(profilePlayer1, imageLoader);
                player2.setDefaultImageResId(R.drawable.ic_android);
                looserStatus.setText(getString(R.string.winner));
            }


            btnReBattle.setOnClickListener(view -> {

                Intent intentReBattle = new Intent(activity, SearchPlayerActivity.class);
                intentReBattle.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentReBattle);
                alertDialog.dismiss();
                finish();
            });
            btnExit.setOnClickListener(view -> {
                alertDialog.dismiss();
                finish();
            });
            alertDialog.show();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void PerformVirtualClick() {
        String option;
        if (Session.getBoolean(Session.E_MODE, getApplicationContext())) {
            option = randomAlphaNumericWith_E();
        } else {
            option = randomAlphaNumeric();
        }
        switch (option) {
            case "A":

                if (btnOpt1.getText().toString().trim().equalsIgnoreCase(question.getTrueAns().trim()))
                    RightVirtualAnswer();
                else
                    WrongVirtualAnswer();

                break;
            case "B":

                if (btnOpt2.getText().toString().trim().equalsIgnoreCase(question.getTrueAns().trim()))
                    RightVirtualAnswer();
                else if (!btnOpt2.getText().toString().trim().equalsIgnoreCase(question.getTrueAns().trim()))
                    WrongVirtualAnswer();

                break;
            case "C":

                if (btnOpt3.getText().toString().trim().equalsIgnoreCase(question.getTrueAns().trim()))
                    RightVirtualAnswer();
                else if (!btnOpt3.getText().toString().trim().equalsIgnoreCase(question.getTrueAns().trim()))
                    WrongVirtualAnswer();

                break;
            case "D":

                if (btnOpt4.getText().toString().trim().equalsIgnoreCase(question.getTrueAns().trim()))
                    RightVirtualAnswer();
                else if (!btnOpt4.getText().toString().trim().equalsIgnoreCase(question.getTrueAns().trim()))
                    WrongVirtualAnswer();

                break;
            case "E":

                if (btnOpt5.getText().toString().trim().equalsIgnoreCase(question.getTrueAns().trim()))
                    RightVirtualAnswer();
                else if (!btnOpt5.getText().toString().trim().equalsIgnoreCase(question.getTrueAns().trim()))
                    WrongVirtualAnswer();

                break;
        }
    }


    private void showResetGameAlert() {

        try {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_reset_game);
            dialog.setCancelable(false);
            TextView tvMessage = dialog.findViewById(R.id.tv_message);
            TextView tvOk = dialog.findViewById(R.id.btn_ok);
            tvMessage.setText(getString(R.string.msg_draw_game));
            tvOk.setOnClickListener(v -> {
                finish();
                dialog.dismiss();
            });
            dialog.show();
            Window window = dialog.getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public String randomAlphaNumeric() {

        String option = "ABCD";
        int character = (int) (Math.random() * 4);
        return String.valueOf(option.charAt(character));
    }

    public String randomAlphaNumericWith_E() {

        String option = "ABCDE";
        int character = (int) (Math.random() * 5);
        return String.valueOf(option.charAt(character));
    }

    ViewSwitcher.ViewFactory mFactory = new ViewSwitcher.ViewFactory() {

        @Override
        public View makeView() {
            // Create a new TextView
            TextView t = new TextView(activity);
            t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
            t.setTextAppearance(android.R.style.TextAppearance_DeviceDefault_Medium);
            t.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.txt_color));
            return t;
        }
    };


    public class MyCountDownTimer extends CountDownTimer {
        private MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            leftTime = millisUntilFinished;
            int progress = (int) (millisUntilFinished / 1000);
            if (progressTimer == null)
                progressTimer = findViewById(R.id.circleTimer);
            else
                progressTimer.setCurrentProgress(progress);

            //when left last 5 second we show progress color red
            if (millisUntilFinished <= 6000)
                progressTimer.SetTimerAttributes(Color.RED, Color.RED);
            else
                progressTimer.setGradientAttributes(activity);
        }

        @Override
        public void onFinish() {
            if (questionIndex < questionList.size()) {
                //WrongQuestion();
                if (optionClicked.equals("false")) {
                    layout_A.setClickable(false);
                    layout_B.setClickable(false);
                    layout_C.setClickable(false);
                    layout_D.setClickable(false);
                    layout_E.setClickable(false);
                    if (virtual_play) {
                        WrongVirtualAnswer();
                    }
                    WrongQuestion();
                    questionIndex++;
                    mHandler.postDelayed(mUpdateUITimerTask, 1000);

                }
            }

        }
    }


    public void RightVirtualAnswer() {
        qIndex_vPlayer++;
        correctQuestion_vPlayer++;
        right_p2.setText(String.valueOf(correctQuestion_vPlayer));
        imgPlayer2.setBorderColor(ContextCompat.getColor(activity, R.color.green));


    }


    public void WrongVirtualAnswer() {

        incorrectQuestion_vPlayer++;
        qIndex_vPlayer++;
        imgPlayer2.setBorderColor(ContextCompat.getColor(activity, R.color.red));
    }

    private void addScore() {
        rightSound();
        if (correctQuestion == questionList.size()) {
            right_p01.setText("");
        }
        right_p1.setText(String.valueOf(correctQuestion));
    }

    private void WrongQuestion() {
        playWrongSound();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();

    }

    @Override
    protected void onDestroy() {

        stopTimer();
        if (quitAlertDialog != null) {
            if (quitAlertDialog.isShowing()) {
                quitAlertDialog.dismiss();
            }
        }
        super.onDestroy();

    }


    public void rightSound() {
        if (Session.getSoundEnableDisable(activity)) {
            Utils.setRightAnsSound(activity);
        }
        if (Session.getVibration(activity)) {
            Utils.vibrate(activity, Utils.VIBRATION_DURATION);
        }
    }

    //play sound when answer is incorrect
    private void playWrongSound() {
        if (Session.getSoundEnableDisable(activity)) {
            Utils.setWrongAnsSound(activity);
        }
        if (Session.getVibration(activity)) {
            Utils.vibrate(activity, Utils.VIBRATION_DURATION);
        }
    }


    @SuppressLint("SetTextI18n")
    private void nextQuizQuestion() {
        queScroll.scrollTo(0, 0);
        optionClicked = "false";

        stopTimer();

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

        layout_A.setClickable(true);
        layout_B.setClickable(true);
        layout_C.setClickable(true);
        layout_D.setClickable(true);
        layout_E.setClickable(true);
        btnOpt1.startAnimation(RightSwipe_A);
        btnOpt2.startAnimation(RightSwipe_B);
        btnOpt3.startAnimation(RightSwipe_C);
        btnOpt4.startAnimation(RightSwipe_D);
        btnOpt5.startAnimation(RightSwipe_E);
        tvA.setVisibility(View.VISIBLE);
        tvB.setVisibility(View.VISIBLE);
        imgPlayer1.setBorderColor(ContextCompat.getColor(activity, R.color.card_color));
        imgPlayer2.setBorderColor(ContextCompat.getColor(activity, R.color.card_color));
        if (questionIndex < questionList.size()) {
            timer = new MyCountDownTimer(Constant.TIME_PER_QUESTION, Constant.COUNT_DOWN_TIMER);
            timer.start();
            question = questionList.get(questionIndex);

            //imgQuestion.resetZoom();
            tvIndex.setText((questionIndex + 1) + "/" + questionList.size());
            if (!question.getImage().isEmpty()) {

                imgQuestion.setImageUrl(question.getImage(), imageLoader);
                tvImgQues.startAnimation(Fade_in);
                tvImgQues.setText(question.getQuestion());
                txtQuestion.setVisibility(View.GONE);
                tvImgQues.setVisibility(View.VISIBLE);
                //  imgZoom.setVisibility(View.VISIBLE);

                imgQuestion.setVisibility(View.VISIBLE);
               /* imgZoom.setOnClickListener(view -> {
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
                //imgZoom.setVisibility(View.GONE);
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
            } else {
                Collections.shuffle(options);
                layout_C.setVisibility(View.VISIBLE);
                layout_D.setVisibility(View.VISIBLE);
                btnOpt1.setGravity(Gravity.NO_GRAVITY);
                btnOpt2.setGravity(Gravity.NO_GRAVITY);
            }
            if (Session.getBoolean(Session.E_MODE, getApplicationContext())) {
                if (options.size() == 4)
                    layout_E.setVisibility(View.GONE);
                else
                    layout_E.setVisibility(View.VISIBLE);
            }

            btnOpt1.setText(options.get(0).trim());
            btnOpt2.setText(options.get(1).trim());
            btnOpt3.setText(options.get(2).trim());
            btnOpt4.setText(options.get(3).trim());
            if (Session.getBoolean(Session.E_MODE, getApplicationContext()))
                if (options.size() == 5)
                    btnOpt5.setText(options.get(4).trim());
        } else
            showBattleResult();
    }

    public void showBattleResult() {
        stopTimer();
        leftTime = 0;

        if (correctQuestion > correctQuestion_vPlayer) {
            winnerMessage = Player1Name + getString(R.string.msg_win_battle);
            winner = "you";
            winDialogTitle = getString(R.string.congrats);
            showWinnerDialog();

        } else if (correctQuestion_vPlayer > correctQuestion) {
            winnerMessage = Player2Name + getString(R.string.msg_opponent_win_battle);
            winner = Player2Name;
            winDialogTitle = getString(R.string.next_time);
            showWinnerDialog();
        } else {
            showResetGameAlert();

        }


    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }


    @Override
    public void onBackPressed() {
        showQuitGameAlertDialog();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.findItem(R.id.report).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.setting) {

            Intent intent = new Intent(activity, SettingActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.open_next, R.anim.close_next);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}