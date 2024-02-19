package com.jc.jcsports.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.jc.jcsports.R;
import com.jc.jcsports.databinding.ItemMemberListBinding;
import com.jc.jcsports.databinding.ItemWaitlistBinding;
import com.jc.jcsports.interfaces.WaitListDialogInterface;
import com.jc.jcsports.model.Member;
import com.jc.jcsports.model.MoveDirectory;
import com.jc.jcsports.utils.ApisCall;

import java.util.List;

public class WaitListAdapter extends ListAdapter<Member, RecyclerView.ViewHolder> {

    private String TAG = "WaitListAdapter";
    private MoveDirectory direction;
    private WaitListDialogInterface waitListDialogInterface;
    private Context ctx;


    public void setInterfaces(WaitListDialogInterface waitListDialogInterface) {
        this.waitListDialogInterface = waitListDialogInterface;
    }

    public void setDirection(MoveDirectory direction) {
        this.direction = direction;
    }

    public WaitListAdapter(@NonNull DiffUtil.ItemCallback<Member> diffCallback, Context ctx) {
        super(diffCallback);
        this.ctx = ctx;
    }

    @Override
    public long getItemId(int position) {
        return getItemCount() == 0 ? super.getItemId(position) : getItem(position).m_Id;
    }


    @Override
    public int getItemViewType(int position) {
        if (direction.getLabel().equals(MoveDirectory.ATTENDANCEDIALOG.getLabel())) {
            return R.layout.item_member_list;
        } else {
            return R.layout.item_waitlist;
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof ItemMemberVH) {
            ItemMemberVH itemMemberVH = (ItemMemberVH) holder;
            itemMemberVH.init();
        } else if (holder instanceof ItemWaitListMemberVH) {
            ItemWaitListMemberVH itemWaitListMemberVH = (ItemWaitListMemberVH) holder;
            itemWaitListMemberVH.init();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case R.layout.item_member_list:
                ItemMemberListBinding binding = ItemMemberListBinding.inflate(inflater, parent, false);
                return new ItemMemberVH(binding);

            case R.layout.item_waitlist:
                ItemWaitlistBinding itemWaitlistBinding = ItemWaitlistBinding.inflate(inflater, parent, false);
                return new ItemWaitListMemberVH(itemWaitlistBinding);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemMemberVH) {
            ItemMemberVH itemMemberVH = (ItemMemberVH) holder;
            itemMemberVH.setItem(getItem(position));
        } else if (holder instanceof ItemWaitListMemberVH) {
            ItemWaitListMemberVH itemWaitListMemberVH = (ItemWaitListMemberVH) holder;
            itemWaitListMemberVH.setItem(getItem(position));
        }
    }

    public class ItemMemberVH extends RecyclerView.ViewHolder {
        private ItemMemberListBinding binding;
        private final View.OnClickListener onClickListener = view -> {
            getItem(getAdapterPosition()).currentStatus = Member.CurrentStatus.IN;
            binding.checkTv1.setText("출석체크 완료");
            notifyItemRangeChanged(0, getAdapterPosition());
            ApisCall.attendanceCheck(getItem(getAdapterPosition()).m_Id);
        };

        public void init() {
            if (getAdapterPosition() < 0) return;
            Member.CurrentStatus currentStatus = getItem(getAdapterPosition()).currentStatus;
            if (currentStatus == Member.CurrentStatus.OUT) {
                binding.checkTv1.setText("");
            }
            Member.Colors c = getItem(getAdapterPosition()).color;
            binding.nameBtn1.setBackgroundColor(c.getColorInt());
            binding.nameBtn1.setTextColor(c == Member.Colors.BLACK || c == Member.Colors.ORANGE || c == Member.Colors.RED ? Color.parseColor("#FFFFFFFF") : Color.parseColor("#FF000000"));
            Drawable roundedDrawable = ContextCompat.getDrawable(ctx, R.drawable.rounded_button);
            binding.nameBtn1.setBackground(roundedDrawable);
        }

        public ItemMemberVH(@NonNull ItemMemberListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            attendanceClick();
        }

        public void setItem(Member m) {
            binding.setMember(m);
            binding.executePendingBindings();
        }


        private void attendanceClick() {
            binding.nameBtn1.setOnClickListener(onClickListener);
        }
    }

    public class ItemWaitListMemberVH extends RecyclerView.ViewHolder {
        private PopupMenu popupMenu;
        private ItemWaitlistBinding binding;
        private PopupMenu.OnMenuItemClickListener menuItemClickListener = menuItem -> {
            waitListDialogInterface.waitListRemove(getItem(getAdapterPosition()));
            return false;
        };
        private final View.OnClickListener onClickListener = view -> {
            if (getAdapterPosition() == -1) return;
            notifyItemRangeChanged(0, getAdapterPosition());
            waitListDialogInterface.waitListInsert(getItem(getAdapterPosition()));
        };
        private final View.OnLongClickListener longClickListener = view -> {
            if (((TextView) view).getText().equals("GUEST")) return false;
            popupMenu.show();
            return true;
        };


        public void init() {
            if (getAdapterPosition() < 0) return;
            Member.Colors c = getItem(getAdapterPosition()).color;
            binding.nameBtn1.setBackgroundColor(c.getColorInt());
            binding.nameBtn1.setTextColor(c == Member.Colors.BLACK || c == Member.Colors.ORANGE || c == Member.Colors.RED ? Color.parseColor("#FFFFFFFF") : Color.parseColor("#FF000000"));
            Drawable roundedDrawable = ContextCompat.getDrawable(ctx, R.drawable.rounded_button);
            binding.nameBtn1.setBackground(roundedDrawable);
        }

        public ItemWaitListMemberVH(@NonNull ItemWaitlistBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            waitListCheck();
        }

        public void setItem(Member m) {
            binding.setMember(m);
            binding.executePendingBindings();
        }


        private void waitListCheck() {
            binding.nameBtn1.setOnClickListener(onClickListener);
            binding.nameBtn1.setOnLongClickListener(longClickListener);
            popupMenu = new PopupMenu(ctx, binding.nameBtn1);
            popupMenu.inflate(R.menu.menu_wailtlist);
            popupMenu.setOnMenuItemClickListener(menuItemClickListener);
        }
    }
}
