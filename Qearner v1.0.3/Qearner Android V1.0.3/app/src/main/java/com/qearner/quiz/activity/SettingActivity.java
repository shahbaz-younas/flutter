package com.qearner.quiz.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;


import com.qearner.quiz.Constant;
import com.qearner.quiz.R;
import com.qearner.quiz.helper.AppController;
import com.qearner.quiz.helper.Session;


public class SettingActivity extends AppCompatActivity {
    AppCompatActivity activity;
    Dialog mCustomDialog;
    SwitchCompat mSoundCheckBox, mVibrationCheckBox;
    TextView tvOk;
    boolean isSoundOn;
    boolean isVibrationOn;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setContentView(R.layout.activity_setting);
        getAllWidgets();
        activity = SettingActivity.this;


        populateSoundContents();
        populateVibrationContents();
        tvOk.setOnClickListener(v -> {
            overridePendingTransition(R.anim.open_next, R.anim.close_next);
            finish();
        });
    }

    private void getAllWidgets() {
        mSoundCheckBox = findViewById(R.id.sound_checkbox);
        mVibrationCheckBox = findViewById(R.id.vibration_checkbox);
        tvOk = findViewById(R.id.tvOk);

    }


    private void switchSoundCheckbox() {
        isSoundOn = !isSoundOn;
        Session.setSoundEnableDisable(activity, isSoundOn);
        populateSoundContents();
    }

    private void switchVibrationCheckbox() {
        isVibrationOn = !isVibrationOn;
        Session.setVibration(activity, isVibrationOn);
        populateVibrationContents();
    }



    protected void populateSoundContents() {
        mSoundCheckBox.setChecked(Session.getSoundEnableDisable(activity));
        isSoundOn = Session.getSoundEnableDisable(activity);
    }

    protected void populateVibrationContents() {
        mVibrationCheckBox.setChecked(Session.getVibration(activity));
        isVibrationOn = Session.getVibration(activity);
    }


    public void viewClickHandler(View view) {
        int id = view.getId();
        if (id == R.id.sound_layout || id == R.id.sound_checkbox) {
            switchSoundCheckbox();
        } else if (id == R.id.vibration_layout || id == R.id.vibration_checkbox) {
            switchVibrationCheckbox();
        } else if (id == R.id.font_layout) {
            try {
                fontSizeDialog();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.ok) {
            onBackPressed();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

    }




    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.close_next, R.anim.open_next);
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        if (activity != null) {
            if (mCustomDialog != null) {
                mCustomDialog.dismiss();
                mCustomDialog = null;
            }
            mVibrationCheckBox = null;
            mSoundCheckBox = null;
            activity = null;
            super.onDestroy();
        }
    }
    public void fontSizeDialog() {
        String changedFontSize;
        changedFontSize = Session.getSavedTextSize(getApplicationContext());
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);


        LayoutInflater inflater1 = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater1.inflate(R.layout.dialog_font_size, null);
        dialog.setView(dialogView);

        alertDialog = dialog.create();
        TextView tvOk = dialogView.findViewById(R.id.tvOk);
        final EditText edt_font_size_value = dialogView.findViewById(R.id.edt_font_size_value);
        final SeekBar skBar_value = dialogView.findViewById(R.id.skBar_value);

        skBar_value.setMax(14);
        skBar_value.setProgress(Integer.parseInt(changedFontSize) - 16);
        edt_font_size_value.setText(changedFontSize);
        edt_font_size_value.setSelection(edt_font_size_value.getText().toString().length());

        skBar_value.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                edt_font_size_value.setText(String.valueOf(progress + 16));
                edt_font_size_value.setSelection(edt_font_size_value.getText().toString().length());
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {

                if (Integer.parseInt(edt_font_size_value.getText().toString().trim()) >= 30) {
                    edt_font_size_value.setText(Constant.TEXT_SIZE_MAX);
                    Session.saveTextSize(getApplicationContext(), Constant.TEXT_SIZE_MAX);
                } else if (Integer.parseInt(edt_font_size_value.getText().toString().trim()) < 16) {
                    edt_font_size_value.setText(Constant.TEXT_SIZE_MIN);
                    Session.saveTextSize(getApplicationContext(), Constant.TEXT_SIZE_MIN);
                } else {
                    Session.saveTextSize(getApplicationContext(), edt_font_size_value.getText().toString().trim());

                }
            }

        });

        edt_font_size_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String currentProgress = edt_font_size_value.getText().toString().trim();
                if (!currentProgress.equals("")) {
                    skBar_value.setProgress(Integer.parseInt(currentProgress) - 16);
                    edt_font_size_value.setSelection(edt_font_size_value.getText().toString().length());
                }
            }
        });
        tvOk.setOnClickListener(v -> alertDialog.dismiss());
        alertDialog.show();
    }

}
