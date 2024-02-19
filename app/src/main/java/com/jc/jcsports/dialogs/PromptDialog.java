package com.jc.jcsports.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.jc.jcsports.R;
import com.jc.jcsports.databinding.DialogPromptBinding;
import com.jc.jcsports.interfaces.ConfirmedWaitListInterface;
import com.jc.jcsports.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class PromptDialog extends Dialog {

    private DialogPromptBinding binding;
    private Context ctx;
    private ConfirmedWaitListInterface confirmedWaitListInterface;
    private int index = 0;
    private boolean isAllRemoved = false;

    public void setAllRemoved(boolean allRemoved) {
        isAllRemoved = allRemoved;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setConfirmedWaitListInterface(ConfirmedWaitListInterface confirmedWaitListInterface) {
        this.confirmedWaitListInterface = confirmedWaitListInterface;
    }

    public PromptDialog(@NonNull Context context) {
        super(context);
        ctx = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(ctx);
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_prompt, null, false);
        setContentView(binding.getRoot());
        setCancelable(true);
        clickListenerInit();
        try {
            JSONObject object = new JSONObject();
            object.put("width", -2);
            object.put("height", -2);
            Util.dialogLayoutInfoUpdate(ctx, getWindow(), object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void clickListenerInit() {
        binding.closeBtn.setOnClickListener((v) -> dismiss());
        binding.deleteBtn.setOnClickListener((v) -> {
            if (isAllRemoved) {
                confirmedWaitListInterface.allRemoveData();
            } else {
                confirmedWaitListInterface.onRemoveItem(index);
            }
            dismiss();
        });
    }


    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void show() {
        super.show();
    }

}
