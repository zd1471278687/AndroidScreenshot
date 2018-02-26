package app.zd.androidscreenshot.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import app.zd.androidscreenshot.common.AppConfig;

/**
 * dialog util
 * Created by zhangdong on 2017/4/10.
 */
public class DialogUtilLib {

    /**
     * Don't let anyone instantiate this class
     */
    private DialogUtilLib() {}

    public static void showShortPromptToast(Context context, int resid) {
        Toast toast = Toast.makeText(context, resid, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, AppConfig.sScreenHeight / 2);
        toast.show();
    }

    public static void showShortPromptToast(Context context, String res) {
        if (StringUtil.isNullOrEmpty(res)) {
            res = "";
        }
        Toast toast = Toast.makeText(context, res, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, AppConfig.sScreenHeight / 2);
        toast.show();
    }

    public static void showLongPromptToast(Context context, int resid) {
        Toast toast = Toast.makeText(context, resid, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, AppConfig.sScreenHeight / 2);
        toast.show();
    }

    public static void showLongPromptToast(Context context, String res) {
        if (StringUtil.isNullOrEmpty(res)) {
            res = "";
        }
        Toast toast = Toast.makeText(context, res, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, AppConfig.sScreenHeight / 2);
        toast.show();
    }
}
