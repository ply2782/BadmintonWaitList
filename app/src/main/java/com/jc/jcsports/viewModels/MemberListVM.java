package com.jc.jcsports.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jc.jcsports.model.Member;

import java.util.ArrayList;
import java.util.List;

public class MemberListVM extends ViewModel {
    private MutableLiveData<List<List<Member>>> mListDatas;
    private List<List<Member>> mListArray;

    public MutableLiveData<List<List<Member>>> getListMutableLiveData() {
        if (mListDatas == null) {
            mListDatas = new MutableLiveData<>();
        }
        if (mListArray == null) {
            mListArray = new ArrayList<>();
        }
        return mListDatas;
    }


    public void addItem(List<Member> mList) {
        mListArray.add(mList);
        mListDatas.postValue(mListArray);
    }

    public void changeItem(int index, List<Member> mList) {
        mListArray.set(index, mList);
        mListDatas.postValue(mListArray);
    }

    public void removeItem(int index) {
        mListArray.remove(index);
        mListDatas.postValue(mListArray);
    }

    public void removeAll(){
        mListArray.clear();
        mListDatas.postValue(mListArray);
    }
}