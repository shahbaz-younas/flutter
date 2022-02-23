package com.qearner.quiz.leaderboard;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.qearner.quiz.R;
import com.qearner.quiz.UI.CircleImageView;
import com.qearner.quiz.helper.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class LeaderboardActivity extends AppCompatActivity {


    ViewPager viewPager;
    TabLayout tabLayout;
    Toolbar toolbar;
    TextView score, coin, tvName;
    CircleImageView imgProfile;
    ViewPagerAdapter adapter;
    String[] tabs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setStatusBarColor(LeaderboardActivity.this,ContextCompat.getColor(getApplicationContext(), R.color.bg_color));
        setContentView(R.layout.activity_tableader);
        tabs = new String[]{getString(R.string.today), getString(R.string.month), getString(R.string.all)};
        getAllWidgets();
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.leaderboard);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    public void getAllWidgets() {
        toolbar = findViewById(R.id.toolBar);
        viewPager = findViewById(R.id.pager);
        setupViewPager(viewPager);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        imgProfile = findViewById(R.id.imgProfile);
        tvName = findViewById(R.id.tvName);
        coin = findViewById(R.id.tvScore);
        score = findViewById(R.id.tvRank);
    }


    private void setupViewPager(ViewPager viewPager) {
        adapter = new LeaderboardActivity.ViewPagerAdapter(this.getSupportFragmentManager());
        adapter.addFrag(new DailyLeaderboardFragment(), tabs[0]);
        adapter.addFrag(new MonthlyLeaderboardFragment(), tabs[1]);
        adapter.addFrag(new AllLeaderboardFragment(), tabs[2]);
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

}
