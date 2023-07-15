package com.byd.dynaudio_app.user;

import android.util.Log;

import com.tencent.mm.opensdk.diffdev.DiffDevOAuthFactory;
import com.tencent.mm.opensdk.diffdev.IDiffDevOAuth;
import com.tencent.mm.opensdk.diffdev.OAuthErrCode;
import com.tencent.mm.opensdk.diffdev.OAuthListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Random;

public class WxApiController {

    private static final String TAG = WxApiController.class.getSimpleName();

    private static class LazierHolder {
        static WxApiController instance = new WxApiController();
    }

    public static WxApiController getInstance() {
        return WxApiController.LazierHolder.instance;
    }

    private IWxLoginCallback loginCallback;

    public void setLoginCallback(IWxLoginCallback loginCallback) {
        this.loginCallback = loginCallback;
    }

    public static final String WECHAT_APP_ID = "";//id
    public static final String WECHAT_SECRET = "";//密钥

    private IDiffDevOAuth oAuth;

    OAuthListener listener = new OAuthListener() {

        /**
         * auth之后返回的二维码接口
         *
         * @param qrcodeImgPath 废弃
         * @param imgBuf 二维码图片数据
         */
        @Override
        public void onAuthGotQrcode(String qrcodeImgPath, byte[] imgBuf) {
            Log.e(TAG, "onAuthGotQrcode: ");
            if (loginCallback != null) {
                loginCallback.GotQrcode(imgBuf);
            }
        }

        /**
         * 用户扫描二维码之后，回调接口
         */
        @Override
        public void onQrcodeScanned() {
            Log.e(TAG, "onQrcodeScanned: ");
        }

        /**
         * 用户点击授权后，回调接口
         */
        @Override
        public void onAuthFinish(OAuthErrCode errCode, String authCode) {
            Log.e(TAG, "onAuthFinish: ");
            if (errCode == OAuthErrCode.WechatAuth_Err_OK) {
                getUserInfo(authCode);
            } else if (errCode == OAuthErrCode.WechatAuth_Err_Timeout) {
                Log.e(TAG, "onAuthFinish: WechatAuth_Err_Timeout >>> 二维码已过期，请点击刷新");
            } else if (errCode == OAuthErrCode.WechatAuth_Err_Cancel) {
                Log.e(TAG, "onAuthFinish: WechatAuth_Err_Cancel >>> 没有授权返回在刷新一下二维码");
            } else if (errCode == OAuthErrCode.WechatAuth_Err_NetworkErr) {
                Log.e(TAG, "onAuthFinish: WechatAuth_Err_NetworkErr >>> 网络错误");
            }
        }
    };

    /**
     * 获取用户信息，用户扫码点击授权后调用
     */
    private void getUserInfo(String authCode) {
        new Thread(() -> {
            try {
                String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + WECHAT_APP_ID + "&secret=" + WECHAT_SECRET + "&code=" + authCode + "&grant_type=authorization_code";
                Log.e(TAG, "onAuthFinish: url: " + url);
                String res = HttpsUtils.submitGetData(url, null);
                Log.e(TAG, "服务器返回: " + res);
                JSONObject jsonObject = new JSONObject(res);
                String openid = jsonObject.getString("openid");
                String access_token = jsonObject.getString("access_token");

                url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token + "&openid=" + openid;
                res = HttpsUtils.submitGetData(url, null);
                Log.e(TAG, "服务器返回: " + res);
                jsonObject = new JSONObject(res);
                String unionid = jsonObject.getString("unionid");
                openid = jsonObject.getString("openid");
                String nickName = jsonObject.getString("nickname");
                int sex = jsonObject.getInt("sex");
                String headimgurl = jsonObject.getString("headimgurl");
                String language = jsonObject.getString("language");
                String city = jsonObject.getString("city");
                String province = jsonObject.getString("province");
                String country = jsonObject.getString("country");
                JSONArray privilege = jsonObject.getJSONArray("privilege");
                Log.e(TAG, "getUserInfo >>> unionid: " + unionid);
                Log.e(TAG, "getUserInfo >>> openid: " + openid);
                Log.e(TAG, "getUserInfo >>> nickName: " + nickName);
                Log.e(TAG, "getUserInfo >>> sex: " + sex);
                Log.e(TAG, "getUserInfo >>> headimgurl: " + headimgurl);
                Log.e(TAG, "getUserInfo >>> language: " + language);
                Log.e(TAG, "getUserInfo >>> city: " + city);
                Log.e(TAG, "getUserInfo >>> province: " + province);
                Log.e(TAG, "getUserInfo >>> country: " + country);
                Log.e(TAG, "getUserInfo >>> privilege: " + privilege);

                if (loginCallback != null) {
                    WxUserInfo userInfo = new WxUserInfo();
                    userInfo.setUnionid(unionid);
                    userInfo.setOpenid(unionid);
                    userInfo.setNickName(nickName);
                    userInfo.setSex(sex);
                    userInfo.setHeadimgurl(headimgurl);
                    userInfo.setLanguage(language);
                    userInfo.setCity(city);
                    userInfo.setProvince(province);
                    userInfo.setCountry(country);
                    loginCallback.GotUserInfo(userInfo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 获取access_token
     */
    public void ConnectWechat() {
        new Thread(() -> {
            try {
                String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + WECHAT_APP_ID + "&secret=" + WECHAT_SECRET;
                Log.e(TAG, "url_1: " + url);
                String res = HttpsUtils.submitGetData(url, null);
                Log.e(TAG, "服务器返回: " + res);

                //获取access_token
                String access_token = new JSONObject(res).getString("access_token");
                url = "https:api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + access_token + "&type=2";
                Log.e(TAG, "url_2: " + url);

                res = HttpsUtils.submitGetData(url, null);

                Log.e(TAG, "服务器返回: " + res);
                String ticket = new JSONObject(res).getString("ticket");

                StringBuilder str = new StringBuilder();// 定义变长字符串
                // 随机生成数字，并添加到字符串
                for (int i = 0; i < 8; i++) {
                    str.append(new Random().nextInt(10));
                }
                String noncestr = str.toString();
                String timeStamp = Long.toString(System.currentTimeMillis()).substring(0, 10);
                String string1 = String.format("appid=%s&noncestr=%s&sdk_ticket=%s&timestamp=%s", WECHAT_APP_ID, noncestr, ticket, timeStamp);
                String sha = EncryptUtils.getSHA(string1);
                Log.e(TAG, "二维码验证方式" + sha);
                if (oAuth == null) {
                    oAuth = DiffDevOAuthFactory.getDiffDevOAuth();
                }
                oAuth.auth(WECHAT_APP_ID, "snsapi_userinfo", noncestr, timeStamp, sha, listener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void removeListener() {
        if (oAuth != null) {
            oAuth.removeAllListeners();
        }
        if (loginCallback != null) {
            loginCallback = null;
        }
    }
}
