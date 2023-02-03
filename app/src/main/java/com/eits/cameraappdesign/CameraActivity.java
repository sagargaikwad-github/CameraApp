package com.eits.cameraappdesign;


import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.CamcorderProfile;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.eits.cameraappdesign.model.SqliteModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity {

    ImageView backBTN, flashBTN;
    ImageView mRecordImageButton;
    Boolean mIsRecording = false;
    File mVideoFolder;

    String mVideoFileName;
    String mTempVideoFileName;


    ProgressDialog progressDialog;
    ArrayList<String> commandListStartRecord = new ArrayList<>();
    ArrayList<String> commandListStopRecord = new ArrayList<>();
    public boolean isListening = false;
    CountDownTimer countDownTimer;
    File mImageFolder;
    String mImageFileName;
    SpeechRecognizer mSpeechRecognizer;
    boolean micStatus;
    TextView recordingTime;

    String finalTime;

    ArrayList<Float> MinMaxAvgList;

    SqliteModel sqliteModel;
    int seconds;

    MediaRecorder mMediaRecorder;
    float Min, Max, Average;


    //This will make true if camera permissions are granted
    //and if true our surface texture and texture will be connect with camera
    boolean isSurfaceAvailable = false;

    // SharedPreferences sharedPreferences;

    private static final int REQUEST_CAMERA_PERMISSION_RESULT = 100;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT = 200;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT_ABOVE11 = 300;
    //private static final int REQUEST_MIC_PERMISSION_RESULT = 300;

    private static final int STATE_PREVIEW = 0;
    private static final int STATE_WAIT_LOCK = 1;
    private int mCaptureState = STATE_PREVIEW;


    TextView Counter;
    TextureView mTextureView;
    TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
            setUpCamera(i, i1);
            if (isSurfaceAvailable == true) {
                connectCamera();
            }

        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
            configureTransform(i, i1);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

        }
    };

    CameraDevice mCameraDevice;
    CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mCameraDevice = camera;


            if (mIsRecording) {

                createVideoFolder();

                // Toast.makeText(MainActivity.this, String.valueOf(flashStatus), Toast.LENGTH_SHORT).show();
                startRecord();

            }
            startPreview();

        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            // Toast.makeText(CameraActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
            camera.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            camera.close();
            mCameraDevice = null;
        }
    };

    private HandlerThread mBackgroundHandlerThread;
    private Handler mBackgroundHandler;

    String mCameraId;
    Size mPreviewSize;
    Size mVideoSize;

    Size mImageSize;
    View mdecorView;
    int CompID, FacID;
    String SiteLocation, Note;
    //private ImageReader mImageReader;
