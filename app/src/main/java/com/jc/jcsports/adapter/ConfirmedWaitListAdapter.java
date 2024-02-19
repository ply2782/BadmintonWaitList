package com.jc.jcsports.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.jc.jcsports.R;
import com.jc.jcsports.databinding.ItemConfirmedWaitlistBinding;
import com.jc.jcsports.dialogs.PromptDialog;
import com.jc.jcsports.dialogs.WaitListDialog;
import com.jc.jcsports.model.Member;

import java.util.List;

public class ConfirmedWaitListAdapter extends ListAdapter<List<Member>, RecyclerView.ViewHolder> {

    private String TAG = "ConfirmedWaitListAdapter";
    private WaitListDialog waitListDialog;
    private PromptDialog promptDialog;

    public void setDialog(WaitListDialog waitListDialog, PromptDialog promptDialog) {
        this.waitListDialog = waitListDialog;
        this.promptDialog = promptDialog;
    }

    public ConfirmedWaitListAdapter(@NonNull DiffUtil.ItemCallback<List<Member>> diffCallback) {
        super(diffCallback);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).toString().hashCode();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemConfirmedWaitlistBinding binding = ItemConfirmedWaitlistBinding.inflate(inflater, parent, false);
        return new ItemConfirmedMemberListVH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemConfirmedMemberListVH) {
            ItemConfirmedMemberListVH itemConfirmedMemberListVH = (ItemConfirmedMemberListVH) holder;
            itemConfirmedMemberListVH.setItem(getItem(position));
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof ItemConfirmedMemberListVH) {
            ItemConfirmedMemberListVH itemMemberVH = (ItemConfirmedMemberListVH) holder;
            itemMemberVH.init();
        }
    }

    public class ItemConfirmedMemberListVH extends RecyclerView.ViewHolder {
        private final ItemConfirmedWaitlistBinding binding;
        private final View.OnClickListener clickListener = view -> {
            if (getAdapterPosition() < 0) return;
            waitListDialog.show();
            waitListDialog.setItemIndex(getAdapterPosition());
        };

        private final View.OnClickListener removeListener = view -> {
            if (getAdapterPosition() < 0) return;
            promptDialog.show();
            promptDialog.setAllRemoved(false);
            promptDialog.setIndex(getAdapterPosition());
        };


        public ItemConfirmedMemberListVH(@NonNull ItemConfirmedWaitlistBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            clickListenerInit();
        }

        private void clickListenerInit() {
            binding.confirmedList1.setOnClickListener(clickListener);
            binding.confirmedList2.setOnClickListener(clickListener);
            binding.confirmedList3.setOnClickListener(clickListener);
            binding.confirmedList4.setOnClickListener(clickListener);
            binding.confirmedListCount.setOnClickListener(removeListener);
        }

        public void setItem(List<Member> memberList) {
            binding.setMemberList(memberList);
            binding.setIndex(getAdapterPosition());
            binding.executePendingBindings();
        }


        public void init() {
            if (getAdapterPosition() < 0) return;
            List<Member> mList = getItem(getAdapterPosition());
            binding.confirmedList1.setText(mList.get(0).name != null ? mList.get(0).name : "미정");
            binding.confirmedList1.setTextColor(mList.get(0).gender == null ? Color.parseColor("#FF000000") : mList.get(0).gender.equals("M") ? Color.parseColor("#FF000000") : Color.parseColor("#FF80AB"));
            binding.confirmedList2.setText(mList.get(1).name != null ? mList.get(1).name : "미정");
            binding.confirmedList2.setTextColor(mList.get(1).gender == null ? Color.parseColor("#FF000000") : mList.get(1).gender.equals("M") ? Color.parseColor("#FF000000") : Color.parseColor("#FF80AB"));
            binding.confirmedList3.setText(mList.get(2).name != null ? mList.get(2).name : "미정");
            binding.confirmedList3.setTextColor(mList.get(2).gender == null ? Color.parseColor("#FF000000") : mList.get(2).gender.equals("M") ? Color.parseColor("#FF000000") : Color.parseColor("#FF80AB"));
            binding.confirmedList4.setText(mList.get(3).name != null ? mList.get(3).name : "미정");
            binding.confirmedList4.setTextColor(mList.get(3).gender == null ? Color.parseColor("#FF000000") : mList.get(3).gender.equals("M") ? Color.parseColor("#FF000000") : Color.parseColor("#FF80AB"));
            binding.confirmedListCount.setText(String.format("%d", getAdapterPosition()));
        }
    }

}
