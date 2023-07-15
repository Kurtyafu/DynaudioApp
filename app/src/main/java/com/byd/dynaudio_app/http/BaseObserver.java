package com.byd.dynaudio_app.http;

import io.reactivex.observers.DisposableObserver;

public abstract class BaseObserver<T> extends DisposableObserver<T> {

    @Override
    public void onNext(T t) {
        onSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        onFail(e);
    }

    @Override
    public void onComplete() {
        // 空实现，子类不重写
    }

    protected abstract void onSuccess(T t);

    protected abstract void onFail(Throwable e);
}