//    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
//        @Override
//        public void onImageAvailable(ImageReader imageReader) {
//            mBackgroundHandler.post(new ImageSaver(imageReader.acquireLatestImage()));
//        }
//    };

    private class ImageSaver implements Runnable {
        private final Image mImage;

        public ImageSaver(Image image) {
            mImage = image;
        }

        @Override
        public void run() {
            ByteBuffer byteBuffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);

            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(mImageFileName);
                fileOutputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }


    int mTotalRotation;

    CameraCaptureSession mPreviewCaptureSession;
    CameraCaptureSession.CaptureCallback mPreviewCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        private void process(CaptureResult captureResult) {
            switch (mCaptureState) {
                case STATE_PREVIEW:
                    break;
                case STATE_WAIT_LOCK:
                    mCaptureState = STATE_PREVIEW;
                    Integer afState = captureResult.get(CaptureResult.CONTROL_AF_STATE);
                    if (afState == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED ||
                            afState == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED) {


                    }

                    break;
            }
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);

            process(result);
        }
    };

    CameraCaptureSession mRecordCaptureSession;
    CameraCaptureSession.CaptureCallback mRecordCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        private void process(CaptureResult captureResult) {
            switch (mCaptureState) {
                case STATE_PREVIEW:
                    break;
                case STATE_WAIT_LOCK:
                    mCaptureState = STATE_PREVIEW;
                    Integer afState = captureResult.get(CaptureResult.CONTROL_AF_STATE);
                    if (afState == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED ||
                            afState == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED) {
                        //   Toast.makeText(CameraActivity.this, "AF Locked", Toast.LENGTH_SHORT).show();

                    }

                    break;
            }
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);

            process(result);
        }
    };

    private CaptureRequest.Builder mCaptureRequestBuilder;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private static class CompareSizeByArea implements Comparator<Size> {
        @Override
        public int compare(Size size, Size t1) {
            return Long.signum((long) size.getWidth() * size.getHeight() /
                    (long) t1.getWidth() * t1.getHeight());
        }
    }

    boolean flashStatus = false;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        if (hasFocus) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        checkCameraPermissions();

        MinMaxAvgList = new ArrayList<>();

        sqliteModel = new SqliteModel(CameraActivity.this);

        progressDialog = new ProgressDialog(this);

        mTextureView = findViewById(R.id.textureView);

        Counter = findViewById(R.id.Text);
        recordingTime = findViewById(R.id.cameraRecordTiming);

        mRecordImageButton = findViewById(R.id.recordBTN);

        backBTN = findViewById(R.id.backBTN);
        flashBTN = findViewById(R.id.flashBTN);

        commandListStartRecord.add("start recording");
        commandListStartRecord.add("start");

        commandListStopRecord.add("stop");
        commandListStopRecord.add("stop recording");

        createVideoFolder();


        mMediaRecorder = new MediaRecorder();

        //createSpeechRecognizer();


        Bundle getValues = getIntent().getExtras();
        CompID = getValues.getInt("CompID");
        FacID = getValues.getInt("FacID");
        SiteLocation = getValues.getString("SiteLocation");
        Note = getValues.getString("Note");

        mRecordImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED) {
                        if (mIsRecording) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        stopRecord();
                                    } catch (RuntimeException e) {
                                        //    Toast.makeText(CameraActivity.this, "Something error occured during save a file", Toast.LENGTH_SHORT).show();
                                    }
                                    // startPreview();
                                }
                            }, 500);
                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //  Toast.makeText(CameraActivity.this, "Recording Started", Toast.LENGTH_SHORT).show();
                                    mIsRecording = true;

                                    createVideoFolder();
                                    startRecord();
                                }
                            }, 500);

                        }
                    } else {
                        checkWriteStoragePermission();
                    }
                } else {
                    if (mIsRecording) {
                        mIsRecording = false;
                        mRecordImageButton.setImageResource(R.drawable.record_paused);

                        //toggleFlashModeRecord(flashStatus);

                        mMediaRecorder.stop();
                        mMediaRecorder.reset();
                        startPreview();
                    } else {
                        mIsRecording = true;
                        mRecordImageButton.setImageResource(R.drawable.record_resumed);
                        createVideoFolder();

                        startRecord();
                    }
                }
            }
        });


        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsRecording) {
                    stopRecord();
                } else {
                    onBackPressed();
                    onBackPressed();
                }
            }
        });


        flashBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flashStatus) {
                    flashStatus = false;
                    flashBTN.setImageResource(R.drawable.ic_flash_off);
                } else {
                    flashStatus = true;
                    flashBTN.setImageResource(R.drawable.ic_flash_on);
                }

                if (mIsRecording) {
                    try {
                        toggleFlashModeRecord(flashStatus);
                    } catch (Exception e) {
                        //   Toast.makeText(CameraActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    toggleFlashMode(flashStatus);
                }

            }
        });
    }

    private void hideSystemUI() {
        mdecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

    }

    private void stopRecord() {

        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setMessage("Processing Please Wait");
        progressDialog.setCancelable(false);


//        Window window = progressDialog.getWindow();
//        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
//        window.getDecorView().setSystemUiVisibility(uiOptions);

        mdecorView = progressDialog.getWindow().getDecorView();

        progressDialog.show();

        hideSystemUI();

        countDownTimer.cancel();

        try {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mRecordImageButton.setImageResource(R.drawable.record_paused);
            mIsRecording = false;
        } catch (Exception e) {

        }

        Max = Float.parseFloat(Counter.getText().toString());

        String path = "/storage/emulated/0/Download/InspRec";
        String tempPath = null;
        File file = new File(path);
        File[] files = file.listFiles();

        if (files != null) {
            for (File file1 : files) {
                if (file1.getPath().contains(mTempVideoFileName)) {
                    tempPath = file1.getPath();
                    break;
                }
            }

            try {
                TextOnVideo(tempPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


    private void TextOnVideo(String fileSavePath) throws IOException {
        createVideoFileName();

        Log.e("InputFile", fileSavePath);
        Log.e("OutputFile", mVideoFileName);

        // String cmd = "-y -i "+fileSavePath+" -vf drawtext=text="+MinMaxAvgList.get(0)+":x=15:y=15:fontfile=/system/fonts/Roboto-Regular.ttf:fontsize=100:fontcolor=yellow:enable='between(t,5,10)' " +FFmPegOutput;
        // String cmd = "-y -i " + fileSavePath + " -vf  fps=25 drawtext=text='Hello':x=15:y=15:fontfile=/system/fonts/Roboto-Regular.ttf:fontsize=100:fontcolor=yellow:box=1:boxcolor=black@0.5:boxborderw=5:x=40:y=40:enable='between(t\\,1\\,5)' -c copy " + mVideoFileName;

        //String a = "-y -i " + fileSavePath + " -framerate 25 -vf [in]" + text() + "[out] " + mVideoFileName;


        String a = "-y -i " + fileSavePath + " -framerate 60 -vf [in]" + text() + "[out] " + mVideoFileName;
        Log.e("text Value", a);

        long executionId = FFmpeg.executeAsync(a, (executionId1, returnCode) -> {
            if (returnCode == Config.RETURN_CODE_SUCCESS) {
                addDataInSqlite();
                progressDialog.dismiss();
                Toast.makeText(this, "Video Saved Sucessfully", Toast.LENGTH_SHORT).show();

                //broadcastreceiever for finishing previous activity
                Intent intent = new Intent("Component_Activity_finish");
                sendBroadcast(intent);

                onBackPressed();
            } else if (returnCode == Config.RETURN_CODE_CANCEL) {
                Log.e(TAG, "Async command execution cancelled by user.");
                progressDialog.dismiss();
                Toast.makeText(this, "Some error Occured 1", Toast.LENGTH_SHORT).show();
                onBackPressed();
            } else {
                Log.e(TAG, String.format("Async command execution failed with returnCode=%d.", returnCode));
                Toast.makeText(this, "Some error Occured 2", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                onBackPressed();
            }
        });
        Log.e(TAG, "execFFmpegMergeVideo executionId-" + executionId);
    }

    private void addDataInSqlite() {


        String path = "/storage/emulated/0/Download/InspRec";
        String tempPath = null;
        File file = new File(path);
        File[] files = file.listFiles();

        if (files != null) {
            for (File file1 : files) {
                if (file1.getPath().contains(mVideoFileName)) {
                    tempPath = file1.getPath();
                    break;
                }
            }
            File filePath = new File(tempPath);
            String fileName = filePath.getName();
            String dateTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").
                    format(new Date(new File(tempPath).lastModified()));
            int compId = CompID;
            int facId = FacID;
            String siteLocation = SiteLocation;

            float min = MinMaxAvgList.get(0);
            for (int i = 0; i < MinMaxAvgList.size(); i++) {
                if (min > MinMaxAvgList.get(i))
                    min = MinMaxAvgList.get(i);
            }

            float max = MinMaxAvgList.get(0);
            for (int i = 0; i < MinMaxAvgList.size(); i++) {
                if (max < MinMaxAvgList.get(i))
                    max = MinMaxAvgList.get(i);
            }

            float total = 0;
            float avg;
            for (int i = 0; i < MinMaxAvgList.size(); i++) {
                total += MinMaxAvgList.get(i);

            }
            avg = total / MinMaxAvgList.size();
            float average = avg;

            System.out.println(Arrays.asList(MinMaxAvgList));

            String fileSavePath = tempPath;
            //float duration=fileDuration(filePath.getAbsolutePath());
            int seconds = fileDuration(filePath.getAbsolutePath());

            String duration;
            int second = seconds % 60;
            int minute = seconds / 60 % 60;
            int hour = seconds / (60 * 60) % 24;

            if (hour > 0) {
                duration = hour + " hr " + minute + " min";
            } else if (minute > 0) {
                duration = minute + " min " + second + " sec";
            } else {
                duration = second + " sec";
            }

            String note = Note;
            sqliteModel.addInVideoFile(fileName, dateTime, compId, facId, siteLocation, max, min, average, fileSavePath, duration, note);

//            sharedPreferences = getSharedPreferences("Component_Written_Data", MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putInt("SpinnerPosition", 0);
//            editor.putString("SiteLocation", "");
//            editor.putString("Note", "");
//            editor.apply();

            File file1 = new File(mTempVideoFileName);
            file1.delete();
        }
    }

    private String text() {
        String text = "";
        for (int i = 0; i < MinMaxAvgList.size(); i++) {
            Log.e("I Value", String.valueOf(i));
//            text = "drawtext=text="+MinMaxAvgList.get(i)+":x=15:y=15:fontfile=/system/fonts/Roboto-Regular.ttf:fontsize=100:fontcolor=yellow:box=1:boxcolor=black@0.5:boxborderw=5:x=40:y=40:enable='between(t\\,"+i+"\\,"+(i+1)+")'";
            if (i < MinMaxAvgList.size() - 1) {
                text = text + "drawtext=text=" + MinMaxAvgList.get(i) + ":x=15:y=15:fontfile=/system/fonts/Roboto-Regular.ttf:fontsize=100:fontcolor=yellow:box=1:boxcolor=black@0.5:boxborderw=5:x=40:y=40:enable='between(t\\," + i + "\\," + (i + 1) + ")',";
            } else {
                text = text + "drawtext=text=" + MinMaxAvgList.get(i) + ":x=15:y=15:fontfile=/system/fonts/Roboto-Regular.ttf:fontsize=100:fontcolor=yellow:box=1:boxcolor=black@0.5:boxborderw=5:x=40:y=40:enable='between(t\\," + i + "\\," + (i + 1) + ")'";
            }
        }
        return text;
    }

    private int fileDuration(String absolutePath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(String.valueOf(Uri.parse(absolutePath)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int d = Integer.parseInt(duration);
        int seconds = (d / 1000);
        retriever.release();
        return seconds;
    }

    private void checkCameraPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                isSurfaceAvailable = true;
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION_RESULT);
            }
        } else {
            isSurfaceAvailable = true;
        }
    }


    private void toggleFlashMode(boolean flashStatus) {
////        if(mRecordCaptureSession==null)
////        {
        try {
            if (flashStatus) {
                mCaptureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
                //mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
            } else {
                mCaptureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
                //mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_OFF);
            }
            mPreviewCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(), null, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void toggleFlashModeRecord(boolean flashStatus) {
        try {
            if (flashStatus) {
                mCaptureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
                //mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
            } else {
                mCaptureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
                //mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_OFF);
            }
            mRecordCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(), null, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
//
//
    }

    @Override
    protected void onResume() {
        super.onResume();


        startBackgroundThread();


        if (mTextureView.isAvailable()) {
            setUpCamera(mTextureView.getWidth(), mTextureView.getHeight());
            if (isSurfaceAvailable == true) {
                connectCamera();
            }
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }

        countDown();
        countDownTimer.start();

    }

    private void countDown() {
        countDownTimer = new CountDownTimer(50000, 1000) {
            @Override
            public void onTick(long l) {
                int seconds = (int) (l / 1000);
                Counter.setText(String.valueOf(seconds));

                if (mIsRecording) {
                    MinMaxAvgList.add(Float.valueOf(Counter.getText().toString()));
                }
            }

            @Override
            public void onFinish() {
                countDown();
                countDownTimer.start();
            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT_ABOVE11) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isSurfaceAvailable = true;
            } else {
                if (shouldShowRequestPermissionRationale(permissions[0])) {
                    //  Toast.makeText(this, "Permission Required", Toast.LENGTH_SHORT).show();
                } else {
                    permissionDialog("Storage");
                }
            }
        }
        if (requestCode == REQUEST_CAMERA_PERMISSION_RESULT) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isSurfaceAvailable = true;
            } else {
                if (shouldShowRequestPermissionRationale(permissions[0])) {
                    // Toast.makeText(this, "Camera Permission Required", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    permissionDialog("Camera");
                }
            }

        }
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                if (shouldShowRequestPermissionRationale(permissions[0])) {
                    //  Toast.makeText(this, "Storage Permission Required", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    permissionDialog("Storage");
                }
            }
        }
//        if (requestCode == REQUEST_MIC_PERMISSION_RESULT) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            } else {
//                if (shouldShowRequestPermissionRationale(permissions[0])) {
//                    Toast.makeText(this, "Permission Required to use this feature", Toast.LENGTH_SHORT).show();
//                } else {
//                    permissionDialog("Voice");
//                }
//            }
//
////            if (grantResults[1] != PackageManager.PERMISSION_GRANTED) {
////                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[1])) {
////                    Toast.makeText(this, "Audio Permission Required", Toast.LENGTH_SHORT).show();
////                } else {
////                    permissionDialog("Audio");
////                }
////            }
//        }
    }


    @Override
    protected void onPause() {
        super.onPause();

//        sharedPreferences = getSharedPreferences("Component_Written_Data", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putInt("SpinnerPosition", 0);
//        editor.putString("SiteLocation", "");
//        editor.putString("Note", "");
//        editor.apply();

        countDownTimer.cancel();

        stopBackgroundThread();

        //flashStatus=false;
        //mIsRecording=false;
        if (mIsRecording == true) {
            stopRecord();
        }

        mIsRecording = false;
        mRecordImageButton.setImageResource(R.drawable.record_paused);
        //toggleFlashModeRecord(flashStatus);

        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.destroy();
        }

        try {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
        } catch (Exception e) {

        }
//        Toast.makeText(this, mIsRecording.toString(), Toast.LENGTH_SHORT).show();

        flashStatus = false;
        if (flashStatus) {
            flashBTN.setImageResource(R.drawable.ic_flash_on);
        } else {
            flashBTN.setImageResource(R.drawable.ic_flash_off);
        }
        CloseCamera();
    }


    static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }

    private void setUpCamera(int width, int height) {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);

                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }


                StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                int deviceOrientation = getWindowManager().getDefaultDisplay().getRotation();

                mTotalRotation = sensorToDeviceOrientation(cameraCharacteristics, deviceOrientation);

