package com.byd.dynaudio_app.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.byd.dynaudio_app.base.BaseViewModel;
import com.byd.dynaudio_app.utils.model.MainRepository;
import com.byd.dynaudio_app.utils.model.RepositoryFactory;

import java.util.List;

public class MainViewModel extends BaseViewModel<MainRepository> {

    private MutableLiveData<List<String>> userList = new MutableLiveData<>();

    public MutableLiveData<List<String>> getUserList() {
        return userList;
    }

    @Override
    protected void init() {
        super.init();

//        mRepository.setCallback(data -> {
//            userList.setValue(data);
//        });
    }

    /**
     * 请求数据
     */
    public void requestData(){
        mRepository.requestData();
    }

    @Override
    protected MainRepository requireRepository() {
        return RepositoryFactory.getInstance().getRepository(MainRepository.class);
    }
}
