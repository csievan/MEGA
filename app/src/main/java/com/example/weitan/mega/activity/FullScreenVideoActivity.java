package com.example.weitan.mega.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.kanmanus.kmutt.sit.ijoint.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FullScreenVideoActivity extends AppCompatActivity implements EasyVideoCallback {
    public static Intent callingIntent(Context context, Uri uri, int position){
        Intent intent = new Intent(context,FullScreenVideoActivity.class);
        intent.putExtra(INTENT_PARAM_URI,uri);
        intent.putExtra(INTENT_PARAM_VIDEO_POSITION,position);
        return intent;
    }
    public static final String INTENT_PARAM_URI = "INTENT_PARAM_URI";
    public static final String INTENT_PARAM_VIDEO_POSITION = "INTENT_PARAM_VIDEO_POSITION";

    @BindView(R.id.videoPlayer)
    EasyVideoPlayer videoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_video);
        ButterKnife.bind(this);

        Uri uri = getIntent().getParcelableExtra(INTENT_PARAM_URI);
        int position = getIntent().getIntExtra(INTENT_PARAM_VIDEO_POSITION,0);
        videoPlayer.setCallback(this);
        videoPlayer.setSource(uri);
        videoPlayer.setInitialPosition(position);
    }

    @Override
    public void onStarted(EasyVideoPlayer player) {

    }

    @Override
    public void onPaused(EasyVideoPlayer player) {

    }

    @Override
    public void onPreparing(EasyVideoPlayer player) {

    }

    @Override
    public void onPrepared(EasyVideoPlayer player) {

    }

    @Override
    public void onBuffering(int percent) {

    }

    @Override
    public void onError(EasyVideoPlayer player, Exception e) {

    }

    @Override
    public void onCompletion(EasyVideoPlayer player) {

    }

    @Override
    public void onRetry(EasyVideoPlayer player, Uri source) {

    }

    @Override
    public void onSubmit(EasyVideoPlayer player, Uri source) {

    }
}
