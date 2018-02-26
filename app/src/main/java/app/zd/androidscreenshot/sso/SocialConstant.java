package app.zd.androidscreenshot.sso;

/**
 * 支付，分享相关配置
 * Created by zhangdong on 2018/2/26.
 */

public class SocialConstant {
    public interface WECHAT { //微信好友
        int ID = 0;
        String APP_ID = "wx4d31a8f227324702";
        String KEY_USER_PROFILE_URL = "headimgurl";
        String KEY_USER_PROFILE_NAME = "nickname";
        int WECHAT_CONTENT_LENGTH = 512;
    }

    public interface WECHAT_CIRCLE { //微信朋友圈
        int ID = 1;
        String APP_ID = "wx126b60e8a3cf32f8";
    }

    public interface WECHAT_FAVORITE { //微信收藏
        int ID = 2;
        String APP_ID = "wx126b60e8a3cf32f8";
    }

    public interface TENCENT { //qq
        int ID = 3;
        String APP_ID = "";
    }

    public interface TENCENT_ZONE { //qq空间
        int ID = 4;
        String APP_ID = "";
    }

    public interface SINA { //微博
        int ID = 5;
        String APP_ID = "";
        /**
         * 当前 DEMO 应用的 APP_KEY，第三方应用应该使用自己的 APP_KEY 替换该 APP_KEY
         */
        public static String APP_KEY = ""; //"432201420";
    }

    public interface  ShareConstant { //分享
        int SHARE_FIRST_TWO = (1 << 2) - 1; //默认前2个（二进制 0011）
        int SHARE_TYPE_WECHAT = 1; //0001
        int SHARE_TYPE_CIRCLE = 2; //0010
        int SHARE_TYPE_QQ = 4; //0100
        int SHARE_TYPE_ZONE = 8; //1000
    }
}
