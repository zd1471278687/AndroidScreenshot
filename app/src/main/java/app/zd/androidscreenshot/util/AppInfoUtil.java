package app.zd.androidscreenshot.util;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import app.zd.androidscreenshot.bean.AppInfo;

/**
 * app info util
 * Created by zhangdong on 2017/4/10.
 */
public class AppInfoUtil {
    public static final String WRONG_ANDROID_ID = "9774d56d682e549c";
    private static String LOG_TAG = AppInfoUtil.class.getSimpleName();

    /**
     * Don't let anyone instantiate this class
     */
    private AppInfoUtil() {
    }

    /**
     * 获取应用当前版名称
     *
     * @param context 上下文
     */
    public static String getCurrentVersionName(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(LOG_TAG, "package info not get", e);
            return "1.0.0";
        } catch (RuntimeException e) {
            Log.e(LOG_TAG, "package info not get", e);
            return "1.0.0";
        }
    }

    /**
     * 获取应用当前版本号
     */
    public static int getCurrentVersionCode(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(LOG_TAG, "package info not get", e);
            return 0;
        } catch (RuntimeException e) {
            Log.e(LOG_TAG, "package info not get", e);
            return 0;
        }
    }

    /**
     * 根据进程号获取进程名称
     *
     * @param context 上下文
     * @param pid     pid
     */
    public static String getAppProcessNameByPID(Context context, int pid) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) {
            return "";
        }
        List<ActivityManager.RunningAppProcessInfo> processInfoList = manager.getRunningAppProcesses();
        if (processInfoList == null) {
            return "";
        }
        for (ActivityManager.RunningAppProcessInfo processInfo : processInfoList) {
            if (processInfo != null && processInfo.pid == pid) {
                return processInfo.processName;
            }
        }
        return "";
    }

    /**
     * 判断应用是否已经启动
     *
     * @param context     一个context
     * @param packageName 要判断应用的包名
     * @return boolean
     */
    public static boolean isAppAlive(Context context, String packageName) {
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
        if (processInfos == null) {
            return false;
        }
        for (int i = 0; i < processInfos.size(); i++) {
            if (processInfos.get(i).processName.equals(packageName)) {
                Log.i(LOG_TAG, String.format("the %s is running, isAppAlive return true", packageName));
                return true;
            }
        }
        Log.i(LOG_TAG, String.format("the %s is not running, isAppAlive return false", packageName));
        return false;
    }

    /**
     * 判断应用是否在前台
     *
     * @param context 上下文
     * @return boolean
     */
    public static boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    /**
     * 创建应用快捷方式
     *
     * @param context        上下文
     * @param shortcutName   快捷方式名称
     * @param shortcutIntent 快捷方式动作
     * @param iconRes        快捷方式图标
     * @param allowRepeat    是否允许重复创建
     */
    public static void createAppShortcut(Context context, String shortcutName, Intent shortcutIntent,
                                         Intent.ShortcutIconResource iconRes, boolean allowRepeat) {
        if (context == null) {
            return;
        }
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        //快捷方式的名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutName);
        //是否允许重复创建
        shortcut.putExtra("duplicate", allowRepeat);
        //快捷方式动作
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        //快捷方式的图标
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
        context.sendBroadcast(shortcut); //发送广播
    }

    /**
     * 不一定所有的手机都有效，因为国内大部分手机的桌面不是系统原生的<br/>
     * 桌面有两种，系统桌面(ROM自带)与第三方桌面，一般只考虑系统自带<br/>
     * 第三方桌面如果没有实现系统响应的方法是无法判断的，比如GO桌面<br/>
     * 此处需要在AndroidManifest.xml中配置相关的桌面权限信息<br/>
     * 错误信息已捕获<br/>
     *
     * @param context 上下文
     * @param title   名称
     * @param intent  动作
     */
    public static boolean isShortCutExist(Context context, String title, Intent intent) {
        boolean result = false;
        try {
            final ContentResolver cr = context.getContentResolver();
            StringBuilder uriStr = new StringBuilder();
            uriStr.append("content://");
            int sdkInt = android.os.Build.VERSION.SDK_INT;
            if (sdkInt < 8) { // Android 2.1.x(API 7)以及以下的
                uriStr.append("com.android.launcher.settings");
            } else if (sdkInt < 19) {// Android 4.4以下
                uriStr.append("com.android.launcher2.settings");
            } else {// 4.4以及以上
                uriStr.append("com.android.launcher3.settings");
            }
            uriStr.append("/favorites?notify=true");
            Uri uri = Uri.parse(uriStr.toString());
            Cursor c = cr.query(uri, new String[]{"title",
                    "iconResource"}, "title=?", new String[]{title}, null);
            if (c != null && c.getCount() > 0) {
                result = true;
            }
            if (c != null && !c.isClosed()) {
                c.close();
            }
        } catch (Exception ex) {
            result = false;
            Log.e(LOG_TAG, "check shortcut exist exception: " + ex.toString());
        }
        Log.e(LOG_TAG, "app shortcut check exist result: " + result);
        return result;
    }

    /**
     * 设备唯一id
     * ANDROID_ID 它在Android <=2.1 or Android >=2.3的版本是可靠、稳定的，但在2.2的版本并不是100%可靠的
     * 在主流厂商生产的设备上，有一个很经常的bug，就是每个设备都会产生相同的ANDROID_ID：9774d56d682e549c
     * 所以生成方案就是以ANDROID_ID为基础，在获取失败时以TelephonyManager.getDeviceId()为备选方法，如果再失败，使用UUID的生成策略
     *
     * @param context 上下文
     * @return 唯一ID
     */
    public static String getAndroidUniqueId(Context context) {
        String uniqueId = "";
        final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        // Use the Android ID unless it's broken, in which case fallback on deviceId,
        // unless it's not available, then fallback on a random number which we store
        // to a prefs file
        try {
            if (!WRONG_ANDROID_ID.equals(androidId) && androidId != null && !"".equals(androidId)) {
                uniqueId = androidId;
            } else {
                final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                uniqueId = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")).toString() : UUID.randomUUID().toString();
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return uniqueId;
    }

    /**
     * 获取手机设备号
     * 6.0及以上系统先取android id，再取imei
     * 6.0以下系统先取imei，再取android id
     */
    public static String getDeviceID(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            return getMDeviceID(context);
        } else {
            return getBeforeMDeviceID(context);
        }
    }

    private static String getBeforeMDeviceID(Context context) {
        String deviceId = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null && hasPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                deviceId = tm.getDeviceId();
            }
            if (StringUtil.isNullOrEmpty(deviceId)) {
                deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        } catch (Exception e) {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceId;
    }

    private static String getMDeviceID(Context context) {
        String deviceId = "";
        try {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (StringUtil.isNullOrEmpty(deviceId)) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (tm != null && hasPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                    deviceId = tm.getDeviceId();
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Fail to getMDeviceID.", e);
        }
        return deviceId;
    }

    public static boolean hasPermission(Context context, String permission) {
        if (context == null || StringUtil.isNullOrEmpty(permission)) {
            return false;
        }
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 跳转应用市场详情页，给应用评分
     *
     * @param context 上下文
     */
    public static void rateForApp(Context context) {
        if (context == null) {
            return;
        }
        String appName = context.getPackageName();
        Intent marketIntent = new Intent(Intent.ACTION_VIEW);
        try {
            marketIntent.setData(Uri.parse("market://details?id=" + appName));
            marketIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(marketIntent);
        } catch (ActivityNotFoundException e) {
            marketIntent.setData(Uri.parse("http://play.google.com/store/apps/details?id=" + appName));
            marketIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(marketIntent);
        }
    }

    /**
     * 判断应用是否安装
     *
     * @param context     上下文
     * @param packageName 应用包名
     */
    public static boolean isPackageInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_META_DATA);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 得到手机中所有的应用程序信息
     *
     * @return 应用信息
     */
    public static List<AppInfo> getAppInfos(Context context) {
        //创建要返回的集合对象
        List<AppInfo> appInfoList = new ArrayList<>();
        if (context == null) {
            return appInfoList;
        }
        PackageManager pm = context.getPackageManager();
        //获取手机中所有安装的应用集合
        List<ApplicationInfo> applicationInfos = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        //遍历所有的应用集合
        for (ApplicationInfo info : applicationInfos) {
            boolean isUser = filterApp(info); //判断应用程序是否是用户程序
            if (!isUser) { //只展示用户程序
                continue;
            }
            AppInfo appInfo = new AppInfo();
            //获取应用程序的图标
            appInfo.appIcon = info.loadIcon(pm);
            //获取应用的名称
            appInfo.appName = info.loadLabel(pm).toString();
            //获取应用的包名
            String packageName = info.packageName;
            appInfo.packageName = packageName;
            try {
                //获取应用的版本号
                PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
                appInfo.appVersion = packageInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(LOG_TAG, "Fail to getAppInfos.", e);
            }
            appInfo.isUserApp = true;
            appInfoList.add(appInfo);
        }
        return appInfoList;
    }

    /**
     * 判断应用程序是否是用户程序
     *
     * @param info 应用信息
     * @return 是否是用户程序
     */
    private static boolean filterApp(ApplicationInfo info) {
        if (info == null) {
            return false;
        }
        //原来是系统应用，用户手动升级
        if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return true;
            //用户自己安装的应用程序
        } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            return true;
        }
        return false;
    }

}
