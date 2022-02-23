package com.rifcode.randochat.Views;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import androidx.appcompat.app.AppCompatActivity;

import com.rifcode.randochat.Utils.CustomTypefaceSpan;
import com.rifcode.randochat.R;

public class PolicyActivity extends AppCompatActivity {

    private SpannableStringBuilder SS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);

        Typeface font2 = Typeface.createFromAsset(getAssets(), "fonts/Ubuntu-Medium.ttf");
        SS = new SpannableStringBuilder(getString(R.string.laws_tv));
        SS.setSpan(new CustomTypefaceSpan("Ubuntu-Medium", font2), 0, SS.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(SS);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
