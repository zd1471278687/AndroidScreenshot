package app.zd.androidscreenshot.sso;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.File;

import app.zd.androidscreenshot.R;
import app.zd.androidscreenshot.bean.WxTokenAccessOutput;
import app.zd.androidscreenshot.bean.WxUserSocialProfile;
import app.zd.androidscreenshot.loader.WXUserInfoLoader;
import app.zd.androidscreenshot.util.BitmapUtilLib;
import app.zd.androidscreenshot.util.StringUtil;

/**
 * 微信分享和登录
 * Created by zhangdong on 2018/2/26.
 */

public class WeChatSocialImpl extends AbsSocial implements SocialShareInterface, WXUserInfoLoader.WxUserInfoListener {
    private static final String LOG_TAG = WeChatSocialImpl.class.getSimpleName();
    private static final int THUMB_SIZE = 10;
    //IWXAPI是第三方app和微信通信的openAPI接口
    private IWXAPI mApi = null;
    //分享场景
    private int mScene;
    //用户信息为防止泄漏，避免存储在app
    private WxTokenAccessOutput mSocialIdentity;

    public WeChatSocialImpl(Activity activity, int scene) {
        mActivity = activity;
        mScene = scene;
        init();
    }

    public void init() {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        mApi = WXAPIFactory.createWXAPI(mActivity.getApplicationContext(), SocialConstant.WECHAT.APP_ID);
        // 将该app注册到微信
        mApi.registerApp(SocialConstant.WECHAT.APP_ID);
        mAppKey = SocialConstant.WECHAT.APP_ID;
    }

    private void requestWXUserInfo() {
        if (mSocialIdentity != null) {
            WXUserInfoLoader task = new WXUserInfoLoader(this);
            StringBuffer url = new StringBuffer("https://api.weixin.qq.com/sns/userinfo");
            url.append("?"); //?access_token=ACCESS_TOKEN&openid=OPENID
            url.append("access_token=").append(mSocialIdentity.access_token);
            url.append("&openid=").append(mSocialIdentity.openid);
            task.execute(url.toString());
        }
    }

    private void doSend(String content, Bitmap thumbBitmap, String title, String weburl) {
        WXWebpageObject wxObj = new WXWebpageObject();
        wxObj.webpageUrl = weburl;
        WXMediaMessage msg = new WXMediaMessage();
        // title限制长度, hard code
        if (title == null || title.length() <= 512) {
            msg.title = title;
        } else {
            msg.title = title.substring(0, 512);
        }
        // content限制长度, hard code
        if (content == null || content.length() <= 1024) {
            msg.description = StringUtil.isNullOrEmpty(content) ? msg.title : content;
        } else {
            msg.description = content.substring(0, 1024);
        }
        msg.mediaObject = wxObj;
        if (thumbBitmap != null) {
            msg.thumbData = BitmapUtilLib.bmpToByteArray(thumbBitmap, true);
        }
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        req.scene = getWXScene();
        mApi.sendReq(req);
    }

    private void doSendLocalImage(String path) {
        File file = new File(path);
        if (!file.exists()) {
            String tip = mActivity.getApplicationContext().getString(R.string.send_img_file_not_exist);
            Toast.makeText(mActivity.getApplicationContext(), tip + " path = " + path, Toast.LENGTH_LONG).show();
            return;
        }
        WXImageObject imgObj = new WXImageObject();
        imgObj.setImagePath(path);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;

        Bitmap bmp = BitmapFactory.decodeFile(path);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        bmp.recycle();
        msg.thumbData = BitmapUtilLib.bmpToByteArray(thumbBmp, false); //bitmap不回收，因为还会返回

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        req.scene = getWXScene();
        mApi.sendReq(req);
    }

