package com.byd.dynaudio_app.base;

import androidx.annotation.CallSuper;
import androidx.lifecycle.ViewModel;

public abstract class BaseViewModel<K extends BaseRepository> extends ViewModel {
    protected K mRepository;

    public BaseViewModel() {
        init();
    }

    @CallSuper
    protected void init() {
        mRepository = requireRepository();
    }

    /**
     * 返回对应的仓库实例 如: BaseRepository对象
     * return RepositoryFactory.getInstance().getRepository(AccountRepository.class);
     */
    protected abstract K requireRepository();
}
