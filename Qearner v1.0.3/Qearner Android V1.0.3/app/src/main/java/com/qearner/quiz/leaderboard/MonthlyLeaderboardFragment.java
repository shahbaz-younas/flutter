package com.qearner.quiz.leaderboard;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;
import com.google.android.material.snackbar.Snackbar;
import com.qearner.quiz.Constant;
import com.qearner.quiz.R;
import com.qearner.quiz.vollyConfigs.ApiConfig;
import com.qearner.quiz.helper.AppController;
import com.qearner.quiz.UI.CircleImageView;
import com.qearner.quiz.helper.Session;
import com.qearner.quiz.helper.Utils;
import com.qearner.quiz.model.LeaderBoard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.qearner.quiz.Constant.PAGE_LIMIT;


public class MonthlyLeaderboardFragment extends Fragment {

    RecyclerView recyclerView;
    ProgressBar progressbar;
    CircleImageView imgProfile;
    RelativeLayout rankLyt;
    ArrayList<LeaderBoard> lbList, topList;
    int PAGE_START = 0;
    LeaderboardAdapter adapter;
    int offset = 0;
    int total;
    String USER_RANK;
    String SCORE, USER_ID;
    Handler handler;
    TextView tvRank, tvScore, tvName, tvAlert;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    String formattedDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        getAllWidgets(view);
        handler = new Handler();
        // Spinner on item click listener
        Date c = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        formattedDate = df.format(c);
        rankLyt.setVisibility(View.GONE);

