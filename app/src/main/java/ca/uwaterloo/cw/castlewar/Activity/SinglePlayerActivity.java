package ca.uwaterloo.cw.castlewar.Activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import ca.uwaterloo.cw.castlewar.Model.Item;
import ca.uwaterloo.cw.castlewar.Model.Level;
import ca.uwaterloo.cw.castlewar.Model.SystemData;
import ca.uwaterloo.cw.castlewar.Model.Unit;
import ca.uwaterloo.cw.castlewar.R;


public class SinglePlayerActivity extends AppCompatActivity {
    private MultithreadGameLogic gameLogic = null;
    private Bitmap levelBitmap = null;
    private DisplayMetrics mDisplayMetrics;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private MediaRecorder mMediaRecorder;
    private int mScreenDensity;
    private MediaProjectionManager mProjectionManager;
    private MediaProjectionCallback mMediaProjectionCallback;
    private static final int REQUEST_PERMISSIONS = 10;
    private static final int REQUEST_CODE = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleplayer);

        mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        mScreenDensity = mDisplayMetrics.densityDpi;
        mMediaRecorder = new MediaRecorder();
        mProjectionManager = (MediaProjectionManager) getSystemService
                (Context.MEDIA_PROJECTION_SERVICE);

        if (ContextCompat.checkSelfPermission(SinglePlayerActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SinglePlayerActivity.this,
                    new String[]{Manifest.permission
                            .WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSIONS);
        }

        prepareRecording();
    }

    @Override
    protected void onStart() {
        super.onStart();

        ImageView imageView = findViewById(R.id.LevelBackground);
        imageView.setImageBitmap(SystemData.scaleBitmap(R.drawable.plane_yellow, SystemData.getScreenWidth(), SystemData.getScreenHeight(),8));

        // Get the RecyclerView instance
        RecyclerView levelsRecyclerView = findViewById(R.id.LevelsRecyclerView);

        // Set the LayoutManager to be vertical (default)
        levelsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Set the adapter which will fill the data on the RecyclerView items
        levelsRecyclerView.setAdapter(new LevelsRecyclerViewAdapter(this));

        startRecording();
    }

    public void onBackPressed(View view) {
        super.onBackPressed();
    }

    public void startLevel(final Level level, final Unit[] unitInStockPlayer1, final Item[] itemInStockPlayer1) {
        setContentView(R.layout.game_screen);
        SystemData.setContext(getApplicationContext());
        SystemData.oneTimeThread.execute(new Runnable() {
            @Override
            public void run() {
                gameLogic = new MultithreadGameLogic(SinglePlayerActivity.this, level, unitInStockPlayer1, itemInStockPlayer1);
                gameLogic.onFirstStart();
            }
        });
    }

    public void onResume() {
        super.onResume();
        if (gameLogic != null)
            gameLogic.onResume();
    }

    public void onPause() {
        super.onPause();
        if (gameLogic != null)
            gameLogic.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyMediaProjection();
    }

    private void startRecording() {
        if (mMediaProjection == null) {
            startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
            return;
        }
        mVirtualDisplay = createVirtualDisplay();
        mMediaRecorder.start();
    }

    private void stopRecording() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        //mMediaRecorder.release(); //If used: mMediaRecorder object cannot
        // be reused again
        destroyMediaProjection();
    }

    private void prepareRecording() {
        int width = mDisplayMetrics.widthPixels;
        int height = mDisplayMetrics.heightPixels;

        try {
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mMediaRecorder.setOutputFile(getCacheDir().getAbsolutePath() + "/gameplay.mp4");
            //mMediaRecorder.setOutputFile("/dev/null");
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mMediaRecorder.setVideoEncodingBitRate(512 * 1000);
            mMediaRecorder.setVideoSize(width, height);
            mMediaRecorder.setVideoFrameRate(30);
            mMediaRecorder.setMaxFileSize(13000000);
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private VirtualDisplay createVirtualDisplay() {
        int width = mDisplayMetrics.widthPixels;
        int height = mDisplayMetrics.heightPixels;
        return mMediaProjection.createVirtualDisplay("Record",
                width, height, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mMediaRecorder.getSurface(), null /*Callbacks*/, null
                /*Handler*/);
    }

    private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            mMediaProjection = null;
            stopRecording();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mMediaProjectionCallback = new MediaProjectionCallback();
        mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
        mMediaProjection.registerCallback(mMediaProjectionCallback, null);
        mVirtualDisplay = createVirtualDisplay();
        mMediaRecorder.start();
    }

    private void destroyMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.unregisterCallback(mMediaProjectionCallback);
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }
}
