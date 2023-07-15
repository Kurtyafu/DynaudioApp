package com.byd.dynaudio_app.http;

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

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {


    /******************************************* 首页 *********************************************/

    /**
     * 首页banner
     */
    @GET("app/home/banner")
    Observable<BaseBean<List<BannerBean>>> getTopBanner();

    /**
     * 演示视频
     */
    @GET("app/home/salesPresentation")
    Observable<BaseBean<List<SalesPresentationBean>>> getSalesPresentation();

    /**
     * 甄选金曲
     */
    @GET("app/home/pick")
    Observable<BaseBean<ModuleBean>> getGoldenSongs();

    /**
     * 有声电台查询
     */
    @GET("app/home/radio")
    Observable<BaseBean<ModuleBean>> getAudioStation(@Query("userId") String userId,
                                                     @Query("equipmentNo") String equipmentNo);

    /**
     * 黑胶专区查询
     */
    @GET("app/home/blackGlue")
    Observable<BaseBean<ModuleBean>> getBlackGlue();

    /**
     * 有声专栏查询
     */
    @GET("app/home/audioColumn")
    Observable<BaseBean<ModuleBean>> getAudioColumn();

    /**
     * 3d沉浸音查询
     */
    @GET("app/home/immersion")
    Observable<BaseBean<ModuleBean>> getImmersion();

    /******************************************* 聚合页 *********************************************/
    /**
     * 黑胶专区聚合页
     */
    @GET("app/detail/blackGlue")
    Observable<BaseBean<ModuleBean>> getBlackGlueAggregate();

    /**
     * 有声专栏聚合页
     */
    @GET("app/detail/audioColumn")
    Observable<BaseBean<ModuleBean>> getAudioColumnAggregate();

    /**
     * 黑胶专区聚合页
     */
    @GET("app/detail/immersion")
    Observable<BaseBean<ModuleBean>> getImmersionAggregate();


    /******************************************* 我的 *********************************************/

    /**
     * 游客登陆
     *
     * @param equipmentNo 设备号
     */
    @GET("app/login/visitorLogin")
    Observable<BaseBean<UserInfoBean>> visitorLogin(@Query("equipmentNo") String equipmentNo);

    /**
     * 获取二维码(微信公众号登录)
     */
    @GET("app/login/getQrcode")
    Observable<BaseBean<QrcodeBean>> getQrCode();

    /**
     * 验证微信用户是否扫码登录成功(轮询请求)
     *
     * @param sceneStr 获取二维码后拿到的东东
     */
    @GET("app/login/checkScanSuccess")
    Observable<BaseBean<UserInfoBean>> checkScanSuccess(@Query("sceneStr") String sceneStr);

    /**
     * 获取用户信息
     *
     * @param userId 用户id
     */
    @GET("app/my/getMyInfo")
    Observable<BaseBean<UserInfoBean>> getUserInfo(@Query("userId") String userId);

    /**
     * 发送验证码
     *
     * @param phone 手机号
     */
    @GET("app/login/sendLoginMsCode")
    Observable<MsCodeBean> sendMsCode(@Query("phone") String phone);


    /**
     * 手机号登录
     *
     * @param body 手机号 + 验证码
     */
    @POST("app/login/mobileLogin")
    Observable<BaseBean<UserInfoBean>> phoneLogin(@Body PhoneLoginBody body);

    /**
     * 修改用户名
     *
     * @param body 用户id + 新的用户名
     */
    @POST("app/my/updateUserInfo")
    Observable<BaseBean> changeName(@Body ChangeNameBody body);

    /**
     * 获取音乐收藏列表
     *
     * @param userId 用户id
     */
    @GET("app/my/getMyMusicCollect")
    Observable<BaseBean<List<MusicListBean>>> getMusicCollect(@Query("userId") String userId);

    /**
     * 获取有声节目收藏列表
     *
     * @param userId 用户id
     */
    @GET("app/my/getMySoundCollect")
    Observable<BaseBean<List<MusicListBean>>> getAudioCollect(@Query("userId") String userId);

    /**
     * 记录收藏
     *
     * @param bodies 用户id + 库id + 库类型
     */
    @POST("app/my/collect")
    Observable<BaseBean> saveCollect(@Body List<SaveOrDeleteBody> bodies);

    /**
     * 删除收藏
     *
     * @param bodies 用户id + 库id + 库类型
     */
    @POST("app/my/deleteMyCollect")
    Observable<BaseBean> deleteCollect(@Body List<SaveOrDeleteBody> bodies);

    /**
     * 获取音乐播放记录
     *
     * @param userId 用户id
     */
    @GET("app/my/getMyMusicRecord")
    Observable<BaseBean<List<MusicListBean>>> getMusicRecord(@Query("userId") String userId);

    /**
     * 获取有声节目播放记录
     *
     * @param userId 用户id
     */
    @GET("app/my/getMySoundRecord")
    Observable<BaseBean<List<MusicListBean>>> getAudioRecord(@Query("userId") String userId);

    /**
     * 记录播放记录
     *
     * @param bodies 用户id + 库id + 库类型
     */
    @POST("app/my/saveRecord")
    Observable<BaseBean> saveRecord(@Body List<SaveOrDeleteBody> bodies);

    /**
     * 删除播放记录
     *
     * @param bodies 用户id + 库id + 库类型
     */
    @POST("app/my/deleteMyRecord")
    Observable<BaseBean> deleteRecord(@Body List<SaveOrDeleteBody> bodies);

    /**
     * 发送验证码(绑定手机号)
     *
     * @param phone 手机号
     */
    @GET("app/my/account/sendMsCode")
    Observable<BaseBean> sendMsCodeForBind(@Query("phone") String phone);

    /**
     * 绑定手机号
     *
     * @param body 验证码 + 手机号 + 用户id
     */
    @POST("app/my/account/bindPhone")
    Observable<BaseBean> bindPhone(@Body BindPhoneBody body);

    /**
     * 解绑手机号
     *
     * @param userId 用户id
     */
    @GET("app/my/account/unbindPhone")
    Observable<BaseBean> unbindPhone(@Query("userId") String userId);

    /**
     * 获取微信绑定二维码
     *
     * @param userId 用户id
     */
    @GET("app/my/account/getBindQrCode")
    Observable<BaseBean<BindQrCodeBean>> getBindQrCode(@Query("userId") String userId);

    /**
     * 验证微信用户是否扫码绑定成功
     *
     * @param sceneStr 场景值
     */
    @GET("app/my/account/checkBindSuccess")
    Observable<BaseBean> checkBindSuccess(@Query("sceneStr") String sceneStr);

    /**
     * 解绑微信
     *
     * @param userId 用户id
     */
    @GET("app/my/account/unbindWx")
    Observable<BaseBean> unbindWx(@Query("userId") String userId);

    /**
     * 注销账号
     *
     * @param body 注销原因 + 备注 + 用户id
     */
    @POST("app/my/account/destroyAccount")
    Observable<BaseBean> destroyAccount(@Body DeleteUserBody body);


    /***************************************** 专辑详情页 ******************************************/

    /**
     * 甄选金曲专辑详情
     *
     * @param userId 用户id
     */
    @GET("app/album/pickMusic")
    Observable<BaseBean<AlbumBean>> goldenSongsMusic(@Query("userId") String userId);

    /**
     * 黑胶专辑详情
     *
     * @param albumId 专辑id
     */
    @GET("app/album/blackGlueMusic")
    Observable<BaseBean<AlbumBean>> blackGlueMusic(@Query("albumId") String albumId,
                                                   @Query("userId") String userId);

    /**
     * 博客类、咨询类专辑详情
     *
     * @param albumId 专辑id
     * @param sort    排序规则 1正序2倒序
     */
    @GET("app/album/podcast")
    Observable<BaseBean<AlbumBean>> blog(@Query("albumId") String albumId, @Query("sort") int sort,
                                         @Query("userId") String userId);

    /**
     * 沉浸音类专辑详情
     *
     * @param albumId 专辑id
     */
    @GET("app/album/immersionMusic")
    Observable<BaseBean<AlbumBean>> immersionMusic(@Query("albumId") String albumId,
                                                   @Query("userId") String userId);

    /*************************************** 静态资源获取 ******************************************/

    /**
     * 静态资源获取（资源图，销售演示音视频等）
     */
    @POST("app/home/staticResource")
    Observable<BaseBean<List<StaticResourceBean>>> staticResource();


    /*************************************** 日志上传 *********************************************/
    @Multipart
    @POST("app/file/fileUpload")
    Observable<BaseBean<String>> fileUpload(@Part MultipartBody.Part file, @Part("file") RequestBody filename);
}
