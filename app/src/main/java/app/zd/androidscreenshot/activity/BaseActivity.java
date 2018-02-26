package app.zd.androidscreenshot.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import app.zd.androidscreenshot.common.screenshot.ScreenShotListenManager;
import app.zd.androidscreenshot.event.BaseEvent;

/**
 * base activity
 * Created by zhangdong on 2018/2/26.
 */

public abstract class BaseActivity extends FragmentActivity {
    protected View mRootView;
    // 截屏分享
    private ScreenShotListenManager mShotListenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRootView = LayoutInflater.from(this).inflate(getContentView(), null);
        setContentView(mRootView);
        //有盟统计记录页面点
        mShotListenManager = ScreenShotListenManager.newInstance(this, this, mRootView);
        getIntentData();
        getIntentData(savedInstanceState);
        initPresenter();
        initHeaderView();
        initContentView();
        initFooterView();
        initSavedInstancesState(savedInstanceState);
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mShotListenManager.startListen();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mShotListenManager.stopListen();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BaseEvent event) {
        onEventMainThread(event);
    }

    protected abstract int getContentView();

    protected abstract void initPresenter();

    protected abstract void initData();

    protected void initContentView() {
    }

    protected void initHeaderView() {

    }

    private void initFooterView() {

    }

    protected void getIntentData() {

    }

    protected void getIntentData(Bundle savedInstanceState) {

    }

    protected void initSavedInstancesState(Bundle savedInstanceState) {

    }

    protected void onEventMainThread(BaseEvent event) {

    }
}
