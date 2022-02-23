package com.qearner.quiz.adapter;

import android.app.Activity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.qearner.quiz.Constant;
import com.qearner.quiz.R;
import com.qearner.quiz.activity.CategoryActivity;
import com.qearner.quiz.activity.PlayActivity;
import com.qearner.quiz.helper.Session;
import com.qearner.quiz.helper.Utils;
import com.qearner.quiz.model.Category;
import com.qearner.quiz.selfchallenge.NewSelfChallengeActivity;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.viewHolder> {

    Activity activity;
    ArrayList<Category> quizList;
    AlertDialog alertDialog;

    public CustomAdapter(Activity activity, ArrayList<Category> quizList, AlertDialog alertDialog) {
        this.activity = activity;
        this.quizList = quizList;
        this.alertDialog = alertDialog;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_playquiz, viewGroup, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(viewHolder viewHolder, int position) {
        Category quizCate = quizList.get(position);

        viewHolder.iconName.setText(quizCate.getName());
        viewHolder.lytBackground.setBackgroundResource(quizCate.getImgRes());
        viewHolder.lytBackground.setOnClickListener(v -> {
            setQuiz(quizCate.getName());

        });
    }

    public void setQuiz(String qName) {
        if (qName.equalsIgnoreCase(activity.getString(R.string.daily_quiz))) {
            if (Constant.DAILY_QUIZ_STATUS.equals("0"))
                DailyRandomQuiz("daily");
            else
                Utils.defaultAlertDialog(activity, activity.getResources().getString(R.string.daily_quiz_already_played));
        } else if (qName.equalsIgnoreCase(activity.getString(R.string.random_quiz))) {
            DailyRandomQuiz("random");
        } else if (qName.equalsIgnoreCase(activity.getString(R.string.self_challenge))) {
            activity.startActivity(new Intent(activity, NewSelfChallengeActivity.class));
        } else if (qName.equalsIgnoreCase(activity.getString(R.string.true_false))) {
            DailyRandomQuiz("true_false");
        } else if (qName.equalsIgnoreCase(activity.getString(R.string.practice))) {
            activity.startActivity(new Intent(activity, CategoryActivity.class).putExtra(Constant.QUIZ_TYPE, Constant.PRACTICE));
        }
    }

    public void DailyRandomQuiz(String quizType) {
        Intent intent = new Intent(activity, PlayActivity.class);
        intent.putExtra("fromQue", quizType);
        activity.startActivity(intent);

    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView iconName;
        RelativeLayout lytBackground;

        public viewHolder(View itemView) {
            super(itemView);

            iconName = itemView.findViewById(R.id.tvTitle);
            lytBackground = itemView.findViewById(R.id.lyt_background);

        }
    }

}