//                boolean swappedDimensions = false;
//                switch (deviceOrientation) {
//                    case Surface.ROTATION_90:
//                    case Surface.ROTATION_270:
//                            swappedDimensions = true;
//                        break;
//                }


//                int rotateWidth = width;
//                int rotateHeight = height;

                int rotateWidth = width;
                int rotateHeight = height;

//                if (swappedDimensions) {
//                     rotateWidth = height;
//                     rotateHeight = width;
//                }


//                boolean swappedDimensions = false;
//                switch (deviceOrientation) {
//                    case Surface.ROTATION_0:
//                    case Surface.ROTATION_180:
//                        if (mTotalRotation == 90 || mTotalRotation == 270) {
//                            swappedDimensions = true;
//                        }
//                        break;
//                    case Surface.ROTATION_90:
//                    case Surface.ROTATION_270:
//                        if (mTotalRotation == 0 || mTotalRotation == 180) {
//                            swappedDimensions = true;
//                        }
//                        break;
//                    default:
//                        Log.e(TAG, "Display rotation is invalid: " + mTotalRotation);
//                }


//                int rotateWidth = width;
//                int rotateHeight = height;
//
//                if (swappedDimensions) {
//                    rotateWidth = height;
//                    rotateHeight = width;
//                }

                boolean swapRotation = mTotalRotation == 90 || mTotalRotation == 270;
                if (swapRotation) {
                    rotateWidth = height;
                    rotateHeight = width;
                }

                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), rotateWidth, rotateHeight);
                mVideoSize = chooseOptimalSize(map.getOutputSizes(MediaRecorder.class), rotateWidth, rotateHeight);
                mImageSize = chooseOptimalSize(map.getOutputSizes(ImageFormat.JPEG), rotateWidth, rotateHeight);
                //mImageReader = ImageReader.newInstance(mImageSize.getWidth(), mImageSize.getHeight(), ImageFormat.JPEG, 1);
                //  mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler);

