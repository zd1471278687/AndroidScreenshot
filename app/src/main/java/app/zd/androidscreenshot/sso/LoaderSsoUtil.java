package app.zd.androidscreenshot.sso;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * 第三方请求工具（没有用网络框架）
 * Created by zhangdong on 2018/2/26.
 */

public class LoaderSsoUtil {
    private LoaderSsoUtil() {

    }

    /**
     * 从InputStream中读取数据，转换成byte数组，最后转换成string
     *
     * @param is InputStream
     * @return string
     */
    public static String getResponseToString(InputStream is) {
        if (is == null) {
            return "";
        }
        byte[] responseByte = getBytesByInputStream(is);
        return getStringByBytes(responseByte);
    }

    /**
     * 从InputStream中读取数据，转换成byte数组，最后关闭InputStream
     */
    private static byte[] getBytesByInputStream(InputStream is) {
        byte[] bytes = null;
        BufferedInputStream bis = new BufferedInputStream(is);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(baos);
        byte[] buffer = new byte[1024 * 8];
        int length = 0;
        try {
            while ((length = bis.read(buffer)) > 0) {
                bos.write(buffer, 0, length);
            }
            bos.flush();
            bytes = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bytes;
    }

    //根据字节数组构建UTF-8字符串
    private static String getStringByBytes(byte[] bytes) {
        String str = "";
        try {
            str = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }
}
