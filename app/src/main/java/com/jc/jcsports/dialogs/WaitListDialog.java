package com.jc.jcsports.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.jc.jcsports.R;
import com.jc.jcsports.adapter.WaitListAdapter;
import com.jc.jcsports.databinding.DialogWaitlistBinding;
import com.jc.jcsports.diffUtils.MembersDiffUtil;
import com.jc.jcsports.interfaces.ConfirmedWaitListInterface;
import com.jc.jcsports.interfaces.WaitListDialogInterface;
import com.jc.jcsports.model.CalculateType;
import com.jc.jcsports.model.Member;
import com.jc.jcsports.model.MoveDirectory;
import com.jc.jcsports.utils.ApisCall;
import com.jc.jcsports.utils.Util;
import com.jc.jcsports.viewModels.MembersVM;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

public class WaitListDialog extends Dialog implements WaitListDialogInterface {

    private ConfirmedWaitListInterface confirmedWaitListInterface;
    private DialogWaitlistBinding binding;
    private Context ctx;
    private final String TAG = "WaitListDialog";
    private Observer<List<Member>> mObserver;
    private WaitListAdapter waitListRVAdapter;
    private MembersVM membersVM;
    private RecyclerView waitListRV;
    private Map<String, Member> waitListMap;
    private List<Map<String, Member>> confirmedListMember, paramsConfirmedListMember;
    private List<Member> mCopyList;

    public void setItemIndex(int itemIndex) {
        if (confirmedListMember.size() < itemIndex) return;
        Map<String, Member> listMap = confirmedListMember.get(itemIndex);
        for (Map.Entry<String, Member> entry : listMap.entrySet()) {
            String key = entry.getKey();
            Member val = entry.getValue();
            waitListMap.put(key, val);
            int textViewId = ctx.getResources().getIdentifier("waitList_" + key, "id", ctx.getPackageName());
            TextView textView = findViewById(textViewId);
            textView.setText(val.name != null ? val.name : ctx.getString(R.string.notConfirm));
            View.OnClickListener clickListener = view -> {
                if (!((TextView) view).getText().equals(ctx.getString(R.string.notConfirm)) && !((TextView) view).getText().equals("GUEST"))
                    notifyData(mCopyList, CalculateType.ADD, val, 0);
                ((TextView) view).setText(ctx.getString(R.string.notConfirm));
                waitListMap.put(key, new Member());
            };
            textView.setOnClickListener(clickListener);
        }
        changeItemClickListener(itemIndex, waitListMap);
    }

    public WaitListDialog(@NonNull Context context) {
        super(context);
        ctx = context;
    }

