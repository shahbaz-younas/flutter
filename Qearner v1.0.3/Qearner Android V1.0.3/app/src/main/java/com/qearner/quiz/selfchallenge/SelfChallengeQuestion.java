package com.qearner.quiz.selfchallenge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.qearner.quiz.Constant;
import com.qearner.quiz.R;
import com.qearner.quiz.activity.ResultActivity;
import com.qearner.quiz.ads.AdUtils;
import com.qearner.quiz.fragment.QuestionFragment;
import com.qearner.quiz.vollyConfigs.ApiConfig;
import com.qearner.quiz.helper.AppController;
import com.qearner.quiz.helper.Session;
import com.qearner.quiz.helper.Utils;
import com.qearner.quiz.model.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class SelfChallengeQuestion extends AppCompatActivity {
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    TextView tvTime, tvSubmit, tvIndex;
    String id, fromQue, limit;
    ImageView imgMark;
    public static ArrayList<Question> questionList;
    long leftTime, resumeTime;
    Timer timer;
    long challengeTime;
    ImageView imgNext;
    ProgressBar progressBar;
    Toolbar toolbar;
    AppCompatActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_challenge_question);
        activity = SelfChallengeQuestion.this;
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.self_challenge));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = findViewById(R.id.viewPager);
        tvTime = findViewById(R.id.tvTime);
        tvSubmit = findViewById(R.id.tvSubmit);


        tvIndex = findViewById(R.id.tvIndex);
        imgNext = findViewById(R.id.imgNext);
        imgMark = findViewById(R.id.imgBookmark);
        progressBar = findViewById(R.id.progressBar);
        id = getIntent().getStringExtra("id");
        fromQue = getIntent().getStringExtra("type");
        limit = getIntent().getStringExtra("limit");
        imgMark.setTag("unmark");
        challengeTime = ((long) getIntent().getIntExtra("time", 1) * 60 * 1000);
        getQuestionsFromJson();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                tvIndex.setText(((position + 1) + "/" + questionList.size()));
                if (position == (questionList.size() - 1)) {
                    imgNext.setVisibility(View.GONE);
                } else {
                    imgNext.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        AdUtils.loadFacebookInterstitialAd(activity);
    }

    public void SubmitTest(View view) {
        resumeTime = leftTime;
        SubmitDialog();
    }


    public void PreviousQuestion(View view) {
        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
    }

    public void NextQuestion(View view) {
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        if (viewPager.getCurrentItem() == (questionList.size() - 1)) {
            imgNext.setVisibility(View.GONE);
        } else {
            imgNext.setVisibility(View.VISIBLE);
        }
    }

    public void getQuestionsFromJson() {
        progressBar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put(Constant.getSelfChallengeQuestions, "1");
        params.put(Constant.LIMIT, limit);
        if (fromQue.equals("cate"))
            params.put(Constant.category, "" + id);
        else if (fromQue.equals("subCate"))
            params.put(Constant.subCategoryId, "" + id);
        if (Session.getBoolean(Session.LANG_MODE, getApplicationContext()))
            params.put(Constant.LANGUAGE_ID, Session.getCurrentLanguage(getApplicationContext()));
        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {
                try {

                    progressBar.setVisibility(View.VISIBLE);
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean(Constant.ERROR);
                    if (!error) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        questionList = new ArrayList<>();
                        questionList.addAll(Utils.getQuestions(jsonArray, activity));
                        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), questionList);
                        viewPager.setAdapter(viewPagerAdapter);
                        starTimer();
                        tvIndex.setText(((viewPager.getCurrentItem() + 1) + "/" + questionList.size()));

                    }
                    progressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params, activity);

    }


    public void starTimer() {
        timer = new Timer(challengeTime, 1000);
        timer.start();
    }

    public void stopTimer() {
        if (timer != null)
            timer.cancel();
    }

    public void BottomResult(View view) {
        BottomSheetDialog();
    }

    public void BottomSheetDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog, null);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(activity, 10));

        ImageView imgClose = view.findViewById(R.id.imgClose);
        TextView tvAnswered = view.findViewById(R.id.tvAnswered);
        TextView tvNotAnswered = view.findViewById(R.id.tvNotAnswered);
        bottomSheetDialog.setContentView(view);
        BottomAdapter adapter = new BottomAdapter(questionList, activity, bottomSheetDialog, viewPager);
        recyclerView.setAdapter(adapter);
        imgClose.setOnClickListener(view1 -> bottomSheetDialog.dismiss());
        bottomSheetDialog.show();

    }

    public static class BottomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


        public ArrayList<Question> questionList;
        public Activity activity;
        BottomSheetDialog dialog;
        ViewPager viewPager;

        public BottomAdapter(ArrayList<Question> questionList, Activity activity, BottomSheetDialog dialog, ViewPager viewPager) {
            this.questionList = questionList;
            this.activity = activity;
            this.viewPager = viewPager;
            this.dialog = dialog;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_layout, parent, false);
            return new BottomAdapter.TestHolder(v);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder1, final int position) {
            TestHolder holder = (TestHolder) holder1;
            final Question question = questionList.get(position);
            holder.tvQueNo.setText("" + (position + 1));

            if (question.isAttended)
                holder.tvQueNo.setBackgroundResource(R.drawable.ic_attended_bg);
            else
                holder.tvQueNo.setBackgroundResource(R.drawable.bottom_view_bg);


            holder.tvQueNo.setOnClickListener(view -> {
                dialog.dismiss();
                viewPager.setCurrentItem(position);
            });

        }

        public class TestHolder extends RecyclerView.ViewHolder {

            TextView tvQueNo;

            public TestHolder(@NonNull View itemView) {
                super(itemView);
                tvQueNo = itemView.findViewById(R.id.tvQueNo);

            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return questionList.size();
            //return 23;
        }
    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter {

        public ArrayList<Question> questionList;

        public ViewPagerAdapter(FragmentManager fm, ArrayList<Question> questionList) {
            super(fm);
            this.questionList = questionList;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return QuestionFragment.newInstance(position, questionList);
        }

        @Override
        public int getCount() {
            return questionList.size();
        }

    }

    public void SubmitDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);

        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.submit_dialog, null);
        dialog.setView(dialogView);
        TextView tvMessage = dialogView.findViewById(R.id.tvMessage);
        TextView btnYes = dialogView.findViewById(R.id.btnYes);
        TextView btnNo = dialogView.findViewById(R.id.btnNo);
        tvMessage.setText(getString(R.string.self_challenge_sub_msg));
        final AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();

        btnNo.setOnClickListener(view -> {
            alertDialog.dismiss();
         /*   if (resumeTime != 0) {
                timer = new Timer(resumeTime, 1000);
                timer.start();
            }*/
        });
        btnYes.setOnClickListener(view -> {
            stopTimer();
            alertDialog.dismiss();
            Constant.CHALLENGE_TIME = challengeTime;
            Constant.TAKE_TIME = (challengeTime - resumeTime);
            resumeTime = 0;
            leftTime = 0;
            challengeTime = 0;
            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
            startActivity(intent);
            finish();
        });

    }

    public void PlayAreaLeaveDialog() {
        resumeTime = leftTime;
        stopTimer();
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_leave_test, null);
        dialog.setView(dialogView);
        TextView btnLeave = dialogView.findViewById(R.id.btnLeave);
        TextView btnResume = dialogView.findViewById(R.id.btnResume);

        final AlertDialog alertDialog = dialog.create();
        Utils.setDialogBg(alertDialog);
        alertDialog.show();

        alertDialog.setCancelable(false);
        btnLeave.setOnClickListener(view -> {
            resumeTime = 0;
            leftTime = 0;
            challengeTime = 0;
            stopTimer();
            alertDialog.dismiss();
            finish();
        });


        btnResume.setOnClickListener(view -> {
            alertDialog.dismiss();
            if (resumeTime != 0) {
                timer = new Timer(resumeTime, Constant.COUNT_DOWN_TIMER);
                timer.start();
            }
        });
        alertDialog.show();
    }

    public class Timer extends CountDownTimer {
        private Timer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        @Override
        public void onTick(long millisUntilFinished) {
            leftTime = millisUntilFinished;
            long totalSecs = (long) (millisUntilFinished / 1000.0);
            long minutes = (totalSecs / 60);
            long seconds = totalSecs % 60;
            tvTime.setText("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));

        }

        @Override
        public void onFinish() {
            stopTimer();
            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onPause() {
        resumeTime = leftTime;
        stopTimer();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (resumeTime != 0) {
            timer = new Timer(resumeTime, 1000);
            timer.start();
        }
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        PlayAreaLeaveDialog();
    }

}