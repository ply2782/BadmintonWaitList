package com.jc.jcsports.diffUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.jc.jcsports.model.Member;

public class MembersDiffUtil extends DiffUtil.ItemCallback<Member> {


    @Override
    public boolean areItemsTheSame(@NonNull Member oldItem, @NonNull Member newItem) {
        return oldItem.m_Id == newItem.m_Id;
    }

    @Override
    public boolean areContentsTheSame(@NonNull Member oldItem, @NonNull Member newItem) {
        return oldItem.name.equals(newItem.name);
    }
}
