package app.zd.androidscreenshot.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import app.zd.androidscreenshot.R;
import app.zd.androidscreenshot.dialog.SocialShareDialog;

public class MainActivity extends Activity implements View.OnClickListener {
    private Button mScreenshotBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mScreenshotBtn = (Button) findViewById(R.id.btn_screenshot);
        mScreenshotBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_screenshot:
                //展示分享对话框
                SocialShareDialog dialog = new SocialShareDialog(this, this);
                dialog.show();
                break;
            default:
                break;
        }
    }
}
