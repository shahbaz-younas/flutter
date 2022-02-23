package com.qearner.quiz.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.snackbar.Snackbar;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.toolbox.ImageLoader;
import com.qearner.quiz.R;
import com.qearner.quiz.adapter.CateRowHolder;
import com.qearner.quiz.ads.AdUtils;
import com.qearner.quiz.helper.Session;
import com.qearner.quiz.model.Category;
import com.qearner.quiz.vollyConfigs.ApiConfig;
import com.qearner.quiz.helper.AppController;
import com.qearner.quiz.Constant;
import com.qearner.quiz.helper.Utils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SubcategoryActivity extends AppCompatActivity {
    RecyclerView recyclerView;

    ArrayList<Category> subCateList;
    LinearLayout alertLyt;

    SwipeRefreshLayout swipeRefreshLayout;
    Snackbar snackbar;
    Toolbar toolbar;
    ShimmerFrameLayout mShimmerViewContainer;
    AppCompatActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        activity = SubcategoryActivity.this;
        getAllWidgets();
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(Constant.CATE_NAME);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        subCateList = new ArrayList<>();
        getData();
        swipeRefreshLayout.setOnRefreshListener(() -> {
            subCateList.clear();
            getData();
            swipeRefreshLayout.setRefreshing(false);
        });
        AdUtils.loadFacebookBannerAds(activity);
    }


    public void getAllWidgets() {
        toolbar = findViewById(R.id.toolBar);
        alertLyt = findViewById(R.id.lyt_alert);
        swipeRefreshLayout = findViewById(R.id.swipeLayout);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);


    }

    private void getData() {
        mShimmerViewContainer.startShimmer();
        //progressBar.setVisibility(View.VISIBLE);
        if (Utils.isNetworkAvailable(activity)) {
            getSubCategoryFromJson();

        } else {
            setSnackBar();
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
        }
    }

    public void getSubCategoryFromJson() {
        mShimmerViewContainer.startShimmer();
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put(Constant.getSubCategory, "1");
        params.put(Constant.categoryId, "" + Constant.CATE_ID);
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, activity));
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean(Constant.ERROR);
                    if (!error) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        alertLyt.setVisibility(View.GONE);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Category subCate = new Category();
                            JSONObject object = jsonArray.getJSONObject(i);
                            subCate.setId(object.getString(Constant.ID));
                            subCate.setName(object.getString(Constant.KEY_SUB_CATE_NAME));
                            subCate.setImage(object.getString(Constant.IMAGE));
                            subCate.setTtlQues(object.getString(Constant.NO_OF_CATE));
                            subCate.setPlayed(object.getString(Constant.IS_PLAY).equals("1"));
                            subCateList.add(subCate);
                        }
                        SubCategoryAdapter adapter = new SubCategoryAdapter(subCateList);
                        Utils.setFallDownLayoutAnimation(activity, recyclerView);
                        recyclerView.setAdapter(adapter);
                    } else {
                        alertLyt.setVisibility(View.VISIBLE);
                        Utils.setAlertMsg(activity, "sub_cate");
                    }
                    mShimmerViewContainer.stopShimmer();
                    mShimmerViewContainer.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params, activity);
    }


    public void setSnackBar() {
        snackbar = Snackbar
                .make(findViewById(android.R.id.content), getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry), view -> getData());

        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }

    public class SubCategoryAdapter extends RecyclerView.Adapter<CateRowHolder> {
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        ArrayList<Category> dataList;

        public SubCategoryAdapter(ArrayList<Category> dataList) {
            this.dataList = dataList;

        }

        @NonNull
        @Override
        public CateRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_category, parent, false);
            return new CateRowHolder(v);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull CateRowHolder holder, final int position) {
            final Category subCate = dataList.get(position);
            holder.tvTitle.setText(subCate.getName());
            holder.noOfQue.setText(getString(R.string.ttl_ques) + subCate.getTtlQues());
            holder.image.setDefaultImageResId(R.drawable.ic_logo);
            holder.image.setImageUrl(subCate.getImage(), imageLoader);

            if (subCate.isPlayed()) {
                holder.lyt_bg.setCardForegroundColor(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.card_trans_color)));
                holder.tvTitle.setTextColor(ContextCompat.getColor(activity, R.color.colorOnSurface));
            }

            holder.layout.setOnClickListener(v -> {
                Constant.SUB_CAT_ID = subCate.getId();
                Constant.SUB_CATE_NAME = subCate.getName();
                Constant.isPlayed = subCate.isPlayed();
                Intent intent = new Intent(activity, PlayActivity.class);
                intent.putExtra("fromQue", "subCate");
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return (null != dataList ? dataList.size() : 0);
        }


    }

    @Override
    public void onPause() {
        super.onPause();
        if (snackbar != null)
            snackbar.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmer();
    }


    @Override
    protected void onDestroy() {
        if (AdUtils.mAdView != null) {
            AdUtils.mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}