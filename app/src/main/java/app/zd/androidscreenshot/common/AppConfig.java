package app.zd.androidscreenshot.common;

/**
 * common config
 * Created by zhangdong on 2018/2/26.
 */

public class AppConfig {
    //屏幕信息
    public static int sScreenWidth;
    public static int sScreenHeight;

    public static int getScreenWidth() {
        return sScreenWidth;
    }

    public static void setScreenWidth(int sScreenWidth) {
        AppConfig.sScreenWidth = sScreenWidth;
    }

    public static int getScreenHeight() {
        return sScreenHeight;
    }

    public static void setScreenHeight(int sScreenHeight) {
        AppConfig.sScreenHeight = sScreenHeight;
    }
}
