package app.zd.androidscreenshot.sso;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.io.File;

import app.zd.androidscreenshot.R;
import app.zd.androidscreenshot.util.AppInfoUtil;
import app.zd.androidscreenshot.util.BitmapUtilLib;
import app.zd.androidscreenshot.util.FileUtil;
import app.zd.androidscreenshot.util.StringUtil;

/**
 * 腾讯分享和登录
 * Created by zhangdong on 2018/2/26.
 */

public class TencentSocialImpl extends AbsSocial implements SocialShareInterface {
    public static final String PACKAGE_NAME = "com.tencent.mobileqq";
    private static final String LOG_TAG = TencentSocialImpl.class.getSimpleName();
    private Tencent mTencent = null;
    //是否初始化成功
    private boolean mIsInitSuccess = false;

    public TencentSocialImpl(Activity activity) {
        mActivity = activity;
        init();
    }

    public void init() {
        try {
            mTencent = Tencent.createInstance(SocialConstant.TENCENT.APP_ID, mActivity.getApplicationContext());
            mIsInitSuccess = true;
            mAppKey = SocialConstant.TENCENT.APP_ID;
        } catch (RuntimeException ex) {
            mIsInitSuccess = false;
            mAppKey = null;
        }
    }

    private void deleteTempFile(String filepath) {
        if (StringUtil.isNullOrEmpty(filepath)) {
            return;
        }
        File file = new File(filepath);
        if (!file.exists()) {
            return;
        }
        boolean delete = file.delete();
        if (!delete) {
            Log.i(LOG_TAG, "delete tempFile fail");
        }
    }

    private boolean isQQApkExist() {
        try {
            mActivity.getApplicationContext().getPackageManager().getApplicationInfo("com.tencent.mobileqq", PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 分享本地图片
     */
    private void shareLocalImage(String imageFilePath) {
        if (StringUtil.isNullOrEmpty(imageFilePath) || !new File(imageFilePath).exists()) {
            return;
        }
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imageFilePath);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, mActivity.getApplicationContext().getResources().getString(R.string.app_name));
        doShareToQQ(params);
    }

    private void doShareToQQ(final Bundle params) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (!mIsInitSuccess) {
                    return;
                }
                String localImagePath = params.getString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL);
                String tempImagePath = ""; //压缩后的临时图片地址
                if (!StringUtil.isNullOrEmpty(localImagePath)) {
                    File file = new File(localImagePath);
                    if (file.exists()) {
                        tempImagePath = file.getParent()
                                + "/" + String.valueOf(System.currentTimeMillis()) + file.getName();
                        boolean isOk = BitmapUtilLib.compressBitmapBySize(localImagePath, tempImagePath, 1500 * 1024); //大小不能超过1.5m 因为参数是long,约等于吧
                        if (isOk) {
                            params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, tempImagePath);
                        }
                    }
                }
                final String finalTempImagePath = tempImagePath;
                mTencent.shareToQQ(mActivity, params, new IUiListener() {

                    @Override
                    public void onComplete(Object o) {
                        mHandler.sendEmptyMessage(MSG_SHARE_SUCCESS);
                        deleteTempFile(finalTempImagePath);
                    }

                    @Override
                    public void onError(UiError e) {
                        mHandler.sendEmptyMessage(MSG_SHARE_FAILED);
                        deleteTempFile(finalTempImagePath);
                    }

                    @Override
                    public void onCancel() {
                        deleteTempFile(finalTempImagePath);
                    }

                });
                if (!AppInfoUtil.isPackageInstalled(mActivity.getApplicationContext(), PACKAGE_NAME)) {
                    mHandler.sendEmptyMessageDelayed(MSG_SHARE_SUCCESS, 1000);
                }
            }
        }).start();
    }

    @Override
    public void socialLogin(SocialLoginListener listener) {

    }

    @Override
    public void getUserInfo(SocialGetUserInfoListener listener) {

    }

    @Override
    public void shareImage(String content, String imagePath, SocialShareListener listener) {
        if (!isQQApkExist()) {
            Toast.makeText(mActivity.getApplicationContext(), mActivity.getApplicationContext().getString(R.string.qq_not_install), Toast.LENGTH_SHORT).show();
            return;
        }
        this.mShareListener = listener;
        shareLocalImage(imagePath);
    }

    @Override
    public void shareImage(String content, Bitmap bitmap, SocialShareListener listener) {

    }

    @Override
    public void sharePage(String content, SocialShareListener listener, String... extras) {
        if (!isQQApkExist()) {
            Toast.makeText(mActivity.getApplicationContext(), mActivity.getApplicationContext().getString(R.string.qq_not_install), Toast.LENGTH_SHORT).show();
            return;
        }
        this.mShareListener = listener;
        if (FileUtil.isFile(extras[2])) {
            shareLocalImage(extras[2]);
        } else {
            Bundle params = new Bundle();
            params.putString(QQShare.SHARE_TO_QQ_TITLE, !StringUtil.isNullOrEmpty(extras[0]) ? extras[0] :
                    mActivity.getApplicationContext().getResources().getString(R.string.my_fortune_share));
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, !StringUtil.isNullOrEmpty(extras[1]) ? extras[1] : "");
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, content);
            /**
             * this line must fill, else will not popup browser when qq is not installed
             */
            if (!StringUtil.isNullOrEmpty(extras[2])) {
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, extras[2]);
            }
            doShareToQQ(params);
        }
    }

    @Override
    public String getRegisterAppKey() {
        return mAppKey;
    }

    private class BaseUiListener implements IUiListener {


        protected void doComplete(JSONObject values) {
        }

        @Override
        public void onComplete(Object o) {
            doComplete((JSONObject) o);
        }

        @Override
        public void onError(UiError e) {
            Toast.makeText(mActivity.getApplicationContext(), R.string.sso_auth_failed, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(mActivity.getApplicationContext(), R.string.sso_auth_cancel, Toast.LENGTH_SHORT).show();
        }
    }
}
