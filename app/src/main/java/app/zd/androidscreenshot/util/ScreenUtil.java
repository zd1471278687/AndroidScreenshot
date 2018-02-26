package app.zd.androidscreenshot.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;

/**
 * screen util
 * Created by zhangdong on 2017/4/13.
 */
public final class ScreenUtil {
    private static final String LOG_TAG = ScreenUtil.class.getSimpleName();

    /**
     * Don't let anyone instantiate this class
     */
    private ScreenUtil() {
    }

    /**
     * dp -> px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px -> dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 显示软键盘
     */
    public static void showSoftInput(Context context, View view) {
        if (null == context || null == view) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 隐藏软键盘
     */
    public static void hideSoftInput(Context context, View view) {
        if (null == context || null == view) {
            return;
        }
        InputMethodManager m = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        m.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    /**
     * 隐藏软键盘
     */
    public static void hideSoftInput(View view) {
        if (view == null) {
            return;
        }
        InputMethodManager m = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        m.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    /**
     * 获取状态栏/通知栏的高度
     *
     * @return px 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        if (context instanceof Activity) {
            Rect frame = new Rect();
            ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            int statusBarHeight = frame.top;
            if (statusBarHeight > 0) {
                return statusBarHeight;
            }

            // 反射获取高度
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object obj = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = Integer.parseInt(field.get(obj).toString());
                statusBarHeight = context.getResources().getDimensionPixelSize(x);
                return statusBarHeight;
            } catch (Exception e) {
                Log.e(LOG_TAG, "get status bar height error.");
            }
        }

        // 以上均失效时，使用默认高度为25dp。
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) Math.ceil(25 * metrics.density);
    }

    /**
     * 当前屏幕截图（不包含状态栏）
     *
     * @param activity 当前页面
     * @return bitmap
     */
    public static Bitmap takeScreenshot(Activity activity) {
        if (activity == null) {
            return null;
        }
        View viewScreen = activity.getWindow().getDecorView();
        viewScreen.setDrawingCacheEnabled(true);
        viewScreen.buildDrawingCache();
        Bitmap screenBitmap = viewScreen.getDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(screenBitmap, 0, 0, screenBitmap.getWidth(), screenBitmap.getHeight());
        viewScreen.destroyDrawingCache();
        return BitmapUtilLib.compressScale(bitmap); //压缩一下
    }
}
