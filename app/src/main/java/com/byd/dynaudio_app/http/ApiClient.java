package com.byd.dynaudio_app.http;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.byd.dynaudio_app.BuildConfig;
import com.byd.dynaudio_app.bean.MusicListBean;
import com.byd.dynaudio_app.bean.request.BindPhoneBody;
import com.byd.dynaudio_app.bean.request.ChangeNameBody;
import com.byd.dynaudio_app.bean.request.DeleteUserBody;
import com.byd.dynaudio_app.bean.request.PhoneLoginBody;
import com.byd.dynaudio_app.bean.request.SaveOrDeleteBody;
import com.byd.dynaudio_app.bean.response.AlbumBean;
import com.byd.dynaudio_app.bean.response.BannerBean;
import com.byd.dynaudio_app.bean.response.BaseBean;
import com.byd.dynaudio_app.bean.response.BindQrCodeBean;
import com.byd.dynaudio_app.bean.response.ModuleBean;
import com.byd.dynaudio_app.bean.response.MsCodeBean;
import com.byd.dynaudio_app.bean.response.QrcodeBean;
import com.byd.dynaudio_app.bean.response.SalesPresentationBean;
import com.byd.dynaudio_app.bean.response.StaticResourceBean;
import com.byd.dynaudio_app.bean.response.UserInfoBean;
import com.byd.dynaudio_app.custom.CustomToast;
import com.byd.dynaudio_app.utils.LogUtils;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {

    private static final String TEST_URL = "http://123.58.5.243:8000/dynaudio/";

    private static final String BASE_URL = TEST_URL;

    private static volatile ApiClient sInstance;
    private ApiService apiService;

    private ApiClient() {
        String baseUrl;
        String type = BuildConfig.HOST;
        if ("dev".equals(type)) {  // 测试环境
            baseUrl = "http://10.10.6.247:4523/m1/2359530-0-default/";
        } else if ("prod".equals(type)) { // 生产
            baseUrl = "http://app.goerdyna.com/dynaudio/";
        } else {// 预生产
            baseUrl = "http://123.58.5.243:8000/dynaudio/";
        }

        LogUtils.d("当前api url type : " + type + " base url : " + baseUrl);

        // 创建 Retrofit 实例
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(OkHttpConfig.okHttpClient())
                .build();

        // 创建 ApiService 实例
        apiService = retrofit.create(ApiService.class);
    }


    public static ApiClient getInstance() {
        if (sInstance == null) {
            synchronized (ApiClient.class) {
                if (sInstance == null) {
                    sInstance = new ApiClient();
                }
            }
        }
        return sInstance;
    }

    public Observable<BaseBean<List<BannerBean>>> getTopBanner() {
        return apiService.getTopBanner().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean<List<SalesPresentationBean>>> getSalesPresentation() {
        return apiService.getSalesPresentation().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean<ModuleBean>> getGoldenSongs() {
        return apiService.getGoldenSongs().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean<ModuleBean>> getAudioStation() {
        return apiService
//                .getAudioStation(UserController.getInstance().getUserId(), UserController.getInstance().getEquipmentId())
                .getAudioStation("", "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public Observable<BaseBean<ModuleBean>> getBlackGlue() {
        return apiService.getBlackGlue()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean<ModuleBean>> getAudioColumn() {
        return apiService.getAudioColumn()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean<ModuleBean>> getImmersion() {
        return apiService.getImmersion()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean<ModuleBean>> getBlackGlueAggregate() {
        return apiService.getBlackGlueAggregate()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean<ModuleBean>> getAudioColumnAggregate() {
        return apiService.getAudioColumnAggregate()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean<ModuleBean>> getImmersionAggregate() {
        return apiService.getImmersionAggregate()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean<UserInfoBean>> visitorLogin(String equipmentNo) {
        return apiService.visitorLogin(equipmentNo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean<QrcodeBean>> getQrCode() {
        return apiService.getQrCode()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean<UserInfoBean>> checkScanSuccess(String sceneStr) {
        return apiService.checkScanSuccess(sceneStr)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean<UserInfoBean>> getUserInfo(String userId) {
        return apiService.getUserInfo(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MsCodeBean> sendMsCode(String phone) {
        return apiService.sendMsCode(phone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean<UserInfoBean>> phoneLogin(PhoneLoginBody body) {
        return apiService.phoneLogin(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean> changeName(ChangeNameBody body) {
        return apiService.changeName(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean<List<MusicListBean>>> getMusicCollect(String userId) {
        return apiService.getMusicCollect(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean<List<MusicListBean>>> getAudioCollect(String userId) {
        return apiService.getAudioCollect(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean> saveCollect(List<SaveOrDeleteBody> bodies) {
        return apiService.saveCollect(bodies)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean> deleteCollect(List<SaveOrDeleteBody> bodies) {
        return apiService.deleteCollect(bodies)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean<List<MusicListBean>>> getMusicRecord(String userId) {
        return apiService.getMusicRecord(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean<List<MusicListBean>>> getAudioRecord(String userId) {
        return apiService.getAudioRecord(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean> saveRecord(List<SaveOrDeleteBody> bodies) {
        return apiService.saveRecord(bodies)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean> deleteRecord(List<SaveOrDeleteBody> bodies) {
        return apiService.deleteRecord(bodies)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean> sendMsCodeForBind(String phone) {
        return apiService.sendMsCodeForBind(phone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean> bindPhone(BindPhoneBody body) {
        return apiService.bindPhone(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean> unbindPhone(String userId) {
        return apiService.unbindPhone(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean<BindQrCodeBean>> getBindQrCode(String userId) {
        return apiService.getBindQrCode(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean> checkBindSuccess(String sceneStr) {
        return apiService.checkBindSuccess(sceneStr)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean> unbindWx(String userId) {
        return apiService.unbindWx(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean> destroyAccount(DeleteUserBody body) {
        return apiService.destroyAccount(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean<AlbumBean>> goldenSongsMusic(String userId) {
        return apiService.goldenSongsMusic(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean<AlbumBean>> blackGlueMusic(String albumId, String userId) {
        return apiService.blackGlueMusic(albumId, userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean<AlbumBean>> blog(String albumId, int sort, String userId) {
        return apiService.blog(albumId, sort, userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean<AlbumBean>> immersionMusic(String albumId, String userId) {
        return apiService.immersionMusic(albumId, userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseBean<List<StaticResourceBean>>> staticResource() {
        return apiService.staticResource()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public boolean isFileUpLoading = false;

    /**
     * 上传日志文件
     */
    public void fileUpload(@NonNull Context context) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "dynaudio_log.txt");
        // todo 这里日志上传的不对 应该判断这个目录下对应的 dynaudio_log.txt.n 对应的n最大的文件
        if (!file.exists()) {
            CustomToast.makeText(context, "日志文件为空！", Toast.LENGTH_LONG).show();
            return;
        }
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
        isFileUpLoading = true;

        apiService.fileUpload(body, filename)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBean<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseBean<String> s) {
                        CustomToast.makeText(context, "日志上传成功...", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        CustomToast.makeText(context, "日志上传出错...", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        isFileUpLoading = false;
                    }

                    @Override
                    public void onComplete() {
                        CustomToast.makeText(context, "日志上传完成...", Toast.LENGTH_LONG).show();
                        isFileUpLoading = false;
                    }
                });
    }
}
