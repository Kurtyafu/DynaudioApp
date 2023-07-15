package com.byd.dynaudio_app.adapter;

public interface IItemOnClickListener {
    void onClick(int position);

    void onSelect(int position, boolean isSelect);

    void onDeleteClick(int position);
}
