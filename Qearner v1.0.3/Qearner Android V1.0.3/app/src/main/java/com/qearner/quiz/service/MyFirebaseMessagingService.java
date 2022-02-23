package com.qearner.quiz.service;


import android.content.Intent;
import android.text.Html;
import android.util.Log;

import androidx.annotation.NonNull;

import com.qearner.quiz.Constant;
import com.qearner.quiz.activity.MainActivity;
import com.qearner.quiz.helper.MyNotificationManager;
import com.qearner.quiz.helper.Session;
import com.qearner.quiz.model.Question;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    public static ArrayList<Question> questionList;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        //Getting registration token

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + s);

        // Saving reg id to shared preferences
        Session.setDeviceToken(s, getApplicationContext());


    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {


            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());

                sendPushNotification(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    //this method will display the notification
    //We are passing the JSONObject that is received from
    //fireBase cloud messaging
    private void sendPushNotification(JSONObject json) {
        try {

            Object object = json.get(Constant.DATA);
            if (object instanceof JSONObject) {
                //getting the json data

                JSONObject data = new JSONObject((object).toString());

                //parsing json data
                String title = data.getString(Constant.TITLE);
                String message = data.getString(Constant.MESSAGE);
                String imageUrl = data.getString(Constant.IMAGE);
                String type = data.getString(Constant.TYPE);
                String typeId = data.getString(Constant.TYPE_ID);
                String no_of = data.getString(Constant.NO_OF_CATE);

                if (type.equalsIgnoreCase("default"))
                    Session.setNCount((Session.getNCount(getApplicationContext()) + 1), getApplicationContext());


                //creating MyNotificationManager object
                MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());
                //creating an intent for the notification
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("cateId", typeId);
                intent.putExtra("type", type);
                intent.putExtra("no_of", no_of);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                //if there is no image
                if (imageUrl.equals("null")) {
                    //displaying small notification
                    mNotificationManager.showSmallNotification(title, Html.fromHtml(message).toString(), intent);
                } else {
                    //if there is an image
                    //displaying a big notification
                    mNotificationManager.showBigNotification(title, Html.fromHtml(message).toString(), imageUrl, intent);
                }
            }


        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }


}
