package com.byd.dynaudio_app.fragment;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.Toast;

import com.byd.dynaudio_app.BuildConfig;
import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseFragment;
import com.byd.dynaudio_app.base.BaseViewModel;
import com.byd.dynaudio_app.custom.CustomToast;
import com.byd.dynaudio_app.databinding.LayoutFragmentAboutBinding;
import com.byd.dynaudio_app.http.ApiClient;
import com.byd.dynaudio_app.utils.CountingUtils;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.SPUtils;
import com.byd.dynaudio_app.utils.TouchUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AboutFragment extends BaseFragment<LayoutFragmentAboutBinding, BaseViewModel> {

    @Override
    protected void initView() {
        TouchUtils.bindClickItem(mDataBinding.btnBack);

        String version = "Ver";

        Properties props = new Properties();
        try {
            InputStream inputStream = getActivity().getAssets().open("local.properties");
            props.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String xVersion;
        String prefix = "";
        String type = String.valueOf(BuildConfig.HOST);
        if ("dev".equals(type)) {  // 测试环境
            prefix = "测试";
        } else if ("pre".equals(type)) { // 预生产
            prefix = "预生产";
        }

        version = prefix + "V" + BuildConfig.VERSION_NAME + " (" + BuildConfig.BUILD_CODE + ")"; // 这里需要获取应用打包时候的日期
        mDataBinding.txtVersion.setText(version);

        Boolean enable = (Boolean) SPUtils.getValue(mContext, SPUtils.SP_KEY_DEMO_MODE, true);
        if (Boolean.TRUE.equals(enable)) {
            mDataBinding.rlDemo.setVisibility(View.VISIBLE);
        }
    }

    private int getVersionCode() {
        PackageManager manager = mContext.getPackageManager();
        int code;
        try {
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            code = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        return code;
    }

    @Override
    protected void initListener() {
        mDataBinding.btnBack.setOnClickListener(view -> {
            LiveDataBus.get().with(LiveDataBusConstants.go_back).postValue(1);
        });

        // TODO 如果 mDataBinding.rlVersion 5s内点击了超过3次 则进行操作
        CountingUtils.setMultipleClickListener(mDataBinding.rlVersion, 5, 10000, (v, clickTimes, effectTime) -> {
            if (clickTimes == 5 && !ApiClient.getInstance().isFileUpLoading) {
                CustomToast.makeText(mContext, "开始上传日志...", Toast.LENGTH_LONG).show();
                ApiClient.getInstance().fileUpload(mContext);
            }
        });

        CountingUtils.setMultipleClickListener(mDataBinding.imgContent, 5, 5000, (v, clickTimes, effectTime) -> {
            if (clickTimes == 5 && !ApiClient.getInstance().isFileUpLoading) {
                mDataBinding.rlDemo.setVisibility(View.VISIBLE);
                SPUtils.putValue(mContext, SPUtils.SP_KEY_DEMO_MODE, true);
            }
        });

        mDataBinding.switchView.setOnCheckedChangeListener((compoundButton, b) -> {
            SPUtils.putValue(mContext, SPUtils.SP_KEY_DEMO_MODE, b);
        });
    }

    @Override
    protected void initObserver() {

    }

    @Override
    protected BaseViewModel getViewModel() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_fragment_about;
    }
}