//                Point displaySize = new Point();
//                MainActivity.this.getWindowManager().getDefaultDisplay().getSize(displaySize);
//                int rotatedPreviewWidth = width;
//                int rotatedPreviewHeight = height;
//
//                if (swappedDimensions) {
//                    rotatedPreviewWidth = height;
//                    rotatedPreviewHeight = width;
//
//                }

//                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
//                        rotateWidth,rotateHeight);
//                mVideoSize = chooseOptimalSize(map.getOutputSizes(MediaRecorder.class), rotateWidth, rotateHeight);
//                mImageSize = chooseOptimalSize(map.getOutputSizes(ImageFormat.JPEG), rotateWidth, rotateHeight);
//                mImageReader=ImageReader.newInstance(mImageSize.getWidth(),mImageSize.getHeight(),ImageFormat.JPEG,1);
//                mImageReader.setOnImageAvailableListener(mOnImageAvailableListener,mBackgroundHandler);

                // We fit the aspect ratio of TextureView to the size of preview we picked.

                mCameraId = cameraId;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
        }
    }

    private void configureTransform(int viewWidth, int viewHeight) {
        Activity activity = CameraActivity.this;
        if (null == mTextureView || null == mPreviewSize || null == activity) {
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();

        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
            // matrix.postRotate(0,centerX,centerY);

        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }

        mTextureView.setTransform(matrix);

    }

