package com.qearner.quiz.helper;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.login.LoginManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.qearner.quiz.Constant;
import com.qearner.quiz.R;


import com.qearner.quiz.UI.ScratchView;
import com.qearner.quiz.activity.PrivacyPolicy;
import com.qearner.quiz.login.LoginActivity;
import com.qearner.quiz.model.Language;
import com.qearner.quiz.model.Question;
import com.qearner.quiz.vollyConfigs.ApiConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;


public class Utils {


    public static AlertDialog alertDialog;
    private static Vibrator sVibrator;
    public static int TotalQuestion = 1;
    public static int correctQuestion = 1;
    public static int wrongQuestion = 1;

    public static int quiz_coin = 1;
    public static int quiz_score = 0;
    public static final long VIBRATION_DURATION = 100;

    public static final boolean DEFAULT_SOUND_SETTING = true;
    public static final boolean DEFAULT_VIBRATION_SETTING = true;
    public static final boolean DEFAULT_MUSIC_SETTING = false;


    public static void backSoundOnclick(Context mContext) {
        try {
            int resourceId = R.raw.click2;
            MediaPlayer mediaplayer = MediaPlayer.create(mContext, resourceId);
            mediaplayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setRightAnsSound(Context mContext) {
        try {
            int resourceId = R.raw.right;
            MediaPlayer mediaplayer = MediaPlayer.create(mContext, resourceId);
            mediaplayer.setOnCompletionListener(mp -> {
                mp.reset();
                mp.release();
            });
            mediaplayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setWrongAnsSound(Context mContext) {
        try {
            int resourceId = R.raw.wrong;
            MediaPlayer mediaplayer = MediaPlayer.create(mContext, resourceId);
            mediaplayer.setOnCompletionListener(mp -> {
                mp.reset();
                mp.release();
            });
            mediaplayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void privacyPolicyMsg(TextView tvPrivacy, Activity activity) {
        tvPrivacy.setClickable(true);
        tvPrivacy.setMovementMethod(LinkMovementMethod.getInstance());

        String message = activity.getString(R.string.term_privacy);
        String s2 = activity.getString(R.string.terms);
        String s1 = activity.getString(R.string.privacy_policy);
        final Spannable wordToSpan = new SpannableString(message);

        wordToSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, PrivacyPolicy.class);
                intent.putExtra("type", "privacy");
                activity.startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(activity, R.color.colorPrimary));
                ds.isUnderlineText();
            }
        }, message.indexOf(s1), message.indexOf(s1) + s1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordToSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, PrivacyPolicy.class);
                intent.putExtra("type", "terms");
                activity.startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(activity, R.color.colorPrimary));
                ds.isUnderlineText();
            }
        }, message.indexOf(s2), message.indexOf(s2) + s2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvPrivacy.setText(wordToSpan);
    }

    public static void vibrate(Context context, long duration) {
        if (sVibrator == null) {
            sVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
        if (sVibrator != null) {
            if (duration == 0) {
                duration = 50;
            }
            sVibrator.vibrate(duration);
        }
    }

    public static void setFallDownLayoutAnimation(AppCompatActivity activity, RecyclerView recyclerView) {
        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(activity, resId);
        recyclerView.setLayoutAnimation(animation);
    }

    public static void setleftLayoutAnimation(AppCompatActivity activity, CoordinatorLayout linearLayout) {
        int resId = R.anim.layout_animation_from_bottom;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(activity, resId);
        animation.setDelay(1);
        linearLayout.setLayoutAnimation(animation);
    }

    public static void setFromBottomLayoutAnimation(AppCompatActivity activity, RecyclerView recyclerView) {
        int resId = R.anim.layout_animation_from_bottom;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(activity, resId);
        recyclerView.setLayoutAnimation(animation);
    }

    public static void setGridBottomLayoutAnimation(AppCompatActivity activity, RecyclerView recyclerView) {
        int resId = R.anim.grid_layout_animation_from_bottom;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(activity, resId);
        recyclerView.setLayoutAnimation(animation);

        recyclerView.scheduleLayoutAnimation();
    }

    public static void setFromRightLayoutAnimation(AppCompatActivity activity, RecyclerView recyclerView) {
        int resId = R.anim.layout_animation_from_right;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(activity, resId);
        recyclerView.setLayoutAnimation(animation);
    }


    public static void setAlertMsg(AppCompatActivity activity, String type) {
        ImageView img = activity.findViewById(R.id.image);
        TextView tvAlert = activity.findViewById(R.id.tvAlert);
        TextView tvAlertMsg = activity.findViewById(R.id.tvAlertMsg);
        TextView tvBack = activity.findViewById(R.id.tvBack);

        switch (type) {
            case "notify":
                img.setImageResource(R.drawable.ic_notifications_none);
                tvAlert.setText(activity.getResources().getString(R.string.no_notification));
                break;
            case "internet":
                img.setImageResource(R.drawable.ic_no_internet);
                tvAlert.setText(activity.getResources().getString(R.string.msg_no_internet));
                break;
            case "cate":
                img.setImageResource(R.drawable.ic_category_none);
                tvAlert.setText(activity.getResources().getString(R.string.no_category));
                break;
            case "sub_cate":
                img.setImageResource(R.drawable.ic_category_none);
                tvAlert.setText(activity.getResources().getString(R.string.no_sub_category));
                break;
            case "bookmark":
                img.setImageResource(R.drawable.ic_bookmarks_none);
                tvAlert.setText(activity.getResources().getString(R.string.no_bookmark));
                break;
            case "question":
                img.setImageResource(R.drawable.ic_not_found);
                tvAlert.setText(activity.getResources().getString(R.string.question_not_available));
                break;
            case "contest":
                img.setImageResource(R.drawable.ic_not_found);
                tvAlert.setText(activity.getResources().getString(R.string.contest_not_available));
                break;
        }
        tvBack.setOnClickListener(v -> activity.onBackPressed());

    }

    public static void userLoggedOutDialog(Activity activity) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity, R.style.BottomSheetTheme);
        View sheetView = activity.getLayoutInflater().inflate(R.layout.lyt_logout_page, null);
        bottomSheetDialog.setCancelable(false);
        sheetView.findViewById(R.id.tvOk).setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
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
        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
        } else {
            return false;
        }
    }

    public static void defaultAlertDialog(Activity activity, String title) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_default, null);
        dialog.setView(dialogView);
        TextView ok = dialogView.findViewById(R.id.ok);
        TextView tvTitle = dialogView.findViewById(R.id.title);
        tvTitle.setText(title);
        final AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        alertDialog.setCancelable(false);
        ok.setOnClickListener(view -> alertDialog.dismiss());
    }


    public static void CheckVibrateOrSound(Context context) {

        if (Session.getSoundEnableDisable(context)) {
            backSoundOnclick(context);
        }
        if (Session.getVibration(context)) {
            vibrate(context, Utils.VIBRATION_DURATION);
        }
    }


    public static void btnClick(View view, Activity activity) {
        Animation myAnim = AnimationUtils.loadAnimation(activity, R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);
        CheckVibrateOrSound(activity);
    }

    public static void setDialogBg(AlertDialog alertDialog) {
        if (alertDialog != null)
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    static class MyBounceInterpolator implements android.view.animation.Interpolator {
        double mAmplitude;
        double mFrequency;

        MyBounceInterpolator(double amplitude, double frequency) {
            mAmplitude = amplitude;
            mFrequency = frequency;
        }

        public float getInterpolation(float time) {
            return (float) (-1 * Math.pow(Math.E, -time / mAmplitude) *
                    Math.cos(mFrequency * time) + 1);
        }
    }

    public static Bitmap getBitmapFromView(View view, int height, int width) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return bitmap;
    }

    public static void saveImage(ScrollView scrollView, Activity activity) {
        try {

            Bitmap bitmap = getBitmapFromView(scrollView, scrollView.getChildAt(0).getHeight(), scrollView.getChildAt(0).getWidth());
            File cachePath = new File(activity.getCacheDir(), "images");
            if (!cachePath.exists()) {
                cachePath.mkdirs(); // don't forget to make the directory
            }
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 75, stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void ShareImage(Activity activity, String shareMsg) {
        File imagePath = new File(activity.getCacheDir(), "images");

        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", newFile);

        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, activity.getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMsg);
            activity.startActivity(Intent.createChooser(shareIntent, "Share via"));
        }
    }

    public static void ShareInfo(ScrollView scrollView, Activity activity, String shareMsg) {
        ProgressDialog pDialog = new ProgressDialog(activity);
        pDialog.setMessage("Please wait...");
        pDialog.setIndeterminate(true);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(true);
        new DownloadFiles(scrollView, pDialog, activity, shareMsg).execute();
    }

    @SuppressLint("StaticFieldLeak")
    public static class DownloadFiles extends AsyncTask<String, Integer, String> {

        ScrollView scrollView;
        ProgressDialog pDialog;
        Activity activity;
        String shareMsg;

        public DownloadFiles(ScrollView linearLayout, ProgressDialog pDialog, Activity activity, String shareMsg) {
            this.scrollView = linearLayout;
            this.pDialog = pDialog;
            this.activity = activity;
            this.shareMsg = shareMsg;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            saveImage(scrollView, activity);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pDialog != null)
                pDialog.show();
        }


        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pDialog != null)
                pDialog.dismiss();
            ShareImage(activity, shareMsg);
        }
    }

    @SuppressLint("SetTextI18n")
    public static void scratchCardDialog(Activity activity) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity, R.style.scratch_dialog);
        LayoutInflater inflater1 = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater1.inflate(R.layout.dialog_scratch_card, null);
        dialog.setView(dialogView);
        ScratchView scratchView;
        LottieAnimationView animationView;
        scratchView = dialogView.findViewById(R.id.scratchView);
        animationView = dialogView.findViewById(R.id.animationView);
        TextView tvCoins = dialogView.findViewById(R.id.tvCoins);
        final int min = 1;
        final int max = Integer.parseInt(Constant.DAILY_EARN_COIN);
        final int random = new Random().nextInt((max - min) + 1) + min;
        tvCoins.setText("" + random);
        scratchView.setRevealListener(new ScratchView.IRevealListener() {
            @Override
            public void onRevealed(ScratchView scratchView) {
                Constant.FREE_COIN_STATUS = "1";
                Utils.AddCoins(activity, "" + random, "Free Coins", "Scratch coin Win", "0");
                updateCoinOrDailyQuizStatus(activity, Constant.FreeCoins);
                animationView.playAnimation();
            }

            @Override
            public void onRevealPercentChangedListener(ScratchView scratchView, float percent) {
                if (percent >= 0.5) {
                    Log.d("Reveal Percentage", "onRevealPercentChangedListener: " + percent);
                }
            }
        });

        AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

    }

    public static void updateCoinOrDailyQuizStatus(Activity activity, String type) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.DailyStatus, Constant.GET_DATA_KEY);
        params.put(Constant.type, type);
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, activity));
        ApiConfig.RequestToVolley((result, response) -> {

        }, params, activity);

    }

    public static void postTokenToServer(final Activity activity, final String token) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.updateFcmId, "1");
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, activity));
        params.put(Constant.fcmId, token);
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject obj = new JSONObject(response);
                    boolean error = obj.getBoolean(Constant.ERROR);
                    if (!error) {
                        Session.setUserData(Session.FCM, token, activity);
                        FirebaseDatabase.getInstance().getReference("user").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("fcm_id").setValue(token);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params, activity);
    }

    public static void UpdateCoin(final Activity activity, final String coins) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.SET_USER_COINS, "1");
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, activity));
        params.put(Constant.COINS, coins);
        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString(Constant.ERROR).equals(Constant.FALSE)) {
                        JSONObject jsonObject = obj.getJSONObject(Constant.DATA);
                        Constant.TOTAL_COINS = Integer.parseInt(jsonObject.getString(Constant.COINS));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params, activity);

    }

    public static void AddCoins(final Activity activity, final String coins, final String type, final String typeTwo, final String CoinStatus) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.AddPoint, "1");
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, activity));
        params.put(Constant.AUTH_ID, Session.getUserData(Session.UID, activity));
        params.put(Constant.POINTS, coins);
        params.put(Constant.type, type);
        params.put(Constant.TypeTwo, typeTwo);
        params.put(Constant.CoinStatus, CoinStatus);
        ApiConfig.RequestToVolley((result, response) -> {

            if (result) {
                try {
                    System.out.println("===add coin " + response);
                    JSONObject obj = new JSONObject(response);
                    obj.getString(Constant.ERROR);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params, activity);
    }

    public static ArrayList<Question> getQuestions(JSONArray jsonArray, Activity activity) {
        ArrayList<Question> questionList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                Question question = new Question();
                JSONObject object = jsonArray.getJSONObject(i);
                question.setId(Integer.parseInt(object.getString(Constant.ID)));
                question.setQuestion(object.getString(Constant.QUESTION));
                question.setImage(object.getString(Constant.IMAGE));
                question.setQueType(object.getString(Constant.QUE_TYPE));
                question.addOption(object.getString(Constant.OPTION_A).trim());
                question.addOption(object.getString(Constant.OPTION_B).trim());
                question.addOption(object.getString(Constant.OPTION_C).trim());
                question.addOption(object.getString(Constant.OPTION_D).trim());
                if (Session.getBoolean(Session.E_MODE, activity)) {
                    if (!object.getString(Constant.OPTION_E).isEmpty() || !object.getString(Constant.OPTION_E).equals(""))
                        question.addOption(object.getString(Constant.OPTION_E).trim());
                }
                question.setSelectedOpt("none");
                String rightAns = object.getString("answer");
                question.setAnsOption(rightAns);
                if (rightAns.equalsIgnoreCase("A")) {
                    question.setTrueAns(object.getString(Constant.OPTION_A).trim());
                } else if (rightAns.equalsIgnoreCase("B")) {
                    question.setTrueAns(object.getString(Constant.OPTION_B).trim());
                } else if (rightAns.equalsIgnoreCase("C")) {
                    question.setTrueAns(object.getString(Constant.OPTION_C).trim());
                } else if (rightAns.equalsIgnoreCase("D")) {
                    question.setTrueAns(object.getString(Constant.OPTION_D).trim());
                } else if (rightAns.equalsIgnoreCase("E")) {
                    question.setTrueAns(object.getString(Constant.OPTION_E).trim());
                }
                question.setNote(object.getString(Constant.NOTE));
                questionList.add(question);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return questionList;
    }

    public static void SignOutWarningDialog(final Activity activity) {
        final AlertDialog.Builder dialog1 = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dailog_logout, null);
        dialog1.setView(dialogView);
        dialog1.setCancelable(true);
        final AlertDialog alertDialog = dialog1.create();
        TextView tvYes = dialogView.findViewById(R.id.tvYes);
        TextView tvNo = dialogView.findViewById(R.id.tvNo);

        tvNo.setOnClickListener(v -> alertDialog.dismiss());
        tvYes.setOnClickListener(view -> {
            alertDialog.dismiss();
            Session.clearUserSession(activity);
            LoginManager.getInstance().logOut();
            LoginActivity.mAuth.signOut();
            FirebaseAuth.getInstance().signOut();
            Intent intentLogin = new Intent(activity, LoginActivity.class);
            activity.startActivity(intentLogin);
            activity.finish();

        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.show();

    }

    public static void transparentStatusAndNavigation(Activity context) {

        context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false, context);
        context.getWindow().setStatusBarColor(Color.TRANSPARENT);
        //context.getWindow().setNavigationBarColor(Color.TRANSPARENT);
    }


    public static void ForgotPasswordPopUp(final Activity activity, final FirebaseAuth firebaseAuth) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.lyt_forgot_password, null);
        dialog.setView(dialogView);
        TextView tvSubmit = dialogView.findViewById(R.id.tvSubmit);

        final EditText edtEmail = dialogView.findViewById(R.id.edtEmail);

        final AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCancelable(true);
        tvSubmit.setOnClickListener(view -> {
            String email = edtEmail.getText().toString().trim();
            if (email.isEmpty())
                edtEmail.setError(activity.getResources().getString(R.string.email_alert_1));
            else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
                edtEmail.setError(activity.getResources().getString(R.string.email_alert_2));
            else {
                firebaseAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(activity, "Email sent", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        });
            }
        });

        alertDialog.show();
    }


    public static void setWindowFlag(final int bits, boolean on, Activity context) {
        Window win = context.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public static int compareVersion(String version1, String version2) {
        String[] arr1 = version1.split("\\.");
        String[] arr2 = version2.split("\\.");

        int i = 0;
        while (i < arr1.length || i < arr2.length) {
            if (i < arr1.length && i < arr2.length) {
                if (Integer.parseInt(arr1[i]) < Integer.parseInt(arr2[i])) {
                    return -1;
                } else if (Integer.parseInt(arr1[i]) > Integer.parseInt(arr2[i])) {
                    return 1;
                }
            } else if (i < arr1.length) {
                if (Integer.parseInt(arr1[i]) != 0) {
                    return 1;
                }
            } else {
                if (Integer.parseInt(arr2[i]) != 0) {
                    return -1;
                }
            }
            i++;
        }
        return 0;
    }

    public static void OpenBottomDialog(final Activity activity) {
        View sheetView = View.inflate(activity, R.layout.lyt_terms_privacy, null);
        ViewGroup parentViewGroup = (ViewGroup) sheetView.getParent();
        if (parentViewGroup != null) {
            parentViewGroup.removeAllViews();
        }
        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(activity);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.show();
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ImageView imgClose = sheetView.findViewById(R.id.imgclose);
        Button btnNotNow = sheetView.findViewById(R.id.btnNotNow);
        Button btnUpdateNow = sheetView.findViewById(R.id.btnUpdateNow);
        mBottomSheetDialog.setCancelable(false);
        imgClose.setOnClickListener(v -> {
            if (mBottomSheetDialog.isShowing())
                mBottomSheetDialog.dismiss();
        });
        btnNotNow.setOnClickListener(v -> {
            if (mBottomSheetDialog.isShowing())
                mBottomSheetDialog.dismiss();
        });
        btnUpdateNow.setOnClickListener(view -> activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.APP_LINK))));
    }

    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

    @SuppressLint("SimpleDateFormat")
    public static String dateFormat(String oldDate, String newPattern) {
        String date = "";
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date newDate;

            newDate = format.parse(oldDate);
            format = new SimpleDateFormat(newPattern);
            assert newDate != null;
            date = format.format(newDate).toLowerCase();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static void setStatusBarColor(AppCompatActivity activity, int color) {
        Window window = activity.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(color);
    }
}