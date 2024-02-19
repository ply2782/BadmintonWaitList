package com.jc.jcsports.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jc.jcsports.model.Member;
import com.jc.jcsports.model.MoveDirectory;
import com.jc.jcsports.utils.ApisCall;

import java.util.List;

public class MembersVM extends ViewModel {
    private MutableLiveData<List<Member>> mListDatas;
    private String TAG = "MembersVM";

    public MutableLiveData<List<Member>> getListMutableLiveData() {
        if (mListDatas == null) {
            mListDatas = new MutableLiveData<>();
        }
        return mListDatas;
    }

    public void refreshData(MoveDirectory moveDirectory) {
        if (moveDirectory.getLabel().equals(MoveDirectory.ATTENDANCEDIALOG.getLabel())) {
            ApisCall.getAllMembers(mListDatas);
        } else {
            ApisCall.getCurrentInPersonLit(mListDatas);
        }
    }
}
