package cn.iterlog.xmimagepicker;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import cn.iterlog.xmimagepicker.Utils.MediaController;
import cn.iterlog.xmimagepicker.Utils.NotificationCenter;
import cn.iterlog.xmimagepicker.adapter.MediaFagmentAdapter;
import cn.iterlog.xmimagepicker.corp.Crop;
import cn.iterlog.xmimagepicker.data.MediasLogic;
import cn.iterlog.xmimagepicker.videoplay.VideoActivity;

public class PickerActivity extends BaseActivity implements NotificationCenter.NotificationCenterDelegate {

    protected int classGuid = 0;
    TabLayout mTabLayout;
    long time = 0;
    private MediaFagmentAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    /**
     * 打开相册
     *
     * @param filterMimeTypes 需要过滤掉的媒体文件类型，以MimeType标识：{http://www.w3school.com.cn/media/media_mimeref.asp}
     *                        <span>eg:new String[]{"image/gif","image/jpeg"}<span/>
     * @param singlePhoto     true:单选 false:多选
     * @param limitPickPhoto  照片选取限制
     * @param requestCode     请求码
     */
    public static void openActivity(
            Activity activity,
            String[] filterMimeTypes,
            boolean singlePhoto,
            int limitPickPhoto,
            int requestCode) {
        limitPickPhoto = singlePhoto ? 1 : limitPickPhoto > 0 ? limitPickPhoto : 1;
        Intent intent = new Intent(activity, PickerActivity.class);
        intent.putExtra(Constants.SINGLE_PHOTO, singlePhoto);
        intent.putExtra(Constants.LIMIT_PICK_PHOTO, limitPickPhoto);
        intent.putExtra(Constants.FILTER_MIME_TYPES, filterMimeTypes);
        intent.putExtra(Constants.HAS_CAMERA, false);
        intent.putExtra(Constants.MEDIA_TYPE, Constants.MEDIA_TYPE);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void openActivity(Activity activity, boolean singlePhoto, int limitPickPhoto,
                                    int requestCode) {
        openActivity(activity, null, singlePhoto, limitPickPhoto, requestCode);
    }

    public static void openActivity(Activity activity, boolean singlePhoto, int requestCode) {
        openActivity(activity, null, singlePhoto, 1, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker);
        Gallery.init(getApplication());
        initToolBar();

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mSectionsPagerAdapter = new MediaFagmentAdapter(getSupportFragmentManager(), this);
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        MediasLogic.getInstance().setLoading(true);
        String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
        if (checkCallingOrSelfPermission(
                READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(new String[]{
                        READ_EXTERNAL_STORAGE
                }, 1);
                return;
            }
        }
        loadMedias();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        loadMedias();
    }

    private void loadMedias() {
        time = System.currentTimeMillis();
        MediaController.loadGalleryPhotosAlbums(classGuid, MediasLogic.getInstance().getFilterMimeTypes());
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.albumsDidLoaded);
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.albumsDidLoaded);
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
                data.putExtra("type", Constants.TYPE_IMAGE);
                handleCrop(resultCode, data);
            } else if (requestCode == VideoActivity.REQUEST_PICK) {
                data.putExtra("type", Constants.TYPE_VIDEO);
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
}
