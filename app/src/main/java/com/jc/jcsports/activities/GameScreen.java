package com.jc.jcsports.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jc.jcsports.adapter.ConfirmedWaitListAdapter;
import com.jc.jcsports.databinding.ActivityGameScreenBinding;
import com.jc.jcsports.dialogs.AttendanceDialog;
import com.jc.jcsports.dialogs.AttendanceStatusDialog;
import com.jc.jcsports.dialogs.JoinDialog;
import com.jc.jcsports.dialogs.LoadingDialog;
import com.jc.jcsports.dialogs.PromptDialog;
import com.jc.jcsports.dialogs.WaitListDialog;
import com.jc.jcsports.diffUtils.MemberListDiffUtil;
import com.jc.jcsports.interfaces.ConfirmedWaitListInterface;
import com.jc.jcsports.model.Member;
import com.jc.jcsports.utils.ApisCall;
import com.jc.jcsports.utils.RFClient;
import com.jc.jcsports.utils.Util;
import com.jc.jcsports.viewModels.MemberListVM;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.reactivex.rxjava3.core.Observable;

public class GameScreen extends AppCompatActivity implements ConfirmedWaitListInterface {

    private ActivityGameScreenBinding binding;
    private WaitListDialog waitListDialog;
    private AttendanceDialog attendanceDialog;
    private AttendanceStatusDialog attendanceStatusDialog;
    private PromptDialog promptDialog;
    private final String TAG = "GameScreenActivity";
    private ConfirmedWaitListAdapter confirmedWaitListAdapter;
    private MemberListVM memberListVM;
    private Observer<List<List<Member>>> mObserver;
    private RecyclerView confirmedWaitListRV;
    private Handler handler;
    private JoinDialog joinDialog;
    private boolean isAllFabsVisible = false;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private Type type;
    private SimpleDateFormat simpleDateFormat;
    private String savedKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGameScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        RFClient.getInstance();
        Util.hideSystemUI(getWindow());
        dialogInit();
        clickListener();
        vmInit();
    }


    private void vmInit() {
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        savedKey = simpleDateFormat.format(new Date());
        preferences = getPreferences(MODE_PRIVATE);
        editor = preferences.edit();
        type = new TypeToken<List<List<Member>>>() {
        }.getType();
        gson = new Gson();
        handler = new Handler();
        memberListVM = new ViewModelProvider(GameScreen.this).get(MemberListVM.class);
        confirmedWaitListRV = binding.confirmedWaitListRV;
        confirmedWaitListRV.setHasFixedSize(true);
        confirmedWaitListAdapter = new ConfirmedWaitListAdapter(new MemberListDiffUtil());
        confirmedWaitListAdapter.setHasStableIds(true);
        confirmedWaitListAdapter.setDialog(waitListDialog, promptDialog);
        confirmedWaitListRV.setAdapter(confirmedWaitListAdapter);
    }

    private void notifyData(@NonNull List<List<Member>> mList) {
        confirmedWaitListAdapter.notifyItemRangeRemoved(0, mList.size());
        confirmedWaitListAdapter.submitList(mList);
        confirmedWaitListAdapter.notifyItemRangeInserted(0, mList.size());
        handler.postDelayed(() -> {
            if (confirmedWaitListRV.getAdapter() != null) {
                final int itemCount = confirmedWaitListRV.getAdapter().getItemCount();
                if (itemCount == 0) return;
                confirmedWaitListRV.smoothScrollToPosition(itemCount - 1);
            }
        }, 500);
        editor.putString(savedKey, gson.toJson(mList));
        editor.apply();
    }


    private void notifyDeleteData(@NonNull List<List<Member>> mList) {
        confirmedWaitListAdapter.notifyItemRangeRemoved(0, mList.size());
        confirmedWaitListAdapter.submitList(mList);
        handler.postDelayed(() -> {
            if (confirmedWaitListRV.getAdapter() != null) {
                final int itemCount = confirmedWaitListRV.getAdapter().getItemCount();
                if (itemCount == 0) return;
                confirmedWaitListRV.smoothScrollToPosition(itemCount - 1);
            }
        }, 500);
        editor.putString(savedKey, gson.toJson(mList));
        editor.apply();
    }

    private void dialogInit() {
        attendanceStatusDialog = new AttendanceStatusDialog(GameScreen.this);
        attendanceDialog = new AttendanceDialog(GameScreen.this);
        waitListDialog = new WaitListDialog(GameScreen.this);
        waitListDialog.setConfirmedWaitListInterface(this);
        promptDialog = new PromptDialog(GameScreen.this);
        promptDialog.setConfirmedWaitListInterface(this);
        promptDialog.setAllRemoved(false);
        joinDialog = new JoinDialog(GameScreen.this);

    }

    private void clickListener() {
        binding.addWaitListBtn.setOnClickListener((v) -> {
            isAllFabsVisible = true;
            binding.fButtons.fButton1.performClick();
            waitListDialog.show();
        });
        binding.fButtons.fButton2.setOnClickListener((view -> {
            binding.fButtons.fButton1.performClick();
            joinDialog.show();
        }));
        binding.fButtons.fButton3.setOnClickListener((view -> {
            binding.fButtons.fButton1.performClick();
            attendanceDialog.show();
        }));
        binding.fButtons.fButton4.setOnClickListener((view -> {
            binding.fButtons.fButton1.performClick();
            promptDialog.show();
            promptDialog.setAllRemoved(true);
        }));
        binding.fButtons.fButton5.setOnClickListener(view -> {
            binding.fButtons.fButton1.performClick();
            loadedData();
        });
        binding.fButtons.fButton6.setOnClickListener(view -> {
            binding.fButtons.fButton1.performClick();
            attendanceStatusDialog.show();
        });
        controlOfFabs();
    }


    private void loadedData() {
        mObserver = this::notifyData;
        memberListVM.getListMutableLiveData().observe(GameScreen.this, mObserver);
        waitListDialog.show();
        waitListDialog.removeAllClear();
        memberListVM.removeAll();
        String jsonString = preferences.getString(savedKey, "DEFAULT");
        if (!jsonString.equals("DEFAULT")) {
            List<List<Member>> mList = gson.fromJson(jsonString, type);
            for (List<Member> m : mList) {
                Map<String, Member> memberMap = new TreeMap<>();
                for (int a = 0; a < 4; a++) {
                    Member item = m.get(a);
                    memberMap.put(String.valueOf((a + 1)), item);
                }
                waitListDialog.addItemWithLoadDatas(memberMap);
                memberListVM.addItem(m);
            }
        } else {
            Util.setMsg(GameScreen.this, "저장된 데이터가 없습니다.");
        }
        waitListDialog.dismiss();
    }

    private void controlOfFabs() {
        binding.fButtons.fButton2.hide();
        binding.fButtons.fButton3.hide();
        binding.fButtons.fButton4.hide();
        binding.fButtons.fButton5.hide();
        binding.fButtons.fButton6.hide();
        binding.fButtons.fButton1.setOnClickListener(view -> {
            if (isAllFabsVisible) {
                binding.fButtons.fButton2.hide();
                binding.fButtons.fButton3.hide();
                binding.fButtons.fButton4.hide();
                binding.fButtons.fButton5.hide();
                binding.fButtons.fButton6.hide();
                isAllFabsVisible = false;
            } else {
                binding.fButtons.fButton2.show();
                binding.fButtons.fButton3.show();
                binding.fButtons.fButton4.show();
                binding.fButtons.fButton5.show();
                binding.fButtons.fButton6.show();
                isAllFabsVisible = true;
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (attendanceDialog != null && attendanceDialog.isShowing()) {
            attendanceDialog.dismiss();
            attendanceDialog = null;
        }
        if (waitListDialog != null && waitListDialog.isShowing()) {
            waitListDialog.dismiss();
            waitListDialog = null;
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    @Override
    public void onAddItem(Object data) {
        mObserver = this::notifyData;
        memberListVM.getListMutableLiveData().observe(GameScreen.this, mObserver);
        List<Member> confirmedList = Observable.fromIterable((List<Map<String, Member>>) data)
                .flatMapIterable(Map::values)
                .toList()
                .blockingGet();
        memberListVM.addItem(confirmedList);
    }

    @Override
    public void onChangeItem(int index, Object data) {
        mObserver = this::notifyData;
        memberListVM.getListMutableLiveData().observe(GameScreen.this, mObserver);
        List<Member> confirmedList = Observable.fromIterable((List<Map<String, Member>>) data)
                .flatMapIterable(Map::values)
                .toList()
                .blockingGet();
        memberListVM.changeItem(index, confirmedList);

    }

    @Override
    public void onRemoveItem(int index) {
        mObserver = this::notifyDeleteData;
        memberListVM.getListMutableLiveData().observe(GameScreen.this, mObserver);
        List<Member> copyList = memberListVM.getListMutableLiveData().getValue().get(index);
        Map<String, Member> resultMap = new TreeMap<>();
        assert copyList != null;
        for (int a = 0; a < 4; a++) {
            Member m = copyList.get(a);
            resultMap.put(String.valueOf((a + 1)), m);
        }
        waitListDialog.addRemovedItemWithIndex(index);
        memberListVM.removeItem(index);
    }

    @Override
    public void allRemoveData() {
        mObserver = this::notifyDeleteData;
        memberListVM.getListMutableLiveData().observe(GameScreen.this, mObserver);
        if (memberListVM.getListMutableLiveData().getValue() == null) return;
        waitListDialog.removeAllClear();
        memberListVM.removeAll();
    }
}