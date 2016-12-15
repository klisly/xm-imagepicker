package cn.iterlog.xmimagepicker;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cn.iterlog.xmimagepicker.Utils.MediaController;
import cn.iterlog.xmimagepicker.Utils.NotificationCenter;
import cn.iterlog.xmimagepicker.adapter.AlbumAdapter;
import cn.iterlog.xmimagepicker.adapter.MediaFagmentAdapter;
import cn.iterlog.xmimagepicker.corp.Crop;
import cn.iterlog.xmimagepicker.data.MediasLogic;
import cn.iterlog.xmimagepicker.videoplay.VideoActivity;

public class PickerActivity extends BaseActivity implements NotificationCenter.NotificationCenterDelegate, MediasLogic.MediaListener {

    protected int classGuid = 0;
    TabLayout mTabLayout;
    private MediaFagmentAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private AlbumAdapter albumAdapter;
    private RecyclerView dirRecy;
    private TextView mTvChooseName;
    private Toolbar toolbar;
    private Point point = new Point();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker);
        Gallery.init(getApplication());
        initToolBar();
        getWindowManager().getDefaultDisplay().getSize(point);
        Log.i("PickerActivity","Size:"+point);
        mTvChooseName = (TextView) findViewById(R.id.tv_dir);
        mTvChooseName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDir();
            }
        });
        MediasLogic.getInstance().updateMediaType(Configs.getMedias().get(0));
        initViewPager();
        initAlbumData();
        loadMediaData();
        MediasLogic.getInstance().registerListener(this, this);
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
                Log.i("PickerActivity", "choose album name:"+name);
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
                MediasLogic.getInstance().updateMediaType(getMediaType(position));
                hideDir();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mTabLayout.setupWithViewPager(mViewPager);
        if(Configs.isSingleMedia()){
            mTabLayout.setVisibility(View.GONE);
            toolbar.setTitle(Configs.getNames().get(0));
        }
    }

    private int getMediaType(int position) {
        if(position == 0){
            return Configs.MEDIA_PICTURE;
        } else if(position == 1){
            return Configs.MEDIA_MOVIE;
        }
        return Configs.MEDIA_PICTURE;
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
        if(dirRecy.getVisibility() == View.VISIBLE){
            hideDir();
        } else {
            super.onBackPressed();
        }
    }



    private void hideDir() {
        ObjectAnimator translationY = ObjectAnimator.ofFloat(dirRecy, "Y", point.y - size, point.y);
        AnimatorSet animatorSet2 = new AnimatorSet();
        animatorSet2.playTogether(translationY);
        animatorSet2.setDuration(600);
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
                animatorSet2.setDuration(600);
                animatorSet2.setInterpolator(new AccelerateDecelerateInterpolator());
                animatorSet2.start();
                animatorSet2.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        Log.i("showDir", "onAnimationStart");
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        Log.i("showDir", "onAnimationStart "+mViewPager.getBottom()+" "+point.y);
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
        Configs.reset();
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
        if(resultCode == RESULT_OK) {
            if (requestCode == Crop.REQUEST_CROP) {
                data.putExtra("type", Configs.MEDIA_PICTURE);
                handleCrop(resultCode, data);
            } else if (requestCode == VideoActivity.REQUEST_PICK) {
                data.putExtra("type", Configs.MEDIA_MOVIE);
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Log.i(PickerActivity.class.getCanonicalName(), Crop.getOutput(result).toString());
            setResult(RESULT_OK, result);
            finish();
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void openActivity(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, PickerActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onMediaLoaded(int type) {
        if(type == Configs.NOTIFY_TYPE_DIRECTORY){
            albumAdapter.setAlbums(MediasLogic.getInstance().getChooseAlbum());
            albumAdapter.notifyDataSetChanged();
            String name = MediasLogic.getInstance().getChooseAlbumName();
            Log.i("PickerActivity", "choose album name:"+name);
            mTvChooseName.setText(name);
        }
    }
}
