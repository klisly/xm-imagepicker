package cn.iterlog.xmimagepicker;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import cn.iterlog.xmimagepicker.Utils.AndroidUtilities;
import cn.iterlog.xmimagepicker.Utils.ImageLoader;
import cn.iterlog.xmimagepicker.Utils.MediaController;
import cn.iterlog.xmimagepicker.Utils.NotificationCenter;
import cn.iterlog.xmimagepicker.adapter.AlbumAdapter;
import cn.iterlog.xmimagepicker.adapter.MediaFagmentAdapter;
import cn.iterlog.xmimagepicker.corp.Crop;
import cn.iterlog.xmimagepicker.data.MediasLogic;
import cn.iterlog.xmimagepicker.widget.RippleChoiceView;

public class PickerActivity extends BaseActivity implements NotificationCenter.NotificationCenterDelegate, MediasLogic.MediaListener {

    protected int classGuid = 0;
    TabLayout mTabLayout;
    private MediaFagmentAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private AlbumAdapter albumAdapter;
    private RecyclerView dirRecy;
    private TextView mTvChooseName;
    private RippleChoiceView mRcvNumber;
    private TextView mTvPreview;
    private TextView mTvChoose;
    private Toolbar toolbar;
    private Point point = new Point();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker);
        Gallery.init(getApplication());
        initConfig(getIntent());
        initToolBar();
        getWindowManager().getDefaultDisplay().getSize(point);
        mTvChooseName = (TextView) findViewById(R.id.tv_dir);
        mTvPreview = (TextView) findViewById(R.id.tv_preview);

        mRcvNumber = (RippleChoiceView) findViewById(R.id.rcv_choice);
        mTvChoose = (TextView) findViewById(R.id.choose);
        if (Configs.getInstance().isMultiChoose()) {
            mTvChoose.setVisibility(View.VISIBLE);
            mTvChoose.setTextColor(getResources().getColor(R.color.white_50));
        }

        initListener();

        MediasLogic.getInstance().updateMediaType(Configs.getInstance().getMedias().get(0));
        initViewPager();
        initAlbumData();
        loadMediaData();
        MediasLogic.getInstance().registerListener(this, this);


    }

    private void initConfig(Intent intent) {
        Configs.getInstance().reset();
        ArrayList<Integer> list = intent.getIntegerArrayListExtra(Configs.PARAM_MEDIAS);
        if(list != null){
            for(Integer m : list){
                Configs.getInstance().addMedia(m);
            }
        }
        Configs.getInstance().setImageSize(intent.getIntExtra(Configs.PARAM_THUMB_SIZE, 250));
        Configs.getInstance().setEditImage(intent.getBooleanExtra(Configs.PARAM_EDIT_IMG, false));
        Configs.getInstance().setPreviewVideo(intent.getBooleanExtra(Configs.PARAM_PREVIEW_VIDEO, false));
        Configs.getInstance().setImageSize(intent.getIntExtra(Configs.PARAM_IMAGE_SZIE, 1));
        Configs.getInstance().setVideoSize(intent.getIntExtra(Configs.PARAM_VIDEO_SIZE, 1));
        Configs.getInstance().setSingleType(intent.getBooleanExtra(Configs.PARAM_SIGNLE_TYPE, false));
        if(Configs.getInstance().getImageSize() > 1 || Configs.getInstance().getVideoSize() > 1){
            Configs.getInstance().setMultiChoose(true);
        } else {
            Configs.getInstance().setMultiChoose(false);
        }
    }

    private void initListener() {
        mTvChooseName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDir();
            }
        });
        mTvChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMultiChoose();
            }
        });
        mTvPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PickerActivity.this, MediasPreviewActivity.class);
                intent.putExtra(Configs.PREVIEW_POS, 0);
                if (MediasLogic.getInstance().getMediaType() == Configs.MEDIA_MOVIE) {
                    intent.putExtra(Configs.PREVIEW_TYPE, Configs.PREVIEW_TYPE_VIDEO);
                } else if (MediasLogic.getInstance().getMediaType() == Configs.MEDIA_PICTURE) {
                    intent.putExtra(Configs.PREVIEW_TYPE, Configs.PREVIEW_TYPE_PICTURE);
                }
                startActivityForResult(intent, Configs.REQUEST_MULTI_PICK);
            }
        });
    }

    private void onMultiChoose() {
        if (MediasLogic.getInstance().getChooseCount() > 0) {
            int vSize = MediasLogic.getInstance().getChooseVideos().size();
            int pSize = MediasLogic.getInstance().getChoosePictures().size();
            Intent intent = new Intent();
            intent.putExtra(Configs.MEDIA_TYPE, Configs.MEDIA_MULTI);
            ArrayList<Uri> pictures = new ArrayList<Uri>();
            ArrayList<Uri> videos = new ArrayList<Uri>();
            for (int i = 0; i < pSize; i++) {
                try {
                    pictures.add(Uri.fromFile(new File(MediasLogic.getInstance().getChoosePictures().get(i).path)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < vSize; i++) {
                try {
                    videos.add(Uri.fromFile(new File(MediasLogic.getInstance().getChooseVideos().get(i).path)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            intent.putParcelableArrayListExtra(Configs.OUT_PUT_VIDEOS, videos);
            intent.putParcelableArrayListExtra(Configs.OUT_PUT_IMAGES, pictures);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void initAlbumData() {
        dirRecy = (RecyclerView) findViewById(R.id.directory);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        dirRecy.setLayoutManager(layoutManager);
        albumAdapter = new AlbumAdapter(MediasLogic.getInstance().getChooseAlbum());
        albumAdapter.setListener(new AlbumAdapter.OnItemChangeListener() {
            @Override
            public void onAlbumChoose(AlbumAdapter.MediaHolder view, int position, MediaController.AlbumEntry entry) {
                hideDir();
                MediasLogic.getInstance().setChooIndex(entry.isVideo, position);
                String name = entry.bucketName;
                mTvChooseName.setText(name);
            }
        });
        dirRecy.setAdapter(albumAdapter);

    }

    private void loadMediaData() {
        MediasLogic.getInstance().setLoading(true);
        String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
        String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
        if (checkCallingOrSelfPermission(
                READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkCallingOrSelfPermission(
                WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(new String[]{
                        READ_EXTERNAL_STORAGE
                }, 1);
                return;
            }
        }
        loadMedias();
    }

    private void initViewPager() {
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mSectionsPagerAdapter = new MediaFagmentAdapter(getSupportFragmentManager(), this);
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                MediasLogic.getInstance().updateMediaType(Configs.getInstance().getMedias().get(position));
                hideDir();
                showPreview();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mTabLayout.setupWithViewPager(mViewPager);
        if (Configs.getInstance().isSingleMedia()) {
            mTabLayout.setVisibility(View.GONE);
            toolbar.setTitle(Configs.getInstance().getNames().get(0));
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        loadMedias();
    }

    private void loadMedias() {
        MediaController.loadGalleryPhotosAlbums(classGuid, MediasLogic.getInstance().getFilterMimeTypes());
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.albumsDidLoaded);
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.media_title);
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
    public void onBackPressed() {
        if (dirRecy.getVisibility() == View.VISIBLE) {
            hideDir();
        } else {
            super.onBackPressed();
        }
    }


    private void hideDir() {
        ObjectAnimator translationY = ObjectAnimator.ofFloat(dirRecy, "Y", point.y - size, point.y);
        AnimatorSet animatorSet2 = new AnimatorSet();
        animatorSet2.playTogether(translationY);
        animatorSet2.setDuration(400);
        animatorSet2.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet2.start();
        animatorSet2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dirRecy.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    int size;

    private void showDir() {
        size = dirRecy.getBottom() - mViewPager.getTop();
        dirRecy.setVisibility(View.VISIBLE);
        dirRecy.setAlpha(0.01f);
        dirRecy.smoothScrollToPosition(MediasLogic.getInstance().getChoosePosition());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dirRecy.setAlpha(1f);
                ObjectAnimator translationY = ObjectAnimator.ofFloat(dirRecy, "Y", point.y, point.y - size);
                AnimatorSet animatorSet2 = new AnimatorSet();
                animatorSet2.playTogether(translationY);
                animatorSet2.setDuration(400);
                animatorSet2.setInterpolator(new AccelerateDecelerateInterpolator());
                animatorSet2.start();
                animatorSet2.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        Log.i("showDir", "onAnimationStart");
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Log.i("showDir", "onAnimationStart " + mViewPager.getBottom() + " " + point.y);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        Log.i("showDir", "onAnimationCancel");
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                        Log.i("showDir", "onAnimationRepeat");
                    }
                });
            }
        }, 100);

    }

    @Override
    protected void onDestroy() {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.albumsDidLoaded);
        MediasLogic.getInstance().unRegisterListener(this);
        MediasLogic.getInstance().clearData();
        Configs.getInstance().reset();
        MediaController.getInstance().cleanup();
        ImageLoader.getInstance().clean();
//        Gallery.clean();
        super.onDestroy();
    }

    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.albumsDidLoaded) {
            int guid = (Integer) args[0];
            if (classGuid == guid) {
                MediasLogic.getInstance().setLoading(false);
                MediasLogic.getInstance().setPictureAlbums((ArrayList<MediaController.AlbumEntry>) args[1]);
                MediasLogic.getInstance().setVideoAlbums((ArrayList<MediaController.AlbumEntry>) args[3]);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Crop.REQUEST_CROP) {
                data.putExtra("type", Configs.MEDIA_PICTURE);
                handleCrop(resultCode, data);
            } else if (requestCode == Configs.REQUEST_VIDEO_PICK) {
                data.putExtra("type", Configs.MEDIA_MOVIE);
                setResult(RESULT_OK, data);
                finish();
            } else if (requestCode == Configs.REQUEST_MULTI_PICK) {
                onMultiChoose();
            }
        }
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK, result);
            finish();
        } else if (resultCode == Crop.RESULT_ERROR) {
            AndroidUtilities.showToast(Crop.getError(result).getMessage());
        }
    }

    /**
     * 启动图片选择器主方法
     * @param activity 启动的Activity
     * @param requestCode
     * @param medias 选择的媒体列表 参见Configs的配置
     * @param thumbSize // 缩略图的打消
     * @param isEditImg // 是否编辑图片
     * @param isPreviewVideo // 是否预览视频
     * @param imgSize // 选择的图片张数
     * @param videoSize // 选择的视频个数
     * @param singleType // 是否只选择其中一种媒体
     */
    public static void openPicker(Activity activity, int requestCode, ArrayList<Integer> medias,
                                  int thumbSize, boolean isEditImg, boolean isPreviewVideo,
                                  int imgSize, int videoSize,
                                  boolean singleType) {
        Intent intent = new Intent(activity, PickerActivity.class);

        intent.putIntegerArrayListExtra(Configs.PARAM_MEDIAS, medias);
        intent.putExtra(Configs.PARAM_THUMB_SIZE, thumbSize);
        intent.putExtra(Configs.PARAM_EDIT_IMG, isEditImg);
        intent.putExtra(Configs.PARAM_PREVIEW_VIDEO, isPreviewVideo);
        intent.putExtra(Configs.PARAM_IMAGE_SZIE, imgSize);
        intent.putExtra(Configs.PARAM_VIDEO_SIZE, videoSize);
        intent.putExtra(Configs.PARAM_SIGNLE_TYPE, singleType);

        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onMediaNotify(int type) {
        if (type == Configs.NOTIFY_TYPE_DIRECTORY) {
            albumAdapter.setAlbums(MediasLogic.getInstance().getChooseAlbum());
            albumAdapter.notifyDataSetChanged();
            String name = MediasLogic.getInstance().getChooseAlbumName();
            mTvChooseName.setText(name);
        } else if (type == Configs.NOTIFY_TYPE_STATUS) {
            showPreview();
            int count = MediasLogic.getInstance().getChooseCount();
            if (count > 0) {
                if (mRcvNumber.getVisibility() != View.VISIBLE) {
                    mRcvNumber.setVisibility(View.VISIBLE);
                    mRcvNumber.setScaleX(0f);
                    mRcvNumber.setScaleX(0f);
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
                                mTvPreview.setVisibility(View.INVISIBLE);
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
        }
    }

    private void showPreview() {
        // TODO 暂时只支持图片的预览
        if (MediasLogic.getInstance().getMediaType() == Configs.MEDIA_PICTURE && MediasLogic.getInstance().getChoosePictures().size() > 0) {
            mTvPreview.setVisibility(View.VISIBLE);
        } else {
            mTvPreview.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public static void chooseSingleMovie(Activity activity, int requestCode, boolean isPreview) {
        ArrayList<Integer> medias = new ArrayList<>();
        medias.add(Configs.MEDIA_MOVIE);
        openPicker(activity, requestCode, medias, 256, false, isPreview, 1, 1, false);
    }

    public static void chooseMultiMovie(Activity activity, int requestCode, int size) {
        ArrayList<Integer> medias = new ArrayList<>();
        medias.add(Configs.MEDIA_MOVIE);
        openPicker(activity, requestCode, medias, 256, false, true, 1, size, false);
    }

    public static void chooseSinglePicture(Activity activity, int requestCode, boolean isEdit) {
        ArrayList<Integer> medias = new ArrayList<>();
        medias.add(Configs.MEDIA_PICTURE);
        openPicker(activity, requestCode, medias, 256, isEdit, false, 1, 1, false);
    }

    public static void chooseMultiPicture(Activity activity, int requestCode, int size) {
        ArrayList<Integer> medias = new ArrayList<>();
        medias.add(Configs.MEDIA_PICTURE);
        openPicker(activity, requestCode, medias, 256, false, false, size, 1, false);
    }
}
