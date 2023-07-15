package com.byd.dynaudio_app.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.byd.dynaudio_app.base.BaseRepository;
import com.byd.dynaudio_app.base.BaseViewModel;

public class PlayViewModel extends BaseViewModel<BaseRepository> {
    // 播放类型 ： 音乐类、语言类
//    private MutableLiveData<Integer> playType = new MutableLiveData<>(PLAY_TYPE_MUSIC);



    // 是否有歌词
    private MutableLiveData<Boolean> hasLyrics = new MutableLiveData<>(true);

    @Override
    protected BaseRepository requireRepository() {
        return null;
    }

//    public MutableLiveData<Integer> getPlayType() {
//        return playType;
//    }

    public MutableLiveData<Boolean> getHasLyrics() {
        return hasLyrics;
    }
}
