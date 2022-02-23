package com.qearner.quiz.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import com.qearner.quiz.Constant;
import com.qearner.quiz.R;
import com.qearner.quiz.helper.AppController;
import com.qearner.quiz.helper.Session;

import com.qearner.quiz.helper.Utils;
import com.qearner.quiz.model.Question;

import java.util.ArrayList;
import java.util.Collections;

public class QuestionFragment extends Fragment implements View.OnClickListener {

    private static final String QUESTION_INDEX = "index";
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    ArrayList<Question> questionList;
    ArrayList<String> options;
    ScrollView mainScroll, queScroll;

    NetworkImageView imgQuestion;
    RelativeLayout layout_A, layout_B, layout_C, layout_D, layout_E;
    TextView  option_a, option_b, option_c, option_d, option_e, txtQuestion, tvImgQues, btnOpt1, btnOpt2, btnOpt3, btnOpt4, btnOpt5;

    ImageView imgZoom;

    View view;


    public QuestionFragment() {
        // Required empty public constructor
    }

    public QuestionFragment(ArrayList<Question> questionList) {
        this.questionList = questionList;
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_questions, container, false);
        getAllWidgets(view);
        setQuestionData();
        layout_A.setOnClickListener(this);
        layout_B.setOnClickListener(this);
        layout_C.setOnClickListener(this);
        layout_D.setOnClickListener(this);
        layout_E.setOnClickListener(this);
        mainScroll.setOnTouchListener((v, event) -> {
            v.findViewById(R.id.queScroll).getParent().requestDisallowInterceptTouchEvent(false);
            return false;
        });
        queScroll.setOnTouchListener((v, event) -> {
            //Disallow the touch request for parent scroll on touch of child view
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        queScroll.scrollTo(0, 0);
        return view;
    }

    public void getAllWidgets(View view) {
        btnOpt1 = view.findViewById(R.id.btnOpt1);
        btnOpt2 = view.findViewById(R.id.btnOpt2);
        btnOpt3 = view.findViewById(R.id.btnOpt3);
        btnOpt4 = view.findViewById(R.id.btnOpt4);
        btnOpt5 = view.findViewById(R.id.btnOpt5);
        txtQuestion = view.findViewById(R.id.txtQuestion);
        tvImgQues = view.findViewById(R.id.tvImgQues);

        imgQuestion = view.findViewById(R.id.imgQuestion);
        imgZoom = view.findViewById(R.id.imgZoom);
        mainScroll = view.findViewById(R.id.mainScroll);
        queScroll = view.findViewById(R.id.queScroll);
        layout_A = view.findViewById(R.id.a_layout);
        layout_B = view.findViewById(R.id.b_layout);
        layout_C = view.findViewById(R.id.c_layout);
        layout_D = view.findViewById(R.id.d_layout);
        layout_E = view.findViewById(R.id.e_layout);
        option_a = view.findViewById(R.id.tvA);
        option_b = view.findViewById(R.id.tvB);
        option_c = view.findViewById(R.id.tvC);
        option_d = view.findViewById(R.id.tvD);
        option_e = view.findViewById(R.id.tvE);


    }

    public void setQuestionData() {
        assert getArguments() != null;
        final Question question = questionList.get(getArguments().getInt(QUESTION_INDEX));
        //imgQuestion.resetZoom();
        options = new ArrayList<>();
        options.addAll(question.getOptions());
        if (question.getQueType().equals(Constant.TRUE_FALSE)) {
            layout_C.setVisibility(View.GONE);
            layout_D.setVisibility(View.GONE);
        } else {
            Collections.shuffle(options);
            layout_C.setVisibility(View.VISIBLE);
            layout_D.setVisibility(View.VISIBLE);
        }
        if (Session.getBoolean(Session.E_MODE, getActivity())) {
            if (options.size() == 4)
                layout_E.setVisibility(View.GONE);
            else
                layout_E.setVisibility(View.VISIBLE);

        }
        btnOpt1.setText(options.get(0).trim());
        btnOpt2.setText(options.get(1).trim());
        btnOpt3.setText(options.get(2).trim());
        btnOpt4.setText(options.get(3).trim());
        if (Session.getBoolean(Session.E_MODE, getActivity())) {
            if (options.size() == 5)
                btnOpt5.setText(options.get(4).trim());

        }
/*
        layout_A.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        layout_B.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        layout_C.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        layout_D.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        layout_E.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
*/


        if (!question.getImage().isEmpty()) {

            imgQuestion.setImageUrl(question.getImage(), imageLoader);
            tvImgQues.setText(question.getQuestion());
            txtQuestion.setVisibility(View.GONE);
            tvImgQues.setVisibility(View.VISIBLE);
            // imgZoom.setVisibility(View.VISIBLE);

            imgQuestion.setVisibility(View.VISIBLE);
           /* imgZoom.setOnClickListener(view -> {
                click++;
                if (click == 1)
                    imgQuestion.setZoom(1.25f);
                else if (click == 2)
                    imgQuestion.setZoom(1.50f);
                else if (click == 3)
                    imgQuestion.setZoom(1.75f);
                else if (click == 4) {
                    imgQuestion.setZoom(2.00f);
                    click = 0;
                }
            });*/
        } else {

            txtQuestion.setText(question.getQuestion());
            txtQuestion.setVisibility(View.VISIBLE);
            tvImgQues.setVisibility(View.GONE);
            // imgZoom.setVisibility(View.GONE);
            imgQuestion.setVisibility(View.GONE);
        }
        if (question.getSelectedAns() != null)
            if (question.getSelectedAns().equals(btnOpt1.getText().toString())) {
                option_a.setTextColor(ContextCompat.getColor(getActivity(), R.color.txt_color));
                option_a.setBackgroundResource(R.drawable.ic_attended_bg);
            } else if (question.getSelectedAns().equals(btnOpt2.getText().toString())) {
                option_b.setTextColor(ContextCompat.getColor(getActivity(), R.color.txt_color));
                option_b.setBackgroundResource(R.drawable.ic_attended_bg);
            } else if (question.getSelectedAns().equals(btnOpt3.getText().toString())) {
                option_c.setTextColor(ContextCompat.getColor(getActivity(), R.color.txt_color));
                option_c.setBackgroundResource(R.drawable.ic_attended_bg);
            } else if (question.getSelectedAns().equals(btnOpt4.getText().toString())) {
                option_d.setTextColor(ContextCompat.getColor(getActivity(), R.color.txt_color));
                option_d.setBackgroundResource(R.drawable.ic_attended_bg);
            } else if (question.getSelectedAns().equals(btnOpt5.getText().toString())) {
                option_e.setTextColor(ContextCompat.getColor(getActivity(), R.color.txt_color));
                option_e.setBackgroundResource(R.drawable.ic_attended_bg);
            }
    }

    @Override
    public void onClick(View v) {

        assert getArguments() != null;
        int id = v.getId();
        if (id == R.id.a_layout) {
            AddReview(questionList.get(getArguments().getInt(QUESTION_INDEX, 0)), btnOpt1, option_a);
            option_b.setTextColor(Color.WHITE);
            option_b.setBackgroundResource(R.drawable.card_bg_light_radius_5);
            option_c.setTextColor(Color.WHITE);
            option_c.setBackgroundResource(R.drawable.card_bg_light_radius_5);
            option_d.setTextColor(Color.WHITE);
            option_d.setBackgroundResource(R.drawable.card_bg_light_radius_5);
            option_e.setTextColor(Color.WHITE);
            option_e.setBackgroundResource(R.drawable.card_bg_light_radius_5);

        } else if (id == R.id.b_layout) {
            AddReview(questionList.get(getArguments().getInt(QUESTION_INDEX, 0)), btnOpt2, option_b);
            option_a.setTextColor(Color.WHITE);
            option_a.setBackgroundResource(R.drawable.card_bg_light_radius_5);
            option_c.setTextColor(Color.WHITE);
            option_c.setBackgroundResource(R.drawable.card_bg_light_radius_5);
            option_d.setTextColor(Color.WHITE);
            option_d.setBackgroundResource(R.drawable.card_bg_light_radius_5);
            option_e.setTextColor(Color.WHITE);
            option_e.setBackgroundResource(R.drawable.card_bg_light_radius_5);

        } else if (id == R.id.c_layout) {
            AddReview(questionList.get(getArguments().getInt(QUESTION_INDEX, 0)), btnOpt3, option_c);
            option_b.setTextColor(Color.WHITE);
            option_b.setBackgroundResource(R.drawable.card_bg_light_radius_5);
            option_a.setTextColor(Color.WHITE);
            option_a.setBackgroundResource(R.drawable.card_bg_light_radius_5);
            option_d.setTextColor(Color.WHITE);
            option_d.setBackgroundResource(R.drawable.card_bg_light_radius_5);
            option_e.setTextColor(Color.WHITE);
            option_e.setBackgroundResource(R.drawable.card_bg_light_radius_5);

        } else if (id == R.id.d_layout) {
            AddReview(questionList.get(getArguments().getInt(QUESTION_INDEX, 0)), btnOpt4, option_d);
            option_b.setTextColor(Color.WHITE);
            option_b.setBackgroundResource(R.drawable.card_bg_light_radius_5);
            option_c.setTextColor(Color.WHITE);
            option_c.setBackgroundResource(R.drawable.card_bg_light_radius_5);
            option_a.setTextColor(Color.WHITE);
            option_a.setBackgroundResource(R.drawable.card_bg_light_radius_5);
            option_e.setTextColor(Color.WHITE);
            option_e.setBackgroundResource(R.drawable.card_bg_light_radius_5);

        } else if (id == R.id.e_layout) {
            AddReview(questionList.get(getArguments().getInt(QUESTION_INDEX, 0)), btnOpt5, option_e);
            option_b.setTextColor(Color.WHITE);
            option_b.setBackgroundResource(R.drawable.card_bg_light_radius_5);
            option_c.setTextColor(Color.WHITE);
            option_c.setBackgroundResource(R.drawable.card_bg_light_radius_5);
            option_d.setTextColor(Color.WHITE);
            option_d.setBackgroundResource(R.drawable.card_bg_light_radius_5);
            option_a.setTextColor(Color.WHITE);
            option_a.setBackgroundResource(R.drawable.card_bg_light_radius_5);

        }

    }

    public void AddReview(Question question, TextView tvBtnOpt, TextView tvOpt) {
        Utils.CheckVibrateOrSound(getActivity());
        if (!question.getSelectedOpt().equalsIgnoreCase(tvOpt.getText().toString())) {
            question.setCorrect(tvBtnOpt.getText().toString().equalsIgnoreCase(question.getTrueAns()));
            question.setSelectedOpt(tvOpt.getText().toString());
            question.setAttended(true);
            tvOpt.setBackgroundResource(R.drawable.ic_attended_bg);
            tvOpt.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
            question.setSelectedAns(tvBtnOpt.getText().toString());
        } else {
            question.setAttended(false);
            question.setCorrect(false);
            tvOpt.setBackgroundResource(R.drawable.card_bg_light_radius_5);
            tvOpt.setTextColor(ContextCompat.getColor(getActivity(), R.color.txt_color));
            question.setSelectedOpt("none");
        }
        // OptionBgChange(question);
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static QuestionFragment newInstance(int sectionNumber, ArrayList<Question> questionList) {
        QuestionFragment fragment = new QuestionFragment(questionList);
        Bundle args = new Bundle();
        args.putInt(QUESTION_INDEX, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
}