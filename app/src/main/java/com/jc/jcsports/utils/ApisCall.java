package com.jc.jcsports.utils;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.jc.jcsports.model.Calculate;
import com.jc.jcsports.model.Member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ApisCall {

    private final static String TAG = "ApisCall";

    public static Single<List<Member>> insertMember(Map<String, String> params) {
        return Single.create(emitter -> {
            try {
                RFClient.getServerInterface().insertMember(URLs.insertMembers, params)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.single())
                        .subscribe(new DisposableSingleObserver<>() {
                            @Override
                            public void onSuccess(@NonNull List<Member> isSuccessed) {
                                Log.d(TAG, "[insertMembers success]");
                                emitter.onSuccess(isSuccessed); // Notify the completion of the asynchronous operation
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                Log.d(TAG, "[insertMembers error] => " + e.getMessage());
                                emitter.onError(e);
                            }
                        });
            } catch (Exception e) {
                Log.d(TAG, "[insertMembers error] => " + e.getMessage());
                emitter.onError(e);
            }
        });
    }


    public static Single<List<String>> getAreas() {
        return Single.create(emitter -> {
            try {
                RFClient.getServerInterface().getAreaas(URLs.getAreas)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.single())
                        .subscribe(new DisposableSingleObserver<>() {
                            @Override
                            public void onSuccess(@NonNull List<String> areasItem) {
                                Log.d(TAG, "[getAreas success]");
                                emitter.onSuccess(areasItem); // Notify the completion of the asynchronous operation
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                Log.d(TAG, "[getAreas error] => " + e.getMessage());
                                emitter.onError(e);
                            }
                        });
            } catch (Exception e) {
                Log.d(TAG, "[getAreas error] => " + e.getMessage());
                emitter.onError(e);
            }
        });
    }


    public static void getAllMembers(MutableLiveData<List<Member>> mListDatas) {
        try {
            Single<List<Member>> getMembers = RFClient.getServerInterface().getMembers(URLs.getAllPerson);
            getMembers.subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.single())
                    .subscribe(new DisposableSingleObserver<>() {
                        @Override
                        public void onSuccess(@NonNull List<Member> members) {
                            Log.d(TAG, "[getAllMembers success]");
                            mListDatas.postValue(members);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.d(TAG, "[getAllMembers error] => " + e.getMessage());
                        }
                    });
        } catch (Exception e) {
            mListDatas.postValue(new ArrayList<>());
            Log.d(TAG, "[getAllMembers error] => " + e.getMessage());
        }
    }

    public static Single<Map<Integer, Calculate>> calculatedAttendanceList() {
        return Single.create(emitter -> {
            try {
                RFClient.getServerInterface().calculatedAttendanceList(URLs.calculatedAttendanceList)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.single())
                        .subscribe(new DisposableSingleObserver<>() {
                            @Override
                            public void onSuccess(@NonNull Map<Integer, Calculate> isSuccessed) {
                                Log.d(TAG, "[calculatedAttendanceList success]");
                                emitter.onSuccess(isSuccessed); // Notify the completion of the asynchronous operation
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                Log.d(TAG, "[calculatedAttendanceList error] => " + e.getMessage());
                                emitter.onError(e);
                            }
                        });
            } catch (Exception e) {
                Log.d(TAG, "[calculatedAttendanceList error] => " + e.getMessage());
                emitter.onError(e);
            }
        });
    }


    public static void getCurrentInPersonLit(MutableLiveData<List<Member>> mListDatas) {
        try {
            Single<List<Member>> getMembers = RFClient.getServerInterface().getMembers(URLs.getCurrentInPersonLit);
            getMembers.subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.single())
                    .subscribe(new DisposableSingleObserver<>() {
                        @Override
                        public void onSuccess(@NonNull List<Member> members) {
                            Log.d(TAG, "[getCurrentInPersonLit success]");
                            mListDatas.postValue(members);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.d(TAG, "[getCurrentInPersonLit error] => " + e.getMessage());
                        }
                    });
        } catch (Exception e) {
            mListDatas.postValue(new ArrayList<>());
            Log.d(TAG, "[getCurrentInPersonLit error] => " + e.getMessage());
        }

    }


    public static void attendanceCheck(int m_Id) {
        try {
            Map<String, Object> params = new TreeMap<>();
            params.put("identity", String.valueOf(m_Id));
            Single<Member> attendanceMember = RFClient.getServerInterface().attendanceChk(URLs.attendanceChk, params);
            attendanceMember.subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.single())
                    .subscribe(new DisposableSingleObserver<>() {
                        @Override
                        public void onSuccess(@NonNull Member members) {
                            Log.d(TAG, "[attendanceCheck success]");
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            Log.d(TAG, "[attendanceCheck error] => " + e.getMessage());
                        }
                    });
        } catch (Exception e) {
            Log.d(TAG, "[attendanceCheck error] => " + e.getMessage());
        }
    }


}
