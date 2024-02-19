package com.jc.jcsports.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.jc.jcsports.R;
import com.jc.jcsports.adapter.WaitListAdapter;
import com.jc.jcsports.databinding.DialogAttendanceBinding;
import com.jc.jcsports.diffUtils.MembersDiffUtil;
import com.jc.jcsports.model.Member;
import com.jc.jcsports.model.MoveDirectory;
import com.jc.jcsports.utils.Util;
import com.jc.jcsports.viewModels.MembersVM;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

public class AttendanceDialog extends Dialog {

    private Context ctx;
    private final String TAG = "AttendanceDialog";
    private Handler handler;
    private DialogAttendanceBinding binding;
    private Observer<List<Member>> mObserver;
    private WaitListAdapter waitListRVAdapter;
    private RecyclerView waitListRV;
    private MembersVM membersVM;
    private final String[] KOREAN_CONSONANTS = {"ㄱ", "ㄲ", "ㄴ", "ㄷ", "ㄸ", "ㄹ", "ㅁ", "ㅂ", "ㅃ", "ㅅ", "ㅆ", "ㅇ", "ㅈ", "ㅉ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ"};


    public AttendanceDialog(@NonNull Context context) {
        super(context);
        handler = new Handler();
        ctx = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(ctx);
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_attendance, null, false);
        setContentView(binding.getRoot());
        setCancelable(true);
        init();
        clickListener();
        try {
            JSONObject object = new JSONObject();
            object.put("width", -1);
            object.put("height", -1);
            Util.dialogLayoutInfoUpdate(ctx, getWindow(), object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void init() {
        membersVM = new ViewModelProvider((ViewModelStoreOwner) ctx).get(MembersVM.class);
        mObserver = members -> {
            areasFilter("양오클럽");
            categoryInit(members);
        };
        waitListRV = binding.waitListRV;
        waitListRV.addItemDecoration(new Util.RecyclerViewDecoration(Util.getPxFromDp(ctx, 20)));
        waitListRV.setHasFixedSize(true);
        waitListRVAdapter = new WaitListAdapter(new MembersDiffUtil(), ctx);
        waitListRVAdapter.setDirection(MoveDirectory.ATTENDANCEDIALOG);
        waitListRVAdapter.setHasStableIds(true);
        waitListRV.setAdapter(waitListRVAdapter);
        membersVM.getListMutableLiveData().observe((LifecycleOwner) ctx, mObserver);

    }


    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void show() {
        super.show();
        membersVM.refreshData(MoveDirectory.ATTENDANCEDIALOG);
    }

    private void clickListener() {
        binding.closeDialogBtn.setOnClickListener((v) -> dismiss());
    }

    private void nameCategoryInit(List<Member> mList, String area) {
        Observable<Member> divideObservable = Observable.fromIterable(mList);
        LinearLayout linearLayout = new LinearLayout(ctx);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(lp);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        binding.nameFilterSV.removeAllViews();
        Disposable disposable;
        disposable = divideObservable
                .filter(v -> v.clubName.equals(area))
                .flatMap(member -> {
                    char choSung = member.name.charAt(0);
                    if (isHangul(choSung)) {
                        return Observable.just(new Pair<>(extractFirstConsonant(choSung), member));
                    } else {
                        return Observable.empty(); // 또는 다른 처리를 수행할 수 있음
                    }
                })
                .distinct(p -> p.first)
                .toSortedList((pair1, pair2) -> pair1.first.compareTo(pair2.first)) // 리스트를 가나다 순으로 정렬
                .subscribe(v -> {
                    handler.post(() -> {
                        for (Pair<String, Member> p : v) {
                            Button areas = new Button(ctx);
                            areas.setText(p.first);
                            areas.setTextColor(Color.BLACK);
                            areas.setTextSize(TypedValue.COMPLEX_UNIT_PX, ctx.getResources().getDimension(R.dimen.sp_10));
                            areas.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            areas.setOnClickListener((view -> {
                                int itemPosition = waitListRVAdapter.getCurrentList().indexOf(p.second);
                                if (itemPosition < 0) return;
                                binding.waitListRV.scrollToPosition(itemPosition);
                            }));
                            linearLayout.addView(areas);
                        }

                    });
                }, throwable -> {
                    Log.d(TAG, throwable.getMessage());
                });
        binding.nameFilterSV.addView(linearLayout);
        disposable.dispose();
    }

    private boolean isHangul(char c) {
        return c >= '가' && c <= '힣';
    }


    private String extractFirstConsonant(char c) {
        char charCode = (char) ((c - 0xAC00) / 28 / 21);
        return KOREAN_CONSONANTS[(int) charCode];
    }

    private void categoryInit(List<Member> mList) {
        Observable<Member> divideObservable = Observable.fromIterable(mList);
        LinearLayout linearLayout = new LinearLayout(ctx);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(lp);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        binding.areaFilterSV.removeAllViews();
        Disposable disposable = divideObservable
                .filter(v -> v.clubName != null)
                .distinct(v -> v.clubName).subscribe(v -> {
            handler.post(() -> {
                Button areas = new Button(ctx);
                areas.setText(v.clubName);
                areas.setTextColor(Color.BLACK);
                areas.setTextSize(TypedValue.COMPLEX_UNIT_PX, ctx.getResources().getDimension(R.dimen.sp_20));
                areas.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                areas.setOnClickListener((btn) -> areasFilter(v.clubName));
                linearLayout.addView(areas);
            });
        });
        binding.areaFilterSV.addView(linearLayout);
        disposable.dispose();
    }

    private void areasFilter(String area) {
        List<Member> mListCopy = membersVM.getListMutableLiveData().getValue();
        if (mListCopy != null) {
            Observable<Member> divideObservable = Observable.fromIterable(mListCopy);
            Disposable disposable = divideObservable
                    .filter(v -> v.clubName != null && v.clubName.equals(area))
                    .toList()
                    .map(list -> {
                        Collections.sort(list); // 리스트를 가나다 순으로 정렬
                        return list;
                    })
                    .subscribe(v -> {
                        waitListRVAdapter.notifyItemRangeRemoved(0, v.size());
                        waitListRVAdapter.submitList(v);
                        waitListRVAdapter.notifyItemRangeInserted(0, v.size());
                        nameCategoryInit(v, area);
                    });
            disposable.dispose();
        }

    }

}
