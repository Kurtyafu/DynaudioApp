package com.byd.dynaudio_app.manager;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.byd.dynaudio_app.bean.response.BaseBean;
import com.byd.dynaudio_app.bean.response.SalesPresentationBean;
import com.byd.dynaudio_app.bean.response.StaticResourceBean;
import com.byd.dynaudio_app.http.ApiClient;
import com.byd.dynaudio_app.http.BaseObserver;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.util.List;

/**
 * 销售演示的数据 提前获取
 * 后续考虑是否需要进入应用就开始下载视频
 */
public class SalesPresentationManager {
    private static SalesPresentationManager instance;
    private SalesPresentationBean mainBean;
    private SalesPresentationBean leftBean;
    private SalesPresentationBean rightBean;

    /**
     * path
     */
    private String thunderstormPath;
    private String symphonyPath;
    private String helicopterPath;


    /**
     * url
     */
    private String thunderstormUrl;

    private String symphonyUrl;
    private String helicopterUrl;
    private FileLoadListener loadListener;

    public void setLoadListener(FileLoadListener loadListener) {
        this.loadListener = loadListener;
    }

    private SalesPresentationManager() {
    }

    public static SalesPresentationManager getInstance() {
        if (instance == null) instance = new SalesPresentationManager();
        return instance;
    }

    private Context mContext;

    public void init(@NonNull Context context) {
        mContext = context;
        String sdCardPath = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/video/";

        thunderstormPath = sdCardPath + "thunderstorm.mp4";// 雷雨
//        thunderstormStereoPath = sdCardPath + "thunderstormStereo.mp4";// 雷雨(立体声)
        symphonyPath = sdCardPath + "symphony.mp4";// 交响乐
//        symphonyStereoPath     = sdCardPath + "symphonyStereo.mp4";// 交响乐(立体声)
        helicopterPath = sdCardPath + "helicopter.mp4";// 直升机

//        helicopterStereoPath   = sdCardPath + "helicopterStereo.mp4";// 直升机(立体声)
    }

    public void requestData() {
        ApiClient.getInstance().staticResource().subscribe(new BaseObserver<>() {
            @Override
            protected void onSuccess(BaseBean<List<StaticResourceBean>> listBaseBean) {
                List<StaticResourceBean> data = listBaseBean.getData();
                if (data == null || data.size() == 0) {
                    return;
                }
                for (StaticResourceBean source : data) {
                    switch (source.getResourceCode()) {
                        case "3DDemo_1_thunderstorm":
                            thunderstormUrl = source.getResourceUrl();
                            break;
                        case "3DDemo_1_thunderstorm_stereo":
//                            thunderstormStereoUrl = source.getResourceUrl();
                            break;
                        case "3DDemo_2_symphony":
                            symphonyUrl = source.getResourceUrl();
                            break;
                        case "3DDemo_2_symphony_stereo":
//                            symphonyStereoUrl = source.getResourceUrl();
                            break;
                        case "3DDemo_3_helicopter":
                            helicopterUrl = source.getResourceUrl();
                            break;
                        case "3DDemo_3_helicopter_stereo":
//                            helicopterStereoUrl = source.getResourceUrl();
                            break;
                    }
                }
                downLoadVideo();
            }

            @Override
            protected void onFail(Throwable e) {

            }
        });
    }

    private void downLoadVideo() {
        FileDownloader.getImpl().create(thunderstormUrl).setPath(thunderstormPath).setTag(0).setListener(downloadListener).start();
//        FileDownloader.getImpl().create(thunderstormStereoUrl).setPath(thunderstormStereoPath).setListener(downloadListener).start();
        FileDownloader.getImpl().create(symphonyUrl).setPath(symphonyPath).setTag(1).setListener(downloadListener).start();
//        FileDownloader.getImpl().create(symphonyStereoUrl).setPath(symphonyStereoPath).setListener(downloadListener).start();
        FileDownloader.getImpl().create(helicopterUrl).setPath(helicopterPath).setTag(2).setListener(downloadListener).start();
//        FileDownloader.getImpl().create(helicopterStereoUrl).setPath(helicopterStereoPath).setListener(downloadListener).start();
    }

    public SalesPresentationBean getMainBean() {
        return mainBean;
    }

    public SalesPresentationBean getLeftBean() {
        return leftBean;
    }

    public SalesPresentationBean getRightBean() {
        return rightBean;
    }

    FileDownloadListener downloadListener = new FileDownloadListener() {
        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            Log.e("FileDownloader", "pending >>> ");
        }

        @Override
        protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
            Log.e("FileDownloader", "connected >>> ");
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            Log.e("FileDownloader", "当前字节 >>> " + soFarBytes);
            Log.e("FileDownloader", "总字节数 >>> " + totalBytes);
            if (loadListener != null) {
                float progress = (float) soFarBytes / totalBytes * 100;
                loadListener.progress(task, (int) progress);
            }
        }

        @Override
        protected void blockComplete(BaseDownloadTask task) {
            Log.e("FileDownloader", "blockComplete >>> ");
        }

        @Override
        protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
            Log.e("FileDownloader", "retry >>> ");
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            Log.e("FileDownloader", "下载完成 >>> " + task.getFilename());
            if (loadListener != null) {
                loadListener.completed(task);
            }
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            Log.e("FileDownloader", "paused >>> ");
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            Log.e("FileDownloader", "error >>> " + e.toString());
        }

        @Override
        protected void warn(BaseDownloadTask task) {
            Log.e("FileDownloader", "warn >>> ");
        }
    };

    public interface FileLoadListener {
        void progress(BaseDownloadTask task, int progress);

        void completed(BaseDownloadTask task);
    }
}
