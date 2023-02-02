package com.eits.cameraappdesign;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


public class VideoPlayActivity extends AppCompatActivity {
    MediaController mediaController;
    VideoView videoView;
    int startPosition;
    int stopPosition = -1;
    Handler handler;
    Runnable runnable;



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



//        mediaController.setAnchorView(videoView);
//        mediaController.setMediaPlayer(videoView);
//        mediaController.show(1000);

        //  mediaController.setAnchorView(videoView);


        videoView.setVideoURI(uri);
        videoView.requestFocus();

        videoView.start();


    }


    @Override
    protected void onResume() {
        super.onResume();

        mediaController = new MediaController(this);
         handler= new Handler();
         runnable=new Runnable() {
            @Override
            public void run() {
                videoView.setMediaController(mediaController);
            }
        };
        handler.postDelayed(runnable,1000);


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

        handler.removeCallbacks(runnable);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }
}