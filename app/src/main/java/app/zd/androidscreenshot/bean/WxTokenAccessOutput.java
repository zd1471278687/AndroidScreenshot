package app.zd.androidscreenshot.bean;

/**
 * 获取微信token返回结果
 * Created by zhangdong on 2018/2/26.
 */

public class WxTokenAccessOutput {
    public String access_token; //接口调用凭证
    public int expires_in; //access_token接口调用凭证超时时间，单位（秒）
    public String refresh_token; //用户刷新access_token
    public String openid; //授权用户唯一标识
    public String scope; //用户授权的作用域，使用逗号（,）分隔
    public String unionid; // 当且仅当该移动应用已获得该用户的userinfo授权时，才会出现该字段
}
