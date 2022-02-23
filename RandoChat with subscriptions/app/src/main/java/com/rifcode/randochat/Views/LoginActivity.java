package com.rifcode.randochat.Views;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rifcode.randochat.R;
import com.rifcode.randochat.Utils.DialogUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private RadioButton rbtnMen;
    private FirebaseAuth mAuth;
    private EditText edtUsername,edtAge;
    private Button btnStartChat;
    private String TAG;
    private DatabaseReference datarefUsers;
    private String sex = "guy";
    private RadioButton rdFemale;
    private boolean flag = true;
    private EditText edtPhone;
    private CheckBox cbPolicy;
    private TextView tvlaws;
    private Button btn_fb_login;
    private CallbackManager mCallbackManager;
    private FirebaseUser user;
    private DatabaseReference dbUsers;
//    private AdView adView;
private ProgressDialog proDial;
    private View mViewInflateChangeLanguage;
    private LinearLayout lyenlish,lyarabic,lyFrench,lyGerman,lyTurkish,lyPortuguese,lySpanish,lyhindi,lyturki;
    String codelang="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edtAge = findViewById(R.id.edtAge);
        SharedPreferences prefs = getSharedPreferences("changeLanguage", MODE_PRIVATE);
        String language_key = prefs.getString("language_key", null);
        if(language_key==null){
            dialogChangeLanguage();
        }

        cbPolicy = findViewById(R.id.cbPolicy);
        tvlaws = findViewById(R.id.tvlaws);
        btn_fb_login = findViewById(R.id.btn_fb_login);

        // progress Dialog :
        proDial = new ProgressDialog(LoginActivity.this);
        proDial.setMessage(getString(R.string.wsin));
        proDial.setCanceledOnTouchOutside(false);
        dbUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth =  FirebaseAuth.getInstance();

        // facebook login
        FacebookSdk.sdkInitialize(getApplicationContext());

        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Success", "Login");
                        handleFacebookAccessToken(loginResult.getAccessToken(),loginResult);
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this, getString(R.string.lcancl), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.w("FacebookException:",exception.getMessage().toString());
                        Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        btn_fb_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this
                        , Arrays.asList("public_profile","email"));
            }
        });


        tvlaws.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent PrivacyPolicyActivity =  new Intent(LoginActivity.this,PolicyActivity.class);
                startActivity(PrivacyPolicyActivity);
            }
        });

        wedgets();

        btnStartChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proDial.show();
                final String username = edtUsername.getText().toString();
                if(edtAge.getText().toString().equals("")){
                    Toast.makeText(LoginActivity.this, R.string.Authentication_failed,
                            Toast.LENGTH_SHORT).show();
                    proDial.dismiss();

                    return;
                }
                if(TextUtils.isEmpty(edtAge.getText()) || TextUtils.isEmpty(edtUsername.getText())
                ){
                    Toast.makeText(LoginActivity.this, getString(R.string.eror_data),
                            Toast.LENGTH_SHORT).show();
                    proDial.dismiss();

                    return;
                }

                if(!cbPolicy.isChecked()) {
                    Toast.makeText(LoginActivity.this, getString(R.string.need_acceplt), Toast.LENGTH_SHORT).show();
                    proDial.dismiss();

                    return;
                }

                if(Integer.parseInt(edtAge.getText().toString())<18){
                    Toast.makeText(LoginActivity.this,getString(R.string.underage),
                            Toast.LENGTH_SHORT).show();
                    proDial.dismiss();

                    return;
                }

                if(checkNumbers(edtUsername.getText().toString())){

                    Toast.makeText(LoginActivity.this,getString(R.string.check_username_digi),
                            Toast.LENGTH_SHORT).show();
                    proDial.dismiss();

                    return;
                }

                if(checkSpecialCharacters(edtUsername.getText().toString())){

                    Toast.makeText(LoginActivity.this,getString(R.string.check_dymbol_username),
                            Toast.LENGTH_SHORT).show();
                    proDial.dismiss();

                    return;
                }


                if(Integer.parseInt(edtAge.getText().toString())>70){
                    Toast.makeText(LoginActivity.this,getString(R.string.plus70),
                            Toast.LENGTH_SHORT).show();
                    proDial.dismiss();

                    return;
                }



                if(edtUsername.getText().length()<6){
                    Toast.makeText(LoginActivity.this,getString(R.string.usernale_error),
                            Toast.LENGTH_SHORT).show();
                    proDial.dismiss();

                    return;
                }



                if (username.equals("")){
                    Toast.makeText(LoginActivity.this, R.string.writeusername, Toast.LENGTH_SHORT).show();
                    Toast.makeText(LoginActivity.this, R.string.Authentication_failed,
                            Toast.LENGTH_SHORT).show();
                    btnStartChat.setVisibility(View.VISIBLE);
                    proDial.dismiss();
                    return;
                }

