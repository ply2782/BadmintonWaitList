package com.jc.jcsports.viewModels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
public class ViewModelFactory implements ViewModelProvider.Factory {


    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {

        if (modelClass.isAssignableFrom(MembersVM.class)) {
            return (T) new MembersVM();
        }else if (modelClass.isAssignableFrom(MemberListVM.class)) {
            return (T) new MemberListVM();
        }

        throw new IllegalArgumentException("Not found ViewModel class.");
    }
}

