package com.qearner.quiz.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.messaging.FirebaseMessaging;
import com.qearner.quiz.R;
import com.qearner.quiz.UI.CircleImageView;
import com.qearner.quiz.UI.GridRecyclerView;
import com.qearner.quiz.adapter.CustomAdapter;
import com.qearner.quiz.adapter.HomeCateAdapter;
import com.qearner.quiz.battle.SearchPlayerActivity;
import com.qearner.quiz.earning.EarningActivity;
import com.qearner.quiz.leaderboard.LeaderboardActivity;
import com.qearner.quiz.login.LoginActivity;
import com.qearner.quiz.contest.ContestFragment;
import com.qearner.quiz.vollyConfigs.ApiConfig;
import com.qearner.quiz.helper.AppController;
import com.qearner.quiz.Constant;
import com.qearner.quiz.helper.Session;
import com.qearner.quiz.helper.Utils;
import com.facebook.login.LoginManager;


import com.google.firebase.auth.FirebaseAuth;
import com.qearner.quiz.model.Category;
import com.qearner.quiz.model.Language;
import com.qearner.quiz.contest.ContestActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;

import static com.qearner.quiz.helper.Utils.OpenBottomDialog;
import static com.qearner.quiz.helper.Utils.compareVersion;


public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    SharedPreferences settings;
    String type, authId;
    View divider;
    RelativeLayout lytCategory, lytContest;
    LinearLayout contentLayout;
    TextView tvAlert, tvViewAll, tvBattleWinCoins, tvBattleEntryCoins;
    AlertDialog alertDialog, maintenanceDialog;
    ImageView imgLanguage;
    RecyclerView recyclerView;
    GridRecyclerView rvQuizList;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    HomeCateAdapter adapter;
    ArrayList<Category> categoryList;
    ArrayList<Category> quizList;
    String[] iconsName;
    AppCompatActivity activity;
    ShimmerFrameLayout mShimmerViewContainer;
    public static CircleImageView imgProfile;
    @SuppressLint("StaticFieldLeak")
    public static TextView tvName, tvEmail, tvScore, tvCoins, tvRank, tvMoney, tvWinCoin;
    CustomAdapter quizTypeAdapter;
    BottomSheetDialog userLoggedOutDialog;

    @SuppressLint({"NewApi", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = MainActivity.this;
        getAllWidgets();
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) authId = user.getUid();
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        imgProfile.setImageUrl(Session.getUserData(Session.PROFILE, activity), imageLoader);

        tvViewAll.setOnClickListener(view -> openCategoryPage(Constant.REGULAR));
        tvName.setText(Session.getUserData(Session.NAME, activity));
        tvEmail.setText(Session.getUserData(Session.EMAIL, activity));
        imgLanguage.setOnClickListener(view -> {
            if (alertDialog != null)
                alertDialog.show();
        });

        settings = getSharedPreferences(Session.SETTING_Quiz_PREF, 0);
        type = getIntent().getStringExtra("type");

        assert type != null;
        if (!type.equals("null")) {
            if (type.equals("category")) {
                Constant.CATE_ID = getIntent().getStringExtra("cateId");
                if (getIntent().getStringExtra("no_of").equals("0")) {
                    Intent intent = new Intent(activity, PlayActivity.class);
                    intent.putExtra("fromQue", "cate");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(activity, SubcategoryActivity.class);
                    startActivity(intent);
                }
            }
        }
        userLoggedOutDialog();
        getData();
    }

    public void getData() {
        if (!Session.getBoolean(Session.IS_FIRST_TIME, activity)) {
            GetUpdate();
            imgLanguage.setVisibility(View.VISIBLE);
        } else {
            getMainCategoryFromJson();
            imgLanguage.setVisibility(View.INVISIBLE);
        }

        setLiveContest();


    }

    public void getAllWidgets() {
        imgProfile = findViewById(R.id.imgProfile);
        toolbar = findViewById(R.id.toolBar);
        divider = findViewById(R.id.divider);
        tvViewAll = findViewById(R.id.tvViewAll);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvBattleWinCoins = findViewById(R.id.tvWinCoins);
        tvBattleEntryCoins = findViewById(R.id.tvEntryCoins);

        lytCategory = findViewById(R.id.lytCategory);
        tvScore = findViewById(R.id.tvScore);
        tvCoins = findViewById(R.id.tvCoins);
        tvWinCoin = findViewById(R.id.tvWinCoin);
        tvRank = findViewById(R.id.tvRank);
        tvMoney = findViewById(R.id.tvMoney);

        imgLanguage = findViewById(R.id.imgLanguage);
        lytContest = findViewById(R.id.lytContest);
        contentLayout = findViewById(R.id.content_layout);
        tvAlert = findViewById(R.id.tvAlert);
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        rvQuizList = findViewById(R.id.rv_quiz_list);
        rvQuizList.setNestedScrollingEnabled(false);

    }

    public void setDefaultQuiz() {
        iconsName = new String[]{getString(R.string.daily_quiz), getString(R.string.random_quiz), getString(R.string.true_false), getString(R.string.self_challenge), getString(R.string.practice)};
        quizList = new ArrayList<>();
        if (Session.getBoolean(Session.GETDAILY, activity)) {
            quizList.add(new Category(getString(R.string.daily_quiz), R.drawable.dailyquiz));
        }
        quizList.add(new Category(getString(R.string.random_quiz), R.drawable.randomquiz));
        quizList.add(new Category(getString(R.string.true_false), R.drawable.truefalse));
        quizList.add(new Category(getString(R.string.self_challenge), R.drawable.selfchallenge));

        quizTypeAdapter = new CustomAdapter(activity, quizList, alertDialog);
        rvQuizList.setAdapter(quizTypeAdapter);
        rvQuizList.setLayoutManager(new GridLayoutManager(activity, 2));
        Utils.setGridBottomLayoutAnimation(activity, rvQuizList);

    }

    public void setLiveContest() {
        try {
            ContestFragment fragment = new ContestFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frameLayout, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            Bundle data = new Bundle();
            data.putString("current_page", getString(R.string.live));
            fragment.setArguments(data);
            ft.commit();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startShimmer() {
        mShimmerViewContainer.startShimmer();
    }

    public void stopShimmer() {
        mShimmerViewContainer.stopShimmer();
        mShimmerViewContainer.setVisibility(View.GONE);
    }

    public void getMainCategoryFromJson() {
        startShimmer();
        contentLayout.setVisibility(View.GONE);
        Map<String, String> params = new HashMap<>();
        if (Session.getBoolean(Session.LANG_MODE, activity)) {
            params.put(Constant.GET_CATE_BY_LANG, "1");
            params.put(Constant.LANGUAGE_ID, Session.getCurrentLanguage(activity));
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
                        lytCategory.setVisibility(View.VISIBLE);
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
                            categoryList.add(category);
                        }
                        stopShimmer();
                        adapter = new HomeCateAdapter(activity, categoryList);
                        Utils.setFromRightLayoutAnimation(activity, recyclerView);
                        recyclerView.setAdapter(adapter);
                        contentLayout.setVisibility(View.VISIBLE);
                    } else {
                        lytCategory.setVisibility(View.GONE);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params, activity);

    }


    public void RandomBattle(View view) {
        if (Constant.TOTAL_COINS >= Integer.parseInt(Constant.BATTLE_QUIZ_ENTRY_COINS))
            searchPlayerCall();
        else
            Utils.defaultAlertDialog(activity, getString(R.string.battle_entry_coin_alert));

    }

    public void searchPlayerCall() {
        if (Constant.isCateEnable)
            openCategoryPage(Constant.BATTLE);
        else
            startActivity(new Intent(activity, SearchPlayerActivity.class));
    }

    public void GetUpdate() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_SYSTEM_CONFIG, "1");
        ApiConfig.RequestLoginWithoutJWT((result, response) -> {
            if (result) {
                try {
                    if (maintenanceDialog != null) {
                        System.out.println("=======dialog " + response);
                        maintenanceDialog.dismiss();
                    }
                    System.out.println("=======system config " + response);
                    JSONObject obj = new JSONObject(response);
                    boolean error = obj.getBoolean(Constant.ERROR);
                    if (!error) {
                        JSONObject jsonObject = obj.getJSONObject(Constant.DATA);
                        Constant.MainTenanceMessage = jsonObject.getString(Constant.MainTenance_Message);
                        Constant.MainTenceStatus = jsonObject.getString(Constant.MainTenance_Status);
                        if (Constant.MainTenceStatus.equals("1")) {
                            showMaintenanceDialog();
                            return;
                        }
                        Constant.APP_LINK = jsonObject.getString(Constant.KEY_APP_LINK);
                        Constant.MORE_APP_URL = jsonObject.getString(Constant.KEY_MORE_APP);
                        Constant.VERSION_CODE = jsonObject.getString(Constant.KEY_APP_VERSION);
                        Constant.REQUIRED_VERSION = jsonObject.getString(Constant.KEY_APP_VERSION);
                        Constant.LANGUAGE_MODE = jsonObject.getString(Constant.KEY_LANGUAGE_MODE);
                        Constant.OPTION_E_MODE = jsonObject.getString(Constant.KEY_OPTION_E_MODE);
                        Constant.SHARE_APP_TEXT = jsonObject.getString(Constant.KEY_SHARE_TEXT);
                        Constant.QUICK_ANSWER_ENABLE = jsonObject.getString(Constant.KEY_ANSWER_MODE);
                        Constant.DAILY_QUIZ_ON = jsonObject.getString(Constant.DailyQuizText);
                        Constant.CONTEST_ON = jsonObject.getString(Constant.ContestText);
                        Constant.FORCE_UPDATE = jsonObject.getString(Constant.ForceUpdateText);
                        Constant.IN_APPPURCHASE=jsonObject.getString(Constant.Get_IN_APPURCHASE);
                        Session.setBoolean(Session.E_MODE, Constant.OPTION_E_MODE.equals("1"), activity);
                        /*FB ads ids*/

                        Constant.IN_APP_MODE = jsonObject.getString(Constant.INAppAdsMode);
                        Constant.ADS_TYPE = jsonObject.getString(Constant.Ads_Type);
                        Constant.FB_REWARDS_ADS = jsonObject.getString(Constant.fbRewardsAds);
                        Constant.FB_INTERSTITIAL = jsonObject.getString(Constant.fbInterstitial);
                        Constant.FB_BANNER = jsonObject.getString(Constant.fbBanner);
                        Constant.FB_NATIVE = jsonObject.getString(Constant.fbNative);

                        Constant.APP_ID = jsonObject.getString(Constant.AppID);
                        Constant.ADMOB_REWARDS_ADS = jsonObject.getString(Constant.AdmobRewardsAds);
                        Constant.ADMOB_INTERSTITIAL = jsonObject.getString(Constant.AdmobInterstitial);
                        Constant.ADMOB_BANNER = jsonObject.getString(Constant.AdmobBanner);
                        Constant.ADMOB_NATIVE = jsonObject.getString(Constant.AdmobNative);
                        Constant.ADMOB_OPEN_ADS = jsonObject.getString(Constant.AdmobOpenAds);


                        if (jsonObject.has(Constant.RANDOM_BATTLE_CATE_MODE))
                            Constant.isCateEnable = jsonObject.getString(Constant.RANDOM_BATTLE_CATE_MODE).equals("1");
                        if (jsonObject.has(Constant.GROUP_BATTLE_CATE_MODE))
                            Constant.isGroupCateEnable = jsonObject.getString(Constant.GROUP_BATTLE_CATE_MODE).equals("1");
                        Session.setBoolean(Session.GETDAILY, Constant.DAILY_QUIZ_ON.equals("1"), activity);
                        Session.setBoolean(Session.GETCONTEST, Constant.CONTEST_ON.equals("1"), activity);
                        lytContest.setVisibility(Session.getBoolean(Session.GETCONTEST, activity) ? View.VISIBLE : View.GONE);

                        if (Constant.LANGUAGE_MODE.equals("1")) {
                            Session.setBoolean(Session.LANG_MODE, true, activity);
                            if (Session.getCurrentLanguage(activity).equals(Constant.D_LANG_ID)) {
                                LanguageDialog(activity);
                            }
                            imgLanguage.setVisibility(View.VISIBLE);
                        } else {
                            Session.setBoolean(Session.LANG_MODE, false, activity);
                            if (!Session.getCurrentLanguage(activity).equals(Constant.D_LANG_ID)) {
                                getMainCategoryFromJson();
                                Session.setCurrentLanguage(Constant.D_LANG_ID, activity);
                            }
                            imgLanguage.setVisibility(View.INVISIBLE);
                        }

                        Session.setBoolean(Session.E_MODE, Constant.OPTION_E_MODE.equals("1"), activity);
                        setDefaultQuiz();
                        String versionName = "";
                        try {

                            PackageInfo packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
                            versionName = packageInfo.versionName;

                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        if (Constant.FORCE_UPDATE.equals("1")) {
                            if (compareVersion(versionName, Constant.VERSION_CODE) < 0) {
                                OpenBottomDialog(activity);
                            } else if (compareVersion(versionName, Constant.REQUIRED_VERSION) < 0) {
                                OpenBottomDialog(activity);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, params);
    }

    @SuppressLint("SetTextI18n")
    public void GetCustomCoins() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_COINS_LIST, "1");
        ApiConfig.RequestLoginWithoutJWT((result, response) -> {
            if (result) {
                try {
                    System.out.println("===get custom coins " + response);

                    JSONObject obj = new JSONObject(response);
                    boolean error = obj.getBoolean(Constant.ERROR);
                    if (!error) {
                        JSONObject jsonObject = obj.getJSONObject(Constant.DATA);
                        Constant.FOR_CORRECT_ANS_COIN = jsonObject.getString(Constant.TrueAnswer);
                        Constant.PENALTY_COIN = jsonObject.getString(Constant.PenaltyWrongAnswer);
                        Constant.DAILY_EARN_COIN = jsonObject.getString(Constant.DailyEarnCoin);
                        Constant.BATTLE_QUIZ_ENTRY_COINS = jsonObject.getString(Constant.BattleQuizEntry);
                        Constant.BATTLE_WINNER_COINS = jsonObject.getString(Constant.BattleWinner);
                        Constant.REFER_COIN_VALUE = jsonObject.getString(Constant.REFER_COIN);
                        Constant.EARN_COIN_VALUE = jsonObject.getString(Constant.EARN_COIN);
                        Constant.REWARD_COIN_VALUE = jsonObject.getString(Constant.REWARD_COIN);
                        System.out.println("==== reward " + Constant.REWARD_COIN_VALUE);
                        tvWinCoin.setText(Constant.DAILY_EARN_COIN);
                        tvBattleWinCoins.setText(Constant.BATTLE_WINNER_COINS + getString(R.string._coins));
                        tvBattleEntryCoins.setText(Constant.BATTLE_QUIZ_ENTRY_COINS + getString(R.string._coins));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, params);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void LanguageDialog(Activity activity) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater1 = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater1.inflate(R.layout.language_dialog, null);
        dialog.setView(dialogView);
        RecyclerView languageView = dialogView.findViewById(R.id.recyclerView);
        languageView.setLayoutManager(new LinearLayoutManager(activity));
        alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        GetLanguage(languageView, activity, alertDialog);
    }


    public void GetUserStatus() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_USER_BY_ID, "1");
        params.put(Constant.ID, Session.getUserData(Session.USER_ID, activity));
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    System.out.println("=== user status " + response);
                    JSONObject obj = new JSONObject(response);
                    String error = obj.getString(Constant.ERROR);
                    if (error.equalsIgnoreCase(Constant.FALSE)) {
                        JSONObject jsonObject = obj.getJSONObject(Constant.DATA);
                        if (jsonObject.getString(Constant.status).equals(Constant.DE_ACTIVE)) {
                            Session.clearUserSession(activity);
                            FirebaseAuth.getInstance().signOut();
                            LoginManager.getInstance().logOut();
                            Intent intentLogin = new Intent(activity, LoginActivity.class);
                            startActivity(intentLogin);
                            finish();
                        } else {
                            Constant.TOTAL_COINS = Integer.parseInt(jsonObject.getString(Constant.TotalCoins));
                            Constant.DAILY_QUIZ_STATUS = jsonObject.getString(Constant.KEY_DAILY_QUIZ_STATUS);
                            Constant.FREE_COIN_STATUS = jsonObject.getString(Constant.KEY_FREE_COIN_STATUS);
                            tvCoins.setText("" + Constant.TOTAL_COINS);
                            tvRank.setText("" + jsonObject.getString(Constant.GLOBAL_RANK));
                            tvMoney.setText(getString(R.string.dollar) + jsonObject.getString(Constant.PaypalAmount));
                            tvScore.setText(jsonObject.getString(Constant.GLOBAL_SCORE));
                            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                                String token = task.getResult();
                                if (token != null)
                                    if (!token.equals(Session.getUserData(Session.FCM, activity))) {
                                        Utils.postTokenToServer(activity, token);
                                    }
                            });
                        }
                    } else {
                        if (obj.getString(Constant.LOGIN).equalsIgnoreCase(Constant.TRUE)) {
                            if (userLoggedOutDialog != null) {
                                userLoggedOutDialog.show();
                            }

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params, activity);
    }

    public void userLoggedOutDialog() {
        userLoggedOutDialog = new BottomSheetDialog(activity, R.style.BottomSheetTheme);
        View sheetView = activity.getLayoutInflater().inflate(R.layout.lyt_logout_page, null);
        userLoggedOutDialog.setCancelable(false);
        sheetView.findViewById(R.id.tvOk).setOnClickListener(view -> {
            userLoggedOutDialog.dismiss();
            Session.clearUserSession(activity);
            LoginManager.getInstance().logOut();
            LoginActivity.mAuth.signOut();
            FirebaseAuth.getInstance().signOut();
            Intent intentLogin = new Intent(activity, LoginActivity.class);
            intentLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intentLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intentLogin);
            activity.finish();
        });
        userLoggedOutDialog.setContentView(sheetView);

    }

    public void UpdateProfile() {
        startActivity(new Intent(activity, ProfileActivity.class));
    }

    //send registration token to server

    public void LeaderBoard(View view) {
        Utils.btnClick(view, activity);
        startActivity(new Intent(activity, LeaderboardActivity.class));
    }

    public void UserProfile(View view) {
        Utils.btnClick(view, activity);
        UpdateProfile();
    }

    public void GetLanguage(final RecyclerView languageView, final Context context, final AlertDialog alertDialog) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_LANGUAGES, "1");
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean(Constant.ERROR);
                    if (!error) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        ArrayList<Language> languageList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Language language = new Language();
                            JSONObject object = jsonArray.getJSONObject(i);
                            language.setId(object.getString(Constant.ID));
                            language.setLanguage(object.getString(Constant.LANGUAGE));
                            languageList.add(language);
                        }
                        if (languageList.size() == 1) {
                            Session.setCurrentLanguage(languageList.get(0).getId(), context);
                            getMainCategoryFromJson();
                            if (!Session.getBoolean(Session.IS_FIRST_TIME, activity)) {
                                Session.setBoolean(Session.IS_FIRST_TIME, true, context);
                            }
                        } else {

                            if (!Session.getBoolean(Session.IS_FIRST_TIME, activity) || Session.getCurrentLanguage(activity).equals(Constant.D_LANG_ID)) {
                                if (alertDialog != null)
                                    alertDialog.show();
                            }
                        }

                        LanguageAdapter languageAdapter = new LanguageAdapter(context, languageList, alertDialog);
                        languageView.setAdapter(languageAdapter);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params, activity);


    }

    public void showAllContest(View view) {
        Intent intent = new Intent(activity, ContestActivity.class);
        startActivity(intent);
    }

    public void EarningActivity(View view) {
        startActivity(new Intent(activity, EarningActivity.class));
    }

    public void getScratchCoins(View view) {

        if (Constant.FREE_COIN_STATUS.equals("0"))
            Utils.scratchCardDialog(activity);
        else
            Utils.defaultAlertDialog(activity, getString(R.string.already_get_coins));

    }

    public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ItemRowHolder> {
        private final ArrayList<Language> dataList;
        private final Context mContext;
        AlertDialog alertDialog;

        public LanguageAdapter(Context context, ArrayList<Language> dataList, AlertDialog alertDialog) {
            this.dataList = dataList;
            this.mContext = context;
            this.alertDialog = alertDialog;
        }

        @NonNull
        @Override
        public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.language_layout, parent, false);
            return new ItemRowHolder(v);
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onBindViewHolder(@NonNull ItemRowHolder holder, final int position) {

            final Language language = dataList.get(position);
            final ItemRowHolder itemRowHolder = holder;
            itemRowHolder.tvLanguage.setText(language.getLanguage());
            if (Session.getCurrentLanguage(mContext).equals(language.getId())) {
                itemRowHolder.radio.setImageResource(R.drawable.ic_radio_check);
            } else {
                itemRowHolder.radio.setImageResource(R.drawable.ic_radio_unchecked);
            }
            itemRowHolder.radio.setOnClickListener(view -> {
                itemRowHolder.radio.setImageResource(R.drawable.ic_radio_check);
                Session.setCurrentLanguage(language.getId(), mContext);
                Session.setBoolean(Session.IS_FIRST_TIME, true, mContext);
                notifyDataSetChanged();
                getMainCategoryFromJson();
                alertDialog.dismiss();
            });

        }

        @Override
        public int getItemCount() {
            return (dataList.size());
        }

        public class ItemRowHolder extends RecyclerView.ViewHolder {
            public ImageView radio;
            public TextView tvLanguage;


            public ItemRowHolder(View itemView) {
                super(itemView);
                radio = itemView.findViewById(R.id.radio);
                tvLanguage = itemView.findViewById(R.id.tvLanguage);
            }
        }

    }


    public void openCategoryPage(String type) {
        startActivity(new Intent(activity, CategoryActivity.class)
                .putExtra(Constant.QUIZ_TYPE, type));
    }


    public void showMaintenanceDialog() {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_maintaince, null);
        dialog.setView(dialogView);
        TextView message = dialogView.findViewById(R.id.text);
        message.setText(Constant.MainTenanceMessage);
         maintenanceDialog = dialog.create();
        Utils.setDialogBg(maintenanceDialog);
        maintenanceDialog.show();

        maintenanceDialog.setCancelable(false);
        maintenanceDialog.setOnKeyListener((dialog1, keyCode, event) -> {
            // Disable Back key and Search key
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                finish();
                maintenanceDialog.dismiss();
                return true;
            }
            return false;
        });
        maintenanceDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (userLoggedOutDialog != null) {
            userLoggedOutDialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.isNetworkAvailable(activity)) {
            GetUpdate();
            GetUserStatus();
            GetCustomCoins();

            invalidateOptionsMenu();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}