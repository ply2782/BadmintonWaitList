package com.jc.jcsports.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.jc.jcsports.R;
import com.jc.jcsports.databinding.FragmentJoin4Binding;
import com.jc.jcsports.interfaces.JoinClickInterface;
import com.jc.jcsports.model.Member;

public class Join_4 extends Fragment implements JoinClickInterface {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private FragmentJoin4Binding binding;
    private Context ctx;

    public Join_4() {
        // Required empty public constructor
    }


    public static Join_4 newInstance(String param1, Context context) {
        Join_4 fragment = new Join_4();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_join_4, container, false);
        clickListener();
        return binding.getRoot();
    }

    private void drawableInit(int selectedId) {
        Drawable drawable = ContextCompat.getDrawable(ctx, R.drawable.selector_radio_button);
        drawable = DrawableCompat.wrap(drawable);
        for (int i = 0; i < binding.colorRG.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) binding.colorRG.getChildAt(i);
            if (selectedId != radioButton.getId()) {
                radioButton.setBackground(drawable);
            }
        }
    }

    private void clickListener() {
        assert getArguments() != null;
        binding.colorRG.setOnCheckedChangeListener((radioGroup, i) -> {
            Drawable drawable;
            switch (i) {
                case R.id.blackRB:
                    drawableInit(i);
                    drawable = binding.blackRB.getBackground();
                    drawable = DrawableCompat.wrap(drawable);
                    DrawableCompat.setTint(drawable, Color.BLACK);
                    binding.blackRB.setBackground(drawable);
                    getArguments().putString(ARG_PARAM1, Member.Colors.BLACK.getLabel());
                    break;

                case R.id.redRB:
                    drawableInit(i);
                    drawable = binding.redRB.getBackground();
                    drawable = DrawableCompat.wrap(drawable);
                    DrawableCompat.setTint(drawable, Color.RED);
                    binding.redRB.setBackground(drawable);
                    getArguments().putString(ARG_PARAM1, Member.Colors.RED.getLabel());
                    break;

                case R.id.orangeRB:
                    drawableInit(i);
                    drawable = binding.orangeRB.getBackground();
                    drawable = DrawableCompat.wrap(drawable);
                    DrawableCompat.setTint(drawable, Color.argb(255, 255, 127, 0));
                    binding.orangeRB.setBackground(drawable);
                    getArguments().putString(ARG_PARAM1, Member.Colors.ORANGE.getLabel());
                    break;

                case R.id.greenRB:
                    drawableInit(i);
                    drawable = binding.greenRB.getBackground();
                    drawable = DrawableCompat.wrap(drawable);
                    DrawableCompat.setTint(drawable, Color.GREEN);
                    binding.greenRB.setBackground(drawable);
                    getArguments().putString(ARG_PARAM1, Member.Colors.GREEN.getLabel());
                    break;

                case R.id.yellowRB:
                    drawableInit(i);
                    drawable = binding.yellowRB.getBackground();
                    drawable = DrawableCompat.wrap(drawable);
                    DrawableCompat.setTint(drawable, Color.YELLOW);
                    binding.yellowRB.setBackground(drawable);
                    getArguments().putString(ARG_PARAM1, Member.Colors.YELLOW.getLabel());
                    break;

            }
        });
    }

    @Override
    public void dialogDisMiss() {
        binding.colorRG.clearCheck();
        drawableInit(-1);
    }
}