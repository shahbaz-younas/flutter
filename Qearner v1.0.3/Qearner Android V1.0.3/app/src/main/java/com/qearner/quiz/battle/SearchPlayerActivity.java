package com.qearner.quiz.battle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.toolbox.ImageLoader;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.qearner.quiz.Constant;
import com.qearner.quiz.R;
import com.qearner.quiz.activity.SettingActivity;
import com.qearner.quiz.helper.AppController;
import com.qearner.quiz.UI.CircleImageView;
import com.qearner.quiz.helper.Session;
import com.qearner.quiz.helper.Utils;
import com.qearner.quiz.model.User;


import java.util.Objects;

public class SearchPlayerActivity extends AppCompatActivity {
    String FORMAT = "%02d";
    CountDownTimer countDownTimer;
    boolean exist = false;
    AppCompatActivity activity;

    TextView tvPlayer1, tvPlayer2, tvTimer;
    CircleImageView imgPlayer1, imgPlayer2;
    DatabaseReference database, myRef;
    ValueEventListener valueEventListener, valueEventListener1;
    boolean isRunning = false;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    LinearLayout lytTimer, content_layout;
    MaterialButton btnSearchPlayer;


    String pauseCheck = "regular", profilePlayer2, player1Name, player2Name, userProfile,
            userId1, userId2, player = "", opponentId = "", matchingId = "", email, languageId;

    AlertDialog timeAlertDialog;
    boolean isAvailable;


    ImageView imgVs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Utils.setStatusBarColor(SearchPlayerActivity.this, ContextCompat.getColor(getApplicationContext(), R.color.bg_color));
        setContentView(R.layout.activity_searchplayer);

        activity = SearchPlayerActivity.this;
        getAllWidgets();

        imgPlayer1.setDefaultImageResId(R.drawable.ic_profile);
        imgPlayer2.setDefaultImageResId(R.drawable.ic_profile);

        tvPlayer1.setText(getString(R.string.player_1));
        tvPlayer2.setText(getString(R.string.player_2));

