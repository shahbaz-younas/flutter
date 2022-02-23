package com.qearner.quiz.adapter;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.android.material.card.MaterialCardView;
import com.qearner.quiz.R;

public class CateRowHolder extends RecyclerView.ViewHolder {
    public NetworkImageView image;
    public TextView tvTitle, noOfQue;
    public LinearLayout layout;
    public MaterialCardView lyt_bg;


    public CateRowHolder(View itemView) {
        super(itemView);

        image = itemView.findViewById(R.id.image);
        tvTitle = itemView.findViewById(R.id.tvTitle);
        layout = itemView.findViewById(R.id.parent_lyt);
        noOfQue = itemView.findViewById(R.id.tvQue);
        lyt_bg = itemView.findViewById(R.id.lyt_bg);
    }
}
