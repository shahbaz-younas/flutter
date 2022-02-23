package com.qearner.quiz.earning;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.qearner.quiz.Constant;
import com.qearner.quiz.R;
import com.qearner.quiz.helper.Session;
import com.qearner.quiz.helper.Utils;
import com.qearner.quiz.model.Question;
import com.qearner.quiz.model.Transcation;
import com.qearner.quiz.vollyConfigs.ApiConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionFragment extends Fragment {
    ArrayList<Question> questionList;
    private static final String QUESTION_INDEX = "question_index";
    public SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView recyclerView;
    TextView tvAlert;
    TransactionAdapter adapter;
    public ArrayList<Transcation> transactionList;

    public TransactionFragment() {
        // Required empty public constructor
    }


    public TransactionFragment(ArrayList<Question> questionList) {
        this.questionList = questionList;

    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transcation, container, false);
        getAllWidgets(view);
        getTransactionDetails();
        swipeRefreshLayout.setOnRefreshListener(() -> {
            getTransactionDetails();
            swipeRefreshLayout.setRefreshing(false);
        });
        return view;
    }


    public void getAllWidgets(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipeLayout);
        tvAlert = view.findViewById(R.id.tvAlert);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    public static TransactionFragment newInstance(int sectionNumber, ArrayList<Question> questionList) {
        TransactionFragment fragment = new TransactionFragment(questionList);
        Bundle args = new Bundle();
        args.putInt(QUESTION_INDEX, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    public void getTransactionDetails() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.AUTH_ID, Session.getUserData(Session.UID, getActivity()));
        params.put(Constant.UserTracker, "1");
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    transactionList = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(response);
                    String error = jsonObject.getString(Constant.ERROR);
                    if (error.equalsIgnoreCase("false")) {
                        tvAlert.setVisibility(View.GONE);
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Transcation category = new Transcation();
                            JSONObject object = jsonArray.getJSONObject(i);
                            category.setCoin(object.getString(Constant.POINTS));
                            category.setType(object.getString(Constant.type));
                            category.setCoinStatus(object.getString(Constant.CoinStatus));
                            category.setType_two(object.getString(Constant.TypeTwo));
                            category.setDate(object.getString(Constant.DATE));
                            transactionList.add(category);
                        }
                        adapter = new TransactionAdapter(getContext(), transactionList);
                        Utils.setFallDownLayoutAnimation((AppCompatActivity) getActivity(), recyclerView);
                        recyclerView.setAdapter(adapter);

                    } else {
                        tvAlert.setText(getString(R.string.no_transaction));
                        tvAlert.setVisibility(View.VISIBLE);
                        if (adapter != null) {
                            adapter = new TransactionAdapter(getContext(), transactionList);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, params, getActivity());

    }

    public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ItemRowHolder> {
        ArrayList<Transcation> dataList;
        Context mContext;

        public TransactionAdapter(Context context, ArrayList<Transcation> dataList) {
            this.dataList = dataList;
            this.mContext = context;
        }

        @Override
        public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_transcation, parent, false);
            return new ItemRowHolder(v);
        }

        @SuppressLint({"ResourceAsColor", "SetTextI18n"})
        @Override
        public void onBindViewHolder(@NonNull ItemRowHolder holder, final int position) {
            final Transcation category = dataList.get(position);
            holder.tvTitle.setText(category.getType());
            holder.date.setText(getString(R.string.bullet) + category.getDate());
            holder.txtTypeTwo.setText(category.getType_two());
            if (category.getCoinStatus().equals("1")) {
                holder.tvCoin.setText(category.getCoin());
                holder.txtTypeTwo.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                holder.tvCoin.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                holder.relLayout.setBackgroundResource(R.drawable.deduct_square);
            } else {
                holder.tvCoin.setText("+" + category.getCoin());
            }
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class ItemRowHolder extends RecyclerView.ViewHolder {
            public TextView tvTitle, date, tvCoin, txtTypeTwo;
            public RelativeLayout relLayout;

            public ItemRowHolder(View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                date = itemView.findViewById(R.id.date);
                tvCoin = itemView.findViewById(R.id.tvCoin);
                relLayout = itemView.findViewById(R.id.relLayout);
                txtTypeTwo = itemView.findViewById(R.id.txtTypeTwo);
            }
        }
    }


}