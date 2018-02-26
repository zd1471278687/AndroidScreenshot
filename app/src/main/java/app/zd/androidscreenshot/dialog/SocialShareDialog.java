package app.zd.androidscreenshot.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.zd.androidscreenshot.R;
import app.zd.androidscreenshot.adapter.CommonAdapter;
import app.zd.androidscreenshot.adapter.ViewHolder;
import app.zd.androidscreenshot.bean.SocialTypeInfo;
import app.zd.androidscreenshot.common.AppConfig;
import app.zd.androidscreenshot.sso.SocialConstant;
import app.zd.androidscreenshot.sso.SocialManager;
import app.zd.androidscreenshot.sso.SocialShareInterface;
import app.zd.androidscreenshot.util.BitmapUtilLib;
import app.zd.androidscreenshot.util.DialogUtilLib;
import app.zd.androidscreenshot.util.ExtendUtilLib;
import app.zd.androidscreenshot.util.ScreenUtil;
import app.zd.androidscreenshot.util.StringUtil;

/**
 * 截屏分享对话框
 */

public class SocialShareDialog extends Dialog implements View.OnClickListener, DialogInterface.OnDismissListener,
        AdapterView.OnItemClickListener, SocialShareInterface.SocialShareListener {
    private static final int TOP_BAR_HEIGHT = 48;
    private String mImagePath;
    private Activity mActivity;
    private ImageView mScreenIv;
    private RelativeLayout mScreenRl;
    private LinearLayout mLoadingLl;
    //需要展示的分享类型
    private int mShareChannel = SocialConstant.ShareConstant.SHARE_FIRST_TWO;
    private GridView mShareTypeGv;
    private List<SocialTypeInfo> mShareList;
    private SocialManager mSocialManager;
    private Bitmap mScreenBitmap;

    public SocialShareDialog(Context context, Activity activity, String path, int themeResId) {
        super(context, themeResId);
        mActivity = activity;
        mImagePath = path;
        initDialog();
    }

    public SocialShareDialog(Context context, Activity activity, String path) {
        this(context, activity, path, R.style.full_screen_dialog);
    }

    public SocialShareDialog(Context context, Activity activity) {
        this(context, activity, "", R.style.full_screen_dialog);
    }

    @Override
    public void show() {
        if (mActivity == null || mActivity.isFinishing()) {
            return;
        }
        if (!isShowing()) {
            super.show();
            //set the dialog fullscreen
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.width = AppConfig.getScreenWidth(); //宽高可设置具体大小
            lp.height = AppConfig.getScreenHeight() - ScreenUtil.getStatusBarHeight(getContext());
            getWindow().setAttributes(lp);
        }
    }

    private void initDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_social_share, null);
        setContentView(view);
        mScreenIv = (ImageView) view.findViewById(R.id.iv_screen_shot);
        mScreenRl = (RelativeLayout) view.findViewById(R.id.rl_screen_shot);
        mLoadingLl = (LinearLayout) view.findViewById(R.id.ll_loading);
        mShareTypeGv = (GridView) view.findViewById(R.id.gv_share_type);
        mShareTypeGv.setOnItemClickListener(this);
        TextView cancelTv = (TextView) view.findViewById(R.id.tv_cancel);
        cancelTv.setOnClickListener(this);
        initImage();
        initShareType(); //分享方式
        setOnDismissListener(this);
    }

    private void initImage() {
        if (StringUtil.isNullOrEmpty(mImagePath)) {
            getScreenShot(); //当前页面截屏获取
        } else { //系统截屏
            mScreenBitmap = BitmapUtilLib.getBitmap(mImagePath);
            if (mScreenBitmap == null) {
                getScreenShot();
            } else {
                showLoading(false);
                mScreenIv.setImageBitmap(mScreenBitmap);
            }
        }
    }

    private void getScreenShot() {
        showLoading(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mScreenBitmap = ScreenUtil.takeScreenshot(mActivity);
                showLoading(false);
                mScreenIv.setImageBitmap(mScreenBitmap);
            }
        }, 1000);
    }

    /**
     * 是否展示loading
     *
     * @param show true：是  false：否
     */
    private void showLoading(boolean show) {
        mLoadingLl.setVisibility(show ? View.VISIBLE : View.GONE);
        mScreenRl.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void initShareType() {
        mShareList = getSocialTypeList();
        mShareTypeGv.setAdapter(new CommonAdapter<SocialTypeInfo>(getContext(), R.layout.list_item_share_type, mShareList) {
            @Override
            protected void convert(ViewHolder viewHolder, SocialTypeInfo item, int position) {
                viewHolder.setText(R.id.tv_share_item, item.appName);
                viewHolder.setCompoundDrawablesWithIntrinsicBounds(R.id.tv_share_item, 0, item.appIcon, 0, 0);
            }
        });
    }

    /**
     * 设置分享内容
     *
     * @return 分享渠道信息
     */
    private List<SocialTypeInfo> getSocialTypeList() {
        List<SocialTypeInfo> infoList = new ArrayList<>();
        if ((SocialConstant.ShareConstant.SHARE_TYPE_WECHAT & mShareChannel) > 0) {
            SocialTypeInfo weChat = new SocialTypeInfo();
            weChat.type = SocialConstant.WECHAT.ID;
            weChat.appName = getContext().getString(R.string.wechat);
            weChat.appIcon = R.drawable.share_to_weixin;
            infoList.add(weChat);
        }
        if ((SocialConstant.ShareConstant.SHARE_TYPE_CIRCLE & mShareChannel) > 0) {
            SocialTypeInfo circle = new SocialTypeInfo();
            circle.type = SocialConstant.WECHAT_CIRCLE.ID;
            circle.appName = getContext().getString(R.string.wechat_circle);
            circle.appIcon = R.drawable.share_to_pyq;
            infoList.add(circle);
        }
        if ((SocialConstant.ShareConstant.SHARE_TYPE_QQ & mShareChannel) > 0) {
            SocialTypeInfo tencent = new SocialTypeInfo();
            tencent.type = SocialConstant.TENCENT.ID;
            tencent.appName = getContext().getString(R.string.qq);
            tencent.appIcon = R.drawable.share_to_qq;
            infoList.add(tencent);
        }
        if ((SocialConstant.ShareConstant.SHARE_TYPE_ZONE & mShareChannel) > 0) {
            SocialTypeInfo zone = new SocialTypeInfo();
            zone.type = SocialConstant.TENCENT_ZONE.ID;
            zone.appName = getContext().getString(R.string.qq_zone);
            zone.appIcon = R.drawable.share_to_qzone;
            infoList.add(zone);
        }
        return infoList;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_cancel) {
            dismiss();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        if (mScreenIv != null) {
            mScreenIv.setImageBitmap(null);
        }
        if (mScreenBitmap != null && !mScreenBitmap.isRecycled()) {
            mScreenBitmap.recycle(); //回收
            mScreenBitmap = null;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (ExtendUtilLib.listIsNullOrEmpty(mShareList) || position >= mShareList.size()
                || mShareList.get(position) == null) {
            return;
        }
        SocialTypeInfo info = mShareList.get(position);
        switch (info.type) {
            case SocialConstant.WECHAT.ID:
                //fall through
            case SocialConstant.WECHAT_CIRCLE.ID:
                //fall through
            case SocialConstant.WECHAT_FAVORITE.ID:
                //fall through
            case SocialConstant.TENCENT.ID:
                //fall through
            case SocialConstant.TENCENT_ZONE.ID:
                if (mSocialManager == null) {
                    mSocialManager = new SocialManager(mActivity);
                }
                mSocialManager.shareImage(info.type, "", BitmapUtilLib.makeBitmapForShare(getContext(), mScreenBitmap,
                        ScreenUtil.dip2px(getContext(), TOP_BAR_HEIGHT)), this);
                mSocialManager.shareImage(info.type, "", mScreenBitmap, this); //暂时不拼接二维码 modify by zhangdong
                break;
            default:
                break;
        }
    }

    @Override
    public void onShareSuccess() {
        DialogUtilLib.showShortPromptToast(getContext(), getContext().getString(R.string.share_success));
    }

    @Override
    public void onShareFailed() {
        DialogUtilLib.showShortPromptToast(getContext(), getContext().getString(R.string.share_failed));
    }
}
