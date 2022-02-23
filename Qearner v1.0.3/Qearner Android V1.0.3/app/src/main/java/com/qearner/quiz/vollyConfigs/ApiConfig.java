package com.qearner.quiz.vollyConfigs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.provider.Settings;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.qearner.quiz.Constant;
import com.qearner.quiz.helper.AppController;
import com.qearner.quiz.helper.Session;
import com.qearner.quiz.model.Question;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ApiConfig {

    public static String VolleyErrorMessage(VolleyError error) {
        String message = "";
        try {
            if (error instanceof NetworkError) {
                message = "Cannot connect to Internet...Please check your connection!";
            } else if (error instanceof ServerError) {
                message = "The server could not be found. Please try again after some time!!";
            } else if (error instanceof AuthFailureError) {
                message = "Cannot connect to Internet...Please check your connection!";
            } else if (error instanceof ParseError) {
                message = "Parsing error! Please try again after some time!!";
            } else if (error instanceof TimeoutError) {
                message = "Connection TimeOut! Please check your internet connection.";
            } else
                message = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }


    public static void RequestToVolley(final VolleyCallback callback, final Map<String, String> params, final Activity activity) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL, response -> callback.onSuccess(true, response),
                error -> callback.onSuccess(false, "")) {
            @SuppressLint("HardwareIds")
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params1 = new HashMap<>();
                //params1.put(Constant.AUTHORIZATION, "Bearer " + AppController.createJWT("quiz", "quiz Authentication"));
                params1.put(Constant.AUTHORIZATION, Session.getUserData(Session.JWT_KEY, activity));
                params1.put(Constant.USER_AUTH, Session.getUserData(Session.UID, activity));
                params1.put(Constant.HEADER_DEVICE_ID, "" + Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));

                return params1;
            }


            @Override
            protected Map<String, String> getParams() {
                params.put(Constant.accessKey, Constant.accessKeyValue);
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    public static void RequestLoginWithoutJWT(final VolleyCallback callback, final Map<String, String> params) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL, response -> callback.onSuccess(true, response),
                error -> callback.onSuccess(false, "")) {

            @Override
            protected Map<String, String> getParams() {
                params.put(Constant.accessKey, Constant.accessKeyValue);
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    public static void MultipartRequestToVolley(final VolleyCallback callback, final Map<String, String> params, final Map<String, String> fileParams, final Activity activity) {
        VolleyMultiPartRequest multipartRequest = new VolleyMultiPartRequest(Constant.QUIZ_URL, response -> callback.onSuccess(true, response),
                error -> callback.onSuccess(false, "")) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params1 = new HashMap<>();
                params1.put(Constant.AUTHORIZATION, Session.getUserData(Session.JWT_KEY, activity));
                params1.put(Constant.USER_AUTH, Session.getUserData(Session.UID, activity));
                return params1;
            }

            @Override
            public Map<String, String> getDefaultParams() {
                params.put(Constant.accessKey, Constant.accessKeyValue);
                return params;
            }

            @Override
            public Map<String, String> getFileParams() {
                return fileParams;
            }
        };

        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(multipartRequest);
    }

    public static void setPlayedStatus(final Activity activity, final String cateId, String subCateId) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.SET_PLAYED_STATUS, "1");
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, activity));
        params.put(Constant.category, cateId);
        params.put(Constant.subCategoryId, subCateId);

        ApiConfig.RequestToVolley((result, response) -> {
        }, params, activity);

    }

    public interface VolleyCallback {
        void onSuccess(boolean result, String message);
        //void onSuccessWithMsg(boolean result, String message);
    }

    public static ArrayList<Question> setQuestions(JSONArray jsonArray, Activity activity) {
        ArrayList<Question> questionList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                Question question = new Question();
                JSONObject object = jsonArray.getJSONObject(i);
                question.setId(Integer.parseInt(object.getString(Constant.ID)));
                question.setQuestion(object.getString(Constant.QUESTION));
                question.setImage(object.getString(Constant.IMAGE));
                question.addOption(object.getString(Constant.OPTION_A).trim());
                question.addOption(object.getString(Constant.OPTION_B).trim());
                question.addOption(object.getString(Constant.OPTION_C).trim());
                question.addOption(object.getString(Constant.OPTION_D).trim());
                question.setQueType(object.getString(Constant.QUE_TYPE));
                if (Session.getBoolean(Session.E_MODE, activity)) {
                    if (!object.getString(Constant.OPTION_E).isEmpty() || !object.getString(Constant.OPTION_E).equals(""))
                        question.addOption(object.getString(Constant.OPTION_E).trim());
                }
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
                if (i != 0 && i % 5 == 0) {
                    questionList.add(new Question(true));
                }
                questionList.add(question);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return questionList;
    }

    public static boolean CheckValidation(String item, boolean emailValidation, boolean mobileValidation) {
        if (item.length() == 0)
            return true;
        else if (emailValidation && (!android.util.Patterns.EMAIL_ADDRESS.matcher(item).matches()))
            return true;
        else return mobileValidation && (item.length() < 9 || item.length() > 11);
    }

}
