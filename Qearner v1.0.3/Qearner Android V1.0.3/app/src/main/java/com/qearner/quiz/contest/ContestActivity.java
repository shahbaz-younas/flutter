package com.qearner.quiz.contest;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.qearner.quiz.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ContestActivity extends AppCompatActivity {

    Toolbar toolbar;
    AppCompatActivity activity;

    String[] tabs;
    ViewPager viewPager;
    TabLayout tabLayout;
    ViewPagerAdapter adapter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contest);
        activity = ContestActivity.this;
        tabs = new String[]{getString(R.string.past), getString(R.string.live), getString(R.string.up_coming)};
        getAllWidgets();
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.contest_zone));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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

    public void getAllWidgets() {
        toolbar = findViewById(R.id.toolBar);
        viewPager = findViewById(R.id.pager);
        setupViewPager(viewPager);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }


    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ContestFragment(), tabs[0]);
        adapter.addFrag(new ContestFragment(), tabs[1]);
        adapter.addFrag(new ContestFragment(), tabs[2]);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
    }

    public static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            Bundle data = new Bundle();
            ContestFragment fragment = new ContestFragment();
            data.putString("current_page", mFragmentTitleList.get(position));
            fragment.setArguments(data);
            return fragment;
            //return mFragmentList.get(position);
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