//    @Override
//    public void onConfigurationChanged(@NonNull Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//
//        boolean isLandscape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;
//
//        int degrees = 0;
//        switch (.getRotation()){
//            case Surface.ROTATION_0:
//                degrees = isLandscape? 0 : 90;
//                break;
//            case Surface.ROTATION_90:
//                degrees = isLandscape? 0 : 270;
//                break;
//            case Surface.ROTATION_180:
//                degrees = isLandscape? 180 : 270;
//                break;
//            case Surface.ROTATION_270:
//                degrees = isLandscape? 180 : 90;
//                break;
//        }
//        mCa.rotateDisplay(degrees, isLandscape);
//    }

    //CameraPermissions
    @SuppressLint("MissingPermission")
    private void connectCamera() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraManager.openCamera(mCameraId, mCameraDeviceStateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    private void permissionDialog(String name) {
        new AlertDialog.Builder(CameraActivity.this)
                .setTitle(name + " permission Denied ")
                .setMessage(name + " permission not granted. Please grant " + name + " permission from app settings.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No, thanks", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void closePreviewSession() {
        if (mPreviewCaptureSession != null) {
            mPreviewCaptureSession.close();
            mPreviewCaptureSession = null;
        }
    }

    private void startRecord() {


        try {
            closePreviewSession();
            setUpMediaRecorder();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mRecordImageButton.setImageResource(R.drawable.record_resumed);

        SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();

        surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

        Surface previewSurface = new Surface(surfaceTexture);
        Surface recordSurface = mMediaRecorder.getSurface();

        try {
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        mCaptureRequestBuilder.addTarget(previewSurface);
        mCaptureRequestBuilder.addTarget(recordSurface);

        if (flashStatus) {
            mCaptureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
        } else {
            mCaptureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
        }

        try {
            mCameraDevice.createCaptureSession(Arrays.asList(previewSurface, recordSurface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            mPreviewCaptureSession = cameraCaptureSession;
                            mRecordCaptureSession = cameraCaptureSession;
                            try {
                                RecordingTimer();
                                cameraCaptureSession.setRepeatingRequest(
                                        mCaptureRequestBuilder.build(), null, null);
                            } catch (CameraAccessException e) {
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                        }
                    }, null);
        } catch (CameraAccessException e) {
        }


    }


    private void RecordingTimer() {

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                String time = String.format(Locale.getDefault(),
                        "%02d:%02d:%02d", hours,
                        minutes, secs);


                if (mIsRecording) {
                    seconds++;
                    recordingTime.setText(time);
                    finalTime = recordingTime.getText().toString();
                } else {
                    // recordingTime.setText("00:00:00");
                    recordingTime.setText(finalTime);
                }
                handler.postDelayed(this, 1000);
            }
        });

    }


    private void startPreview() {
        SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();


        surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Surface previewSurface = new Surface(surfaceTexture);


        try {
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mCaptureRequestBuilder.addTarget(previewSurface);


            if (flashStatus) {
                mCaptureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
            } else {
                mCaptureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
            }

            mCameraDevice.createCaptureSession(Arrays.asList(previewSurface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            mPreviewCaptureSession = cameraCaptureSession;
                            try {
                                mPreviewCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(),
                                        null, mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                            //   Toast.makeText(CameraActivity.this, "Unable to Preview", Toast.LENGTH_SHORT).show();
                        }
                    }, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }


    private void CloseCamera() {
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    private void startBackgroundThread() {
        mBackgroundHandlerThread = new HandlerThread("Camera2API");
        mBackgroundHandlerThread.start();
        mBackgroundHandler = new Handler(mBackgroundHandlerThread.getLooper());
    }

    private void stopBackgroundThread() {
        mBackgroundHandlerThread.quitSafely();
        try {
            mBackgroundHandlerThread.join();
            mBackgroundHandlerThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private static int sensorToDeviceOrientation(CameraCharacteristics cameraCharacteristics, int deviceOrientation) {
        int sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        deviceOrientation = ORIENTATIONS.get(deviceOrientation);
        return (sensorOrientation + deviceOrientation * 1 + 360) % 360;

    }

    private static Size chooseOptimalSize(Size[] choices, int width, int height) {
        List<Size> bigEnough = new ArrayList<Size>();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * height / width &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizeByArea());
        } else {
            return choices[0];
        }
    }


    private void createVideoFolder() {
        File movieFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        mVideoFolder = new File(movieFile, "InspRec");
        if (!mVideoFolder.exists()) {
            mVideoFolder.mkdirs();
        }
    }


    private void createVideoFileName() throws IOException {
        String timestamp = new SimpleDateFormat("YYYYMMDD_HHmmss").format(new Date());
        String fileName = "ECG" + timestamp;

        String filePath = mVideoFolder + File.separator + fileName + ".mp4";
        File file = new File(filePath);
        mVideoFileName = file.getAbsolutePath();
    }

    private void createTempFFmPEGVideoFileName() throws IOException {
        String timestamp = new SimpleDateFormat("YYYYMMDD_HHmmss").format(new Date());
        String fileName = "ECG-ffmpeg" + timestamp;

        String filePath = mVideoFolder + File.separator + fileName + ".mp4";
        File file = new File(filePath);
        mTempVideoFileName = file.getAbsolutePath();
    }

    private void checkWriteStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
            } else {
                requestPermissions(new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT_ABOVE11);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {

            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //  Toast.makeText(this, "Please grant Permission", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_RESULT);
            }
        } else {
        }
    }

    @SuppressLint("NewApi")
    private void setUpMediaRecorder() throws IOException {

        try {
            createTempFFmPEGVideoFileName();
        } catch (Exception e) {

        }

        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);

        CamcorderProfile camcorderProfile;

//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
//            camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
//        } else {
//            camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_1080P);
//        }

        camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        int targetVideoBitRate = camcorderProfile.videoBitRate;

        mMediaRecorder.setOutputFile(mTempVideoFileName);

        mMediaRecorder.setVideoEncodingBitRate(targetVideoBitRate);

        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setVideoFrameRate(camcorderProfile.videoFrameRate);
        //mMediaRecorder.setVideoFrameRate(20);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);


        System.out.println("Width : " + mVideoSize.getWidth() + " Height : " + mVideoSize.getHeight());
        System.out.println("Width : " + camcorderProfile.videoFrameWidth + " Height : " + camcorderProfile.videoFrameHeight);


//        Camera mCamera = Camera.open();
//        Camera.Parameters params = mCamera.getParameters();
//
//        List<Camera.Size> sizes = params.getSupportedPictureSizes();
//
//        Camera.Size mSize = null;
//        for (Camera.Size size : sizes) {
//            Log.i(TAG, "Available resolution: "+size.width+" "+size.height);
//            mSize = size;
//        }
//
//        System.out.println("Size Width : "+mSize.width +"Size Height : "+mSize.height);

        if (mVideoSize.getWidth() < 1080) {
            mMediaRecorder.setVideoSize(960, 720);
        } else if (mVideoSize.getWidth() <= 1280) {
            mMediaRecorder.setVideoSize(1280, 720);
        } else {
            mMediaRecorder.setVideoSize(1920, 1080);
        }


        //mMediaRecorder.setVideoSize(camcorderProfile.videoFrameWidth, camcorderProfile.videoFrameHeight);
        // mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());


        mMediaRecorder.prepare();

        mMediaRecorder.start();


        //Sensor Orientation While Recording
//        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
//        switch (mSensorOrientation) {
//            case SENSOR_ORIENTATION_DEFAULT_DEGREES:
//                mMediaRecorder.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation));
//                break;
//            case SENSOR_ORIENTATION_INVERSE_DEGREES:
//                mMediaRecorder.setOrientationHint(INVERSE_ORIENTATIONS.get(rotation));
//                break;
//        }


    }


    @Override
    public void onBackPressed() {
        if (progressDialog.isShowing()) {
            Toast.makeText(this, "Please Wait Video Processing needs to complete", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }

    }
}