package com.byd.dynaudio_app.utils.model;

import com.byd.dynaudio_app.http.ApiClient;
import com.byd.dynaudio_app.base.BaseRepository;
import com.byd.dynaudio_app.bean.ResponseData;

import io.reactivex.Observable;

public class HomeRepository extends BaseRepository {

    public Observable<ResponseData> requestHifiInfo(long uid) {
        return null;
    }
}
