package com.qearner.quiz.earning;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import com.qearner.quiz.Constant;
import com.qearner.quiz.R;

import com.qearner.quiz.helper.Session;
import com.qearner.quiz.helper.Utils;

import com.qearner.quiz.model.Question;
import com.qearner.quiz.vollyConfigs.ApiConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class EarningFragment extends Fragment {
    ArrayList<Question> questionList;
    private static final String QUESTION_INDEX = "question_index";
    BottomSheetDialog bottomSheetDialog;
    RelativeLayout relayPaypal, relayPaytm;
    TextView txtEarn, txtEarnCoin;
    String PaypalEarn, PaytmEarn;

    public EarningFragment() {
        // Required empty public constructor
    }


    public EarningFragment(ArrayList<Question> questionList) {
        this.questionList = questionList;

    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_earnings, container, false);
        getAllWidgets(view);


        return view;
    }

    public void getAllWidgets(View view) {
        relayPaypal = view.findViewById(R.id.relayPaypal);
        relayPaytm = view.findViewById(R.id.relayPaytm);
        txtEarn = view.findViewById(R.id.txtEarn);
        txtEarnCoin = view.findViewById(R.id.txtEarncoin);


        relayPaypal.setOnClickListener(view1 -> {
            if (Float.parseFloat(PaypalEarn) >= 1) {
                BottomEarningDialog("paypal");
            } else {
                Toast.makeText(getActivity(), getString(R.string.not_enough_amount), Toast.LENGTH_SHORT).show();
            }

        });
        relayPaytm.setOnClickListener(view12 -> {
            if (Float.parseFloat(PaypalEarn) >= 1) {
                BottomEarningDialog("paytm");
            } else {
                Toast.makeText(getActivity(), getString(R.string.not_enough_amount), Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        GetUserStatus();
        super.onResume();
    }

    public static EarningFragment newInstance(int sectionNumber, ArrayList<Question> questionList) {
        EarningFragment fragment = new EarningFragment(questionList);
        Bundle args = new Bundle();
        args.putInt(QUESTION_INDEX, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public void BottomEarningDialog(String type) {
        bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetTheme);
        View sheetView = LayoutInflater.from(getActivity()).inflate(R.layout.bottom_earningdailog, null);
        RelativeLayout relayFirstpayment = sheetView.findViewById(R.id.relayFirstpayment);
        RelativeLayout relPaytm = sheetView.findViewById(R.id.relPaytm);
        TextView txtDoller = sheetView.findViewById(R.id.txtDoller);
        TextView txtEarn = sheetView.findViewById(R.id.txtEarn);
        EditText editPaypaldetails = sheetView.findViewById(R.id.editPaypaldetails);
        EditText editPaypaldetails2 = sheetView.findViewById(R.id.editPaypaldetails2);
        MaterialButton btnConfirm = sheetView.findViewById(R.id.btnConfirm);

        if (type.equals("paypal")) {
            txtEarn.setText(PaypalEarn);
            txtDoller.setText(getString(R.string.dollar));
            relPaytm.setVisibility(View.GONE);
        } else {
            txtEarn.setText(PaytmEarn);
            txtDoller.setText(getString(R.string.rs));
            relayFirstpayment.setVisibility(View.GONE);
        }


        btnConfirm.setOnClickListener(view -> {
            if (type.equals("paypal")) {
                String PaypalAmount = editPaypaldetails.getText().toString();
                if (PaypalAmount.trim().equals("")) {
                    Toast.makeText(getActivity(), getString(R.string.accountDetails), Toast.LENGTH_SHORT).show();
                } else {
                    SendRedeemRequest(PaypalAmount, "Paypal", PaypalEarn, txtEarnCoin.getText().toString());
                }
            } else {
                String PaypalAmount = editPaypaldetails2.getText().toString();
                if (PaypalAmount.trim().equals("")) {
                    Toast.makeText(getActivity(), getString(R.string.accountDetails), Toast.LENGTH_SHORT).show();
                } else {
                    SendRedeemRequest(PaypalAmount, "Paytm", PaytmEarn, txtEarnCoin.getText().toString());
                }
            }

        });

        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }


    @SuppressLint("SetTextI18n")
    public void GetUserStatus() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.GET_USER_BY_ID, "1");
        params.put("device_id","1234");
        params.put(Constant.ID, Session.getUserData(Session.USER_ID, getActivity()));
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {

                    JSONObject obj = new JSONObject(response);
                    boolean error = obj.getBoolean(Constant.ERROR);
                    if (!error) {
                        JSONObject jsonObject = obj.getJSONObject(Constant.DATA);
                        PaypalEarn = jsonObject.getString(Constant.PaypalAmount);
                        PaytmEarn = jsonObject.getString(Constant.PaytmAmount);
                        txtEarn.setText(getString(R.string.dollar) + jsonObject.getString(Constant.PaypalAmount));
                        txtEarnCoin.setText(jsonObject.getString(Constant.TotalCoins));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params, getActivity());
    }

    public void SendRedeemRequest(String paymentAddress, String requestType, String requestAmount, String pointUsed) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.PaymentRequest, "1");
        params.put(Constant.userId, Session.getUserData(Session.USER_ID, getActivity()));
        params.put(Constant.AUTH_ID, Session.getUserData(Session.UID, getActivity()));
        params.put(Constant.PaymentAddress, paymentAddress);
        params.put(Constant.RequestType, requestType);
        params.put(Constant.RequestAmount, requestAmount);
        params.put(Constant.PointsUsed, pointUsed);
        params.put(Constant.Remarks, "User ID : " + Session.getUserData(Session.USER_ID, getActivity()) + " requested for Redeem Amount!!");
        params.put(Constant.status, "0");

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject obj = new JSONObject(response);
                    boolean error = obj.getBoolean(Constant.ERROR);
                    bottomSheetDialog.dismiss();
                    if (!error) {
                        SignOutWarningDialog(getActivity(), getString(R.string.redeem_success), getString(R.string.redeem_msg), getString(R.string.congratulations));
                        Utils.AddCoins(getActivity(), "-" + pointUsed, requestType + " Redeem", "REDEEM", "1");
                        GetUserStatus();
                    } else {
                        SignOutWarningDialog(getActivity(), getString(R.string.already_redeem), obj.getString(Constant.messageReport), getString(R.string.ok));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params, getActivity());
    }


    public static void SignOutWarningDialog(final Activity activity, String title, String message, String btn) {
        final AlertDialog.Builder dialog1 = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dailog_redeemsuccesfully, null);
        dialog1.setView(dialogView);
        dialog1.setCancelable(true);

        final AlertDialog alertDialog = dialog1.create();
        TextView tvTitle, tv_message;
        LottieAnimationView animationView;
        tvTitle = dialogView.findViewById(R.id.tvTitle);
        animationView = dialogView.findViewById(R.id.animationView);
        tv_message = dialogView.findViewById(R.id.tv_message);
        tvTitle.setText(title);
        tv_message.setText(message);

        if (tvTitle.getText().toString().equals(activity.getString(R.string.already_redeem))) {
            animationView.setVisibility(View.GONE);
        } else {
            animationView.setVisibility(View.VISIBLE);
        }

        MaterialButton btnCongo = dialogView.findViewById(R.id.btnCongo);

        btnCongo.setText(btn);
        btnCongo.setOnClickListener(view -> alertDialog.dismiss());
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.show();

    }


}