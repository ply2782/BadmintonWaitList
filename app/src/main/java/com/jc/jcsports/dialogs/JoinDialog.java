package com.jc.jcsports.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.CompositePageTransformer;

import com.jc.jcsports.R;
import com.jc.jcsports.adapter.JoinAdapter;
import com.jc.jcsports.databinding.DialogJoinBinding;
import com.jc.jcsports.fragments.Join_1;
import com.jc.jcsports.fragments.Join_2;
import com.jc.jcsports.fragments.Join_3;
import com.jc.jcsports.fragments.Join_4;
import com.jc.jcsports.model.Member;
import com.jc.jcsports.utils.ApisCall;
import com.jc.jcsports.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class JoinDialog extends Dialog {

    private final String TAG = "JoinDialog";
    private Context ctx;
    private DialogJoinBinding binding;
    private JoinAdapter joinAdapter;
    private Map<String, String> joinMap;
    private List<Fragment> fragmentList;


    public JoinDialog(@NonNull Context context) {
        super(context);
        ctx = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(ctx);
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_join, null, false);
        setContentView(binding.getRoot());
        setCancelable(true);
        init();
        try {
            JSONObject object = new JSONObject();
            object.put("width", 0.8);
            object.put("height", -2);
            Util.dialogLayoutInfoUpdate(ctx, getWindow(), object);
        } catch (JSONException e) {
            Log.d(TAG, "[JSONException Error] => " + e.getMessage());
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        joinInfoInit();
    }

    @Override
    public void show() {
        super.show();
    }

    private void joinInfoInit() {
        for (Fragment f : fragmentList) {
            if (f instanceof Join_1) {
                ((Join_1) f).dialogDisMiss();
            } else if (f instanceof Join_2) {
                ((Join_2) f).dialogDisMiss();
            } else if (f instanceof Join_3) {
                ((Join_3) f).dialogDisMiss();
            } else if (f instanceof Join_4) {
                ((Join_4) f).dialogDisMiss();
            }
            assert f.getArguments() != null;
            f.getArguments().putString("param1", "null");
        }
        joinMap.clear();
        binding.viewPager2.setCurrentItem(0);
        binding.joinCompleteBtn.setText(ctx.getString(R.string.choose));
    }

    private void init() {
        joinMap = new TreeMap<>();
        binding.joinCompleteBtn.setText(ctx.getString(R.string.choose));
        joinAdapter = new JoinAdapter((FragmentActivity) ctx);
        fragmentList = new ArrayList<>();
        Fragment join_1 = Join_1.newInstance("null", ctx);
        Fragment join_2 = Join_2.newInstance("null", ctx);
        Fragment join_3 = Join_3.newInstance("null", ctx);
        Fragment join_4 = Join_4.newInstance("null", ctx);


        fragmentList.add(join_1);
        fragmentList.add(join_2);
        fragmentList.add(join_3);
        fragmentList.add(join_4);

        joinAdapter.setFragmentList(fragmentList);
        binding.viewPager2.setAdapter(joinAdapter);
        binding.viewPager2.setPageTransformer(new CompositePageTransformer());
        binding.viewPager2.setOffscreenPageLimit(fragmentList.size());
        binding.viewPager2.setUserInputEnabled(false);
        clickListener(fragmentList);
    }

    private void clickListener(List<Fragment> fragmentList) {
        binding.closeDialogBtn.setOnClickListener((v) -> dismiss());
        binding.joinCompleteBtn.setOnClickListener(view -> {
            if (binding.joinCompleteBtn.getText().equals(ctx.getString(R.string.complete))) {
                List<Member> res = ApisCall.insertMember(joinMap).blockingGet();
                if (res.size() > 0) {
                    Util.setMsg(ctx, "회원추가를 완료했습니다.");
                    dismiss();
                } else {
                    Util.setMsg(ctx, "서버와의 통신이 원할하지 않습니다.");
                }
            }

            String areas = fragmentList.get(0).getArguments().get("param1").toString();
            if (areas.equals("null")) {

            } else {
                joinMap.put("clubName", areas);
                String name = fragmentList.get(1).getArguments().get("param1").toString();
                if (name.equals("null")) {
                    Util.setMsg(ctx, "성함을 입력해주세요.");
                    binding.viewPager2.setCurrentItem(1, true);
                } else {
                    joinMap.put("name", name);
                    String gender = fragmentList.get(2).getArguments().get("param1").toString();
                    if (gender.equals("null")) {
                        Util.setMsg(ctx, "성별을 선택해주세요.");
                        binding.viewPager2.setCurrentItem(2, true);
                    } else {
                        joinMap.put("gender", gender);
                        String color = fragmentList.get(3).getArguments().get("param1").toString();
                        if (color.equals("null")) {
                            Util.setMsg(ctx, "색깔을 선택해주세요.");
                            binding.viewPager2.setCurrentItem(3, true);
                        } else {
                            joinMap.put("color", color);
                            binding.joinCompleteBtn.setText(ctx.getString(R.string.complete));
                        }
                    }
                }
            }
        });
    }
}
