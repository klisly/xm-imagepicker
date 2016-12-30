package cn.iterlog.xmimagepicker;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.iterlog.xmimagepicker.data.MediasLogic;

public class Configs implements Serializable {
    public static final String SINGLE_PHOTO = "SINGLE_PHOTO";
    public static final String LIMIT_PICK_PHOTO = "LIMIT_PICK_PHOTO";
    public static final String HAS_CAMERA = "HAS_CAMERA";
    public static final String FILTER_MIME_TYPES = "FILTER_MIME_TYPES";
    public static final String MEDIA_TYPE = "MEDIA_TYPE";

    public static final int MEDIA_MULTI = 1024;
    public static final int MEDIA_PICTURE = 1;
    public static final int MEDIA_MOVIE = 2;
    public static final int MEDIA_DOCUMENT = 4;
    public static final int MEDIA_MUSIC = 8;

    public static final String IMAGE_ALBUM_SUFFIX = "张图片";
    public static final String VIDEO_ALBUM_SUFFIX = "个视频";

    public static final String OUT_PUT = "OUT_PUT";
    public static final String OUT_PUT_IMAGES = "OUT_PUT_IMAGES";
    public static final String OUT_PUT_VIDEOS = "OUT_PUT_VIDEOS";
    public static final int REQUEST_VIDEO_PICK = 60010;
    public static final int REQUEST_MULTI_PICK = 60011;

    public static int NOTIFY_TYPE_MEDIA = 1; // 媒体加载变化
    public static int NOTIFY_TYPE_DIRECTORY = 21; // 目录选择变化
    public static int NOTIFY_TYPE_STATUS = 22; // Media选中与取消选中的选择

    public static String PREVIEW_TYPE = "PREVIEW_TYPE"; // preview参数
    public static String PREVIEW_POS = "PREVIEW_POS"; // preview参数
    public static int PREVIEW_TYPE_VIDEO = 1; // 选择的视频
    public static int PREVIEW_TYPE_PICTURE = 2; // 选择的图片
    public static int PREVIEW_TYPE_VIDEO_ALL = 3; // 预览当前Album的所有视频
    public static int PREVIEW_TYPE_PICTURE_ALL = 4; // 预览当前Album所有的图片

    public static int THUMB_SIZE = 512;

    private boolean editImage = true;
    private boolean previewVideo = true;
    private boolean multiChoose = false;
    private int imageSize = 6;
    private int videoSize = 1;
    private boolean simpleType = false;
    private static Configs instance;
    private Context context;
    public static Configs getInstance() {
        if(Gallery.applicationContext == null){
            throw new NullPointerException("Gallery.applicationContext not init");
        }
        return getInstance(Gallery.applicationContext);
    }
    public static Configs getInstance(Context context) {
        if (instance == null) {
            synchronized (MediasLogic.class) {
                if (instance == null) {
                    instance = new Configs();
                    instance.setContext(context);
                }
            }
        }
        return instance;
    }

    public Configs() {

    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public boolean isEditImage() {
        return editImage;
    }

    public void setEditImage(boolean editImage) {
        this.editImage = editImage;
    }

    public boolean isMultiChoose() {
        return multiChoose;
    }

    public void setMultiChoose(boolean multiChoose) {
        this.multiChoose = multiChoose;
    }

    public boolean isSingleMedia() { // 小余最大MEDIA_TYPE的值，那么只有一种类型
        if (types.size() <= 1) {
            return true;
        }
        return false;
    }

    private List<String> names = new ArrayList<>();

    public List<String> getNames() {
        return names;
    }

    private List<Integer> types = new ArrayList<>();

    public List<Integer> getMedias() {
        return types;
    }

    public Configs addMedia(int type) {

        if (type == MEDIA_PICTURE) {
            types.add(MEDIA_PICTURE);
            names.add(context.getString(R.string.picture));

        } else if (type == MEDIA_MOVIE) {
            types.add(MEDIA_MOVIE);
            names.add(context.getString(R.string.video));
        } else if (type == MEDIA_DOCUMENT) {
            types.add(MEDIA_DOCUMENT);
            names.add(context.getString(R.string.document));
        } else if (type == MEDIA_MUSIC) {
            types.add(MEDIA_MUSIC);
            names.add(context.getString(R.string.music));
        }
        return this;
    }

    public boolean isPreviewVideo() {
        return previewVideo;
    }

    public Configs setPreviewVideo(boolean previewVideo) {
        this.previewVideo = previewVideo;
        return this;
    }

    public int getVideoSize() {
        return videoSize;
    }

    public Configs setVideoSize(int videoSize) {
        this.videoSize = videoSize;
        return this;
    }

    public int getImageSize() {
        return imageSize;
    }

    public void setImageSize(int imageSize) {
        this.imageSize = imageSize;
    }

    public boolean isSimpleType() {
        return simpleType;
    }

    public void setSimpleType(boolean simpleType) {
        this.simpleType = simpleType;
    }

    public void reset() {
        editImage = true;
        previewVideo = true;
        types.clear();
        names.clear();
        multiChoose = false;
        videoSize = 1;
        imageSize = 1;
        simpleType = false;
    }
}
