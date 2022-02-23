package com.qearner.quiz.contest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.qearner.quiz.UI.GridRecyclerView;
import com.qearner.quiz.Constant;
import com.qearner.quiz.R;


import com.qearner.quiz.vollyConfigs.ApiConfig;
import com.qearner.quiz.helper.Session;
import com.qearner.quiz.helper.Utils;
import com.qearner.quiz.model.Model;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ContestFragment extends Fragment {

    GridRecyclerView recyclerView;
    TextView tvAlert, tvBack;
    ProgressBar progressbar;
    LinearLayout alertLayout;
    List<Model> contestList = new ArrayList<>();
    ContestAdapter adapter;
    String type;

    ImageView img;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_list, container, false);
        getAllWidgets(view);
        assert getArguments() != null;
        type = getArguments().getString("current_page");
        img.setImageResource(R.drawable.ic_not_found);
        tvAlert.setText(getString(R.string.contest_not_available));
        prepareData(type);
        return view;
    }

    public void getAllWidgets(View view) {
        img = view.findViewById(R.id.image);
        progressbar = view.findViewById(R.id.progressbar);
        recyclerView = view.findViewById(R.id.recyclerView);
        tvAlert = view.findViewById(R.id.tvAlert);
        tvBack = view.findViewById(R.id.tvBack);
        tvBack.setVisibility(View.GONE);
        alertLayout = view.findViewById(R.id.lyt_alert);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setNestedScrollingEnabled(false);


    }

    private void prepareData(final String type) {
        if (Utils.isNetworkAvailable(getActivity())) {
            progressbar.setVisibility(View.VISIBLE);
            Map<String, String> params = new HashMap<>();
            params.put(Constant.GET_CONTEST, Constant.GET_DATA_KEY);
            params.put(Constant.userId, Session.getUserData(Session.USER_ID, getActivity()));
            ApiConfig.RequestToVolley((result, response) -> {
                if (result) {
                    try {
                        System.out.println("=====contest " + response);
                        alertLayout.setVisibility(View.GONE);
                        JSONObject jsonObject1 = new JSONObject(response);


                        JSONObject jsonObject = null;
                        if (!jsonObject1.has(Constant.ERROR)) {
                            if (type.equalsIgnoreCase(getString(R.string.live)))
                                jsonObject = jsonObject1.getJSONObject(Constant.LIVE_CONTEST);
                            else if (type.equalsIgnoreCase(getString(R.string.up_coming)))
                                jsonObject = jsonObject1.getJSONObject(Constant.UPCOMING_CONTEST);
                            else if (type.equalsIgnoreCase(getString(R.string.past)))
                                jsonObject = jsonObject1.getJSONObject(Constant.PAST_CONTEST);
                            assert jsonObject != null;
                            if (!jsonObject.getBoolean(Constant.ERROR)) {
                                contestList.clear();
                                JSONArray object = jsonObject.getJSONArray(Constant.DATA);
                                for (int i = 0; i < object.length(); i++) {
                                    JSONObject obj = object.getJSONObject(i);
                                    Model model = new Model(obj.getString(Constant.ID), obj.getString(Constant.name), obj.getString(Constant.START_DATE), obj.getString(Constant.END_DATE), obj.getString(Constant.DESCRIPTION), obj.getString(Constant.IMAGE), obj.getString(Constant.ENTRY), obj.getString(Constant.TOP_USERS), obj.getString(Constant.POINTS), obj.getString(Constant.DATE_CREATED), obj.getString(Constant.PARTICIPANTS), "");
                                    contestList.add(model);
                                }
                                adapter = new ContestAdapter(type, contestList, getActivity());
                                Utils.setGridBottomLayoutAnimation((AppCompatActivity) getActivity(), recyclerView);
                                recyclerView.setVisibility(View.VISIBLE);
                                recyclerView.setAdapter(adapter);


                            } else {
                                recyclerView.setVisibility(View.GONE);
                                alertLayout.setVisibility(View.VISIBLE);

                            }
                        }
                        progressbar.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, params, getActivity());
        } else {
            Snackbar snackbar = Snackbar
                    .make(requireActivity().findViewById(android.R.id.content), getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE)
                    .setActionTextColor(Color.YELLOW)
                    .setAction(getString(R.string.retry), view -> {
                        Intent intent = requireActivity().getIntent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    });
            snackbar.show();
        }

    }

    public void reWardsNotLoad() {
        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.lifeline_dialog, null);
        dialog.setView(dialogView);
        TextView ok = dialogView.findViewById(R.id.ok);
        TextView title = dialogView.findViewById(R.id.title);
        TextView message = dialogView.findViewById(R.id.message);
        message.setText(getResources().getString(R.string.not_enough_entry_coin));
        title.setText(getResources().getString(R.string.not_enough_coin));
        final android.app.AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        alertDialog.setCancelable(false);
        ok.setOnClickListener(view -> alertDialog.dismiss());
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}