package com.qearner.quiz.activity;

import android.annotation.SuppressLint;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.shimmer.ShimmerFrameLayout;

import com.qearner.quiz.adapter.CateRowHolder;
import com.qearner.quiz.ads.AdUtils;
import com.qearner.quiz.battle.SearchPlayerActivity;
import com.qearner.quiz.vollyConfigs.ApiConfig;
import com.qearner.quiz.helper.Session;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;

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

import android.widget.RelativeLayout;

import android.widget.Toast;


import com.android.volley.toolbox.ImageLoader;
import com.qearner.quiz.R;
import com.qearner.quiz.helper.AppController;
import com.qearner.quiz.Constant;
import com.qearner.quiz.helper.Utils;

import com.qearner.quiz.model.Category;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.Map;

public class CategoryActivity extends AppCompatActivity {

    public RecyclerView recyclerView;

    LinearLayout alertLyt;
    public RelativeLayout layout;
    public ArrayList<Category> categoryList;
    public SwipeRefreshLayout swipeRefreshLayout;
    public Snackbar snackbar;
    public Toolbar toolbar;
    public AlertDialog alertDialog;
    public CategoryAdapter adapter;
    protected ShimmerFrameLayout mShimmerViewContainer;
    String quizType;
    AppCompatActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        getAllWidgets();
        activity = CategoryActivity.this;
        quizType = getIntent().getStringExtra(Constant.QUIZ_TYPE);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.select_category));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getData();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            getData();
            swipeRefreshLayout.setRefreshing(false);
        });
        AdUtils.loadFacebookBannerAds(activity);
    }

    public void getAllWidgets() {
        layout = findViewById(R.id.layout);
        toolbar = findViewById(R.id.toolBar);
        alertLyt = findViewById(R.id.lyt_alert);

        swipeRefreshLayout = findViewById(R.id.swipeLayout);
        recyclerView = findViewById(R.id.recyclerView);
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
    }


    private void getData() {
        mShimmerViewContainer.startShimmer();
        if (Utils.isNetworkAvailable(activity)) {
            getMainCategoryFromJson();
            invalidateOptionsMenu();

        } else {
            setSnackBar();
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
        }

    }


    public void setSnackBar() {
        snackbar = Snackbar
                .make(findViewById(android.R.id.content), getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry), view -> getData());

        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }

    /*
     * Get Quiz Category from Json
     */
    public void getMainCategoryFromJson() {
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();

        Map<String, String> params = new HashMap<>();
        if (Session.getBoolean(Session.LANG_MODE, getApplicationContext())) {
            params.put(Constant.GET_CATE_BY_LANG, "1");
            params.put(Constant.LANGUAGE_ID, Session.getCurrentLanguage(getApplicationContext()));
        } else
            params.put(Constant.getCategories, "1");
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, activity));
        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {
                try {
                    categoryList = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(response);
                    String error = jsonObject.getString(Constant.ERROR);

                    if (error.equalsIgnoreCase(Constant.FALSE)) {
                        alertLyt.setVisibility(View.GONE);
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Category category = new Category();
                            JSONObject object = jsonArray.getJSONObject(i);
                            category.setId(object.getString(Constant.ID));
                            category.setName(object.getString(Constant.CATEGORY_NAME));
                            category.setImage(object.getString(Constant.IMAGE));
                            category.setTtlQues(object.getString(Constant.NO_OF_QUES));
                            category.setNoOfCate(object.getString(Constant.NO_OF_CATE));
                            category.setPlayed(object.getString(Constant.IS_PLAY).equals("1"));
                         /*   if (i != 0 && i % 3 == 0) {
                                categoryList.add(new Category(true));
                            }*/
                            categoryList.add(category);
                        }
                        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                        Utils.setFallDownLayoutAnimation(activity, recyclerView);
                        adapter = new CategoryAdapter(activity, categoryList);
                        recyclerView.setAdapter(adapter);

                    } else {

                        alertLyt.setVisibility(View.VISIBLE);
                        Utils.setAlertMsg(activity, "cate");
                        if (adapter != null) {
                            adapter = new CategoryAdapter(activity, categoryList);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                    mShimmerViewContainer.stopShimmer();
                    mShimmerViewContainer.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params, activity);


    }


    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmer();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (snackbar != null) {
            snackbar.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        if (AdUtils.mAdView != null) {
            AdUtils.mAdView.destroy();
        }
        super.onDestroy();
    }

    public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        ArrayList<Category> dataList;
        Context mContext;
        private final int MENU_ITEM_VIEW_TYPE = 0;
        private final int UNIFIED_NATIVE_AD_VIEW_TYPE = 1;

        public CategoryAdapter(Context context, ArrayList<Category> dataList) {
            this.dataList = dataList;
            this.mContext = context;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType) {
              /*  case UNIFIED_NATIVE_AD_VIEW_TYPE:
                    View unifiedNativeLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_unified_rec, parent, false);
                    return new UnifiedNativeAdViewHolder(unifiedNativeLayoutView);*/
                case MENU_ITEM_VIEW_TYPE:
                default:
                    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_category, parent, false);
                    return new CateRowHolder(v);
            }

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder1, final int position) {
            int viewType = getItemViewType(position);
            switch (viewType) {
             /*   case UNIFIED_NATIVE_AD_VIEW_TYPE:
                   // AdUtils.loadNativeAd(activity, holder1);
                    break;*/
                case MENU_ITEM_VIEW_TYPE:
                    final CateRowHolder holder = (CateRowHolder) holder1;
                    final Category category = dataList.get(position);

                    holder.tvTitle.setText(category.getName());
                    holder.noOfQue.setText(getString(R.string.que) + category.getTtlQues());
                    holder.image.setErrorImageResId(R.drawable.ic_logo);
                    holder.image.setDefaultImageResId(R.drawable.ic_logo);
                    holder.image.setImageUrl(category.getImage(), imageLoader);
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
                            if (quizType.equals(Constant.REGULAR)) {
                                if (!category.getNoOfCate().equals("0")) {
                                    Intent intent;
                                    intent = new Intent(activity, SubcategoryActivity.class);
                                    startActivity(intent);

                                } else {
                                    Intent intent = new Intent(activity, PlayActivity.class);
                                    intent.putExtra("fromQue", "cate");
                                    startActivity(intent);
                                }
                            } else {
                                Intent intent;
                                intent = new Intent(activity, SearchPlayerActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(activity, getString(R.string.question_not_available), Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        @Override
        public int getItemViewType(int position) {
           /* if (categoryList.get(position).isAdsShow()) {
                return UNIFIED_NATIVE_AD_VIEW_TYPE;
            }*/
            return MENU_ITEM_VIEW_TYPE;
        }


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