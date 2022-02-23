package com.qearner.quiz.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;

import com.qearner.quiz.Constant;
import com.qearner.quiz.R;
import com.qearner.quiz.activity.PlayActivity;
import com.qearner.quiz.activity.SubcategoryActivity;
import com.qearner.quiz.helper.AppController;
import com.qearner.quiz.model.Category;

import java.util.ArrayList;

public class HomeCateAdapter extends RecyclerView.Adapter<CateRowHolder> {

    private final ArrayList<Category> dataList;
    public Activity activity;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public HomeCateAdapter(Activity activity, ArrayList<Category> dataList) {
        this.dataList = dataList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public CateRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_maincat, parent, false);
        return new CateRowHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CateRowHolder holder, final int position) {

        final Category category = dataList.get(position);
        holder.tvTitle.setText(category.getName());
        // holder.image.setErrorImageResId(R.drawable.ic_logo);
       holder.image.setDefaultImageResId(R.drawable.ic_logo);
        holder.image.setImageUrl(category.getImage(), imageLoader);

        holder.noOfQue.setText(category.getTtlQues() + " " + activity.getString(R.string.que));
        if (category.getNoOfCate().equals("0")) {
            if (category.isPlayed()) {
                holder.lyt_bg.setCardForegroundColor(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.card_trans_color)));
                holder.tvTitle.setTextColor(ContextCompat.getColor(activity, R.color.colorOnSurface));
            }
        }
        holder.layout.setOnClickListener(v -> {
            Constant.CATE_ID = category.getId();
            Constant.CATE_NAME = category.getName();
            Constant.isPlayed = category.isPlayed();
            if (!category.getTtlQues().equals("0")) {
                Intent intent;
                if (!category.getNoOfCate().equals("0")) {
                    intent = new Intent(activity, SubcategoryActivity.class);
                } else {
                    intent = new Intent(activity, PlayActivity.class);
                    intent.putExtra("fromQue", "cate");
                }
                activity.startActivity(intent);
            } else {
                Toast.makeText(activity, activity.getString(R.string.question_not_available), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


}
