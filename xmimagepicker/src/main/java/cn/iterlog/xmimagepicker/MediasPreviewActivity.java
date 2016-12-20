package cn.iterlog.xmimagepicker;

import android.animation.Animator;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cn.iterlog.xmimagepicker.Utils.AndroidUtilities;
import cn.iterlog.xmimagepicker.Utils.MediaController;
import cn.iterlog.xmimagepicker.data.MediasLogic;
import cn.iterlog.xmimagepicker.widget.PhotoViewer;
import cn.iterlog.xmimagepicker.widget.RippleChoiceView;

import static cn.iterlog.xmimagepicker.R.id.sv;

public class MediasPreviewActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnVideoSizeChangedListener, MediasLogic.MediaListener {
    private static String TAG = MediasPreviewActivity.class.getSimpleName();

    private static int displayType = Configs.PREVIEW_TYPE_PICTURE;
    private int curPos = 0;
    private Toolbar toolbar;
    private TextView mTvChoose;
    private RippleChoiceView mRcvNumber;
    private RippleChoiceView mRcvItemChoose;
    private FrameLayout mViewFrame;
    private List<MediaController.PhotoEntry> datas;

    private ImageView playButton;
    private SurfaceView surfaceView;
    private MediaPlayer mediaPlayer;
    private int svWidth;
    private int svHeight;
    private int vWidht;
    private int vHeight;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previews);
        displayType = getIntent().getIntExtra(Configs.PREVIEW_TYPE, Configs.PREVIEW_TYPE_PICTURE);
        curPos = getIntent().getIntExtra(Configs.PREVIEW_POS, 0);
        mTvChoose = (TextView) findViewById(R.id.choose);
        mRcvNumber = (RippleChoiceView) findViewById(R.id.rcv_choice);
        mViewFrame = (FrameLayout) findViewById(R.id.view_frame);
        mRcvItemChoose = (RippleChoiceView) findViewById(R.id.rcv_item_choice);
        if (displayType == Configs.PREVIEW_TYPE_VIDEO) {
            datas = MediasLogic.getInstance().getChooseVideos();
        } else if (displayType == Configs.PREVIEW_TYPE_PICTURE) {
            datas =  MediasLogic.getInstance().getChoosePictures();
        } else {
            datas =  MediasLogic.getInstance()
                    .getChooseAlbum().get(MediasLogic.getInstance().getChoosePosition())
                    .photos;
        }
        if (MediasLogic.getInstance().getChooseCount() > 0) {
            mRcvNumber.setVisibility(View.VISIBLE);
            updateChooseCount();
        } else {
            mTvChoose.setTextColor(getResources().getColor(R.color.white_50));
        }

        playButton = (ImageView) findViewById(R.id.iv_play);
        surfaceView = (SurfaceView) findViewById(sv);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    onPausePlay();
                } else {
                    playButton.setVisibility(View.INVISIBLE);
                    onstartPlay();
                }
            }
        });
        if (displayType == Configs.PREVIEW_TYPE_VIDEO || displayType == Configs.PREVIEW_TYPE_VIDEO_ALL) {
            playButton.setVisibility(View.VISIBLE);
            surfaceView.setVisibility(View.VISIBLE);
        } else {
            playButton.setVisibility(View.GONE);
            surfaceView.setVisibility(View.GONE);
        }

        surfaceView.getHolder().addCallback(this);
        surfaceView.setBackgroundDrawable(getResources().getDrawable(R.drawable.video_texture));
        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    onPausePlay();
                } else {
                    playButton.setVisibility(View.GONE);
                    onstartPlay();
                }
            }
        });
        initSvSize();

        initToolBar();
        initListener();
        updateChooseStatus();
        openPreview();

    }

    @Override
    protected void onResume() {
        super.onResume();
        surfaceView.setBackgroundColor(getResources().getColor(R.color.black_p50));
    }

    @Override
    protected void onPause() {
        super.onPause();
        onStopPlay();
    }

    private void onCompletePlay() {
        Log.i(TAG, "curl pos:" + mediaPlayer.getCurrentPosition() + " duration:" + mediaPlayer.getDuration());
        if(mediaPlayer.getDuration() == 0){
            AndroidUtilities.showToast(getString(R.string.play_error));
            finish();
            onStopPlay();
            return;
        }
        surfaceView.setVisibility(View.INVISIBLE);

        surfaceView.animate()
                .alpha(0.0f)
                .setDuration(300)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        surfaceView.setVisibility(View.INVISIBLE);
                        surfaceView.setAlpha(1);
                        surfaceView.setBackgroundColor(getResources().getColor(R.color.black_p50));
                        mViewFrame.setVisibility(View.VISIBLE);
                        mViewFrame.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mViewFrame.setVisibility(View.VISIBLE);
                            }
                        }, 300);
