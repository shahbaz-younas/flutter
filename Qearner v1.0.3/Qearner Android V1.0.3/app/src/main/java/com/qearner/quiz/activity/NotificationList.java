package com.qearner.quiz.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.facebook.ads.AdView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.qearner.quiz.ads.AdUtils;
import com.qearner.quiz.Constant;
import com.qearner.quiz.R;
import com.qearner.quiz.vollyConfigs.ApiConfig;
import com.qearner.quiz.helper.AppController;
import com.qearner.quiz.helper.Session;
import com.qearner.quiz.helper.Utils;
import com.qearner.quiz.model.Category;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NotificationList extends AppCompatActivity {
    RecyclerView recyclerView;
    AdView mAdView;
    public static ArrayList<Category> notificationList;
    SwipeRefreshLayout swipeRefreshLayout;
    Snackbar snackbar;
    Toolbar toolbar;
    ShimmerFrameLayout mShimmerViewContainer;
    AppCompatActivity activity;
    LinearLayout alertLyt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        getAllWidgets();
        activity = NotificationList.this;
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.notification));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getData();
        Session.setNCount(0, getApplicationContext());

        swipeRefreshLayout.setOnRefreshListener(() -> {
            getData();
            swipeRefreshLayout.setRefreshing(false);
        });
        AdUtils.loadFacebookBannerAds(activity);
    }


    public void getAllWidgets() {
        toolbar = findViewById(R.id.toolBar);
        alertLyt = findViewById(R.id.lyt_alert);
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);


        swipeRefreshLayout = findViewById(R.id.swipeLayout);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }

    private void getData() {
        mShimmerViewContainer.startShimmer();
        if (Utils.isNetworkAvailable(NotificationList.this)) {
            GetNotificationList();
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
    public void GetNotificationList() {
        mShimmerViewContainer.startShimmer();
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_NOTIFICATIONS, "1");
        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {
                try {

                    notificationList = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean(Constant.ERROR);

                    if (!error) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Category category = new Category();
                            JSONObject object = jsonArray.getJSONObject(i);
                            category.setName(object.getString(Constant.TITLE));
                            category.setMessage(object.getString(Constant.MESSAGE));
                            category.setImage(object.getString(Constant.IMAGE));
                            category.setDate(object.getString(Constant.DATE_SENT));
                            if (i != 0 && i % 5 == 0) {
                                notificationList.add(new Category(true));
                            }
                            notificationList.add(category);

                        }
                        NotificationAdapter adapter = new NotificationAdapter(notificationList);
                        Utils.setFallDownLayoutAnimation(activity, recyclerView);
                        recyclerView.setAdapter(adapter);

                    } else {
                        alertLyt.setVisibility(View.VISIBLE);
                        Utils.setAlertMsg(activity, "notify");

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

    }

    @Override
    public void onPause() {
        super.onPause();
        if (snackbar != null) {
            snackbar.dismiss();
        }
    }

    public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        private ArrayList<Category> dataList;
        private final int MENU_ITEM_VIEW_TYPE = 0;
        private final int UNIFIED_NATIVE_AD_VIEW_TYPE = 1;


        public NotificationAdapter(ArrayList<Category> dataList) {
            this.dataList = dataList;

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
            /*    case UNIFIED_NATIVE_AD_VIEW_TYPE:
                    View unifiedNativeLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_unified_rec, parent, false);
                    return new UnifiedNativeAdViewHolder(unifiedNativeLayoutView);*/
                case MENU_ITEM_VIEW_TYPE:
                default:
                    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_lyt, parent, false);
                    return new ItemRowHolder(v);
            }

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder1, int position) {
            int viewType = getItemViewType(position);
            switch (viewType) {
              /*  case UNIFIED_NATIVE_AD_VIEW_TYPE:
                   // AdUtils.loadNativeAd(activity, holder1);
                    break;*/
                case MENU_ITEM_VIEW_TYPE:
                    final ItemRowHolder holder = (ItemRowHolder) holder1;
                    final Category notification = dataList.get(position);
                    holder.tvTitle.setText(notification.getName());
                    holder.tvDes.setText(Html.fromHtml(notification.getMessage()));
                    String date = Utils.dateFormat(notification.getDate(), "MMM dd, hh:mm a");
                    date = date.substring(0, 1).toUpperCase() + date.substring(1).toLowerCase();
                    holder.tvDate.setText(date);
                    if (!notification.getImage().isEmpty()) {
                        holder.img.setImageUrl(notification.getImage(), imageLoader);
                        holder.img.setVisibility(View.VISIBLE);

                    }
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return (null != dataList ? dataList.size() : 0);
        }


        @Override
        public int getItemViewType(int position) {
       /*     if (notificationList.get(position).isAdsShow()) {
                return UNIFIED_NATIVE_AD_VIEW_TYPE;
            }*/
            return MENU_ITEM_VIEW_TYPE;
        }

        public class ItemRowHolder extends RecyclerView.ViewHolder {

            public TextView tvTitle, tvDes, tvDate;
            public NetworkImageView img;
            RelativeLayout lytMain;

            public ItemRowHolder(View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvDes = itemView.findViewById(R.id.tvDes);
                tvDate = itemView.findViewById(R.id.tvDate);
                img = itemView.findViewById(R.id.img);
                lytMain = itemView.findViewById(R.id.lytMain);

            }
        }
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
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
