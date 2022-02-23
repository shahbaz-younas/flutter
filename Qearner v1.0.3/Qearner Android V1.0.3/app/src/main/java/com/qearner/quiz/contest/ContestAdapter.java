package com.qearner.quiz.contest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.qearner.quiz.Constant;
import com.qearner.quiz.R;
import com.qearner.quiz.activity.PlayActivity;
import com.qearner.quiz.helper.AppController;
import com.qearner.quiz.helper.Utils;
import com.qearner.quiz.model.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ContestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<Model> contestList;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    Activity activity;
    String type;

    public ContestAdapter(String type, List<Model> contestList, Activity activity) {
        this.type = type;
        this.contestList = contestList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_contest, parent, false);
        return new ItemViewHolder(v);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder1, int position) {


        ItemViewHolder holder = (ItemViewHolder) holder1;
        final Model model = contestList.get(position);
        holder.tvTitle.setText(model.getName());
        holder.tvCoin.setText(activity.getResources().getString(R.string.entry_fees) + model.getEntry() + activity.getResources().getString(R.string._coins));

        holder.image.setImageUrl(model.getImage(), imageLoader);
        holder.image.setOnClickListener(v -> contestDetail(model));

    }

    @Override
    public int getItemCount() {
        return contestList.size();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle, tvCoin;
        NetworkImageView image;


        public ItemViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCoin = itemView.findViewById(R.id.tvCoin);
            image = itemView.findViewById(R.id.image);
        }
    }

    @SuppressLint("SetTextI18n")
    public void contestDetail(Model model) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity, R.style.BottomSheetTheme);
        View itemView = activity.getLayoutInflater().inflate(R.layout.bottom_sheet_content_detail, null);
        TextView tvName, tvDes, tvStartDate, tvEndDate, tvEntry, tvDateHeader, tvParticipant;
        MaterialButton btnPlay;
        NetworkImageView image1, image2;
        btnPlay = itemView.findViewById(R.id.btnPlay);
        btnPlay.setVisibility(View.VISIBLE);

        image1 = itemView.findViewById(R.id.image1);
        image2 = itemView.findViewById(R.id.image2);
        tvName = itemView.findViewById(R.id.tvName);
        tvDes = itemView.findViewById(R.id.tvDes);
        tvStartDate = itemView.findViewById(R.id.tvStartDate);
        tvEndDate = itemView.findViewById(R.id.tvEndDate);
        tvEntry = itemView.findViewById(R.id.tvEntryCoin);

        tvDateHeader = itemView.findViewById(R.id.tv_end_title);
        tvParticipant = itemView.findViewById(R.id.tvPlayers);
        image1.setImageUrl(model.getImage(), imageLoader);
        image2.setImageUrl(model.getImage(), imageLoader);
        RecyclerView recyclerView = itemView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setNestedScrollingEnabled(false);
        tvName.setText(model.getName());
        tvDes.setText(model.getDescription());

        tvEntry.setText(model.getEntry() + activity.getResources().getString(R.string._coins));
        tvParticipant.setText(model.getParticipants());


        if (type.equalsIgnoreCase(activity.getResources().getString(R.string.up_coming))) {
            btnPlay.setVisibility(View.INVISIBLE);
            // lytParticipant.setVisibility(View.INVISIBLE);
            tvDateHeader.setText(activity.getResources().getString(R.string.live_on));
            tvStartDate.setText(model.getStart_date());
            tvEndDate.setText(model.getEnd_date());
        } else if (type.equalsIgnoreCase(activity.getResources().getString(R.string.live))) {
            tvDateHeader.setText(activity.getResources().getString(R.string.end_on));
            tvStartDate.setText(model.getStart_date());
            tvEndDate.setText(model.getEnd_date());
        } else if (type.equalsIgnoreCase(activity.getString(R.string.past))) {
            tvDateHeader.setText(activity.getResources().getString(R.string.ending_on));
            tvStartDate.setText(model.getStart_date());
            tvEndDate.setText(model.getEnd_date());
            btnPlay.setText(activity.getResources().getString(R.string.leaderboard));
        }
        try {
            JSONArray jsonArray = new JSONArray(model.getPoints());
            ArrayList<Model> priceList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                priceList.add(new Model(jsonObject.getString(Constant.TOP_WINNERS), jsonObject.getString(Constant.POINTS)));
            }

            ContestPriceAdapter priceAdapter = new ContestPriceAdapter(activity, priceList);
            recyclerView.setAdapter(priceAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btnPlay.setOnClickListener(v -> {
            if (type.equalsIgnoreCase(activity.getResources().getString(R.string.past))) {
                Intent i = new Intent(activity, ContestLeaderboard.class);
                i.putExtra("data", model.getId());
                activity.startActivity(i);
            } else {
                if (Constant.TOTAL_COINS < Double.parseDouble(model.getEntry())) {
                    notEnoughCoinMsg();
                } else {
                    Constant.TOTAL_COINS = Constant.TOTAL_COINS - Integer.parseInt(model.getEntry());
                    Intent i = new Intent(activity, PlayActivity.class);
                    i.putExtra("id", model.getId());
                    i.putExtra("entrypoint", model.getEntry());
                    i.putExtra("title", model.getName());
                    i.putExtra("fromQue", "contest");
                    activity.startActivity(i);
                    activity.finish();
                }
            }
            bottomSheetDialog.cancel();
        });
        bottomSheetDialog.setContentView(itemView);
        bottomSheetDialog.show();
    }

    public void notEnoughCoinMsg() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.lifeline_dialog, null);
        dialog.setView(dialogView);
        TextView ok = dialogView.findViewById(R.id.ok);
        TextView title = dialogView.findViewById(R.id.title);
        TextView message = dialogView.findViewById(R.id.message);
        message.setText(activity.getResources().getString(R.string.not_enough_entry_coin));
        title.setText(activity.getResources().getString(R.string.not_enough_coin));
        final AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        alertDialog.setCancelable(false);
        ok.setOnClickListener(view -> alertDialog.dismiss());
    }


}