package com.denys.common.videoplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_PERMISSION = 1;
    public static String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE
    };
    public static boolean isFirst = true;

    private android.widget.VideoView videoView;
    private String videoPath;
    private String videoURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoView = findViewById(R.id.surfaceView);
    }
    private void init () {
        videoURL = "http://34.210.212.174/Yp650OuYGo-20200926075619.mp4";
        videoPath = Environment.getExternalStorageDirectory() + "/Download/test.mp4";

        downloadFile();
//        playVideo();
    }
    private void playVideo() {
        videoView.setVideoURI(Uri.parse(videoURL));
        videoView.setVideoPath(videoPath);

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
    private void downloadFile() {
        BaseTask.run(new BaseTask.TaskListener() {
            @Override
            public Object onTaskRunning(int taskId, Object object) {
                try {
                    URL u = new URL(videoURL);
                    URLConnection cnx = u.openConnection();
                    cnx.connect();
                    int lenghtOfFile = cnx.getContentLength();
                    InputStream is = u.openStream();
                    FileOutputStream fos = new FileOutputStream(videoPath);

                    byte data[] = new byte[1024 * 1024];

                    int count = 0;
                    long total = 0;
                    int progress = 0;

                    while ((count=is.read(data)) != -1)
                    {
                        total += count;
                        int progress_temp = (int)total*100/lenghtOfFile;
                        if(progress_temp%10 == 0 && progress != progress_temp){
                            progress = progress_temp;
                        }
                        fos.write(data, 0, count);
                    }

                    is.close();
                    fos.close();
                } catch (IOException e) {
                    Log.d("Error....", e.toString());
                }
                return null;
            }
            @Override
            public void onTaskResult(int taskId, Object result) {
                playVideo();
            }
            @Override
            public void onTaskPrepare(int taskId, Object data) { }
            @Override
            public void onTaskProgress(int taskId, Object... values) {}
            @Override
            public void onTaskCancelled(int taskId) { }
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