package app.zd.androidscreenshot.sso;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

/**
 * 第三方管理
 * Created by zhangdong on 2018/2/26.
 */

public class SocialManager {
    private SocialShareInterface mTencent;
    private SocialShareInterface mSina;
    private SocialShareInterface mWeChat;
    private SocialShareInterface mWeChatCircle;
    private SocialShareInterface mWeChatFavorite;
    private Context mContext;

    public SocialManager(Activity ctx) {
        mTencent = new TencentSocialImpl(ctx);
        mSina = new SinaSocialImpl(ctx);
        mWeChat = new WeChatSocialImpl(ctx, SocialConstant.WECHAT.ID);
        mWeChatCircle = new WeChatSocialImpl(ctx, SocialConstant.WECHAT_CIRCLE.ID);
        mWeChatFavorite = new WeChatSocialImpl(ctx, SocialConstant.WECHAT_FAVORITE.ID);
        mContext = ctx.getApplicationContext();
    }

    public void login(int type, SocialShareInterface.SocialLoginListener listener) {
        String key = "";
        String appKey = "";
        switch (type) {
            case SocialConstant.SINA.ID:
                key = mSina.getRegisterAppKey();
                appKey = SocialConstant.SINA.APP_KEY;
                mSina.socialLogin(listener);
                break;
            case SocialConstant.TENCENT.ID:
                key = mTencent.getRegisterAppKey();
                appKey = SocialConstant.TENCENT.APP_ID;
                mTencent.socialLogin(listener);
                break;
            case SocialConstant.WECHAT.ID:
                key = mWeChat.getRegisterAppKey();
                appKey = SocialConstant.WECHAT.APP_ID;
                mWeChat.socialLogin(listener);
            default:
                break;
        }
        if (TextUtils.isEmpty(key)) { //注册的appkey为空,错误
            Log.e(SocialManager.class.getSimpleName(), "appkey is null" + appKey);
        }
    }

    public void sharePage(int type, String share, SocialShareInterface.SocialShareListener listener, String... extras) {
        switch (type) {
            case SocialConstant.SINA.ID:
                mSina.sharePage(share, listener, extras);
                break;
            case SocialConstant.TENCENT.ID:
                mTencent.sharePage(share, listener, extras);
                break;
            case SocialConstant.WECHAT.ID:
                mWeChat.sharePage(share, listener, extras);
                break;
            case SocialConstant.WECHAT_CIRCLE.ID:
                mWeChatCircle.sharePage(share, listener, extras);
                break;
            case SocialConstant.WECHAT_FAVORITE.ID:
                mWeChatFavorite.sharePage(share, listener, extras);
            default:
                break;
        }
    }

    public void shareImage(int type, String title, String imgPath, SocialShareInterface.SocialShareListener listener) {
        switch (type) {
            case SocialConstant.SINA.ID:
                mSina.shareImage(title, imgPath, listener);
                break;
            case SocialConstant.TENCENT.ID:
                mTencent.shareImage(title, imgPath, listener);
                break;
            case SocialConstant.WECHAT.ID:
                mWeChat.shareImage(title, imgPath, listener);
                break;
            case SocialConstant.WECHAT_CIRCLE.ID:
                mWeChatCircle.shareImage(title, imgPath, listener);
                break;
            default:
                break;
        }
    }

    public void shareImage(int type, String title, Bitmap bitmap, SocialShareInterface.SocialShareListener listener) {
        switch (type) {
            case SocialConstant.SINA.ID:
                mSina.shareImage(title, bitmap, listener);
                break;
            case SocialConstant.TENCENT.ID:
                mTencent.shareImage(title, bitmap, listener);
                break;
            case SocialConstant.WECHAT.ID:
                mWeChat.shareImage(title, bitmap, listener);
                break;
            case SocialConstant.WECHAT_CIRCLE.ID:
                mWeChatCircle.shareImage(title, bitmap, listener);
                break;
            default:
                break;
        }
    }

    public void getUserInfo(int type, SocialShareInterface.SocialGetUserInfoListener listener) {
        switch (type) {
            case SocialConstant.SINA.ID:
                mSina.getUserInfo(listener);
                break;
            case SocialConstant.TENCENT.ID:
                mTencent.getUserInfo(listener);
                break;
            case SocialConstant.WECHAT.ID:
                mWeChat.getUserInfo(listener);
                break;
            default:
                break;
        }
    }
}
