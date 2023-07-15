package com.byd.dynaudio_app;

import static android.view.KeyEvent.KEYCODE_BACK;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.byd.dynaudio_app.base.BaseActivity;
import com.byd.dynaudio_app.base.BaseFragment;
import com.byd.dynaudio_app.base.BaseViewModel;
import com.byd.dynaudio_app.bean.response.BaseBean;
import com.byd.dynaudio_app.bean.response.UserInfoBean;
import com.byd.dynaudio_app.car.CarManager;
import com.byd.dynaudio_app.custom.CustomToast;
import com.byd.dynaudio_app.custom.xpop.MiniPlayerPopupView;
import com.byd.dynaudio_app.database.DBHelper;
import com.byd.dynaudio_app.databinding.ActivityMainBinding;
import com.byd.dynaudio_app.dialog.CustomDialog;
import com.byd.dynaudio_app.dialog.IOnClickListener;
import com.byd.dynaudio_app.fragment.AlbumDetailsFragment;
import com.byd.dynaudio_app.fragment.BaseExperienceFragment;
import com.byd.dynaudio_app.fragment.GuideFragment;
import com.byd.dynaudio_app.fragment.Immersion3dFragment;
import com.byd.dynaudio_app.fragment.MyCollectFragment;
import com.byd.dynaudio_app.fragment.NewMainFragment;
import com.byd.dynaudio_app.fragment.PlayRecordFragment;
import com.byd.dynaudio_app.fragment.PolicyFragment;
import com.byd.dynaudio_app.fragment.SalesPresentationFragment;
import com.byd.dynaudio_app.fragment.SoundSettingsFragment;
import com.byd.dynaudio_app.http.ApiClient;
import com.byd.dynaudio_app.http.BaseObserver;
import com.byd.dynaudio_app.manager.MusicPlayManager;
import com.byd.dynaudio_app.manager.PhoneManager;
import com.byd.dynaudio_app.manager.PlayerVisionManager;
import com.byd.dynaudio_app.manager.VideoPlayerManager;
import com.byd.dynaudio_app.network.NetworkObserver;
import com.byd.dynaudio_app.permission.UsesPermission;
import com.byd.dynaudio_app.permission.com_hjq_permissions.Permission;
import com.byd.dynaudio_app.user.UserController;
import com.byd.dynaudio_app.utils.DensityUtils;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.LogUtils;
import com.byd.dynaudio_app.utils.SPUtils;
import com.byd.dynaudio_app.utils.SharedPreferencesUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 入口 用于作为多fragment的容器
 */
public class MainActivity extends BaseActivity<ActivityMainBinding, BaseViewModel> {

    private FragmentManager fragmentManager;
    private AlbumDetailsFragment.Type detailType;
    private MediaSessionCompat mediaSession;

    private volatile boolean isStop;

    Runnable runnable = () -> {
        if (isStop) {
            return;
        }
        mDataBinding.llSplash.setVisibility(View.GONE);
    };
    private CustomDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.LOG_PATH = getFilesDir().getAbsolutePath();
        // 检查登录状态 获取token
        checkLoginStatus();

        super.onCreate(savedInstanceState);

        new Handler().postDelayed(runnable, 2000);

        checkPermission();
        CarManager.getInstance().init(this);
        initPlayer();
        initDbHelper();

        // 获取ip地址显示在界面上 方便调试使用
        setIp();
        initMediaBtn();

        // 设置应用内dpi 减少ui适配
        if (!SPUtils.isPad()) {
            mDataBinding.tvIp.setVisibility(View.GONE);
        }

