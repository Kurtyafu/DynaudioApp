package com.byd.dynaudio_app.base;

public class BaseRepository<T extends BaseRepository.BaseCallback> {

    protected T callback;

    public void setCallback(T baseCallback) {
        this.callback = baseCallback;
    }

    public interface BaseCallback {

    }
}
