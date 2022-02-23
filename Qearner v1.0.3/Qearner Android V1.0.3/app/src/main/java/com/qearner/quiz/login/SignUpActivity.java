package com.qearner.quiz.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;

import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.qearner.quiz.R;
import com.qearner.quiz.helper.Session;
import com.qearner.quiz.helper.Utils;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    public static FirebaseAuth mAuth;
    TextInputEditText edtName, edtEmail, edtPassword, edtRefer;
    TextInputLayout inputName, inputEmail, inputPass;
    ProgressDialog mProgressDialog;
    MaterialButton btnSignUp;
    Activity activity;
    TextView tvPrivacy;
    CheckBox checkPrivacy;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getAllWidgets();
        activity = SignUpActivity.this;
        mAuth = FirebaseAuth.getInstance();
        Utils.privacyPolicyMsg(tvPrivacy, activity);
    }


    public void getAllWidgets() {

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtRefer = findViewById(R.id.edtRefer);
        inputName = findViewById(R.id.inputName);
        inputEmail = findViewById(R.id.inputEmail);
        inputPass = findViewById(R.id.inputPass);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvPrivacy = findViewById(R.id.tvPrivacy);
        checkPrivacy = findViewById(R.id.checkPrivacy);
    }

    public void SignUpWithEmail(View view) {
        if (checkPrivacy.isChecked()) {
            if (!validateForm()) {
                return;
            }
            showProgressDialog();
            final String email = String.valueOf(edtEmail.getText());
            final String password = String.valueOf(edtPassword.getText());
            final String name = String.valueOf(edtName.getText());
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(activity, task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                    assert user != null;
                    user.updateProfile(profileUpdates).addOnCompleteListener(task1 -> {
                    });
                    sendEmailVerification();
                } else {
                    hideProgressDialog();
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (FirebaseAuthInvalidCredentialsException | FirebaseAuthInvalidUserException | FirebaseAuthUserCollisionException invalidEmail) {
                        inputEmail.setError(invalidEmail.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                        inputEmail.setError(e.getMessage());
                    }
                }
            });
        } else {
            Toast.makeText(activity, "Please Accept our Priacy Policy Then after you can Login!!", Toast.LENGTH_LONG).show();

        }


    }

    public void Login(View view) {
        startActivity(new Intent(activity, LoginActivity.class));
        finish();
    }

    private boolean validateForm() {
        boolean valid = true;

        String name = String.valueOf(edtName.getText()).trim();
        String email = String.valueOf(edtEmail.getText()).trim();
        String password = String.valueOf(edtPassword.getText()).trim();

        if (TextUtils.isEmpty(name)) {
            inputName.setError(getString(R.string.enter_name));
            valid = false;
        } else {
            inputName.setError(null);
        }

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
            inputPass.setError(getString(R.string.empty_alert_msg));
            valid = false;
        } else if (password.length() < 6) {
            inputPass.setError(getString(R.string.password_valid));
            valid = false;
        } else {
            inputPass.setError(null);
        }

        return valid;
    }

    private void sendEmailVerification() {

        final FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(activity, task -> {
                    // [START_EXCLUDE]
                    // Re-enable button
                    if (task.isSuccessful()) {
                        String refer = String.valueOf(edtRefer.getText());
                        if (!refer.isEmpty())
                            Session.setFCode(refer, activity);
                        Toast.makeText(activity, getString(R.string.verify_email_sent) + user.getEmail(), Toast.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().signOut();
                        Intent i = new Intent(activity, LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);

                    } else {

                        Toast.makeText(activity, getString(R.string.verify_email_sent_f), Toast.LENGTH_LONG).show();
                        final FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                        AuthCredential authCredential = EmailAuthProvider.getCredential(String.valueOf(edtEmail.getText()), String.valueOf(edtPassword.getText()));
                        assert user1 != null;
                        user1.reauthenticate(authCredential).addOnCompleteListener(task1 -> user1.delete());

                    }
                });
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

}
