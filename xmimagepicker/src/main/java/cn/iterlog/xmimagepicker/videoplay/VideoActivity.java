package cn.iterlog.xmimagepicker.videoplay;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
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

import java.io.IOException;

import cn.iterlog.xmimagepicker.R;

public class VideoActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnVideoSizeChangedListener {
    public static final int REQUEST_PICK = 7098;
    public Uri src;
    private ImageView playButton;
    private SurfaceView sv;
    private MediaPlayer mediaPlayer;
    private int status = 0; // unplay 0 playing 1 pause 2
    private boolean isAuto = true;
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
        src = getIntent().getParcelableExtra("src");
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
            mediaPlayer.setDataSource(this, src);
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
                if (status == 1 && mediaPlayer.isPlaying()) {
                    isAuto = false;
                    onPausePlay();
                } else {
                    onstartPlay();
                }
            }
        });

        mediaPlayer.setOnVideoSizeChangedListener(this);
        initSvSize();
    }

    private void initSvSize() {
        sv.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                int tmpWidth = right - left;
                int tmpHeight = bottom - top;
                if(tmpHeight != svWidth || tmpHeight != svHeight ){
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
            playButton.setVisibility(View.VISIBLE);
            status = 0;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        onPausePlay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (status == 2 && isAuto) {
            onstartPlay();
        }

        Log.i(VideoActivity.class.getSimpleName(), "svw:" + sv.getLayoutParams().height + "svh:" + sv.getLayoutParams().width);

    }

    private void onCompletePlay() {
        playButton.setVisibility(View.VISIBLE);
    }

    private void onPausePlay() {
        playButton.setVisibility(View.VISIBLE);
        status = 2;
        mediaPlayer.pause();
    }

    private void onstartPlay() {
        mediaPlayer.start();
        status = 1;
        playButton.setVisibility(View.INVISIBLE);
        isAuto = true;
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
    protected void onDestroy() {
        super.onDestroy();
        stop();
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        if(vHeight != height || width != vWidht){
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
        if(svWidth == 0 || svHeight == 0 || vWidht == 0 || vHeight == 0){
            return;
        }

        float rate = vWidht * 1.0f / vHeight;
        Log.i(VideoActivity.class.getSimpleName(), "video w/h rate:" + rate);
        if(svHeight * rate > svWidth){
            svHeight = (int) (svWidth / rate);
        } else {
            svWidth =(int) (svHeight * rate);
        }
        ViewGroup.LayoutParams params = sv.getLayoutParams();
        params.width = svWidth;
        params.height = svHeight;
        sv.setLayoutParams(params);

    }
}