//                        mViewFrame.setAlpha(0.1f);
//                        mViewFrame.animate()
//                                .alpha(1f)
//                                .setDuration(300)
//                                .setListener(new Animator.AnimatorListener() {
//                                    @Override
//                                    public void onAnimationStart(Animator animation) {
//
//                                    }
//
//                                    @Override
//                                    public void onAnimationEnd(Animator animation) {
//                                        mViewFrame.setAlpha(1);
//                                    }
//
//                                    @Override
//                                    public void onAnimationCancel(Animator animation) {
//
//                                    }
//
//                                    @Override
//                                    public void onAnimationRepeat(Animator animation) {
//
//                                    }
//                                })
//                                .start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();
        playButton.setVisibility(View.VISIBLE);
        playButton.setAlpha(0.2f);
        playButton.animate()
                .alpha(1f)
                .setDuration(300)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();
    }

    private void onPausePlay() {
        playButton.setVisibility(View.VISIBLE);
        mediaPlayer.pause();
    }


    private void onstartPlay() {
        surfaceView.setVisibility(View.VISIBLE);
        mViewFrame.animate()
                .alpha(0.9f)
                .setDuration(200)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        surfaceView.setBackgroundDrawable(null);
                        mViewFrame.setVisibility(View.INVISIBLE);
                        mViewFrame.setAlpha(1.0f);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();
        if (mediaPlayer != null) {
            mediaPlayer.start();
            playButton.setVisibility(View.INVISIBLE);
            return;
        }

        try {

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(this, Uri.fromFile(new File(datas.get(curPos).path)));
            mediaPlayer.prepareAsync();
            mediaPlayer.setDisplay(surfaceView.getHolder());
            mediaPlayer.setOnVideoSizeChangedListener(this);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    playButton.setVisibility(View.INVISIBLE);
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

        } catch (IOException e) {
            e.printStackTrace();
            onStopPlay();
            AndroidUtilities.showToast(getString(R.string.play_error));
            finish();
            return;
        }

    }

    protected void onStopPlay() {
        if (!(displayType == Configs.PREVIEW_TYPE_VIDEO || displayType == Configs.PREVIEW_TYPE_VIDEO_ALL)) {
            return;
        }
        playButton.setVisibility(View.VISIBLE);
        surfaceView.setVisibility(View.INVISIBLE);
        mViewFrame.setVisibility(View.VISIBLE);
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
            Log.i(TAG, "has changed video size w:" + width + "video size h:" + height);
            refreshSvSize();
        }
    }

    private void initSvSize() {
        surfaceView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                int tmpWidth = right - left;
                int tmpHeight = bottom - top;
                if (tmpHeight != svWidth || tmpHeight != svHeight) {
                    svWidth = tmpWidth;
                    svHeight = tmpHeight;
                    Log.i(TAG, "surfaceView size changed sw:" + svWidth + "sh:" + svHeight);
                    refreshSvSize();
                }
            }
        });
    }

    private void refreshSvSize() {
        if (svWidth == 0 || svHeight == 0 || vWidht == 0 || vHeight == 0) {
            return;
        }

        float rate = vWidht * 1.0f / vHeight;
        Log.i(TAG, "video w/h rate:" + rate);
        if (svHeight * rate > svWidth) {
            svHeight = (int) (svWidth / rate);
        } else {
            svWidth = (int) (svHeight * rate);
        }
        ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
        params.width = svWidth;
        params.height = svHeight;
        surfaceView.setLayoutParams(params);
        params = mViewFrame.getLayoutParams();
        params.width = svWidth;
        params.height = svHeight;
        mViewFrame.setLayoutParams(params);
    }


    public void openPreview() {
        PhotoViewer.getInstance().setParentActivity(this, mViewFrame);
        PhotoViewer.getInstance().openPhotoForSelect(datas, true, curPos, 0, new CustomProvider(datas));
        PhotoViewer.getInstance().setChangeListener(new PhotoViewer.OnIndexChangeListener() {
            @Override
            public void onIndexChange(int curIndex) {
                curPos = curIndex;
                toolbar.setTitle(String.format(title, curPos + 1, datas.size()));
                updateChooseStatus();
            }
        });
    }

    public static class CustomProvider extends PhotoViewer.PreviewEmptyPhotoViewerProvider {
        private MediaController.PhotoEntry[] selectArr;
        private MediaController.PhotoEntry[] removedArr;

        public CustomProvider( List<MediaController.PhotoEntry> selectPhotos) {
            int size = selectPhotos.size();
            selectArr = new MediaController.PhotoEntry[size];
            removedArr = new MediaController.PhotoEntry[size];
            for (int i = 0; i < size; i++) {
                selectArr[i] = selectPhotos.get(i);
            }
        }

        @Override
        public boolean isPhotoChecked(int index) {
            return selectArr[index] != null;
        }

        @Override
        public void sendButtonPressed(int index) {

        }

        @Override
        public int getSelectedCount() {
            return getRealCount();
        }

        @Override
        public boolean checkboxEnable() {
            return true;
        }

        @Override
        public int getCheckeCorner(int index) {
            return index + 1;
        }

        @Override
        public void selectChanged(int index, boolean checked) {
            MediaController.PhotoEntry photoEntry;
            if (checked) {
                selectArr[index] = removedArr[index];
                photoEntry = removedArr[index];
                System.arraycopy(removedArr, index, selectArr, index, 1);
                removedArr[index] = null;
            } else {
                removedArr[index] = selectArr[index];
                photoEntry = selectArr[index];
                System.arraycopy(selectArr, index, removedArr, index, 1);
                selectArr[index] = null;
            }
        }

        @Override
        public void previewExit() {
            super.previewExit();
        }

        private int getRealCount() {
            int count = 0;
            for (int i = 0; i < selectArr.length; i++) {
                if (selectArr[i] != null) {
                    count++;
                }
            }
            return count;
        }
    }

    private void initListener() {

        MediasLogic.getInstance().registerListener(this, this);

        mTvChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(displayType == Configs.PREVIEW_TYPE_VIDEO || displayType == Configs.PREVIEW_TYPE_VIDEO_ALL){
                    if(MediasLogic.getInstance().getChooseVideos().size() <= 0) {
                        return;
                    }
                }
                if(displayType == Configs.PREVIEW_TYPE_PICTURE || displayType == Configs.PREVIEW_TYPE_PICTURE_ALL){
                    if(MediasLogic.getInstance().getChoosePictures().size() <= 0) {
                        return;
                    }
                }
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void updateChooseCount() {
        int count = 0;
        if (displayType == Configs.PREVIEW_TYPE_VIDEO || displayType == Configs.PREVIEW_TYPE_VIDEO_ALL) {
            count = MediasLogic.getInstance().getChooseVideos().size();
        } else {
            count = MediasLogic.getInstance().getChoosePictures().size();
        }

        if (count > 0) {
            if (mRcvNumber.getVisibility() != View.VISIBLE) {
                mRcvNumber.setVisibility(View.VISIBLE);
                mRcvNumber.setScaleX(0.5f);
                mRcvNumber.setScaleX(0.5f);
                mRcvNumber.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(300)
                        .setInterpolator(new OvershootInterpolator())
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mRcvNumber.setScaleX(1f);
                                mRcvNumber.setScaleX(1f);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                mRcvNumber.setScaleX(1f);
                                mRcvNumber.setScaleX(1f);
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        })
                        .start();
                mTvChoose.setTextColor(getResources().getColor(R.color.white));
            }
            mRcvNumber.setmNumber(count);

        } else {
            mRcvNumber.animate()
                    .scaleX(0.0f)
                    .scaleY(0.0f)
                    .setDuration(300)
                    .setInterpolator(new AccelerateInterpolator())
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mRcvNumber.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            mRcvNumber.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    })
                    .start();
            mTvChoose.setTextColor(getResources().getColor(R.color.white_50));
        }

        mRcvItemChoose.setOnCheckedChangeListener(new RippleChoiceView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RippleChoiceView view, boolean isChecked) {

                if (isChecked) {
                    if (Configs.isSimpleType()) {
                        if ((datas.get(curPos).isVideo && MediasLogic.getInstance().getChoosePictures().size() > 0)
                                || datas.get(curPos).isVideo && MediasLogic.getInstance().getChooseVideos().size() > 0) {
                            StringBuilder builder = new StringBuilder();
                            builder.append("只能选择")
                                    .append(Configs.getImageSize())
                                    .append("张图片或者")
                                    .append(Configs.getVideoSize())
                                    .append("条视频");
                            AndroidUtilities.showToast(builder.toString());
                            mRcvItemChoose.setmChecked(false);
                            return;
                        }
                    }

                    if (datas.get(curPos).isVideo
                            && MediasLogic.getInstance().getChooseVideos().size() >= Configs.getVideoSize()) {
                        StringBuilder builder = new StringBuilder();
                        builder.append("只能选择")
                                .append(Configs.getVideoSize())
                                .append("条视频");
                        AndroidUtilities.showToast(builder.toString());
                        mRcvItemChoose.setmChecked(false);
                        return;
                    }
                    if (!datas.get(curPos).isVideo
                            && MediasLogic.getInstance().getChoosePictures().size() >= Configs.getImageSize()) {
                        StringBuilder builder = new StringBuilder();
                        builder.append("只能选择")
                                .append(Configs.getImageSize())
                                .append("张图片");
                        AndroidUtilities.showToast(builder.toString());
                        mRcvItemChoose.setmChecked(false);
                        return;
                    }
                    MediasLogic.getInstance().chooseMedia(datas.get(curPos));
                } else {
                    MediasLogic.getInstance().unChooseMedia(datas.get(curPos));
                }
            }
        });
    }

    private String title = "%s/%s";

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(String.format(title, curPos + 1, datas.size()));
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
    public void onMediaNotify(int type) {
        if (type == Configs.NOTIFY_TYPE_STATUS) {
            updateChooseStatus();
        }
    }

    private void updateChooseStatus() {
        if (MediasLogic.getInstance().isChoosed(datas.get(curPos))) {
            mRcvItemChoose.setmChecked(true);
        } else {
            mRcvItemChoose.setmChecked(false);
        }
        updateChooseCount();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        datas = null;
        MediasLogic.getInstance().unRegisterListener(this);
        PhotoViewer.getInstance().destroyPhotoViewer();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
