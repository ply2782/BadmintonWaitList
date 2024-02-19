package com.jc.jcsports.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.jc.jcsports.R;
import com.jc.jcsports.databinding.FragmentJoin1Binding;
import com.jc.jcsports.interfaces.JoinClickInterface;
import com.jc.jcsports.utils.ApisCall;

public class Join_1 extends Fragment implements JoinClickInterface{
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private FragmentJoin1Binding binding;
    private ArrayAdapter<String> adapter;
    private Context ctx;


    public Join_1() {
        // Required empty public constructor
    }


    public static Join_1 newInstance(String param1, Context context) {
        Join_1 fragment = new Join_1();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_join_1, container, false);
        init();
        clickListener();
        return binding.getRoot();
    }

    private void init() {
        adapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner_list);
        binding.clubSpinner.setAdapter(adapter);
        adapter.addAll(ApisCall.getAreas().blockingGet());
    }

    private void clickListener() {
        binding.clubSpinner.setSelection(0);
        binding.clubSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedAreas = adapterView.getItemAtPosition(i).toString();
                assert getArguments() != null;
                getArguments().putString(ARG_PARAM1, selectedAreas);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void dialogDisMiss() {
        binding.clubSpinner.setSelection(0);
    }
}