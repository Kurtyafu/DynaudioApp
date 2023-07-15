package com.byd.dynaudio_app.utils.model;

import androidx.annotation.NonNull;

import com.byd.dynaudio_app.base.BaseRepository;

import java.util.HashMap;

public class RepositoryFactory {
    private static RepositoryFactory sInstance;

    private RepositoryFactory() {
    }

    @NonNull
    public static RepositoryFactory getInstance() {
        if (sInstance == null) {
            sInstance = new RepositoryFactory();
        }
        return sInstance;
    }

    private HashMap<String, BaseRepository> mRepositoryMap = new HashMap<>();

    /**
     * 获取对应的repository对象 如果有就用之前的 没有就创一个新的
     */
    public <T extends BaseRepository> T getRepository(@NonNull Class<T> repositoryClass) {
        String canonicalName = repositoryClass.getCanonicalName();
        if (mRepositoryMap.containsKey(canonicalName)) {
            return (T) mRepositoryMap.get(canonicalName);
        } else {
            BaseRepository repository = create(repositoryClass);
            mRepositoryMap.put(canonicalName, repository);
            return (T) repository;
        }
    }

    /**
     * 使用默认构造创建一个新的
     */
    @NonNull
    private <T extends BaseRepository> T create(@NonNull Class<T> repositoryClass) {
        try {
            return repositoryClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Cannot create an instance of " + repositoryClass, e);
        }
    }
}
