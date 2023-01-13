package com.eits.cameraappdesign;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoPlayActivity extends AppCompatActivity {
    MediaController mediaController;
    VideoView videoView;
    int startPosition;
    int stopPosition = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        Bundle b = getIntent().getExtras();
        String VideoUrl = b.getString("VideoUrl");

        videoView = findViewById(R.id.videoView);
        Uri uri = Uri.parse(VideoUrl);
        videoView.setVideoURI(uri);

        mediaController = new MediaController(this);
        // mediaController.setAnchorView(videoView);
        mediaController.setMediaPlayer(videoView);
        mediaController.show(1000);
        videoView.setMediaController(mediaController);
        videoView.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (stopPosition != -1) {
            videoView.seekTo(stopPosition);
        }

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                AlertDialog.Builder build = new AlertDialog.Builder(VideoPlayActivity.this);
                build.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                });
                AlertDialog alt = build.create();
                alt.setMessage("Can't Play this Video");
                alt.show();
                return true;
            }
        });

    }



    @Override
    protected void onPause() {
        super.onPause();
        stopPosition = videoView.getCurrentPosition();

    }



    @Override
    public void onBackPressed() {

       // videoView.setMediaController(null);

        finish();
    }
}