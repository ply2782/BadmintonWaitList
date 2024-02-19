package com.jc.jcsports.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.jc.jcsports.R;
import com.jc.jcsports.databinding.FragmentJoin2Binding;
import com.jc.jcsports.interfaces.JoinClickInterface;

public class Join_2 extends Fragment implements JoinClickInterface {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private FragmentJoin2Binding binding;
    private Context ctx;

    public Join_2() {
        // Required empty public constructor
    }


    public static Join_2 newInstance(String param1, Context context) {
        Join_2 fragment = new Join_2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        fragment.ctx = context;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_join_2, container, false);
        clickListener();
        return binding.getRoot();
    }


    private void clickListener() {
        binding.messageInputEditTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                String text = s.toString().replaceAll(" ", "").trim();
                assert getArguments() != null;
                getArguments().putString(ARG_PARAM1, text);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public void dialogDisMiss() {
        binding.messageInputEditTextView.setText("");
    }
}