package app.zd.androidscreenshot;

import android.app.Application;
import android.util.DisplayMetrics;

import app.zd.androidscreenshot.common.AppConfig;

/**
 * application for init
 * Created by zhangdong on 2018/2/26.
 */

public class ScreenApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 获取屏幕尺寸
        DisplayMetrics dm = getResources().getDisplayMetrics();
        if (dm.widthPixels <= dm.heightPixels) {
            AppConfig.setScreenWidth(dm.widthPixels);
            AppConfig.setScreenHeight(dm.heightPixels);
        } else { // 确保screen width取实际屏幕宽度
            AppConfig.setScreenWidth(dm.heightPixels);
            AppConfig.setScreenHeight(dm.widthPixels);
        }
    }
}
