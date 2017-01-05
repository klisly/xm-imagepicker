package cn.iterlog.xmimagepicker;

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
    public static final String PARAM_MEDIAS = "PARAM_MEDIAS";
    public static final String PARAM_THUMB_SIZE = "PARAM_THUMB_SIZE";
    public static final String PARAM_SIGNLE_TYPE = "PARAM_SIGNLE_TYPE";
    public static final String PARAM_VIDEO_SIZE = "PARAM_VIDEO_SIZE";
    public static final String PARAM_IMAGE_SZIE = "PARAM_IMAGE_SZIE";
    public static final String PARAM_PREVIEW_VIDEO = "PARAM_PREVIEW_VIDEO";
    public static final String PARAM_EDIT_IMG = "PARAM_EDIT_IMG";

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
    private boolean singleType = false; // 是否是只选多重类型中的一种，如只能选图片或者视频
    private static Configs instance;

    public static Configs getInstance() {
        if (instance == null) {
            synchronized (MediasLogic.class) {
                if (instance == null) {
                    instance = new Configs();
                }
            }
        }
        return instance;
    }

    public Configs() {

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
            names.add("图片");

        } else if (type == MEDIA_MOVIE) {
            types.add(MEDIA_MOVIE);
            names.add("视频");
        } else if (type == MEDIA_DOCUMENT) {
            types.add(MEDIA_DOCUMENT);
            names.add("文档");
        } else if (type == MEDIA_MUSIC) {
            types.add(MEDIA_MUSIC);
            names.add("音乐");
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

    public boolean isSingleType() {
        return singleType;
    }

    public void setSingleType(boolean singleType) {
        this.singleType = singleType;
    }


    public void reset() {
        editImage = true;
        previewVideo = true;
        types.clear();
        names.clear();
        multiChoose = false;
        videoSize = 1;
        imageSize = 1;
        singleType = false;
    }
}
