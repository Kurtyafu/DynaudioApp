package com.byd.dynaudio_app.viewmodel;

import com.byd.dynaudio_app.base.BaseViewModel;
import com.byd.dynaudio_app.bean.ResponseData;
import com.byd.dynaudio_app.utils.model.HomeRepository;
import com.byd.dynaudio_app.utils.model.RepositoryFactory;
import com.byd.dynaudio_app.utils.AccountUtils;

import io.reactivex.Observable;

public class HomeViewModel extends BaseViewModel<HomeRepository> {
    @Override
    protected HomeRepository requireRepository() {
        return RepositoryFactory.getInstance().getRepository(HomeRepository.class);
    }

    public Observable<ResponseData> requestHifiInfo() {
        return mRepository.requestHifiInfo(AccountUtils.getUid());
    }
}
