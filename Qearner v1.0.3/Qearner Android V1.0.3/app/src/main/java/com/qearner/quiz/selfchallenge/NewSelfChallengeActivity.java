package com.qearner.quiz.selfchallenge;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import android.widget.RelativeLayout;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;
import com.google.android.material.snackbar.Snackbar;
import com.qearner.quiz.Constant;
import com.qearner.quiz.R;

import com.qearner.quiz.adapter.CateRowHolder;
import com.qearner.quiz.ads.AdUtils;
import com.qearner.quiz.helper.AppController;
import com.qearner.quiz.vollyConfigs.ApiConfig;
import com.qearner.quiz.helper.Session;
import com.qearner.quiz.helper.Utils;
import com.qearner.quiz.model.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NewSelfChallengeActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView cateRecyclerView, subRecyclerView, questionView, timeView;
    String FORMAT = "%02d";
    public ArrayList<Category> categoryList, subCateList;

    Snackbar snackbar;
    RelativeLayout subCateLyt,queLyt;

    String cateId = "", subCateId = "", selectedQues = "", selectedMin = "";
    boolean isSubCateAvailable;
    String challengeType = "", ID = "";
    TextView tvSelectQues;

    ArrayList<Integer> queNoList, minuteList;
    AppCompatActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_self_challenge);
        getAllWidgets();
        activity = NewSelfChallengeActivity.this;

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.self_challenge));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        questionView.setLayoutManager(new GridLayoutManager(this,2, LinearLayoutManager.HORIZONTAL, false));
        timeView.setLayoutManager(new GridLayoutManager(this, 3,LinearLayoutManager.HORIZONTAL, false));

        getData();
        setTime();
    }

    public void getAllWidgets() {

        toolbar = findViewById(R.id.toolBar);
        subCateLyt = findViewById(R.id.subCateLyt);
        tvSelectQues = findViewById(R.id.tvSelectQues);
        queLyt = findViewById(R.id.queLyt);

        cateRecyclerView = findViewById(R.id.cateRecyclerview);
        cateRecyclerView.setLayoutManager(new LinearLayoutManager(this,  LinearLayoutManager.HORIZONTAL, false));
        subRecyclerView = findViewById(R.id.subRecyclerview);
        subRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        questionView = findViewById(R.id.questionView);
        timeView = findViewById(R.id.timeView);

    }

    public void setTime() {
        minuteList = new ArrayList<>();
        for (int i = 0; i <= Constant.MAX_MINUTES; i++) {
            if (i % 3 == 0) {
                if (i != 0)
                    minuteList.add(i);
            }
        }
        SelectAdapter adapter = new SelectAdapter(getApplicationContext(), minuteList, "time");
        timeView.setAdapter(adapter);
    }

    public void setQuestionCount(int queLength) {

        queNoList = new ArrayList<>();
        for (int i = 0; i <= queLength; i++) {
            if (i % 5 == 0) {
                if (i != 0)
                    queNoList.add(i);
            }
        }
        SelectAdapter adapter = new SelectAdapter(getApplicationContext(), queNoList, "que");
        questionView.setAdapter(adapter);
    }

    public void setSnackBar() {
        snackbar = Snackbar
                .make(findViewById(android.R.id.content), getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry), view -> getData());
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }

    private void getData() {
        if (Utils.isNetworkAvailable(activity)) {
            GetCategories();
            invalidateOptionsMenu();
        } else {
            setSnackBar();
        }
    }

    private void setTextViewDrawableColor(TextView textView) {
        textView.setBackgroundResource(R.drawable.card_bg);
        textView.setTextColor(Color.WHITE);
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.txt_color), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    public void GetCategories() {
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
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Category category = new Category();
                            JSONObject object = jsonArray.getJSONObject(i);
                            category.setId(object.getString(Constant.ID));
                            category.setName(object.getString(Constant.CATEGORY_NAME));
                            category.setImage(object.getString(Constant.IMAGE));
                            category.setTtlQues(object.getString(Constant.NO_OF_QUES));
                            category.setNoOfCate(object.getString(Constant.NO_OF_CATE));
                            categoryList.add(category);
                        }

                        CateAdapter cateAdapter = new CateAdapter(activity, categoryList, "cate",R.layout.layout_selfcat);
                        cateRecyclerView.setAdapter(cateAdapter);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params, activity);

    }

    public void GetSubCategories(final String cateId) {

        Map<String, String> params = new HashMap<>();
        params.put(Constant.getSubCategory, "1");
        params.put(Constant.categoryId, cateId);
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, activity));
        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {
                try {
                    subCateList = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(response);
                    String error = jsonObject.getString(Constant.ERROR);

                    if (error.equalsIgnoreCase(Constant.FALSE)) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Category category = new Category();
                            JSONObject object = jsonArray.getJSONObject(i);
                            category.setId(object.getString(Constant.ID));
                            category.setName(object.getString(Constant.KEY_SUB_CATE_NAME));
                            category.setImage(object.getString(Constant.IMAGE));
                            category.setTtlQues(object.getString(Constant.NO_OF_CATE));
                            subCateList.add(category);
                        }
                        isSubCateAvailable = true;
                        subCateLyt.setVisibility(View.VISIBLE);
                        CateAdapter cateAdapter = new CateAdapter(activity, subCateList, "subCate",R.layout.layout_self_category);
                        subRecyclerView.setAdapter(cateAdapter);
                    } else {
                        isSubCateAvailable = false;
                        subCateLyt.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params, activity);

    }

    public void StartChallenge(View view) {
        if (cateId.isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.category_select), Toast.LENGTH_SHORT).show();
        } else if (isSubCateAvailable && subCateId.isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.subcategory_select), Toast.LENGTH_SHORT).show();
        } else if (selectedQues.isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.question_select), Toast.LENGTH_SHORT).show();
        } else if (selectedMin.isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.minutes_select), Toast.LENGTH_SHORT).show();
        } else {

            if (!isSubCateAvailable) {
                challengeType = "cate";
                ID = cateId;
            } else {
                challengeType = "subCate";
                ID = subCateId;
            }
            Intent intent = new Intent(getApplicationContext(), SelfChallengeQuestion.class);
            intent.putExtra("type", challengeType);
            intent.putExtra("id", ID);
            intent.putExtra("limit", "" + selectedQues);
            intent.putExtra("time", Integer.parseInt(selectedMin));
            startActivity(intent);
        }
    }

    public class CateAdapter extends RecyclerView.Adapter<CateRowHolder> {

        private final ArrayList<Category> dataList;
        public Activity activity;
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        String type;
        int resId;

        public CateAdapter(Activity activity, ArrayList<Category> dataList, String type, int resId) {
            this.dataList = dataList;
            this.activity = activity;
            this.type = type;
            this.resId = resId;
        }

        @NonNull
        @Override
        public CateRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(resId, parent, false);
            return new CateRowHolder(v);
        }

        @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
        @Override
        public void onBindViewHolder(@NonNull CateRowHolder holder, final int position) {
            final Category category = dataList.get(position);
            if (type.equals("cate")) {
                if (cateId.equals(category.getId())) {
                    holder.setIsRecyclable(false);
                    holder.image.setDefaultImageResId(R.drawable.selected_bg);
                } else {
                    holder.image.setImageUrl(category.getImage(), imageLoader);
                    holder.image.setDefaultImageResId(R.drawable.ic_logo);
                }
            } else {
                if (subCateId.equals(category.getId())) {
                    holder.setIsRecyclable(false);
                    holder.image.setDefaultImageResId(R.drawable.selected_bg);
                } else {
                    holder.image.setImageUrl(category.getImage(), imageLoader);
                    holder.image.setDefaultImageResId(R.drawable.ic_logo);
                }
            }
            holder.tvTitle.setText(category.getName());
           // holder.image.setDefaultImageResId(R.drawable.ic_logo);
            holder.noOfQue.setText(category.getTtlQues() + " " + activity.getString(R.string.que));
            holder.layout.setOnClickListener(v -> {
                if (type.equals("cate")) {
                    cateId = category.getId();
                    if (category.getNoOfCate().equalsIgnoreCase("0")) {
                        if (Integer.parseInt(category.getTtlQues()) >= 5) {
                            queLyt.setVisibility(View.VISIBLE);
                            setQuestionCount(Integer.parseInt(category.getTtlQues()));
                        } else {
                            queLyt.setVisibility(View.GONE);
                            selectedQues = "";
                        }
                        isSubCateAvailable = false;
                        subCateLyt.setVisibility(View.GONE);
                    } else
                        GetSubCategories(cateId);
                } else {
                    subCateId = category.getId();
                    if (Integer.parseInt(category.getTtlQues()) >= 5) {
                        setQuestionCount(Integer.parseInt(category.getTtlQues()));
                        queLyt.setVisibility(View.VISIBLE);
                    } else {
                        queLyt.setVisibility(View.GONE);
                        selectedQues = "";
                    }
                }
                holder.image.setImageUrl("", imageLoader);
                holder.image.setImageResource(R.drawable.selected_bg);

                notifyDataSetChanged();

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

    public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.ItemRowHolder> {
        ArrayList<Integer> cateList;
        Context mContext;
        String type;

        public SelectAdapter(Context context, ArrayList<Integer> cateList, String type) {
            this.cateList = cateList;
            this.mContext = context;
            this.type = type;
        }

        @NonNull
        @Override
        public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lyt_slection, parent, false);
            return new ItemRowHolder(v);
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onBindViewHolder(@NonNull final ItemRowHolder holder, final int position) {
            if (type.equals("time")) {
                if (selectedMin.equals(String.valueOf(cateList.get(position)))) {
                    holder.tvSelect.setBackgroundResource(R.drawable.selected_gradient);
                    holder.tvSelect.setTextColor(Color.BLACK);
                    holder.setIsRecyclable(false);
                } else
                    holder.tvSelect.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.txt_color));

            } else {

                if (selectedQues.equals(String.valueOf(cateList.get(position)))) {
                    holder.tvSelect.setBackgroundResource(R.drawable.selected_gradient);
                    holder.tvSelect.setTextColor(Color.BLACK);
                    holder.setIsRecyclable(false);
                } else
                    holder.tvSelect.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.txt_color));
            }

            holder.tvSelect.setText(String.format(FORMAT,cateList.get(position)));


            holder.tvSelect.setOnClickListener(v -> {

                if (type.equals("time")) {
                    selectedMin = String.valueOf(cateList.get(position));
                } else {
                    selectedQues = String.valueOf(cateList.get(position));

                }
                notifyDataSetChanged();

            });
        }

        @Override
        public int getItemCount() {
            return (null != cateList ? cateList.size() : 0);
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class ItemRowHolder extends RecyclerView.ViewHolder {

            public TextView tvSelect;

            public ItemRowHolder(View itemView) {
                super(itemView);
                tvSelect = itemView.findViewById(R.id.tvSelect);

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
