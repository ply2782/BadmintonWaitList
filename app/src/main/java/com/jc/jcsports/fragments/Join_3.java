package com.jc.jcsports.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.jc.jcsports.R;
import com.jc.jcsports.databinding.FragmentJoin3Binding;
import com.jc.jcsports.interfaces.JoinClickInterface;

public class Join_3 extends Fragment implements JoinClickInterface {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private FragmentJoin3Binding binding;
    private Context ctx;

    public Join_3() {
        // Required empty public constructor
    }


    public static Join_3 newInstance(String param1, Context context) {
        Join_3 fragment = new Join_3();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_join_3, container, false);
        clickListener();
        return binding.getRoot();
    }

    private void clickListener() {
        assert getArguments() != null;
        binding.genderRG.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.manRB:
                    getArguments().putString(ARG_PARAM1, "M");
                    break;
                case R.id.womanRB:
                    getArguments().putString(ARG_PARAM1, "F");
                    break;
            }
        });
    }

    @Override
    public void dialogDisMiss() {
        binding.genderRG.clearCheck();
    }
}