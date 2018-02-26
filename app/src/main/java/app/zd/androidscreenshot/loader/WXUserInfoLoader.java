package app.zd.androidscreenshot.loader;

import android.os.AsyncTask;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import app.zd.androidscreenshot.bean.WxUserSocialProfile;
import app.zd.androidscreenshot.sso.LoaderSsoUtil;
import app.zd.androidscreenshot.util.JsonUtil;
import app.zd.androidscreenshot.util.StringUtil;

/**
 * 微信用户信息请求（没有用网络框架）
 * Created by zhangdong on 2017/7/11.
 */
public class WXUserInfoLoader extends AsyncTask<String, Void, WxUserSocialProfile> {
    private WxUserInfoListener mListener;

    public WXUserInfoLoader(WxUserInfoListener listener) {
        mListener = listener;
    }

    @Override
    protected WxUserSocialProfile doInBackground(String... params) {
        String url = params[0]; //http请求的url
        if (StringUtil.isNullOrEmpty(url)) {
            return null;
        }
        return getHttpClientResult(url);
    }

    @Override
    protected void onPostExecute(WxUserSocialProfile result) {
        if (result == null) {
            if (mListener != null) {
                mListener.wxUserInfoResult(false, null);
            }
        } else {
            if (mListener != null) {
                mListener.wxUserInfoResult(true, result);
            }
        }
        super.onPostExecute(result);
    }

    private WxUserSocialProfile getHttpClientResult(String urlString) {
        //发送GET请求
        URL url = null;//请求的URL地址
        HttpURLConnection conn = null;
        try {
            url = new URL(urlString);

            conn = (HttpURLConnection) url.openConnection();
            //HttpURLConnection默认就是用GET发送请求，所以下面的setRequestMethod可以省略
            conn.setRequestMethod("GET");
            //HttpURLConnection默认也支持从服务端读取结果流，所以下面的setDoInput也可以省略
            conn.setDoInput(true);
            //用setRequestProperty方法设置一个自定义的请求头:action，由于后端判断
            conn.setRequestProperty("action", "NETWORK_GET");
            //禁用网络缓存
            conn.setUseCaches(false);
            //在对各种参数配置完成后，通过调用connect方法建立TCP连接，但是并未真正获取数据
            //conn.connect()方法不必显式调用，当调用conn.getInputStream()方法时内部也会自动调用connect方法
            //conn.connect();
            //调用getInputStream方法后，服务端才会收到请求，并阻塞式地接收服务端返回的数据
            InputStream is = conn.getInputStream();
            //将InputStream转换成byte数组,getBytesByInputStream会关闭输入流，最后转化成string返回
            String result = LoaderSsoUtil.getResponseToString(is);
            return JsonUtil.decode(result, WxUserSocialProfile.class);
        } catch (Exception ie) {
        } finally {
            //最后将conn断开连接
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    public interface WxUserInfoListener {
        void wxUserInfoResult(boolean result, WxUserSocialProfile profile);
    }
}
