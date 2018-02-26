package app.zd.androidscreenshot.sso;

import android.graphics.Bitmap;

import app.zd.androidscreenshot.bean.WxUserSocialProfile;

/**
 * 分享相关
 * Created by zhangdong on 2018/2/26.
 */

public interface SocialShareInterface {
    /**
     * 第三方登录
     *
     * @param listener 结果监听
     */
    void socialLogin(SocialLoginListener listener);

    /**
     * 第三方用户信息
     *
     * @param listener 结果监听
     */
    void getUserInfo(SocialGetUserInfoListener listener);

    /**
     * 分享图片
     *
     * @param content   文字描述
     * @param imagePath 图片路径
     * @param listener  结果监听
     */
    void shareImage(String content, String imagePath, SocialShareListener listener);

    void shareImage(String content, Bitmap bitmap, SocialShareListener listener);

    /**
     * 分享文字，页面
     *
     * @param content  文字描述
     * @param listener 结果监听
     * @param extras   其他信息（跳转url，缩略图等）
     */
    void sharePage(String content, SocialShareListener listener, String... extras);

    String getRegisterAppKey();

    interface SocialLoginListener {
        void onLoginSuccess();

        void onLoginFailed();
    }

    interface SocialGetUserInfoListener {
        void onGetUserInfoSuccess(WxUserSocialProfile profile);

        void onGetUserInfoFailed();
    }

    interface SocialShareListener {
        void onShareSuccess();

        void onShareFailed();
    }
}
