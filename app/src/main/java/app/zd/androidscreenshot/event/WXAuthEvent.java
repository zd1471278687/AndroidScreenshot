package app.zd.androidscreenshot.event;

/**
 * 微信授权事件
 * Created by zhangdong on 2017/7/11.
 */
public class WXAuthEvent extends BaseEvent {
    public boolean success;
    public String code;

    public WXAuthEvent(boolean success, String code) {
        this.success = success;
        this.code = code;
    }
}