    public void setConfirmedWaitListInterface(ConfirmedWaitListInterface confirmedWaitListInterface) {
        this.confirmedWaitListInterface = confirmedWaitListInterface;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(ctx);
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_waitlist, null, false);
        setContentView(binding.getRoot());
        setCancelable(true);
        init();
        try {
            JSONObject object = new JSONObject();
            object.put("width", -1);
            object.put("height", -1);
            Util.dialogLayoutInfoUpdate(ctx, getWindow(), object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void waitListInit() {
        waitListMap = new TreeMap<>();
        waitListMap.put("1", new Member());
        waitListMap.put("2", new Member());
        waitListMap.put("3", new Member());
        waitListMap.put("4", new Member());
        addItemClickListener();
    }

    private void init() {
        confirmedListMember = new ArrayList<>();
        membersVM = new ViewModelProvider((ViewModelStoreOwner) ctx).get(MembersVM.class);
        mObserver = this::confirmMemberFilter;
        waitListRV = binding.waitListRV;
        waitListRV.addItemDecoration(new Util.RecyclerViewDecoration(Util.getPxFromDp(ctx, 20)));
        waitListRV.setHasFixedSize(true);
        waitListRVAdapter = new WaitListAdapter(new MembersDiffUtil(), ctx);
        waitListRVAdapter.setDirection(MoveDirectory.WAITLISTDIALOG);
        waitListRVAdapter.setHasStableIds(true);
        waitListRVAdapter.setInterfaces(this);
        waitListRV.setAdapter(waitListRVAdapter);
        membersVM.getListMutableLiveData().observe((LifecycleOwner) ctx, mObserver);
    }


    @Override
    public void dismiss() {
        super.dismiss();
        waitListInit();
        waitListTVInit();
    }

    private void waitListTVInit() {
        binding.waitList1.setText(ctx.getString(R.string.notConfirm));
        binding.waitList1.setOnClickListener(null);
        binding.waitList2.setText(ctx.getString(R.string.notConfirm));
        binding.waitList2.setOnClickListener(null);
        binding.waitList3.setText(ctx.getString(R.string.notConfirm));
        binding.waitList3.setOnClickListener(null);
        binding.waitList4.setText(ctx.getString(R.string.notConfirm));
        binding.waitList4.setOnClickListener(null);
    }

    @Override
    public void show() {
        super.show();
        waitListInit();
        membersVM.refreshData(MoveDirectory.WAITLISTDIALOG);
    }


    private void changeItemClickListener(int index, Map<String, Member> params) {
        binding.confirmBtn.setOnClickListener((v) -> {
            confirmedListMember.set(index, params);
            paramsConfirmedListMember = new ArrayList<>();
            paramsConfirmedListMember.add(params);
            confirmedWaitListInterface.onChangeItem(index, paramsConfirmedListMember);
            dismiss();
        });
    }

    private void addItemClickListener() {
        binding.closeDialogBtn.setOnClickListener((v) -> dismiss());
        binding.confirmBtn.setOnClickListener((v) -> {
            confirmedListMember.add(waitListMap);
            paramsConfirmedListMember = new ArrayList<>();
            paramsConfirmedListMember.add(waitListMap);
            confirmedWaitListInterface.onAddItem(paramsConfirmedListMember);
            dismiss();
        });
    }


    public void removeAllClear() {
        confirmedListMember.clear();
    }

    public void addRemovedItemWithIndex(int index) {
        confirmedListMember.remove(index);
    }

    public void addItemWithLoadDatas(Map<String, Member> loadData) {
        confirmedListMember.add(loadData);
    }

    @Override
    public void waitListInsert(Member member) {
        for (Map.Entry<String, Member> entry : waitListMap.entrySet()) {
            int index = mCopyList.indexOf(member);
            if (entry.getValue().name == null) {
                int textViewId = ctx.getResources().getIdentifier("waitList_" + entry.getKey(), "id", ctx.getPackageName());
                TextView textView = findViewById(textViewId);
                textView.setOnClickListener((v) -> {
                    if (!((TextView) v).getText().equals(ctx.getString(R.string.notConfirm)) && !entry.getValue().name.equals("GUEST")) {
                        notifyData(mCopyList, CalculateType.ADD, entry.getValue(), index);
                    }
                    ((TextView) v).setText(ctx.getString(R.string.notConfirm));
                    entry.setValue(new Member());
                });
                entry.setValue(member);
                textView.setText(member.name);
                if (!member.name.equals("GUEST")) {
                    if (mCopyList.contains(member)) {
                        notifyData(mCopyList, CalculateType.SUBSTRACT, member, -1);
                    }
                }
                break;
            }
        }
    }

    @Override
    public void waitListRemove(Member member) {
        if (mCopyList.contains(member)) {
            notifyData(mCopyList, CalculateType.SUBSTRACT, member, -1);
            mCopyList.remove(member);
        }
        ApisCall.attendanceCheck(member.m_Id);
    }


    private void confirmMemberFilter(List<Member> mListCopy) {
        Member guest = new Member();
        guest.name = "GUEST";
        guest.color = Member.Colors.BLACK;
        guest.currentStatus = Member.CurrentStatus.IN;
        mListCopy.add(guest);
        mCopyList = new ArrayList<>();
        if (confirmedListMember.size() > 0) {
            List<Member> confirmedList = Observable.fromIterable(confirmedListMember)
                    .flatMapIterable(Map::values)
                    .filter(m -> m.name != null && !m.name.equals("GUEST"))
                    .toList()
                    .blockingGet();
            Observable<Member> originalMemberList = Observable.fromIterable(mListCopy);
            Disposable d = originalMemberList.filter(v -> !confirmedList.contains(v))
                    .toList()
                    .map(list -> {
                        Collections.sort(list); // 리스트를 가나다 순으로 정렬
                        return list;
                    })
                    .subscribe(v -> {
                        notifyData(v, CalculateType.NONE, null, -1);
                        mCopyList.addAll(v);
                    });
            d.dispose();
        } else {
            notifyData(mListCopy, CalculateType.NONE, null, -1);
            Observable<Member> originalMemberList = Observable.fromIterable(mListCopy);
            Disposable d = originalMemberList
                    .toList()
                    .map(list -> {
                        Collections.sort(list); // 리스트를 가나다 순으로 정렬
                        return list;
                    })
                    .subscribe(v -> {
                        notifyData(v, CalculateType.NONE, null, -1);
                        mCopyList.addAll(v);
                    });
            d.dispose();
        }
    }

    private void notifyData(List<Member> mList, CalculateType type, Member member, int index) {
        waitListRVAdapter.notifyItemRangeRemoved(0, mList.size());
        if (type == CalculateType.ADD) {
            if (member.name != null)
                mCopyList.add(mCopyList.size() < index ? mCopyList.size() - 1 : index, member);
        } else if (type == CalculateType.SUBSTRACT) {
            if (member.name != null) mCopyList.remove(member);
        }
        waitListRVAdapter.submitList(mList);
        waitListRVAdapter.notifyItemRangeInserted(0, mList.size());
    }
}
