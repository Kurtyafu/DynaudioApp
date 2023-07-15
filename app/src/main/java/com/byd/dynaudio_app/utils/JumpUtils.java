package com.byd.dynaudio_app.utils;

import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.fragment.AlbumDetailsFragment;

public class JumpUtils {
    // 跳转详情
    public static void jumpToDetail(AlbumDetailsFragment.Type blackGlue, int id) {
        LiveDataBus.get().with(LiveDataBusConstants.detail_type).postValue(blackGlue);
        LiveDataBus.get().with(LiveDataBusConstants.current_album_id).postValue(id);
    }
}
