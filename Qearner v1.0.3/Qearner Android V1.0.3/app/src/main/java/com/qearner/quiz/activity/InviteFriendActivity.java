package com.qearner.quiz.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.qearner.quiz.Constant;
import com.qearner.quiz.R;
import com.qearner.quiz.helper.Session;

import com.qearner.quiz.vollyConfigs.ApiConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InviteFriendActivity extends AppCompatActivity {

    TextView tvReferCoin, tvCode, tvCopy, tvInvite;
    Toolbar toolbar;
    AppCompatActivity activity;
    MaterialButton btnInvite;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_frnd);

        activity = InviteFriendActivity.this;
        getAllWidgets();
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.refer_amp_earn));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvReferCoin.setText(getString(R.string.refer_message_1) + Constant.EARN_COIN_VALUE + getString(R.string.refer_message_2) + Constant.REFER_COIN_VALUE + getString(R.string.refer_message_3));

        if (Session.getUserData(Session.REFER_CODE, getApplicationContext()) == null) {
            getUserData();
        } else {
            tvCode.setText(Session.getUserData(Session.REFER_CODE, getApplicationContext()));
        }

        tvCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", tvCode.getText());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(activity, R.string.refer_code_copied, Toast.LENGTH_SHORT).show();
        });
    }

    public void getAllWidgets() {
        toolbar = findViewById(R.id.toolBar);
        tvReferCoin = findViewById(R.id.tvReferCoin);
        tvCode = findViewById(R.id.tvCode);
        tvCopy = findViewById(R.id.tvCopy);
        btnInvite = findViewById(R.id.btnInvite);
    }


    public void getUserData() {
        Map<String, String> params = new HashMap<>();

        params.put(Constant.GET_USER_BY_ID, "1");
        params.put("device_id","1234");
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, getApplicationContext()));
        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {
                try {

                    JSONObject obj = new JSONObject(response);
                    boolean error = obj.getBoolean(Constant.ERROR);
                    if (!error) {
                        JSONObject jsonObject = obj.getJSONObject(Constant.DATA);
                        tvCode.setText(jsonObject.getString("refer"));

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params, activity);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void OnInviteFrdClick(View view) {
        if (!tvCode.getText().toString().equals("code")) {
            try {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.refer_share_msg_1) + getResources().getString(R.string.app_name) + getString(R.string.refer_share_msg_2) + "\n\" " + tvCode.getText().toString() + " \"\n\n" + Constant.APP_LINK);
                startActivity(Intent.createChooser(sharingIntent, "Invite Friend Using"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.refer_code_generate_error_msg), Toast.LENGTH_SHORT).show();
        }
    }
}
