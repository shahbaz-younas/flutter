package com.qearner.quiz.activity;


import static com.qearner.quiz.ads.AdUtils.interstitialAd;

import android.Manifest;
import android.annotation.SuppressLint;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;


import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.qearner.quiz.ads.AdUtils;
import com.qearner.quiz.leaderboard.LeaderboardActivity;
import com.qearner.quiz.login.LoginActivity;
import com.qearner.quiz.model.Language;
import com.qearner.quiz.vollyConfigs.ApiConfig;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;


import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;

import com.qearner.quiz.Constant;
import com.qearner.quiz.R;
import com.qearner.quiz.helper.AppController;
import com.qearner.quiz.UI.CircleImageView;
import com.qearner.quiz.helper.Session;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;


import com.qearner.quiz.helper.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView imgProfile;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    ProgressBar progressBar;
    String name, mobile, email, filePath = null, currentPhotoPath;
    BottomSheetDialog bottomSheetDialog;
    FloatingActionButton fabProfile;

    TextView tvName, tvPhone, tvEmail, txtCoinstore;

    int REQUEST_IMAGE_CAPTURE = 100, REQUEST_CROP_IMAGE = 120, SELECT_FILE = 110, reqWritePermission = 2;
    File output = null;
    Uri imageUri;
    AppCompatActivity activity;
    public Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getAllWidgets();
        activity = ProfileActivity.this;
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.profile));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //imgProfile.setDefaultImageResId(R.drawable.ic_logo);
        imgProfile.setImageUrl(Session.getUserData(Session.PROFILE, activity), imageLoader);
        fabProfile.setOnClickListener(view -> SelectProfileImage());
        name = Session.getUserData(Session.NAME, activity);
        mobile = Session.getUserData(Session.MOBILE, activity);
        email = Session.getUserData(Session.EMAIL, activity);
        tvName.setText(name);
        tvPhone.setText(mobile);
        tvEmail.setText(email);
        AdUtils.loadFacebookInterstitialAd(activity);

    }

    public void getAllWidgets() {
        tvName = findViewById(R.id.tvName);
        tvPhone = findViewById(R.id.tvPhone);
        tvEmail = findViewById(R.id.tvEmail);
        //lytEditProfile = findViewById(R.id.lytEditProfile);
        toolbar = findViewById(R.id.toolBar);
        fabProfile = findViewById(R.id.fabProfile);
        progressBar = findViewById(R.id.progressBar);
        imgProfile = findViewById(R.id.imgProfile);
        txtCoinstore = findViewById(R.id.txtCoinstore);
        if (Constant.IN_APPPURCHASE.equals("1")) {
            txtCoinstore.setVisibility(View.VISIBLE);
        } else {
            txtCoinstore.setVisibility(View.GONE);
        }

    }

    public void ProfileEdit() {
        bottomSheetDialog = new BottomSheetDialog(activity, R.style.BottomSheetTheme);
        @SuppressLint("InflateParams") View sheetView = getLayoutInflater().inflate(R.layout.bottomsheet_profileupdate, null);
        sheetView.findViewById(R.id.imgClose).setOnClickListener(view -> bottomSheetDialog.dismiss());
        final TextInputEditText edtName, editTextPhone;


        edtName = sheetView.findViewById(R.id.edtName);
        editTextPhone = sheetView.findViewById(R.id.edtMobile);

        edtName.setText(name);
        editTextPhone.setText(mobile);

        sheetView.findViewById(R.id.btnSubmit).setOnClickListener(view -> {
            final String name = String.valueOf(edtName.getText()).trim();
            final String number = String.valueOf(editTextPhone.getText()).trim();

            if (name.isEmpty())
                edtName.setError(getString(R.string.empty_alert_msg));
            else
                UpdateProfile(name, number);

        });
        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();

    }

    public void dialogs() {
        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.permission_dialog, null);
        dialog.setView(dialogView);
        TextView ok = dialogView.findViewById(R.id.ok);
        final android.app.AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();
        alertDialog.setCancelable(false);
        ok.setOnClickListener(view -> alertDialog.dismiss());
    }


    @SuppressLint("SetTextI18n")
    public void UpdateProfileEmail(final String name, final String email, final String mobile) {
        if (Utils.isNetworkAvailable(this)) {
            Map<String, String> params = new HashMap<>();
            params.put(Constant.updateProfile, "1");
            params.put(Constant.userId, Session.getUserData(Session.USER_ID, activity));
            params.put(Constant.email, email);
            params.put(Constant.mobile, mobile);
            params.put(Constant.name, name);
            ApiConfig.RequestToVolley((result, response) -> {
                if (result) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        boolean error = obj.getBoolean(Constant.ERROR);
                        String message = obj.getString(Constant.MESSAGE);
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        if (!error) {
                            Session.setUserData(Session.NAME, name, activity);
                            Session.setUserData(Session.EMAIL, email, activity);
                            tvName.setText(name);
                            tvEmail.setText(email);
                            MainActivity.tvName.setText(getString(R.string.hello) + name);
                            bottomSheetDialog.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, params, activity);

        } else {
            setUpdate();
        }
    }

    public void uploadFileWithVolley(String filePath) {
        progressBar.setVisibility(View.VISIBLE);
        if (Utils.isNetworkAvailable(this)) {
            //default params
            Map<String, String> params = new HashMap<>();
            params.put(Constant.upload_profile_image, "1");
            params.put(Constant.userId, Session.getUserData(Session.USER_ID, activity));
            //file params
            Map<String, String> fileParams = new HashMap<>();
            fileParams.put(Constant.image, filePath);
            ApiConfig.MultipartRequestToVolley((result, response) -> {
                if (result) {
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(response);
                        boolean error = jsonObject.getBoolean(Constant.ERROR);
                        if (!error) {
                            String imagePath = jsonObject.getString(Constant.FILE_PATH);
                            Session.setUserData(Session.PROFILE, imagePath, activity);
                            imgProfile.setImageUrl(imagePath, imageLoader);
                            MainActivity.imgProfile.setImageUrl(imagePath, imageLoader);
                        }
                        Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, params, fileParams, activity);
        }
    }

    @SuppressLint("SetTextI18n")
    public void UpdateProfile(final String name, final String mobile) {
        if (Utils.isNetworkAvailable(this)) {
            Map<String, String> params = new HashMap<>();
            params.put(Constant.updateProfile, "1");
            params.put(Constant.userId, Session.getUserData(Session.USER_ID, activity));
            params.put(Constant.name, name);
            params.put(Constant.email, Session.getUserData(Session.EMAIL, activity));
            params.put(Constant.mobile, mobile);
            ApiConfig.RequestToVolley((result, response) -> {

                if (result) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        boolean error = obj.getBoolean(Constant.ERROR);
                        String message = obj.getString(Constant.MESSAGE);
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        if (!error) {
                            Session.setUserData(Session.MOBILE, mobile, activity);
                            Session.setUserData(Session.NAME, name, activity);
                            tvName.setText(name);
                            tvPhone.setText(mobile);
                            MainActivity.tvName.setText(getString(R.string.hello) + name);
                            bottomSheetDialog.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, params, activity);

        } else {
            setUpdate();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == reqWritePermission) {
            // for each permission check if the user granted/denied them
            // you may want to group the rationale in a single dialog,
            // this is just an example
            for (int i = 0, len = permissions.length; i < len; i++) {
                String permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    //user rejected the permission
                    boolean showRationale = shouldShowRequestPermissionRationale(permission);
                    if (!showRationale) {
                        dialogs();
                        // user also CHECKED "never ask again"
                        // you can either enable some fall back,
                        // disable features of your app
                        // or open another dialog explaining
                        // again the permission and directing to
                        // the app setting
                    }
                }
            }
        }
    }

    public void SelectProfileImage() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, reqWritePermission);
        } else if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, reqWritePermission);
        } else {
            selectDialog();
        }
    }

    public void selectDialog() {
        final CharSequence[] items = {getString(R.string.from_library), getString(R.string.from_camera), getString(R.string.cancel)};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
        builder.setTitle("Add Photo!");
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals(getString(R.string.from_library))) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_FILE);
            } else if (items[item].equals(getString(R.string.from_camera))) {
                dispatchTakePictureIntent();
            } else if (items[item].equals(getString(R.string.cancel))) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private File createImageFile() throws IOException {
        // Create an image file name

        String imageFileName = getString(R.string.app_name) + System.currentTimeMillis() + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                imageUri = data.getData();
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setOutputCompressQuality(90)
                        .setRequestedSize(300, 300)
                        .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setAspectRatio(1, 1)
                        .start(activity);

            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setOutputCompressQuality(90)
                        .setRequestedSize(300, 300)
                        .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setAspectRatio(1, 1)
                        .start(activity);
            } else if (requestCode == REQUEST_CROP_IMAGE) {


                CropImage.activity(FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", output)).start(activity);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                assert result != null;
                filePath = result.getUriFilePath(getApplicationContext(), true);
                uploadFileWithVolley(filePath);
                // new UploadFileToServer().execute();
            }
        }
    }


    public void setSnackBar() {
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry), view -> SelectProfileImage());
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }


    public void setUpdate() {
        bottomSheetDialog.dismiss();
        Snackbar snackbar = Snackbar
                .make(findViewById(android.R.id.content), getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry), view -> ProfileEdit());
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }


    public void Logout(View view) {
        final AlertDialog.Builder dialog1 = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dailog_logout, null);
        dialog1.setView(dialogView);
        dialog1.setCancelable(true);
        final AlertDialog alertDialog = dialog1.create();
        TextView tvYes = dialogView.findViewById(R.id.tvYes);
        TextView tvNo = dialogView.findViewById(R.id.tvNo);

        tvNo.setOnClickListener(v -> alertDialog.dismiss());
        tvYes.setOnClickListener(view1 -> {
            alertDialog.dismiss();
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
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public void UserStatistics(View view) {
        Intent intent = new Intent(getApplicationContext(), UserStatistics.class);
        startActivity(intent);
    }

    public void CoinStore(View view) {
        Intent intent = new Intent(getApplicationContext(), CoinStoreActivity.class);
        startActivity(intent);
    }

    public void LeaderBoard(View view) {
        AdUtils.showFacebookInterstitialAd(ProfileActivity.this);
        Intent intent = new Intent(getApplicationContext(), LeaderboardActivity.class);
        startActivity(intent);
    }

    public void InviteFriend(View view) {
        Intent intent = new Intent(getApplicationContext(), InviteFriendActivity.class);
        startActivity(intent);
    }

    public void Notification(View view) {
        Intent intent = new Intent(getApplicationContext(), NotificationList.class);
        startActivity(intent);
    }

    public void Instrucation(View view) {
        AdUtils.showFacebookInterstitialAd(ProfileActivity.this);
        Intent intent = new Intent(getApplicationContext(), PrivacyPolicy.class);
        intent.putExtra("type", "instruction");
        startActivity(intent);

    }

    public void AboutUs(View view) {
        AdUtils.showFacebookInterstitialAd(ProfileActivity.this);
        Intent intent = new Intent(getApplicationContext(), PrivacyPolicy.class);
        intent.putExtra("type", "about");
        startActivity(intent);

    }

    public void TermsOfService(View view) {
        AdUtils.showFacebookInterstitialAd(ProfileActivity.this);
        Intent intent = new Intent(getApplicationContext(), PrivacyPolicy.class);
        intent.putExtra("type", "terms");
        startActivity(intent);

    }

    public void PrivacyPolicy(View view) {
        AdUtils.showFacebookInterstitialAd(ProfileActivity.this);
        Intent intent = new Intent(getApplicationContext(), PrivacyPolicy.class);
        intent.putExtra("type", "privacy");
        startActivity(intent);

    }

    public void ShareApp(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, Constant.SHARE_APP_TEXT + " " + Constant.APP_LINK);
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        startActivity(Intent.createChooser(intent, getString(R.string.share_via)));

    }

    public void RateApp(View view) {
        AdUtils.showFacebookInterstitialAd(ProfileActivity.this);
        rateClicked();
    }


    private void rateClicked() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.APP_LINK)));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //supportFinishAfterTransition();
    }

    public void EditUserProfile(View view) {
        ProfileEdit();
    }

    public void DeleteUserProfile(View view) {
        final AlertDialog.Builder dialog1 = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dailog_deleteaccount, null);
        dialog1.setView(dialogView);
        dialog1.setCancelable(true);
        final AlertDialog alertDialog = dialog1.create();
        TextView tvYes = dialogView.findViewById(R.id.tvYes);
        TextView tvNo = dialogView.findViewById(R.id.tvNo);

        tvNo.setOnClickListener(v -> alertDialog.dismiss());
        tvYes.setOnClickListener(view1 -> {
            alertDialog.dismiss();
            deleteuser();
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void deleteuser() {

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.

        user.delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DeleteAccount();
                        Toast.makeText(ProfileActivity.this, "Deleted User Successfully,", Toast.LENGTH_LONG).show();
                    } else {
                        ReLoginAgain();
                    }
                });
    }


    public void DeleteAccount() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.ACCOUNT_REMOVE, "1");
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, activity));
        System.out.println("PARAMS::=" + params);
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    System.out.println("Respons::=" + response);
                    Session.clearUserSession(activity);
                    LoginManager.getInstance().logOut();
                    LoginActivity.mAuth.signOut();

                    FirebaseAuth.getInstance().signOut();
                    Intent intentLogin = new Intent(activity, LoginActivity.class);
                    intentLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intentLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intentLogin);
                    activity.finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params, activity);


    }

    @SuppressLint("SetTextI18n")
    public void ReLoginAgain() {
        final AlertDialog.Builder dialog1 = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dailog_deleteaccount, null);
        dialog1.setView(dialogView);
        dialog1.setCancelable(true);
        final AlertDialog alertDialog = dialog1.create();
        TextView title = dialogView.findViewById(R.id.tvTitle);
        TextView message = dialogView.findViewById(R.id.tv_message);
        title.setText(getString(R.string.re_login));
        message.setText(getString(R.string.relogin_message));
        TextView tvYes = dialogView.findViewById(R.id.tvYes);
        TextView tvNo = dialogView.findViewById(R.id.tvNo);
        tvYes.setText(getString(R.string.logout));
        tvNo.setText(getString(R.string.cancel));
        tvNo.setOnClickListener(v -> alertDialog.dismiss());
        tvYes.setOnClickListener(view1 -> {
            alertDialog.dismiss();

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
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.show();
    }


    // Prompt the user to re-provide their sign-in credentials

    @Override
    protected void onDestroy() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }
}