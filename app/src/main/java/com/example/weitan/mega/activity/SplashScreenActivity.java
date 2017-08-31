package com.example.weitan.mega.activity;

import android.os.Bundle;
import android.os.Handler;

import com.github.silvestrpredko.dotprogressbar.DotProgressBar;
import com.kanmanus.kmutt.sit.ijoint.MyApplication;
import com.kanmanus.kmutt.sit.ijoint.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashScreenActivity extends BaseActivity {
    Handler handler;
    Runnable runnable;
    long delay_time;
    long time = 1400L;
    @BindView(R.id.dot_progress_bar)
    DotProgressBar dotProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                if (MyApplication.getInstance().getSession() != null) {
                    navigator.navigateToMain(SplashScreenActivity.this);
                }
                else {
                    navigator.navigateToLogin(SplashScreenActivity.this);
                }
                finish();
            }
        };
    }


    public void onResume() {
        super.onResume();
        delay_time = time;
        handler.postDelayed(runnable, delay_time);
        time = System.currentTimeMillis();
    }

    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        time = delay_time - (System.currentTimeMillis() - time);
    }
}