    private void doSendImageBitmap(Bitmap bmp) {
        WXImageObject imgObj = new WXImageObject(bmp);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;

        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, bmp.getWidth() / THUMB_SIZE, bmp.getHeight() / THUMB_SIZE, true);
        msg.thumbData = BitmapUtilLib.bmpToByteArray(thumbBmp, true);  // 缩略图  bitmap不回收，因为还会返回

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        req.scene = getWXScene();
        mApi.sendReq(req);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    //微信分享默认图片
    private Bitmap getDefaultBitmap() {
        Bitmap bitmap = BitmapFactory.decodeResource(mActivity.getApplicationContext().getResources(), R.mipmap.ic_launcher);
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float scale = (float) Math.max(60.0 / width, 60.0 / height);
            bitmap = Bitmap.createScaledBitmap(bitmap, (int) (scale * width), (int) (scale * height), true);
        }
        return bitmap;
    }

    /**
     * 分享场景
     *
     * @return 聊天界面，朋友圈，微信收藏
     */
    private int getWXScene() {
        switch (mScene) {
            case SocialConstant.WECHAT.ID:
                //发送到聊天界面
                return SendMessageToWX.Req.WXSceneSession;
            case SocialConstant.WECHAT_CIRCLE.ID:
                //发送到朋友圈
                return SendMessageToWX.Req.WXSceneTimeline;
            case SocialConstant.WECHAT_FAVORITE.ID:
                //添加到微信收藏
                return SendMessageToWX.Req.WXSceneFavorite;
            default:
                return SendMessageToWX.Req.WXSceneSession;
        }
    }

    @Override
    public void socialLogin(SocialLoginListener listener) {
        if (mApi == null) {
            init();
        }
        if (!mApi.isWXAppInstalled()) {
            Toast.makeText(mActivity.getApplicationContext(),
                    mActivity.getApplicationContext().getString(R.string.wechat_not_install), Toast.LENGTH_SHORT).show();
            return;
        }
        mLoginListener = listener;
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "com.lottery.fortuneapp";
        //第三方向微信终端发送一个SendAuth.Req消息结构
        mApi.sendReq(req);
    }

    @Override
    public void getUserInfo(SocialGetUserInfoListener listener) {
        mGetInfoListener = listener;
        requestWXUserInfo();
    }

    @Override
    public void shareImage(String content, String imagePath, SocialShareListener listener) {
        if (mApi == null) {
            init();
        }
        if (!mApi.isWXAppInstalled()) {
            Toast.makeText(mActivity.getApplicationContext(),
                    mActivity.getApplicationContext().getString(R.string.wechat_not_install), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!StringUtil.isNullOrEmpty(imagePath)) {
            doSendLocalImage(imagePath);
        }
    }

    @Override
    public void shareImage(String content, Bitmap bitmap, SocialShareListener listener) {
        if (mApi == null) {
            init();
        }
        if (!mApi.isWXAppInstalled()) {
            Toast.makeText(mActivity.getApplicationContext(),
                    mActivity.getApplicationContext().getString(R.string.wechat_not_install), Toast.LENGTH_SHORT).show();
            return;
        }
        if (bitmap != null) {
            doSendImageBitmap(bitmap);
        }
    }

    @Override
    public void sharePage(String content, SocialShareListener listener, String... extras) {
        if (mApi == null) {
            init();
        }
        if (!mApi.isWXAppInstalled()) {
            Toast.makeText(mActivity.getApplicationContext(),
                    mActivity.getApplicationContext().getString(R.string.wechat_not_install), Toast.LENGTH_SHORT).show();
            return;
        }
        String imageUrl = extras[0];
        String title = extras[1];
        String shareUrl = extras[2];
        if (!StringUtil.isNullOrEmpty(imageUrl)) {
            doSendLocalImage(imageUrl);
        } else {
            doSend(content, getDefaultBitmap(), title, shareUrl);
        }
    }

    @Override
    public String getRegisterAppKey() {
        return mAppKey;
    }

    @Override
    public void wxUserInfoResult(boolean result, WxUserSocialProfile profile) {
        //获取token结果
        if (mHandler != null) {
            if (result && profile != null) {
                Message message = new Message();
                message.obj = profile;
                message.what = MSG_GET_USER_INFOR_SUCCESS;
                mHandler.sendMessage(message);
            } else {
                mHandler.sendEmptyMessage(MSG_GET_USER_INFOR_FAILED);
            }
        }
    }
}
