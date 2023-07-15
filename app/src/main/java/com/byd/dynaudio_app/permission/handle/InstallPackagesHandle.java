package com.byd.dynaudio_app.permission.handle;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.byd.dynaudio_app.permission.UsesPermission;
import com.byd.dynaudio_app.permission.view.Settings;

/**
 * 检测是否有安装权限，和实现了授权申请方法
 * github: https://github.com/xiangyuecn/Android-UsesPermission
 */
public class InstallPackagesHandle extends Handle {

    @Override
    public Handle.CheckResult Check(UsesPermission obj, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (obj.GetContext().getPackageManager().canRequestPackageInstalls()) {
                return Handle.CheckResult.Resolve;
            } else {
                return Handle.CheckResult.FinalReject;
            }
        } else {
            return Handle.CheckResult.Resolve;
        }
    }

    @Override
    public void Request(UsesPermission obj, String permission, final RequestCallback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + obj.GetActivity().getPackageName()));

            Settings.OpenSettings(obj.GetActivity(), intent, new Settings.OpenSettingsCallback() {
                @Override
                public void onResult() {
                    callback.onResult();
                }
            });
        } else {
            callback.onResult();
        }
    }
}
