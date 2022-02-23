package com.qearner.quiz.earning;

import android.app.Activity;
import android.graphics.Color;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;


import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.android.material.tabs.TabLayout;
import com.qearner.quiz.R;
import com.qearner.quiz.ads.AdUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EarningActivity extends AppCompatActivity {


    ViewPager viewPager;
    TabLayout tabLayout;
    Toolbar toolbar;
    public static ViewPagerAdapter adapter;
    String[] tabs;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earning);
        getAllWidgets();
        activity = EarningActivity.this;
        tabs = new String[]{getString(R.string.my_earning), getString(R.string.transaction)};

        setupViewPager(viewPager);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.wallet);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);

        AdUtils.loadFacebookBannerAds(activity);
    }

    public void getAllWidgets() {
        toolbar = findViewById(R.id.toolBar);
        viewPager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tabs);

    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(this.getSupportFragmentManager());
        adapter.addFrag(new EarningFragment(), tabs[0]);
        adapter.addFrag(new TransactionFragment(), tabs[1]);

        viewPager.setAdapter(adapter);
    }


    static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }


        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {

            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }
    }


    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        if (AdUtils.mAdView != null) {
            AdUtils.mAdView.destroy();
        }
        super.onDestroy();
    }
}

