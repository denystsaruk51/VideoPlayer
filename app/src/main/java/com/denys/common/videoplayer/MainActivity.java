package com.denys.common.videoplayer;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.MediaController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_PERMISSION = 1;
    public static String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE
    };
    public static boolean isFirst = true;

    private android.widget.VideoView videoView;
    private String videoURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoView = findViewById(R.id.surfaceView);
    }
    private void init () {
        videoURL = "http://34.210.212.174/Yp650OuYGo-20200926075619.mp4";

        playVideo();
    }
    private void playVideo() {
        videoView.setVideoURI(Uri.parse(videoURL));

        final MediaController ctlr = new MediaController(this);
        ctlr.setMediaPlayer(videoView);
        videoView.setMediaController(ctlr);
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoView.start();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (isFirst) {
            isFirst = false;
            verifyStoragePermissions(this);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    init();
                }
            }, 1000);
        }
    }
    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission0 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission3 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_NETWORK_STATE);
        if (permission0 != PackageManager.PERMISSION_GRANTED
                || permission3 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS,
                    REQUEST_PERMISSION
            );
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    init();
                }
            }, 1000);
        }
    }
}