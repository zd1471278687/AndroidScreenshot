package app.zd.androidscreenshot.sso;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import app.zd.androidscreenshot.bean.WxUserSocialProfile;

/**
 * 分享处理的父类
 * Created by zhangdong on 2018/2/26.
 */

public class AbsSocial {
    protected H mHandler = new H(Looper.getMainLooper());

    public static final int MSG_LOGIN_SUCCESS = 1;
    public static final int MSG_LOGIN_FAILED = 2;

    public static final int MSG_GET_USER_INFOR_SUCCESS = 3;
    public static final int MSG_GET_USER_INFOR_FAILED = 4;

    public static final int MSG_SHARE_SUCCESS = 5;
    public static final int MSG_SHARE_FAILED = 6;

    SocialShareInterface.SocialLoginListener mLoginListener = null;
    SocialShareInterface.SocialGetUserInfoListener mGetInfoListener = null;
    SocialShareInterface.SocialShareListener mShareListener = null;
    public Activity mActivity;
    protected String mAppKey;

    public AbsSocial() {
    }

    void handleMsg(Message msg) {
        switch (msg.what) {
            case MSG_LOGIN_SUCCESS:
                if (mLoginListener != null) {
                    mLoginListener.onLoginSuccess();
                }
                break;
            case MSG_LOGIN_FAILED:
                if (mLoginListener != null) {
                    mLoginListener.onLoginFailed();
                }
                break;
            case MSG_GET_USER_INFOR_SUCCESS:
                if (mGetInfoListener != null) {
                    mGetInfoListener.onGetUserInfoSuccess((WxUserSocialProfile) msg.obj);
                }
                break;
            case MSG_GET_USER_INFOR_FAILED:
                if (mGetInfoListener != null) {
                    mGetInfoListener.onGetUserInfoFailed();
                }
                break;
            case MSG_SHARE_SUCCESS:
                if (mShareListener != null) {
                    mShareListener.onShareSuccess();
                }
                break;
            case MSG_SHARE_FAILED:
                if (mShareListener != null) {
                    mShareListener.onShareFailed();
                }
                break;
            default:
                break;
        }
    }

    //make handler runing in main thread
    class H extends Handler {

        H(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg == null) {
                return;
            }
            handleMsg(msg);
        }
    }
}
