package com.qearner.quiz.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;

import android.provider.Settings;
import android.text.TextUtils;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.google.firebase.auth.AdditionalUserInfo;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


import com.google.firebase.database.FirebaseDatabase;

import com.qearner.quiz.Constant;
import com.qearner.quiz.R;

import com.qearner.quiz.activity.MainActivity;

import com.qearner.quiz.helper.Session;
import com.qearner.quiz.helper.Utils;
import com.qearner.quiz.model.User;
import com.qearner.quiz.vollyConfigs.ApiConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static com.qearner.quiz.helper.Utils.OpenBottomDialog;
import static com.qearner.quiz.helper.Utils.compareVersion;
import static com.qearner.quiz.helper.Utils.privacyPolicyMsg;


public class LoginActivity extends AppCompatActivity {
    String TAG = "LoginActivity";
    int RC_SIGN_IN = 9001;
    CallbackManager mCallbackManager;
    String token;
    GoogleSignInClient mGoogleSignInClient;
    TextView tvPrivacy;
    ProgressDialog mProgressDialog;
    TextInputEditText edtEmail, edtPassword;
    TextInputLayout inputEmail, inputPass;
    String id;
    MaterialButton btnLogin;
    Activity activity;
    BottomSheetDialog bottomSheetDialog;
    public static FirebaseAuth mAuth;
    CheckBox checkPrivacy;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setStatusBarColor(LoginActivity.this, ContextCompat.getColor(getApplicationContext(), R.color.card_color_light));
        setContentView(R.layout.activity_login);
        activity = LoginActivity.this;
        getAllWidgets();
        mAuth = FirebaseAuth.getInstance();