//                if (ConnectionQuality().equals("POOR") || ConnectionQuality().equals("UNKNOWN")){
//                    Toast.makeText(LoginActivity.this, getString(R.string.badconnection), Toast.LENGTH_SHORT).show();
//                    btnStartChat.setVisibility(View.VISIBLE);
//                    return;
//                }
                login();


            }
        });

    }



    private void login(){
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
                                proDial.dismiss();
                                btnStartChat.setVisibility(View.VISIBLE);

                                setInfo();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.Authentication_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void handleFacebookAccessToken(AccessToken token, final LoginResult result) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            user = FirebaseAuth.getInstance().getCurrentUser();
                            try {
                                proDial.show();
                            }catch(Exception e){

                            }

                            dbUsers.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        Intent nextintent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(nextintent);
                                        finish();
                                        proDial.dismiss();
                                    }else{
                                        dbUsers.child(mAuth.getUid()).child("type_account").setValue("facebook")
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        Toast.makeText(LoginActivity.this, R.string.regsecc, Toast.LENGTH_SHORT).show();

                                                        ////---------------- Token device for notification ------------------//
                                                        FirebaseMessaging.getInstance().getToken().addOnCompleteListener (new OnCompleteListener<String>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<String> task) {
                                                                String deviceToken = task.getResult();
                                                                final String currentUser = mAuth.getCurrentUser().getUid();
                                                                dbUsers.child(currentUser).child("device_token").setValue(deviceToken)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {

                                                                                Intent nextintent = new Intent(LoginActivity.this, LoginContinueActivity.class);
                                                                                startActivity(nextintent);
                                                                                finish();
                                                                                proDial.dismiss();
                                                                            }
                                                                        });
                                                            }
                                                        });



                                                        ////--------------------- end token ------------------------//
                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });



                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, getString(R.string.dbauthf),
                                    Toast.LENGTH_SHORT).show();
                            proDial.dismiss();

                        }
                        //...
                    }
                });
    }






    private boolean checkSpecialCharacters(String username){
        Pattern regex = Pattern.compile("[ $&+,:;=\\\\?@#|/'<>.^*()%!-]");

        if (regex.matcher(username).find()) {
            Log.d("checkSpecialCharacters", "SPECIAL CHARS FOUND");
            return true;
        }else{
            return false;
        }
    }

    private boolean checkNumbers(String username){
        Pattern regex = Pattern.compile("[0123456789]");

        if (regex.matcher(username).find()) {
            Log.d("checkSpecialCharacters", "Numbers FOUND");
            return true;
        }else{
            return false;
        }
    }

    public String ConnectionQuality() {

        NetworkInfo info = getInfo(this);
        if (info == null || !info.isConnected()) {
            return "UNKNOWN";
        }

        // type wifi //
        if(info.getType() == ConnectivityManager.TYPE_WIFI) {
            WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            int numberOfLevels = 5;
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
            if(level == 2 )
                return "POOR";
            else if(level == 3 )
                return "MODERATE";
            else if(level == 4 )
                return "GOOD";
            else if(level == 5 )
                return "EXCELLENT";
            else
                return "UNKNOWN";
            // type mobile
        }else if(info.getType() == ConnectivityManager.TYPE_MOBILE) {
            int networkClass = getNetworkClass(getNetworkType(this));
            if(networkClass == 1)
                return "POOR";
            else if(networkClass == 2 )
                return "GOOD";
            else if(networkClass == 3 )
                return "EXCELLENT";
            else
                return "UNKNOWN";
        }else
            return "UNKNOWN";
    }



    public NetworkInfo getInfo(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
    }



    public int getNetworkClass(int networkType) {
        try {
            return getNetworkClassReflect(networkType);
        }catch (Exception ignored) {
        }

        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case 16: // TelephonyManager.NETWORK_TYPE_GSM:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return 1;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case 17: // TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                return 2;
            case TelephonyManager.NETWORK_TYPE_LTE:
            case 18: // TelephonyManager.NETWORK_TYPE_IWLAN:
                return 3;
            default:
                return 0;
        }
    }

    private int getNetworkClassReflect(int networkType) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getNetworkClass = TelephonyManager.class.getDeclaredMethod("getNetworkClass", int.class);
        if (!getNetworkClass.isAccessible()) {
            getNetworkClass.setAccessible(true);
        }
        return (Integer) getNetworkClass.invoke(null, networkType);
    }

    public static int getNetworkType(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkType();
    }


    private String selectionSex(){

        if(rbtnMen.isChecked()){
            sex = "guy";
            rbtnMen.setChecked(true);
            rdFemale.setChecked(false);
        }else {
            sex = "girl";
            rbtnMen.setChecked(false);
            rdFemale.setChecked(true);
        }
        return sex;
    }



    private void setInfo() {


        final String idUSer = FirebaseAuth.getInstance().getUid();
        datarefUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(idUSer);
        final HashMap<String, String> userMap = new HashMap<>();
        userMap.put("username", edtUsername.getText().toString());
        userMap.put("sex", selectionSex());
        userMap.put("age",edtAge.getText().toString());
        userMap.put("image", "default");
        userMap.put("rateApp", "false");
        userMap.put("purchase", "false");

        ////---------------- for notifications ------------------//
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener (new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                userMap.put("device_token", task.getResult());

            }
        });

        datarefUsers.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if ((task.isSuccessful())) {

                    // for cancel and finish waiting progress dialog:

                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    Toast.makeText(LoginActivity.this, getString(R.string.Authentication_seccess), Toast.LENGTH_SHORT).show();
                    startActivity(mainIntent);
                    finish();

                    datarefUsers.child("number").setValue(-1* System.currentTimeMillis());




                }
            }
        });

    }

    private void getDataFromFacebook(){
        user = FirebaseAuth.getInstance().getCurrentUser();

        if(Profile.getCurrentProfile()!=null) {
            // Name, email address, and profile photo Url
            Profile profile1 = Profile.getCurrentProfile();
            for (UserInfo profiloo : user.getProviderData()) {

                String name = profile1.getFirstName();

                edtUsername.setText(name);
                Toast.makeText(this, getString(R.string.cosishfn), Toast.LENGTH_SHORT).show();

            }
        }

    }

    private void wedgets() {
        btnStartChat = findViewById(R.id.btnLogIn);
        edtUsername = findViewById(R.id.edtUsername);
        rbtnMen = findViewById(R.id.rMen);
        rdFemale = findViewById(R.id.rdFemale);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //facebook  login
        if(mCallbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }

    }

    private void dialogChangeLanguage(){
        mViewInflateChangeLanguage = getLayoutInflater().inflate(R.layout.dialog_change_language,null);

        lyenlish = mViewInflateChangeLanguage.findViewById(R.id.lyenglish);
        lyarabic = mViewInflateChangeLanguage.findViewById(R.id.lyArabic);
        lyFrench = mViewInflateChangeLanguage.findViewById(R.id.lyFrench);
        lyGerman = mViewInflateChangeLanguage.findViewById(R.id.lyGerman);
        lyTurkish = mViewInflateChangeLanguage.findViewById(R.id.lyTurkish);
        lyPortuguese = mViewInflateChangeLanguage.findViewById(R.id.lyPortuguese);
        lySpanish = mViewInflateChangeLanguage.findViewById(R.id.lySpanish);

        lyhindi = mViewInflateChangeLanguage.findViewById(R.id.lyhindi);
        lyturki = mViewInflateChangeLanguage.findViewById(R.id.lyturki);

        Button btnSavelanguage = mViewInflateChangeLanguage.findViewById(R.id.btnSavelanguage);
        Button btnCancelDialogLang = mViewInflateChangeLanguage.findViewById(R.id.btnCancelDialogLang);
        final TextView tvselectedlang = mViewInflateChangeLanguage.findViewById(R.id.tvselectedlang);

        AlertDialog.Builder alertDialogBuilderChangeLang = DialogUtils.CustomAlertDialog(mViewInflateChangeLanguage
                ,LoginActivity.this);
        final android.app.AlertDialog alertDialogChangeLang = alertDialogBuilderChangeLang.create();
        alertDialogChangeLang.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialogChangeLang.setCancelable(true);
        alertDialogChangeLang.show();

        btnCancelDialogLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogChangeLang.dismiss();
            }
        });

        lyturki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codelang="tr";
                tvselectedlang.setText(R.string.turkish);
            }
        });

        lyhindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codelang="hi";
                tvselectedlang.setText(R.string.hindi);
            }
        });

        lyenlish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codelang="en";
                tvselectedlang.setText(R.string.english);
            }
        });

        lyarabic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codelang="ar";
                tvselectedlang.setText(R.string.arabic);

            }
        });
        lyFrench.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codelang="fr";
                tvselectedlang.setText(R.string.french);

            }
        });
        lyGerman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codelang="de";
                tvselectedlang.setText(R.string.german);

            }
        });
        lyPortuguese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codelang="pt";
                tvselectedlang.setText(R.string.portuguese);

            }
        });
        lyTurkish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codelang="ru";
                tvselectedlang.setText(R.string.russian);

            }
        });
        lySpanish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codelang="es";
                tvselectedlang.setText(R.string.spanish);

            }
        });
        btnSavelanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, getString(R.string.change_lang_sucs), Toast.LENGTH_SHORT).show();
                changeLanguage(codelang);
            }
        });
    }

    private void changeLanguage(String codeLang){
        SharedPreferences.Editor editor = getSharedPreferences("changeLanguage", MODE_PRIVATE).edit();
        editor.putString("language_key", codeLang);
        editor.apply();

        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(new Locale(codeLang.toLowerCase()));
        }else{
            conf.locale = new Locale(codeLang.toLowerCase());
        }
        res.updateConfiguration(conf,dm);

        // restart app
        Intent i = getBaseContext().getPackageManager().
                getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }


}