        // this interface can be used to receive events about data changes at a location
        // This method will be called with a snapshot of the data at this location.
        getData();
    }

    public void getAllWidgets() {
        lytTimer = findViewById(R.id.lytTimer);
        content_layout = findViewById(R.id.content_layout);
        tvPlayer1 = findViewById(R.id.tv_player1_name);

        imgPlayer1 = findViewById(R.id.imgPlayer1);

        tvPlayer2 = findViewById(R.id.tv_player2_name);
        imgPlayer2 = findViewById(R.id.imgPlayer2);
        tvTimer = findViewById(R.id.tvTimer);

        btnSearchPlayer = findViewById(R.id.btnSearchPlayer);
        imgVs = findViewById(R.id.imgVs);

    }

    public void getData() {

        if (Utils.isNetworkAvailable(activity)) {
            player = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            database = FirebaseDatabase.getInstance().getReference();
            //Utils.RemoveGameRoomId(player);
            //call listener
            player1Name = Session.getUserData(Session.NAME, getApplicationContext());
            userId1 = Session.getUserData(Session.USER_ID, getApplicationContext());
            userProfile = Session.getUserData(Session.PROFILE, getApplicationContext());
            email = Session.getUserData(Session.EMAIL, getApplicationContext());
            languageId = Session.getCurrentLanguage(getApplicationContext());

            tvPlayer1.setText(player1Name);
            imgPlayer1.setImageUrl(userProfile, imageLoader);
            btnSearchPlayer.setVisibility(View.VISIBLE);
            lytTimer.setVisibility(View.GONE);
            btnSearchPlayer.setOnClickListener(view -> {
                if (Utils.isNetworkAvailable(activity)) {
                    btnSearchPlayer.setVisibility(View.GONE);
                    lytTimer.setVisibility(View.VISIBLE);
                    Animation animZoomIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in_out);

                    imgVs.startAnimation(animZoomIn);
                    SearchPlayerClickMethod();
                } else {
                    setSnackBar();
                }
            });
        } else {
            content_layout.setVisibility(View.GONE);
            setSnackBar();

        }

    }


    public void SearchPlayerClickMethod() {
        exist = true;
        lytTimer.setVisibility(View.VISIBLE);
        btnSearchPlayer.setVisibility(View.GONE);
        myRef = FirebaseDatabase.getInstance().getReference(Constant.DB_GAME_ROOM_NEW);

        final User user = new User(userId1, player1Name, userProfile, Constant.getValue,
                languageId, Constant.CATE_ID);
        myRef.child(player).setValue(user);
        startTimer();
        valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (ds.exists()) {
                                if (ds.child(Constant.IS_AVAIL).getValue() != null) {
                                    if (Objects.requireNonNull(ds.child(Constant.IS_AVAIL).getValue()).toString().equals("1") && Objects.requireNonNull(ds.child(Constant.LANG_ID).getValue()).toString().equals(languageId) && !Objects.requireNonNull(ds.getKey()).equals(player)) {
                                        if (Objects.requireNonNull(ds.child(Constant.cateId).getValue()).toString().equals(Constant.CATE_ID)) {
                                            opponentId = ds.getKey();
                                            player2Name = Objects.requireNonNull(ds.child(Constant.NAME).getValue()).toString();
                                            profilePlayer2 = Objects.requireNonNull(ds.child(Constant.IMAGE).getValue()).toString();
                                            userId2 = Objects.requireNonNull(ds.child(Constant.USER_ID).getValue()).toString();
                                            matchingId = player;
                                            isAvailable = true;
                                            tvPlayer2.setText(player2Name);
                                            imgPlayer2.setImageUrl(profilePlayer2, imageLoader);
                                            myRef.child(opponentId).child(Constant.MATCHING_ID).setValue(player);
                                            myRef.child(opponentId).child(Constant.IS_AVAIL).setValue("0");
                                            myRef.child(opponentId).child(Constant.OPPONENT_ID).setValue(player);

                                            myRef.child(player).child(Constant.MATCHING_ID).setValue(player);
                                            myRef.child(player).child(Constant.IS_AVAIL).setValue("0");
                                            myRef.child(player).child(Constant.OPPONENT_ID).setValue(opponentId);
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // This method will be triggered in the event that this listener either failed at the server
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        myRef.addListenerForSingleValueEvent(valueEventListener1);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Constant.IS_AVAIL).exists()) {
                    if (Objects.requireNonNull(dataSnapshot.child(Constant.IS_AVAIL).getValue()).toString().equals("0")) {
                        if (dataSnapshot.child(Constant.OPPONENT_ID).exists() && dataSnapshot.child(Constant.MATCHING_ID).exists()) {
                            opponentId = Objects.requireNonNull(dataSnapshot.child(Constant.OPPONENT_ID).getValue()).toString();
                            if (!opponentId.isEmpty()) {
                                myRef.child(opponentId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.getKey() != null) {
                                            opponentId = snapshot.getKey();
                                            player2Name = snapshot.child(Constant.NAME).getValue(String.class);
                                            profilePlayer2 = snapshot.child(Constant.IMAGE).getValue(String.class);
                                            userId2 = snapshot.child(Constant.USER_ID).getValue(String.class);
                                            matchingId = snapshot.child(Constant.MATCHING_ID).getValue(String.class);
                                            isAvailable = true;
                                            tvPlayer2.setText(player2Name);
                                            imgPlayer2.setImageUrl(profilePlayer2, imageLoader);
                                            callBattlePlayActivity(opponentId, matchingId, userId2, player2Name, profilePlayer2);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }

                    } else {
                        isAvailable = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        myRef.child(player).addValueEventListener(valueEventListener);
    }

    public void setSnackBar() {
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry), view -> getData());

        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }


    // use timer toh get opposite player in specific time
    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(Constant.OPPONENT_SEARCH_TIME, Constant.COUNT_DOWN_TIMER) {
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            public void onTick(long millisUntilFinished) {
                isRunning = true;
                int progress = (int) (millisUntilFinished / 1000);
                tvTimer.setText("" + String.format(FORMAT, progress));
            }

            @SuppressLint("SetTextI18n")
            public void onFinish() {
                isRunning = false;
                tvTimer.setText("00");
                showTimeUpAlert(getString(R.string.robot));
            }
        }.start();
    }

    @SuppressLint("SetTextI18n")
    private void showTimeUpAlert(final String playWith) {
        try {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            myRef.child(player).child(Constant.IS_AVAIL).setValue("0");
            final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View dialogView = inflater.inflate(R.layout.dialog_time_up, null);
            dialog.setView(dialogView);
            TextView tvExit = dialogView.findViewById(R.id.tvExit);
            LinearLayout tryLayout = dialogView.findViewById(R.id.tryLayout);
            TextView btnRobot = dialogView.findViewById(R.id.btnRobot);
            TextView btnTryAgain = dialogView.findViewById(R.id.btnTryAgain);

            tryLayout.setVisibility(View.VISIBLE);

            timeAlertDialog = dialog.create();
            tvExit.setOnClickListener(v -> {
                deleteGameRoom();
                finish();
                timeAlertDialog.dismiss();
            });

            btnRobot.setOnClickListener(view -> {
                if (playWith.equals(getString(R.string.player_2)) || playWith.equals(getString(R.string.robot))) {
                    callRobotPlayActivity();

                }
                timeAlertDialog.dismiss();
            });
            btnTryAgain.setOnClickListener(view -> {
                timeAlertDialog.dismiss();
                deleteGameRoom();
                ReloadUserForBattle();
            });
            timeAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            timeAlertDialog.setCancelable(false);
            timeAlertDialog.show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ReloadUserForBattle() {
        tvPlayer1.setText(getString(R.string.player_1));
        tvPlayer2.setText(getString(R.string.player_2));
        player = "";
        opponentId = "";
        imgPlayer1.setImageUrl("removed", imageLoader);
        imgPlayer2.setImageUrl("removed", imageLoader);
        imgPlayer1.setDefaultImageResId(R.drawable.ic_profile);
        imgPlayer2.setDefaultImageResId(R.drawable.ic_profile);
        imgPlayer1.setErrorImageResId(R.drawable.ic_profile);
        imgPlayer2.setErrorImageResId(R.drawable.ic_profile);
        matchingId = "";
        getData();
        SearchPlayerClickMethod();

    }

    private void callBattlePlayActivity(String opponentId, String matchingId, String userId2, String player2Name, String player2Profile) {


        exist = false;
        startActivity(new Intent(activity, BattlePlayActivity.class)
                .putExtra("gameId", matchingId)
                .putExtra("opponentId", opponentId)
                .putExtra("user_id1", userId1)
                .putExtra("user_id2", userId2)
                .putExtra("player2Name", player2Name)
                .putExtra("player2Profile", player2Profile)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        if (valueEventListener != null)
            myRef.child(player).removeEventListener(valueEventListener);

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        finish();


    }

    private void callRobotPlayActivity() {
        exist = false;
        startActivity(new Intent(activity, RobotPlayActivity.class)
                .putExtra("battlePlayer", tvPlayer2.getText().toString())
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        deleteGameRoom();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        finish();

    }


    @Override
    protected void onStop() {
        super.onStop();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

    }


    public void BackDialog() {
        // Setting Dialog Message

        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_leave_test, null);
        dialog.setView(dialogView);
        TextView btnLeave = dialogView.findViewById(R.id.btnLeave);
        TextView btnResume = dialogView.findViewById(R.id.btnResume);
        TextView txtFirstMsg = dialogView.findViewById(R.id.txtFirstmsg);
        TextView text = dialogView.findViewById(R.id.text);

        txtFirstMsg.setText(getString(R.string.back_message));
        text.setVisibility(View.GONE);
        final AlertDialog alertDialog = dialog.create();
        Utils.setDialogBg(alertDialog);
        alertDialog.show();

        alertDialog.setCancelable(false);
        btnLeave.setOnClickListener(view -> {
            if (Utils.isNetworkAvailable(activity)) {
                if (countDownTimer != null)
                    countDownTimer.cancel();
                if (myRef != null) {
                    deleteGameRoom();
                }
            }
            alertDialog.dismiss();
            finish();
        });


        btnResume.setOnClickListener(view -> alertDialog.dismiss());
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (countDownTimer != null)
            BackDialog();
        else
            super.onBackPressed();

    }

    @Override
    protected void onPause() {
        super.onPause();
        //AppController.StopSound();
        if (Utils.isNetworkAvailable(activity))
            if (pauseCheck.equals("regular")) {
                if (exist) {
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    myRef.child(player).removeEventListener(valueEventListener);
                    finish();
                }
            }

    }

    public void deleteGameRoom() {
        if (myRef != null && valueEventListener != null) {
            myRef.child(player).removeValue();
            myRef.child(player).removeEventListener(valueEventListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Utils.RemoveGameRoomId(player);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //    deleteGameRoom();
        if (myRef != null && valueEventListener != null)
            myRef.child(player).removeEventListener(valueEventListener);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.report).setVisible(false);
        //  menu.findItem(R.id.setting).setVisible(true);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.setting) {
            pauseCheck = "setting";
            Intent playQuiz = new Intent(getApplicationContext(), SettingActivity.class);
            startActivity(playQuiz);
            overridePendingTransition(R.anim.open_next, R.anim.close_next);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}