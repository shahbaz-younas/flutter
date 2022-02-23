package com.qearner.quiz.contest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qearner.quiz.R;
import com.qearner.quiz.model.Model;

import java.util.ArrayList;

public class ContestPriceAdapter extends RecyclerView.Adapter<ContestPriceAdapter.ItemRowHolder> {
    ArrayList<Model> dataList;
    Context mContext;

    public ContestPriceAdapter(Context context, ArrayList<Model> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_price, parent, false);
        return new ItemRowHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ItemRowHolder holder, final int position) {
        final Model model = dataList.get(position);
        holder.tvPrice.setText(model.getPoints() + mContext.getResources().getString(R.string._coins));
        holder.tvWinner.setText( ordinal(Integer.parseInt(model.getTop_users())) +mContext.getResources().getString(R.string._rank));
    }
    public static String ordinal(int i) {
        String[] suffixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + suffixes[i % 10];

        }
    }

    @Override
    public int getItemCount() {
        return (dataList.size());
    }

    public  class ItemRowHolder extends RecyclerView.ViewHolder {

        TextView tvWinner, tvPrice;


        public ItemRowHolder(View itemView) {
            super(itemView);

            tvWinner = itemView.findViewById(R.id.tvWinner);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }

    }

}