        if (!Utils.isNetworkAvailable(activity)) {
            setSnackBar(getString(R.string.msg_no_internet), getString(R.string.retry));
        }
        if (Session.isLogin(activity)) {
            Intent intent = new Intent(activity, MainActivity.class);
            intent.putExtra("type", "default");
            startActivity(intent);
            finish();
        } else {
            GetUpdate(activity);


            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
            mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
            mGoogleSignInClient.signOut();
            mCallbackManager = CallbackManager.Factory.create();

            token = Session.getDeviceToken(activity);
            Random rand = new Random();
            id = String.format("%04d", rand.nextInt(10000));
            privacyPolicyMsg(tvPrivacy, activity);
        }
    }

    public void getAllWidgets() {
        tvPrivacy = findViewById(R.id.tvPrivacy);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        inputEmail = findViewById(R.id.inputEmail);
        inputPass = findViewById(R.id.inputPass);
        btnLogin = findViewById(R.id.btnLogin);
        checkPrivacy = findViewById(R.id.checkPrivacy);
    }

    public void continueWithGoogle(View view) {
        if (checkPrivacy.isChecked()) {
            if (Utils.isNetworkAvailable(activity))
                signIn();
            else
                setSnackBar(getString(R.string.msg_no_internet), getString(R.string.retry));
        } else {
            Toast.makeText(activity, getString(R.string.privacy_policy_alert_msg), Toast.LENGTH_LONG).show();
        }
    }

    public void continueWithFacebook(View view) {
        if (checkPrivacy.isChecked()) {

            if (Utils.isNetworkAvailable(activity)) {
                LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "email"));
                LoginManager.getInstance().registerCallback(mCallbackManager,
                        new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                activity.setResult(RESULT_OK);
                                handleFacebookAccessToken(loginResult.getAccessToken());
                            }

                            @Override
                            public void onCancel() {
                                hideProgressDialog();
                            }

                            @Override
                            public void onError(FacebookException error) {
                                Log.d(TAG, "facebook:onError", error);
                                error.printStackTrace();
                            }
                        });
            } else
                setSnackBar(getString(R.string.msg_no_internet), getString(R.string.retry));

        } else {
            Toast.makeText(activity, getString(R.string.privacy_policy_alert_msg), Toast.LENGTH_LONG).show();

        }
    }

    public void loginWithEmail(View view) {

        if (!validateForm()) {
            return;
        }
        showProgressDialog();
        mAuth.signInWithEmailAndPassword(String.valueOf(edtEmail.getText()), String.valueOf(edtPassword.getText()))
                .addOnCompleteListener(activity, task -> {

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String personName = user.getDisplayName() + "";
                            if (user.isEmailVerified()) {
                                if (user.getEmail() != null) {
                                    String[] userName = user.getEmail().split("@");
                                    UserSignUpWithSocialMedia(user.getUid(), Session.getFCode(activity), userName[0] + id, personName, user.getEmail(), "", "email");

                                }
                            } else {
                                mAuth.signOut();
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                                alertDialog.setTitle(getString(R.string.act_verify_1));
                                alertDialog.setIcon(R.drawable.ic_privacy);
                                alertDialog.setMessage(getString(R.string.act_verify_2));
                                alertDialog.setPositiveButton(getString(R.string.ok), (dialog, which) -> dialog.cancel());
                                alertDialog.show();
                            }
                        }
                    } else {
                        try {
                            if (task.getException() != null)
                                throw task.getException();
                        } catch (FirebaseAuthInvalidUserException invalidEmail) {
                            inputEmail.setError(getString(R.string.signup_alert));
                        } catch (FirebaseAuthInvalidCredentialsException wrongPassword) {
                            inputPass.setError(getString(R.string.invalid_pass));
                        } catch (Exception e) {
                            Log.d(TAG, "onComplete last: " + e.getMessage());
                        }
                    }
                    hideProgressDialog();
                });


    }

    public static void GetUpdate(final Activity activity) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_SYSTEM_CONFIG, "1");
        ApiConfig.RequestLoginWithoutJWT((result, response) -> {
            if (result) {
                try {
                    JSONObject obj = new JSONObject(response);
                    boolean error = obj.getBoolean(Constant.ERROR);
                    if (!error) {
                        JSONObject jsonObj = obj.getJSONObject(Constant.DATA);
                        Constant.APP_LINK = jsonObj.getString(Constant.KEY_APP_LINK);
                        Constant.MORE_APP_URL = jsonObj.getString(Constant.KEY_MORE_APP);
                        Constant.VERSION_CODE = jsonObj.getString(Constant.KEY_APP_VERSION);
                        Constant.REQUIRED_VERSION = jsonObj.getString(Constant.KEY_APP_VERSION);
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

    public void ShowReferDialog(final String authId, final String referCode, final String name, final String email, final String profile, final String type) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.refer_dailog, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        final AlertDialog alertDialog = dialog.create();

        TextView tvCancel = dialogView.findViewById(R.id.tvCancel);
        TextView tvApply = dialogView.findViewById(R.id.tvApply);
        final EditText edtRefCode = dialogView.findViewById(R.id.edtRefCode);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        tvCancel.setOnClickListener(v -> {
            UserSignUpWithSocialMedia(authId, edtRefCode.getText().toString(), referCode + id, name, email, profile, type);
            alertDialog.dismiss();
        });
        tvApply.setOnClickListener(view -> {
            showProgressDialog();
            UserSignUpWithSocialMedia(authId, edtRefCode.getText().toString(), referCode + id, name, email, profile, type);
            alertDialog.dismiss();
        });
        alertDialog.show();
    }

    public void UserSignUpWithSocialMedia(final String authId, final String fCode, final String referCode, final String name, final String email, final String profile, final String type) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.userSignUp, "1");
        params.put(Constant.email, email);
        params.put(Constant.AUTH_ID, authId);
        params.put(Constant.name, name);
        params.put(Constant.PROFILE, profile);
        params.put(Constant.fcmId, token);
        params.put(Constant.type, type);
        params.put(Constant.mobile, "");
        params.put(Constant.REFER_CODE, referCode);
        params.put(Constant.FRIENDS_CODE, fCode);
        params.put(Constant.KEY_DEVICE_ID, Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));
        ApiConfig.RequestLoginWithoutJWT((result, response) -> {

            if (result) {
                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getString(Constant.ERROR).equals(Constant.FALSE)) {
                        JSONObject jsonObj = obj.getJSONObject(Constant.DATA);
                        if (!jsonObj.getString(Constant.status).equals(Constant.DE_ACTIVE)) {
                            Session.saveUserDetail(activity,
                                    jsonObj.getString(Constant.userId),
                                    jsonObj.getString(Constant.name),
                                    jsonObj.getString(Constant.email),
                                    jsonObj.getString(Constant.mobile),
                                    jsonObj.getString(Constant.PROFILE), jsonObj.getString(Constant.REFER_CODE), type, jsonObj.getString(Constant.Token), authId);
                            Intent i = new Intent(activity, MainActivity.class);
                            i.putExtra("type", "default");
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            activity.finish();
                            hideProgressDialog();
                        } else
                            setSnackBarStatus();
                    } else {
                        LoginManager.getInstance().logOut();
                        Toast.makeText(activity, obj.getString(Constant.MESSAGE), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {

                    e.printStackTrace();

                }

            }
        }, params);


    }

    private void handleFacebookAccessToken(AccessToken token) {
        showProgressDialog();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    try {
                        if (task.isSuccessful()) {
                            //Sign in success, update UI with the signed-in user's information
                            callAfterTaskSuccessful(task, "fb");
                        } else {
                            // If sign in fails, display a message to the user.
                            mAuth.signOut();
                            LoginManager.getInstance().logOut();
                            hideProgressDialog();
                            try {
                                if (task.getException() != null)
                                    throw task.getException();
                            } catch (FirebaseAuthInvalidCredentialsException | FirebaseAuthInvalidUserException | FirebaseAuthUserCollisionException invalidEmail) {
                                setSnackBar(invalidEmail.getMessage(), getString(R.string.ok));
                            } catch (Exception e) {
                                e.printStackTrace();
                                setSnackBar(e.getMessage(), getString(R.string.ok));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        callAfterTaskSuccessful(task, "gmail");
                    } else {
                        LoginActivity.this.hideProgressDialog();
                        try {
                            if (task.getException() != null)
                                throw task.getException();
                        } catch (FirebaseAuthInvalidCredentialsException | FirebaseAuthInvalidUserException | FirebaseAuthUserCollisionException invalidEmail) {
                            LoginActivity.this.setSnackBar(invalidEmail.getMessage(), LoginActivity.this.getString(R.string.ok));
                        } catch (Exception e) {
                            e.printStackTrace();
                            LoginActivity.this.setSnackBar(e.getMessage(), LoginActivity.this.getString(R.string.ok));
                        }
                    }
                });
    }

    public void callAfterTaskSuccessful(Task<AuthResult> task, String loginProvider) {
        //Sign in success, update UI with the signed-in user's information
        AdditionalUserInfo aUI = task.getResult().getAdditionalUserInfo();
        if (aUI != null) {
            boolean isNew = aUI.isNewUser();
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                String personName = user.getDisplayName();
                String userEmail = user.getEmail();
                String userProfile = String.valueOf(user.getPhotoUrl());
                String userAuthId = user.getUid();
                if (personName != null)
                    if (personName.contains(" ")) {
                        personName = personName.substring(0, personName.indexOf(" "));
                    }
                String referCode;
                if (userEmail != null) {
                    String[] refer = userEmail.split("@");
                    referCode = refer[0];
                } else {
                    referCode = user.getPhoneNumber();
                }
                if (isNew) {
                    hideProgressDialog();
                    ShowReferDialog(userAuthId, referCode + id, personName, userEmail, userProfile, loginProvider);
                } else
                    UserSignUpWithSocialMedia(userAuthId, "", referCode + id, personName, userEmail, userProfile, loginProvider);

            }
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        showProgressDialog();
    }


    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    private boolean validateForm() {
        boolean valid = true;
        String email = String.valueOf(edtEmail.getText()).trim();
        String password = String.valueOf(edtPassword.getText()).trim();
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError(getString(R.string.email_alert_1));
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            valid = false;
            inputEmail.setError(getString(R.string.email_alert_2));
        } else {
            inputEmail.setError(null);
        }
        if (TextUtils.isEmpty(password)) {
            inputPass.setError(getString(R.string.pass_alert));
            valid = false;
        } else {
            inputPass.setError(null);
        }

        return valid;
    }

    public void setSnackBar(String message, String action, int color) {
        bottomSheetDialog.dismiss();
        final Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(action, view -> snackbar.dismiss());
        snackbar.setActionTextColor(color);
        View view = snackbar.getView();
        TextView textView = view.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.show();
    }

    public void BottomSheetDialog(final FirebaseAuth firebaseAuth) {

        bottomSheetDialog = new BottomSheetDialog(activity, R.style.BottomSheetTheme);
        View sheetView = LayoutInflater.from(activity).inflate(R.layout.forget_passwordbottom, findViewById(R.id.bottom_sheet));
        sheetView.findViewById(R.id.imgClose).setOnClickListener(view -> bottomSheetDialog.dismiss());
        final EditText editText = sheetView.findViewById(R.id.edtEmail);

        sheetView.findViewById(R.id.btnSubmit).setOnClickListener(view -> {
            showProgressDialog();
            String email = editText.getText().toString().trim();
            if (email.isEmpty()) {
                hideProgressDialog();
                editText.setError(activity.getResources().getString(R.string.email_alert_1));
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                hideProgressDialog();
                editText.setError(activity.getResources().getString(R.string.email_alert_2));
            } else {
                firebaseAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                hideProgressDialog();
                                Toast.makeText(activity, "Email sent", Toast.LENGTH_SHORT).show();
                                bottomSheetDialog.dismiss();
                            }
                        });
            }
        });
        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }


    public void setSnackBar(String message, String action) {
        final Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(action, view -> {
            if (Utils.isNetworkAvailable(activity)) {
                snackbar.dismiss();
            } else {
                snackbar.show();
            }
        });
        View view = snackbar.getView();
        TextView textView = view.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }

    public void setSnackBarStatus() {
        final Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), getString(R.string.account_deactivate), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(getString(R.string.ok), view -> {

            Session.clearUserSession(activity);
            mAuth.signOut();
            LoginManager.getInstance().logOut();

        });

        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
        hideProgressDialog();
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            hideProgressDialog();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with FireBase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                e.printStackTrace();
            }
        } else {
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void SignUpWithEmail(View view) {
        startActivity(new Intent(activity, SignUpActivity.class));
    }

    public void forgotPassword(View view) {
        BottomSheetDialog(mAuth);
    }

}