        String file_path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/video";
        LogUtils.d("file_path : " + file_path);
        LogUtils.LOG_PATH = file_path;
        Deposit(file_path, "SalesDemo.mp4");
        Deposit(file_path, "SalesDemoMute.mp4");


    }

    private void checkLoginStatus() {

        String userId = (String) SPUtils.getValue(this, SPUtils.SP_KEY_USER_ID, "");
        String token = (String) SPUtils.getValue(this, SPUtils.SP_KEY_TOKEN, "");
        LogUtils.d("TOKEN的值" + token);
        if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(token)) {
            UserController.getInstance().setLoginStates(true);
            UserController.getInstance().setUserId(userId);
            UserController.getInstance().setToken(token);
        } else if (!TextUtils.isEmpty(token)) {
            UserController.getInstance().setToken(token);
        } else {
            // 游客登录
            String equipmentId = UserController.getInstance().getEquipmentId();// 设备号
            ApiClient.getInstance().visitorLogin(equipmentId).subscribe(new BaseObserver<>() {
                @Override
                protected void onSuccess(BaseBean<UserInfoBean> bean) {
                    if (bean.isSuccess()) {
                        UserInfoBean data = bean.getData();
                        UserController.getInstance().setToken(data.getToken());
                        SPUtils.putValue(MainActivity.this, SPUtils.SP_KEY_TOKEN, data.getToken());
                    }
                }

                @Override
                protected void onFail(Throwable e) {
                    // 无网状态下使用的默认token,只能获取首页数据
                    UserController.getInstance().setToken("lLLJqH1aaYGQCP8BS4rk");
                    SPUtils.putValue(MainActivity.this, SPUtils.SP_KEY_TOKEN, "lLLJqH1aaYGQCP8BS4rk");
                }
            });
        }
    }


    /**
     * 初始化后台广播 用以监听应用后台时候的下一曲上一曲 播放的键值
     */
    private void initMediaBtn() {
        mediaSession = new MediaSessionCompat(this, "MediaSession");
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
                String intentAction = mediaButtonEvent.getAction();
                if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
                    KeyEvent event = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                    if (event != null && event.getAction() == KeyEvent.ACTION_DOWN) {
                        LogUtils.d("bg key : " + event.getKeyCode());
                        switch (event.getKeyCode()) {
                            case KeyEvent.KEYCODE_MEDIA_NEXT:
                                MusicPlayManager.getInstance().playNext();// 下一首
                                MusicPlayManager.getInstance().playMusic();
                                return true;
                            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                                MusicPlayManager.getInstance().playPrevious();// 上一首
                                MusicPlayManager.getInstance().playMusic();
                                return true;
                            case KeyEvent.KEYCODE_MEDIA_PLAY:
                            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                                boolean playing = MusicPlayManager.getInstance().isPlaying();
                                if (playing) {
                                    MusicPlayManager.getInstance().pauseMusic();
                                } else {
                                    MusicPlayManager.getInstance().playMusic();
                                }
                                return true;
                        }
                    }
                }

                return super.onMediaButtonEvent(mediaButtonEvent);
            }
        });

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(focusChange -> {
            if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                mediaSession.setActive(true);
                MediaControllerCompat mediaController = new MediaControllerCompat(MainActivity.this,
                        mediaSession.getSessionToken());
                MediaControllerCompat.setMediaController(MainActivity.this, mediaController);
            }
        }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }


    //将assets内文件存储到本地下载目录
    @Override
    protected void initView() {
        mDataBinding.getRoot().postDelayed(() -> {
            LogUtils.d("屏幕的宽度为(dp)：" + DensityUtils.px2Dp(MainActivity.this, getWindow().getDecorView().getWidth()));
            LogUtils.d("屏幕的高度为(dp)：" + DensityUtils.px2Dp(MainActivity.this, mDataBinding.getRoot().getHeight()));
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels;
            LogUtils.d("屏幕的宽度为(px)：" + screenWidth);
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int dpi = metrics.densityDpi;
            LogUtils.d("屏幕的dpi为：" + dpi);
            int currentApiVersion = android.os.Build.VERSION.SDK_INT;
            LogUtils.d("Android Version" + "Current Android version: " + currentApiVersion);

        }, 100);
    }

    /**
     * 隐藏状态栏
     */
    private void hideStatusBar() {
        // 这个会快速的隐藏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void showStatusBar() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    public Boolean Deposit(String path, String fileName) {
        InputStream inputStream;
        try {

            //判断文件是否存在
            File file1 = new File(path + "/" + fileName);

            if (!file1.exists()) {

                inputStream = getAssets().open(fileName);
                File file = new File(path);
                //当目录不存在时创建目录
                if (!file.exists()) {
                    file.mkdirs();
                }

                FileOutputStream fileOutputStream = new FileOutputStream(path + "/" + fileName);// 保存到本地的文件夹下的文件
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = inputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, count);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                inputStream.close();

            } else {
                // Toast.makeText(MainActivity.this, "已存在", Toast.LENGTH_LONG).show();
            }

            return true;


        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void initDbHelper() {
        DBHelper.getInstance(this);
    }

    private void initPlayer() {
        MusicPlayManager.getInstance().init(this);
        PlayerVisionManager.getInstance()
                .init(MainActivity.this)
//                .initMiniPlayer(MainActivity.this)
                .initFullPlayer(MainActivity.this)

                .initPlayList(MainActivity.this);
        VideoPlayerManager.getInstance().init(this);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initObserver() {
        // 接受数据 切换fragment
        LiveDataBus.get().with(LiveDataBusConstants.to_fragment, BaseFragment.class)
                .observe(this, fragment -> toFragment(fragment, Gravity.RIGHT));
        // 返回上一个
        LiveDataBus.get().with(LiveDataBusConstants.go_back).observe(this, o -> {
            if (o != null) goBack();
        });
        // 下面两个配合使用 跳转到详情界面
        LiveDataBus.get().with(LiveDataBusConstants.current_album_id, Integer.class).observe(this,
                id -> {
                    if (id != null) toDetailFragment(id);
                });
        LiveDataBus.get().with(LiveDataBusConstants.detail_type, AlbumDetailsFragment.Type.class)
                .observe(this, type -> {
                    if (type != null) detailType = type;
                });
    }

    /**
     * 跳转到对应的fragment
     */
    private final List<BaseFragment> pageStack = new ArrayList<>();
    public BaseFragment currentFragment;

    public void toFragment(@NonNull BaseFragment fragment, int gravity) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        fragmentTransaction.addToBackStack(null);
        setTransition(fragmentTransaction, gravity);
        if (currentFragment == fragment) {
            return;
        }
        if (fragment instanceof SalesPresentationFragment
                || fragment instanceof BaseExperienceFragment) {
            statusBar.hideStatusBar(true);
        } else {
            statusBar.hideStatusBar(false);
        }


        if (currentFragment == null) {
            fragmentTransaction.add(R.id.fragment_container, fragment).commit();
            currentFragment = fragment;
        }
        if (currentFragment != fragment) {
            // 先判断是否被add过
            if (!fragment.isAdded()) {
                // 隐藏当前的fragment，add下一个到Activity中
                fragmentTransaction.hide(currentFragment)
                        .add(R.id.fragment_container, fragment).commit();
            } else {
                LogUtils.d("进入了隐藏");
                // 隐藏当前的fragment，显示下一个
                fragmentTransaction.hide(currentFragment).show(fragment)
                        .commit();
            }
        }
        currentFragment = fragment;
        pageStack.add(fragment);//进入了下一级界面 将界面添加到任务栈中
    }

    /**
     * 返回上一个fragment
     */
    public void goBack() {
        statusBar.hideStatusBar(false);
        int currentIndex = pageStack.size() - 1;

        if (currentIndex == 0 && currentFragment instanceof PolicyFragment) {// 新手引导 查看协议
            fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.remove(currentFragment).commit();
            pageStack.remove(currentIndex);

            if (dialog != null) {
                dialog.show();
            }
            return;
        }

        if (currentFragment instanceof NewMainFragment) { //Main
            moveTaskToBack(true);
            LogUtils.d("进入了返回");
            return;
        }
        if (currentFragment instanceof SalesPresentationFragment) {
            // 销售演示播放页退出至预览页
            if (((SalesPresentationFragment) currentFragment).isInPlay()) {
                ((SalesPresentationFragment) currentFragment).stopVideo();
                return;
            }
            // 销售演示退回到首页 如果进入销售显示的时候是播放音乐的 此时应该继续播放
            if (((SalesPresentationFragment) currentFragment).isInWithMusicPlay()) {
                mDataBinding.getRoot().postDelayed(() ->
                        MusicPlayManager.getInstance().playMusic(), 300);
                ((SalesPresentationFragment) currentFragment).setInWithMusicPlay(false);
            }
        }
        if (currentFragment instanceof MyCollectFragment) {// 编辑模式
            if (((MyCollectFragment) currentFragment).isEditMode()) {
                ((MyCollectFragment) currentFragment).cancelEditMode();
                return;
            }
        }
        if (currentFragment instanceof PlayRecordFragment) {// 编辑模式
            if (((PlayRecordFragment) currentFragment).isEditMode()) {
                ((PlayRecordFragment) currentFragment).cancelEditMode();
                return;
            }
        }
        if (currentFragment instanceof SoundSettingsFragment) { // 如果来自于全屏播放器跳转设置 需要返回的时候返回全屏播放器
            Boolean value = LiveDataBus.get().with(LiveDataBusConstants.to_soundSettings_from_fullPlayer, Boolean.class).getValue();
            if (value != null && value) {
                PlayerVisionManager.getInstance().showFullPlayer();
                LiveDataBus.get().with(LiveDataBusConstants.to_soundSettings_from_fullPlayer, Boolean.class).postValue(false);

                mDataBinding.getRoot().postDelayed(() -> {
                    goBack();
                }, 300);
                return;
            }
        }
//
        BaseFragment fragment = pageStack.get(currentIndex - 1);//获取上一个Fragment
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        setTransition(fragmentTransaction, Gravity.LEFT);
        if (currentFragment != fragment) {
            if (currentFragment instanceof Immersion3dFragment && fragment instanceof SalesPresentationFragment) {
                fragmentTransaction.remove(currentFragment).show(fragment).commit();
            } else {
                // 先判断是否被add过
                if (!fragment.isAdded()) {
                    // 隐藏当前的fragment，add下一个到Activity中
                    fragmentTransaction.hide(currentFragment)
                            .add(R.id.fragment_container, fragment).commit();
                } else {
                    // 隐藏当前的fragment，显示下一个
                    fragmentTransaction.hide(currentFragment).show(fragment)
                            .commit();
                }
            }
        }
        currentFragment = fragment;
        if (currentFragment instanceof SalesPresentationFragment
                || currentFragment instanceof BaseExperienceFragment) {
            statusBar.hideStatusBar(true);
        } else {
            statusBar.hideStatusBar(false);
        }
        pageStack.remove(currentIndex);
    }

    /**
     * 跳转到详情页
     */
    public void toDetailFragment(int albumId) {
        AlbumDetailsFragment fragment = new AlbumDetailsFragment();
        fragment.setAlbumId(String.valueOf(albumId));
        fragment.setType(detailType);
        toFragment(fragment, Gravity.RIGHT);
    }

    /**
     * 所有的fragment切换效果在这里统一管理
     *
     * @param gravity 切换方向（要打开的fragment所在的方向）
     */
    private void setTransition(@NonNull FragmentTransaction fragmentTransaction, int gravity) {
        // LogUtils.d("to : " + gravity);
        if (gravity == Gravity.RIGHT) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (gravity == Gravity.LEFT) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KEYCODE_BACK) {
            goBack();
            return true;
        }/* else if (keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
            MusicPlayManager.getInstance().playPrevious();// 上一首
            MusicPlayManager.getInstance().playMusic();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_NEXT) {
            MusicPlayManager.getInstance().playNext();// 下一首
            MusicPlayManager.getInstance().playMusic();
            return true;
        }*/
        switch (event.getKeyCode()) {
            case 89:
                CarManager.getInstance().trySendP(true);
                break;
            case 90:
                CarManager.getInstance().trySendP(false);
                break;
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected BaseViewModel getViewModel() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    public static void setSkinMode() {
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    /**
     * 权限相关的都写在这里
     */
    private void checkPermission() {
        //假设需要获取摄像头、录音权限，直接在调用的地方实现抽象类，调用逻辑能简单直观不少

        new UsesPermission(MainActivity.this,
                Permission.READ_EXTERNAL_STORAGE,
                Permission.WRITE_EXTERNAL_STORAGE,
                Permission.ACCESS_COARSE_LOCATION,
                Permission.ACCESS_FINE_LOCATION,
                Permission.READ_PHONE_STATE) {
            @Override
            protected void onTrue(@NonNull ArrayList<String> lowerPermissions) {
                //获取了全部权限执后行此函数，
                LogUtils.d("权限全部都成功获取了...");
            }

            @Override
            protected void onFalse(@NonNull ArrayList<String> rejectFinalPermissions,
                                   @NonNull ArrayList<String> rejectPermissions,
                                   @NonNull ArrayList<String> invalidPermissions) {
                //未全部授权时执行此函数
                LogUtils.e("有权限被拒绝了...");
            }

            //要么实现上面两个方法即可，onTrue或onFalse只会有一个会被调用一次
            //要么仅仅实现下面这个方法，不管授权了几个权限都会调用一次

            @Override
            protected void onComplete(@NonNull ArrayList<String> resolvePermissions,
                                      @NonNull ArrayList<String> lowerPermissions,
                                      @NonNull ArrayList<String> rejectFinalPermissions,
                                      @NonNull ArrayList<String> rejectPermissions,
                                      @NonNull ArrayList<String> invalidPermissions) {
                //完成回调，可能全部已授权、全部未授权、或者部分已授权
                //通过resolvePermissions.contains(Permission.XXX)来判断权限是否已授权
                LogUtils.e("下列权限被拒绝了>>>>>>>>>>>>>>>>>>>>>");
                for (String name : rejectPermissions) {
                    LogUtils.e("permission : " + name);
                }

                if (resolvePermissions.contains(Permission.READ_PHONE_STATE)) {
                    // 同意了电话权限再开始初始化 不然会崩
                    PhoneManager.getInstance().init(MainActivity.this).addPhoneListener( // 蓝牙电话停止后 续播
                            new PhoneStateListener() {
                                private boolean isPlayingBeforeCalling = false;

                                @Override
                                public void onCallStateChanged(int state, String phoneNumber) {
                                    super.onCallStateChanged(state, phoneNumber);
                                    switch (state) {
                                        case TelephonyManager.CALL_STATE_IDLE:
                                            // 电话停止，继续播放
                                            if (!PhoneManager.getInstance().isCallActive()
                                                    && isPlayingBeforeCalling) {
                                                MusicPlayManager.getInstance().playMusic();
                                            }
                                            break;
                                        case TelephonyManager.CALL_STATE_OFFHOOK:
                                        case TelephonyManager.CALL_STATE_RINGING:
                                            // 来电或通话中，暂停播放
                                            if (MusicPlayManager.getInstance().isPlaying()) {
                                                MusicPlayManager.getInstance().pauseMusic();
                                                isPlayingBeforeCalling = true;
                                            }
                                            break;
                                    }
                                }
                            }
                    );
                }

                // 跳转相应页面
                Boolean enable = (Boolean) SPUtils.getValue(MainActivity.this,
                        SPUtils.SP_KEY_DEMO_MODE, true);
                Boolean firstRun = (Boolean) SPUtils.getValue(MainActivity.this,
                        SPUtils.SP_KEY_FIRST_RUN, true);
                if (Boolean.TRUE.equals(enable)) {
                    toFragment(new SalesPresentationFragment(true), Gravity.RIGHT);
                } else if (Boolean.TRUE.equals(firstRun)) {
                    dialog = new CustomDialog(MainActivity.this);
                    dialog.setTitle("用户协议与隐私政策");

                    SpannableStringBuilder spanBuilder = new SpannableStringBuilder(
                            "欢迎使用丹拿之声，在您使用丹拿之声服务前\n请认真阅读");

                    //用户协议
                    SpannableString span = new SpannableString("《用户协议》");
                    span.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View view) {
                            if (!NetworkObserver.getInstance().isConnectNormal(MainActivity.this)) {
                                CustomToast.makeText(MainActivity.this, "无法查看,暂无可用网络", Toast.LENGTH_SHORT).show();
                            } else {
                                toFragment(new PolicyFragment(PolicyFragment.PolicyType.userAgreement), Gravity.RIGHT);
                                dialog.hide();
                            }
                        }

                        @Override
                        public void updateDrawState(@NonNull TextPaint ds) {
                            ds.setUnderlineText(false);
                        }
                    }, 0, span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //设置颜色
                    span.setSpan(new ForegroundColorSpan(Color.WHITE), 0, span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spanBuilder.append(span);

                    spanBuilder.append("与");

                    //隐私政策
                    span = new SpannableString("《隐私政策》");
                    span.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View view) {
                            if (!NetworkObserver.getInstance().isConnectNormal(MainActivity.this)) {
                                CustomToast.makeText(MainActivity.this, "无法查看,暂无可用网络", Toast.LENGTH_SHORT).show();
                            } else {
                                toFragment(new PolicyFragment(PolicyFragment.PolicyType.privacyPolicy), Gravity.RIGHT);
                                dialog.hide();
                            }
                        }

                        public void updateDrawState(@NonNull TextPaint ds) {
                            ds.setUnderlineText(false);
                        }

                    }, 0, span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    span.setSpan(new ForegroundColorSpan(Color.WHITE), 0, span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    spanBuilder.append(span);
                    spanBuilder.append("\n以了解用户权利义务和个人信息处理规则");

                    dialog.setContent(spanBuilder);
                    dialog.setPositiveText("同意并继续");
                    dialog.setNegativeText("放弃并退出");
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setClickListener(new IOnClickListener() {
                        @Override
                        public void onPositiveButtonListener() {
                            toFragment(new GuideFragment(), Gravity.RIGHT);
                            SPUtils.putValue(MainActivity.this, SPUtils.SP_KEY_FIRST_RUN, false);
                        }

                        @Override
                        public void onNegativeButtonListener() {
                            finish();
                        }
                    });
                    dialog.show();
                } else {
                    toFragment(new NewMainFragment(), Gravity.RIGHT);
                }
            }
        };
    }

    private void setIp() {
        mDataBinding.tvIp.setText(getIpAddress("wlan0"));
    }

    public static String getIpAddress(String ipType) {
        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();

                Log.e("tiwolf", "getIpAddress: 开机获取ip=" + ni.getName());
                if (ni.getName().equals(ipType)) {


                    Enumeration<InetAddress> ias = ni.getInetAddresses();
                    while (ias.hasMoreElements()) {

                        ia = ias.nextElement();
                        if (ia instanceof Inet6Address) {
                            continue;// skip ipv6
                        }
                        String ip = ia.getHostAddress();

                        // 过滤掉127段的ip地址
                        if (!"127.0.0.1".equals(ip)) {
                            hostIp = ia.getHostAddress();
                            break;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        Log.d("tiwolf", "手机IP地址get the IpAddress--> " + hostIp + "");
        return hostIp;
    }

    private void setDpi() {
        // 获取当前设备的 DPI 值
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int currentDpi = metrics.densityDpi;
        LogUtils.d("原来dpi : " + currentDpi);
        // 设置应用程序内部的 DPI 值
        DisplayMetrics newMetrics = new DisplayMetrics();
        newMetrics.setTo(metrics);
        newMetrics.densityDpi = 320/* 您需要设置的 DPI 值 */;
        getResources().getDisplayMetrics().setTo(newMetrics);
        LogUtils.d("设置应用内dpi : " + currentDpi);
    }

    public MiniPlayerPopupView getMiniPlayer() {
        return mDataBinding.miniPlayer;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isStop = true;
        runnable = null;
        mediaSession.setActive(false);
    }

    private boolean isBeforeHidePlaying = false;

    @Override
    protected void onResume() {
        super.onResume();

        if (isBeforeHidePlaying){
            mDataBinding.getRoot().postDelayed(() -> // 0531临时解决切换到后台返回后音乐暂停
                    MusicPlayManager.getInstance().playMusic(),100);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 储存信息 倍速
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(this);
        float speed = MusicPlayManager.getInstance().getSpeed();
        sharedPreferencesUtil.putFloat(LiveDataBusConstants.Player.PLAY_SPEED, speed);

        // 播放的时候返回 再回来没续播
        isBeforeHidePlaying = MusicPlayManager.getInstance().isPlaying();
    }

    /**
     * 有些界面不能显示mini播放器
     */
    public boolean canShowMiniPlayer() {
        return currentFragment != null
                && !(currentFragment instanceof SalesPresentationFragment)
                /*&& !(currentFragment instanceof SoundSettingsFragment)0529:声音设置需要显示mini*/
                && !(currentFragment instanceof BaseExperienceFragment)
                && !(currentFragment instanceof PolicyFragment);
    }

    public BaseFragment getCurrentFragment() {
        return currentFragment;
    }
}