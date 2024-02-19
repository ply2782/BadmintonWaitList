package com.jc.jcsports.interfaces;

public interface ConfirmedWaitListInterface<T> {
    void onAddItem(T data);

    void onChangeItem(int index, T data);

    void onRemoveItem(int index);

    void allRemoveData();
}
