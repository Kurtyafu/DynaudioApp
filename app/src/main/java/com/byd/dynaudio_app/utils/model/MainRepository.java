package com.byd.dynaudio_app.utils.model;

import androidx.annotation.NonNull;

import com.byd.dynaudio_app.base.BaseRepository;

import java.util.List;

public class MainRepository extends BaseRepository<MainRepository.OnRequestBaseCallback> {

    public void getGoldenSongs(){
    }

    /**
     * 请求数据
     */
    public void requestData() {
        // 这里延时操作来模拟数据返回
        new Thread(() -> {
            // 这里模拟请求数据返回
//            List<String> data = new ArrayList<String>() {{
//                add("张三");
//                add("李四");
//                add("王五");
//            }};
//
//            if (callback != null) {
//                callback.onUserCallback(data);
//            }
        }).start();
    }

    public interface OnRequestBaseCallback extends BaseCallback {
        void onUserCallback(@NonNull List<String> data);
    }
}
