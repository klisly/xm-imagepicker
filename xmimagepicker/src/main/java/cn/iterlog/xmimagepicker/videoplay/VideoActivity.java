package cn.iterlog.xmimagepicker.videoplay;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import cn.iterlog.xmimagepicker.Gallery;
import cn.iterlog.xmimagepicker.R;
import cn.iterlog.xmimagepicker.Utils.VideoRequestHandler;

public class VideoActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnVideoSizeChangedListener {
    private static String TAG = VideoActivity.class.getSimpleName();
    public static final int REQUEST_PICK = 5098;
    public String src;
    private ImageView playButton;
    private SurfaceView sv;
    private MediaPlayer mediaPlayer;
    private int svWidth;
    private int svHeight;
    private int vWidht;
    private int vHeight;
    private ImageView preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        initToolBar();
        src = getIntent().getStringExtra("src");
        Log.i(VideoActivity.class.getSimpleName(), "src:" + src);
        if (src == null) {
            Toast.makeText(getApplicationContext(), R.string.no_video, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        playButton = (ImageView) findViewById(R.id.iv_play);
        preview = (ImageView) findViewById(R.id.preview);
        sv = (SurfaceView) findViewById(R.id.sv);
        sv.getHolder().addCallback(this);
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(this, Uri.fromFile(new File(src)));
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    playButton.setVisibility(View.VISIBLE);
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.i(TAG, "position:" + mp.getCurrentPosition());
                    onCompletePlay();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            finish();
            return;
        }

        sv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    onPausePlay();
                } else {
                    onstartPlay();
                }
            }
        });

        mediaPlayer.setOnVideoSizeChangedListener(this);
        initSvSize();
        loadImagePreview();
        initChooseListener();
    }

    private void initChooseListener() {
        findViewById(R.id.choose)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(src)));
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
    }

    private void loadImagePreview() {
        Gallery.picasso.load(VideoRequestHandler.SCHEME_VIDEO + ":" + src).into(preview);
    }

    private void initSvSize() {
        sv.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                int tmpWidth = right - left;
                int tmpHeight = bottom - top;
                if (tmpHeight != svWidth || tmpHeight != svHeight) {
                    svWidth = tmpWidth;
                    svHeight = tmpHeight;
                    Log.i(VideoActivity.class.getSimpleName(), "sv size changed sw:" + svWidth + "sh:" + svHeight);
                    refreshSvSize();
                }
            }
        });
    }

    protected void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        onStopPlay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(VideoActivity.class.getSimpleName(), "svw:" + sv.getLayoutParams().height + "svh:" + sv.getLayoutParams().width);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop();
    }


    private void onCompletePlay() {
        if (mediaPlayer.getCurrentPosition() == mediaPlayer.getDuration()) {
            preview.setVisibility(View.VISIBLE);
        }
        playButton.setVisibility(View.VISIBLE);
    }

    private void onStopPlay() {
        playButton.setVisibility(View.VISIBLE);
        preview.setVisibility(View.VISIBLE);
        mediaPlayer.pause();
    }

    private void onPausePlay() {
        playButton.setVisibility(View.VISIBLE);
        mediaPlayer.pause();
    }

    private void onstartPlay() {
        mediaPlayer.start();
        playButton.setVisibility(View.INVISIBLE);
        preview.setVisibility(View.INVISIBLE);
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.video_preview);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mediaPlayer.setDisplay(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        if (vHeight != height || width != vWidht) {
            vHeight = height;
            vWidht = width;
            Log.i(VideoActivity.class.getSimpleName(), "has changed video size w:" + width + "video size h:" + height);
            refreshSvSize();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }


    private void refreshSvSize() {
        if (svWidth == 0 || svHeight == 0 || vWidht == 0 || vHeight == 0) {
            return;
        }

        float rate = vWidht * 1.0f / vHeight;
        Log.i(VideoActivity.class.getSimpleName(), "video w/h rate:" + rate);
        if (svHeight * rate > svWidth) {
            svHeight = (int) (svWidth / rate);
        } else {
            svWidth = (int) (svHeight * rate);
        }
        ViewGroup.LayoutParams params = sv.getLayoutParams();
        params.width = svWidth;
        params.height = svHeight;
        sv.setLayoutParams(params);
        params = preview.getLayoutParams();
        params.width = svWidth;
        params.height = svHeight;
        preview.setLayoutParams(params);

    }

}