        PAGE_START = 0;
        offset = 0;
        total = 0;
        LeaderBoardData(formattedDate,  0);
        return view;
    }

    public void getAllWidgets(View view) {
        progressbar = view.findViewById(R.id.progressBar);
        tvAlert = view.findViewById(R.id.tvAlert);
        tvRank = view.findViewById(R.id.tvRank);
        tvName = view.findViewById(R.id.tvName);
        tvScore = view.findViewById(R.id.tvScore);
        imgProfile = view.findViewById(R.id.imgProfile);
        rankLyt = view.findViewById(R.id.rankLyt);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }


    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    public void LeaderBoardData(final String date, final int startOffset) {

        if (Utils.isNetworkAvailable(getActivity())) {
            progressbar.setVisibility(View.VISIBLE);
            Map<String, String> params = new HashMap<>();
            params.put(Constant.GET_MONTHLY_LB, "1");
            params.put(Constant.DATE, date);
            params.put(Constant.OFFSET, String.valueOf(startOffset));
            params.put(Constant.LIMIT, String.valueOf(PAGE_LIMIT));
            params.put(Constant.userId, Session.getUserData(Session.USER_ID, getActivity()));
            ApiConfig.RequestToVolley((result, response) -> {

                if (result) {
                    try {
                        if (getActivity() != null) {

                            lbList = new ArrayList<>();
                            topList = new ArrayList<>();
                            JSONObject obj = new JSONObject(response);
                            tvAlert.setVisibility(View.GONE);
                            JSONArray jsonArray = obj.getJSONArray(Constant.DATA);
                            JSONObject userObject = jsonArray.getJSONObject(0).getJSONObject(Constant.MY_RANK);
                            if (userObject.length() > 0) {
                                if (!userObject.getString(Constant.RANK).equals("0")) {
                                    if (userObject.getString(Constant.userId).equals(Session.getUserData(Session.USER_ID, getActivity()))) {
                                        rankLyt.setVisibility(View.VISIBLE);
                                        imgProfile.setImageUrl(Session.getUserData(Constant.PROFILE, getActivity()), imageLoader);
                                        tvScore.setText("" + userObject.getString(Constant.SCORE));
                                        tvName.setText(Session.getUserData(Constant.USER_NAME, getActivity()));
                                        tvRank.setText("" + userObject.getString(Constant.RANK));
                                    }
                                }
                            } else {
                                rankLyt.setVisibility(View.GONE);
                            }
                            progressbar.setVisibility(View.GONE);
                            if (jsonArray.length() > 1) {
                                total = Integer.parseInt(obj.getString(Constant.TOTAL));

                                for (int i = 1; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    USER_RANK = object.getString(Constant.RANK);

                                    switch (USER_RANK) {
                                        case "1":
                                        case "2":
                                        case "3":
                                            topList.add(new LeaderBoard(object.getString(Constant.RANK),
                                                    object.getString(Constant.name), object.getString(Constant.SCORE),
                                                    object.getString(Constant.userId), object.getString(Constant.PROFILE)));
                                            break;

                                        default:
                                            //lbList.add(0, new LeaderBoard(topList));
                                            LeaderBoard leaderBoard = new LeaderBoard(object.getString(Constant.RANK),
                                                    object.getString(Constant.name), object.getString(Constant.SCORE),
                                                    object.getString(Constant.userId), object.getString(Constant.PROFILE));
                                            lbList.add(leaderBoard);
                                            break;
                                    }
                                }

                                if (jsonArray.length() == 2)
                                    lbList.add(0, new LeaderBoard(topList));
                                else if (jsonArray.length() == 3)
                                    lbList.add(0, new LeaderBoard(topList));
                                else if (jsonArray.length() == 4)
                                    lbList.add(0, new LeaderBoard(topList));
                                else
                                    lbList.add(0, new LeaderBoard(topList));


                                if (startOffset == 0) {
                                    adapter = new LeaderboardAdapter(getActivity(), lbList, recyclerView);
                                    adapter.setHasStableIds(true);
                                    recyclerView.setAdapter(adapter);
                                    adapter.setOnLoadMoreListener(() -> {
                                        //add null , so the adapter will check view_type and show progress bar at bottom
                                        if (lbList.size() < total) {
                                            lbList.add(null);
                                            adapter.notifyItemInserted(lbList.size() - 1);

                                            new Handler().postDelayed(() -> {
                                                //   remove progress item
                                               /* if (lbList.contains(null)) {
                                                    lbList.remove(lbList.size() - 1);
                                                    adapter.notifyItemRemoved(lbList.size());
                                                    for (int i = 0; i < lbList.size(); i++) {
                                                        if (lbList.get(i) == null) {
                                                            lbList.remove(i);
                                                            break;
                                                        }
                                                    }
                                                }*/

                                                offset = offset + PAGE_LIMIT;
                                                Map<String, String> params1 = new HashMap<>();

                                                params1.put(Constant.GET_MONTHLY_LB, "1");
                                                params1.put(Constant.DATE, date);
                                                /*setLeaderboardApiParams(date, type, params);*/
                                                params1.put(Constant.OFFSET, String.valueOf(offset));
                                                params1.put(Constant.LIMIT, String.valueOf(PAGE_LIMIT));
                                                if (getActivity() != null) {
                                                    params1.put(Constant.userId, Session.getUserData(Session.USER_ID, getActivity()));
                                                }
                                                ApiConfig.RequestToVolley((result1, response1) -> {

                                                    if (result1) {
                                                        try {
                                                            lbList.remove(lbList.size() - 1);
                                                            adapter.notifyItemRemoved(lbList.size());
                                                            SCORE = Constant.SCORE;
                                                            USER_ID = Constant.userId;
                                                            JSONObject obj1 = new JSONObject(response1);
                                                            JSONArray jsonArray1 = obj1.getJSONArray(Constant.DATA);
                                                            if (jsonArray1.length() > 1) {
                                                                for (int i = 1; i < jsonArray1.length(); i++) {
                                                                    JSONObject object = jsonArray1.getJSONObject(i);
                                                                    USER_RANK = object.getString(Constant.RANK);
                                                                    LeaderBoard leaderBoard = new LeaderBoard(object.getString(Constant.RANK), object.getString(Constant.name), object.getString(SCORE), object.getString(USER_ID), object.getString(Constant.PROFILE));
                                                                    lbList.add(leaderBoard);
                                                                    //adapter.notifyItemInserted(lbList.size());
                                                                }
                                                                adapter.notifyDataSetChanged();
                                                                adapter.setLoaded();
                                                            } else {
                                                                progressbar.setVisibility(View.GONE);
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }, params1, getActivity());
                                            }, 1000);
                                        }
                                    });

                                }
                            } else {

                                progressbar.setVisibility(View.GONE);
                                tvAlert.setText(getString(R.string.no_data));
                                tvAlert.setVisibility(View.VISIBLE);
                                if (adapter != null)
                                    adapter.notifyDataSetChanged();

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, params, getActivity());

        } else {
            setSnackBar();
        }
    }

    public void setSnackBar() {
        Snackbar snackbar = Snackbar.make(getView().findViewById(android.R.id.content), getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry), view -> LeaderBoardData(formattedDate,  0));
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }


    @Override
    public void onResume() {
        super.onResume();

    }
}
