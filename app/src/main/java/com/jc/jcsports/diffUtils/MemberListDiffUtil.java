package com.jc.jcsports.diffUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.jc.jcsports.model.Member;

import java.util.List;

public class MemberListDiffUtil extends DiffUtil.ItemCallback<List<Member>> {

    @Override
    public boolean areItemsTheSame(@NonNull List<Member> oldItem, @NonNull List<Member> newItem) {
        if (oldItem.size() != newItem.size()) {
            return false;
        } else {
            for (int i = 0; i < oldItem.size(); i++) {
                if (oldItem.get(i).m_Id != newItem.get(i).m_Id) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean areContentsTheSame(@NonNull List<Member> oldItem, @NonNull List<Member> newItem) {
        if (oldItem.size() != newItem.size()) {
            return false;
        } else {
            for (int i = 0; i < oldItem.size(); i++) {
                if (!oldItem.get(i).name.equals(newItem.get(i).name)) {
                    return false;
                }
            }
        }
        return true;
    }
}
