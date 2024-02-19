package com.jc.jcsports.interfaces;

import com.jc.jcsports.model.Member;

public interface WaitListDialogInterface<T> {
     void waitListInsert(Member member);
     void waitListRemove(Member member);
}
