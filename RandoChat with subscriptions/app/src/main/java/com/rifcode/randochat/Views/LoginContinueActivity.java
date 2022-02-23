package com.rifcode.randochat.Views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rifcode.randochat.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.regex.Pattern;



public class LoginContinueActivity extends AppCompatActivity {

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

    private FirebaseUser user;
    private DatabaseReference dbUsers;
//    private AdView adView;
private ProgressDialog proDial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_continue);
        edtAge = findViewById(R.id.edtAge);

        cbPolicy = findViewById(R.id.cbPolicy);
        tvlaws = findViewById(R.id.tvlaws);

        // progress Dialog :
        proDial = new ProgressDialog(LoginContinueActivity.this);
        proDial.setMessage(getString(R.string.wsin));
        proDial.setCanceledOnTouchOutside(false);
        dbUsers = FirebaseDatabase.getInstance().getReference().child("Users");



        tvlaws.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent PrivacyPolicyActivity =  new Intent(LoginContinueActivity.this, PolicyActivity.class);
                startActivity(PrivacyPolicyActivity);
            }
        });

        wedgets();


        edtUsername.setVisibility(View.GONE);
        btnStartChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String username = edtUsername.getText().toString();
                if(edtAge.getText().toString().equals("")){
                    Toast.makeText(LoginContinueActivity.this, R.string.Authentication_failed,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(edtAge.getText()) || TextUtils.isEmpty(edtUsername.getText())
                ){
                    Toast.makeText(LoginContinueActivity.this, getString(R.string.eror_data),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!cbPolicy.isChecked()) {
                    Toast.makeText(LoginContinueActivity.this, getString(R.string.need_acceplt), Toast.LENGTH_SHORT).show();

                    return;
                }

                if(Integer.parseInt(edtAge.getText().toString())<18){
                    Toast.makeText(LoginContinueActivity.this,getString(R.string.underage),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if(checkNumbers(edtUsername.getText().toString())){

                    Toast.makeText(LoginContinueActivity.this,getString(R.string.check_username_digi),
                            Toast.LENGTH_SHORT).show();

                    return;
                }

                if(checkSpecialCharacters(edtUsername.getText().toString())){

                    Toast.makeText(LoginContinueActivity.this,getString(R.string.check_dymbol_username),
                            Toast.LENGTH_SHORT).show();
                    return;
                }


                if(Integer.parseInt(edtAge.getText().toString())>70){
                    Toast.makeText(LoginContinueActivity.this,getString(R.string.plus70),
                            Toast.LENGTH_SHORT).show();
                    return;
                }



                if(edtUsername.getText().length()<6){
                    Toast.makeText(LoginContinueActivity.this,getString(R.string.usernale_error),
                            Toast.LENGTH_SHORT).show();
                    return;
                }



                if (username.equals("")){
                    Toast.makeText(LoginContinueActivity.this, R.string.writeusername, Toast.LENGTH_SHORT).show();
                    Toast.makeText(LoginContinueActivity.this, R.string.Authentication_failed,
                            Toast.LENGTH_SHORT).show();
                    btnStartChat.setVisibility(View.VISIBLE);
                    return;
                }

//                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
//                    if (ConnectionQuality().equals("POOR") || ConnectionQuality().equals("UNKNOWN")) {
//                        Toast.makeText(LoginContinueActivity.this, getString(R.string.badconnection), Toast.LENGTH_SHORT).show();
//                        btnStartChat.setVisibility(View.VISIBLE);
//                        return;
//                    }
//                }
                setInfo();
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
        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("username", edtUsername.getText().toString());
        userMap.put("sex", selectionSex());
        userMap.put("age",edtAge.getText().toString());
        userMap.put("image", "default");
        userMap.put("rateApp", "false");
        userMap.put("purchase", "false");

        datarefUsers.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if ((task.isSuccessful())) {

                    // for cancel and finish waiting progress dialog:

                    Intent mainIntent = new Intent(LoginContinueActivity.this, MainActivity.class);
                    Toast.makeText(LoginContinueActivity.this, getString(R.string.Authentication_seccess), Toast.LENGTH_SHORT).show();
                    startActivity(mainIntent);
                    finish();

                    datarefUsers.child("number").setValue(-1* System.currentTimeMillis());

                }
            }
        });

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



}
