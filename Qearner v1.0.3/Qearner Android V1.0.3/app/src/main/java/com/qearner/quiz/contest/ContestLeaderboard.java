package com.qearner.quiz.contest;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;
import com.qearner.quiz.Constant;
import com.qearner.quiz.R;
import com.qearner.quiz.vollyConfigs.ApiConfig;
import com.qearner.quiz.helper.AppController;
import com.qearner.quiz.UI.CircleImageView;
import com.qearner.quiz.helper.Utils;
import com.qearner.quiz.model.Model;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class ContestLeaderboard extends AppCompatActivity {


    RecyclerView recyclerView;
    TextView tvAlert;
    ProgressBar progressbar;

    List<Model> historyList = new ArrayList<>();
    TopUserAdapter adapter;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    String quizId;
    LinearLayout lytTop;
    Toolbar toolbar;
    LinearLayout lyt_rank3, lyt_rank2, lyt_rank1;
    AppCompatActivity activity;
    NestedScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_leaderboard);
        getAllWidget();
        activity = ContestLeaderboard.this;
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.leaderboard);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        quizId = getIntent().getStringExtra(Constant.DATA);
        if (Utils.isNetworkAvailable(activity)) {
            // getUserData();
            prepareData();
        }
    }

    public void getAllWidget() {
        progressbar = findViewById(R.id.progressbar);
        tvAlert = findViewById(R.id.tvAlert);
        scrollView = findViewById(R.id.scrollView);
        toolbar = findViewById(R.id.toolBar);
        lytTop = findViewById(R.id.topLyt);
        lyt_rank1 = findViewById(R.id.lytRank1);
        lyt_rank2 = findViewById(R.id.lytRank2);
        lyt_rank3 = findViewById(R.id.lytRank3);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setNestedScrollingEnabled(false);
    }

    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void prepareData() {
        progressbar.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_LEADERBOARD, Constant.GET_DATA_KEY);
        params.put(Constant.CONTEST_ID, quizId);
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject1 = new JSONObject(response);
                    if (jsonObject1.getString(Constant.ERROR).equalsIgnoreCase(Constant.FALSE)) {

                        JSONArray jsonArray = jsonObject1.getJSONArray(Constant.DATA);
                        historyList.clear();
                        lytTop.setVisibility(View.VISIBLE);

                        for (int i = 1; i <= 3; i++) {
                            LinearLayout lyt = findViewById(getResources().getIdentifier("lytRank" + i, "id", getPackageName()));
                            lyt.setVisibility(View.INVISIBLE);
                        }

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            progressbar.setVisibility(View.GONE);

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
                            } else {
                                Model model = new Model(jsonObject.getString(Constant.RANK), jsonObject.getString(Constant.userId), jsonObject.getString(Constant.name), jsonObject.getString(Constant.SCORE), jsonObject.getString(Constant.PROFILE));
                                historyList.add(model);
                            }

                        }
                        adapter = new TopUserAdapter(historyList);
                        recyclerView.setVisibility(View.VISIBLE);
                        recyclerView.setAdapter(adapter);

                    } else {
                        recyclerView.setVisibility(View.GONE);
                        tvAlert.setVisibility(View.VISIBLE);
                        tvAlert.setText(jsonObject1.getString(Constant.MESSAGE));
                        progressbar.setVisibility(View.GONE);
                        lytTop.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params, activity);

    }


    class TopUserAdapter extends RecyclerView.Adapter<TopUserAdapter.ViewHolder> {

        public List<Model> historyList;

        public TopUserAdapter(List<Model> historyList) {
            this.historyList = historyList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_leaderboard, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            Model model = historyList.get(position);

            holder.tvScore.setText(model.getScore());
            holder.tvName.setText(model.getName());
            holder.tvNo.setText(model.getRank());
            holder.imgProfile.setImageUrl(model.getImage(), imageLoader);

        }

        @Override
        public int getItemCount() {
            return historyList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView tvName, tvScore, tvNo;
            CircleImageView imgProfile;

            public ViewHolder(View itemView) {
                super(itemView);
                imgProfile = itemView.findViewById(R.id.imgProfile);
                tvNo = itemView.findViewById(R.id.tvRank);
                tvScore = itemView.findViewById(R.id.tvScore);
                tvName = itemView.findViewById(R.id.tvName);
            }

        }
    }
}






