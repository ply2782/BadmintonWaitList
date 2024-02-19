package com.jc.jcsports.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.jc.jcsports.R;
import com.jc.jcsports.databinding.DialogLoadingBinding;

import java.util.Objects;

public class LoadingDialog extends Dialog {
    private DialogLoadingBinding loadingBinding;

    private static LoadingDialog instance;

    public static synchronized LoadingDialog getInstance(Context context) {
        if (instance == null) {
            instance = new LoadingDialog(context);
        }
        return instance;
    }


    public LoadingDialog(@NonNull Context context) {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        loadingBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_loading, null, false);
        setContentView(loadingBinding.getRoot());
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);
    }

    @Override
    public void show() {
        super.show();

    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
