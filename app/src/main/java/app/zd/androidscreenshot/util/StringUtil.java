package app.zd.androidscreenshot.util;

import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.StringRes;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.zd.androidscreenshot.R;

/**
 * string util
 * Created by zhangdong on 2017/4/8.
 */
public final class StringUtil {
    //加密姓名
    public static final int ENCODE_NAME = 1;
    //加密数字（如：身份证，手机号）
    public static final int ENCODE_NUMBER = 2;
    //中文姓名校验规则（中文 长度2-15）
    public static final String CHINESE_NAME_REGEX = "([\u4E00-\u9FA5\\s\\·\\•]{2,15})";

    /**
     * Don't let anyone instantiate this class
     */
    private StringUtil() {
    }

    public static boolean isNullOrEmpty(String input) {
        if (null == input || input.trim().equals("")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否都不是空
     *
     * @param strings 字符串
     * @return true:都不是空  false:有空字符串
     */
    public static boolean isAllNotNullOrEmpty(String... strings) {
        if (strings == null) {
            return false;
        }
        for (String string : strings) {
            if (isNullOrEmpty(string)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串是否为手机号
     */
    public static boolean isPhoneNumber(String number) {
        if (isNullOrEmpty(number)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^1[3-9][0-9]{9}$");
        return pattern.matcher(number).matches();
    }

    /**
     * 判断字符串是否为邮箱
     */
    public static boolean isEmail(String email) {
        if (isNullOrEmpty(email)) {
            return false;
        }
        boolean isExist = false;
        Pattern p = Pattern.compile("^[A-Za-z0-9]+([._\\\\-]*[A-Za-z0-9])*@([A-Za-z0-9]+[-A-Z-a-z0-9]*[A-Za-z0-9]+\\.){1,63}[A-Za-z0-9]+$");
        Matcher m = p.matcher(email);
        boolean b = m.matches();
        if (b) {
            isExist = true;
        }
        return isExist;
    }

    public static String getString(Context context, @StringRes int resId, Object... params) {
        if (context != null) {
            if (params != null) {
                return context.getResources().getString(resId, params);
            }
            return context.getResources().getString(resId);

        }
        return null;
    }

    /**
     * 个人信息加密
     *
     * @param sourceString 原数据
     * @param codeType     加密方式
     * @return 加密后的数据
     */
    public static String personInfoEncode(String sourceString, int codeType) {
        if (isNullOrEmpty(sourceString)) {
            return sourceString;
        }
        StringBuilder builder = new StringBuilder();
        if (codeType == ENCODE_NAME) { //名字只保留姓，其他用*代替
            builder.append(sourceString.charAt(0));
            for (int i = 1, size = sourceString.length(); i < size; i++) {
                builder.append("*");
            }
        } else { //手机号和身份证号都是保留前后3位数字
            if (sourceString.length() <= 3) {
                return builder.toString();
            }
            builder.append(sourceString.substring(0, 3));
            int index = 3;
            for (int size = sourceString.length() - 3; index < size; index++) {
                builder.append("*");
            }
            builder.append(sourceString.substring(index, sourceString.length()));
        }
        return builder.toString();
    }

    /**
     * emoji表情转html展示的字符（并不是全部都支持）
     *
     * @param source 输入的字符串
     * @return 转换后的字符串
     */
    public static String emojiToString(String source) {
        int a = 0;
        String aa = "";
        String sss = "";
        int b = 0;
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            //判断是否为表情
            if (isEmojiCharacter(c)) {
                b++;
                int i1 = c - 48;//转为ascill
                a += i1;
                if (b % 2 == 0) {
                    sss += "&#";
                    aa = (a + 16419) + ";";
                    a = 0;
                    b = 0;
                    sss = sss + aa;
                }
            } else {
                sss = sss + Character.toString(c);
            }
        }
        return sss;
    }

    private static boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
                || (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
    }

    /**
     * 转html展示
     *
     * @param source 源数据
     * @return 转换后的string
     */
    public static String getHtmlString(String source) {
        if (isNullOrEmpty(source)) {
            return source;
        }
        try {
            Spanned spanned =  Html.fromHtml(source);
            return spanned.toString();
        } catch (Exception e) {
            Log.e(StringUtil.class.getSimpleName(), "nick name form html failed");
        }
        return source;
    }

    /**
     * 复制到剪切板
     */
    public static void onClickCopy(Context context, String text) {
        // 从API11开始android推荐使用android.content.ClipboardManager
        // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(text);
        DialogUtilLib.showShortPromptToast(context, context.getString(R.string.copy_success));
    }
}
