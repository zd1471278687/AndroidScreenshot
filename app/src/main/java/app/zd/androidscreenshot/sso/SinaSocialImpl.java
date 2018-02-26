package app.zd.androidscreenshot.sso;

import android.app.Activity;
import android.graphics.Bitmap;

/**
 * 新浪微博
 * Created by zhangdong on 2018/2/26.
 */

public class SinaSocialImpl extends AbsSocial implements SocialShareInterface {

    public SinaSocialImpl(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void socialLogin(SocialLoginListener listener) {

    }

    @Override
    public void getUserInfo(SocialGetUserInfoListener listener) {

    }

    @Override
    public void shareImage(String content, String imagePath, SocialShareListener listener) {

    }

    @Override
    public void shareImage(String content, Bitmap bitmap, SocialShareListener listener) {

    }

    @Override
    public void sharePage(String content, SocialShareListener listener, String... extras) {

    }

    @Override
    public String getRegisterAppKey() {
        return mAppKey;
    }
}
