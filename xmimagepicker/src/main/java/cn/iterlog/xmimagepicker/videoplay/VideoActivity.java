package cn.iterlog.xmimagepicker.videoplay;

import android.animation.Animator;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import cn.iterlog.xmimagepicker.BaseActivity;
import cn.iterlog.xmimagepicker.Gallery;
import cn.iterlog.xmimagepicker.R;
import cn.iterlog.xmimagepicker.Utils.VideoRequestHandler;

public class VideoActivity extends BaseActivity implements SurfaceHolder.Callback, MediaPlayer.OnVideoSizeChangedListener {
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
        sv.setBackgroundDrawable(getResources().getDrawable(R.drawable.video_texture));
        findViewById(R.id.activity_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    onPausePlay();
                } else {
                    onstartPlay();
                }
            }
        });
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

    @Override
    protected void onPause() {
        super.onPause();
        onStopPlay();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(VideoActivity.class.getSimpleName(), "svw:" + sv.getLayoutParams().height + "svh:" + sv.getLayoutParams().width);
        findViewById(R.id.activity_video).setBackgroundColor(getResources().getColor(R.color.black_p50));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void onCompletePlay() {
        if (mediaPlayer != null && mediaPlayer.getCurrentPosition() == mediaPlayer.getDuration()) {
            preview.setVisibility(View.VISIBLE);
            onStopPlay();
        }
        playButton.setVisibility(View.VISIBLE);
    }

    private void onPausePlay() {
        playButton.setVisibility(View.VISIBLE);
        mediaPlayer.pause();
    }

    private void onstartPlay() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            playButton.setVisibility(View.INVISIBLE);
            return;
        }
        try {

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(this, Uri.fromFile(new File(src)));
            mediaPlayer.prepareAsync();
            mediaPlayer.setDisplay(sv.getHolder());
            mediaPlayer.setOnVideoSizeChangedListener(this);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
                    playButton.setVisibility(View.INVISIBLE);
                    preview.animate()
                            .alpha(0.8f)
                            .setDuration(400)
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    sv.setBackgroundDrawable(null);
                                    preview.setVisibility(View.INVISIBLE);
                                    preview.setAlpha(1.0f);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });

//                    }, 200);
                    mediaPlayer.start();

                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.i(TAG, "position:" + mp.getCurrentPosition());
                    onCompletePlay();
                }
            });

        } catch (
                IOException e
                )

        {
            e.printStackTrace();
            finish();
            return;
        }

    }

    protected void onStopPlay() {
        playButton.setVisibility(View.VISIBLE);
        preview.setVisibility(View.VISIBLE);
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            mediaPlayer = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        onStopPlay();
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

