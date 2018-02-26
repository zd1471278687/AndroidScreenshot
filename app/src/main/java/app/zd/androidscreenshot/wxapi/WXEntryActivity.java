package app.zd.androidscreenshot.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

import app.zd.androidscreenshot.R;
import app.zd.androidscreenshot.event.WXAuthEvent;
import app.zd.androidscreenshot.sso.SocialConstant;
import app.zd.androidscreenshot.util.DialogUtilLib;

/**
 * 接收微信返回
 * Created by zhangdong on 2018/2/26.
 */

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI mApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApi = WXAPIFactory.createWXAPI(this, SocialConstant.WECHAT.APP_ID, false);
        mApi.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mApi.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        // 微信发送请求到第三方应用时，会回调到该方法
        this.finish();
    }

    @Override
    public void onResp(BaseResp baseResp) {
        if (baseResp == null) {
            return;
        }
        // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
        int result = 0;
        switch (baseResp.getType()) {
            case ConstantsAPI.COMMAND_SENDAUTH:
                result = authResultStringId(baseResp);
                break;
            case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
                result = shareResultStringId(baseResp.errCode);
                break;
            default:
        }
        if (result != 0) {
            DialogUtilLib.showShortPromptToast(this, result);
        }
        this.finish();
    }

    /**
     * 授权请求结果
     *
     * @param baseResp 返回信息
     * @return 结果
     */
    private int authResultStringId(BaseResp baseResp) {
        int result;
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = R.string.error_code_auth_success;
                if (baseResp instanceof SendAuth.Resp) {
                    SendAuth.Resp authRest = (SendAuth.Resp) baseResp;
                    EventBus.getDefault().post(new WXAuthEvent(true, authRest.code)); //发送授权结果
                } else {
                    EventBus.getDefault().post(new WXAuthEvent(false, ""));
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = R.string.error_code_auth_cancel;
                EventBus.getDefault().post(new WXAuthEvent(false, ""));
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = R.string.error_code_auth_deny;
                EventBus.getDefault().post(new WXAuthEvent(false, ""));
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                result = R.string.errcode_unsupported;
                EventBus.getDefault().post(new WXAuthEvent(false, ""));
                break;
            default:
                result = R.string.errcode_unknown;
                EventBus.getDefault().post(new WXAuthEvent(false, ""));
                break;
        }
        return result;
    }

    /**
     * 分享请求结果
     *
     * @param errorCode 返回编码
     * @return 结果
     */
    private int shareResultStringId(int errorCode) {
        int result;
        switch (errorCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = R.string.errcode_success;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = R.string.errcode_cancel;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = R.string.errcode_deny;
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                result = R.string.errcode_unsupported;
                break;
            default:
                result = R.string.errcode_unknown;
                break;
        }
        return result;
    }
}
