package cn.iterlog.xmimagepicker;

import java.util.ArrayList;
import java.util.List;

public class Configs {
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

    private static boolean editImage = true;
    private static boolean previewVideo = true;
    private static boolean multiChoose = false;
    private static int imageSize = 6;
    private static int videoSize = 1;
    private static boolean simpleType = false;

    public static boolean isEditImage() {
        return editImage;
    }

    public static void setEditImage(boolean editImage) {
        Configs.editImage = editImage;
    }

    public static boolean isMultiChoose() {
        return multiChoose;
    }

    public static void setMultiChoose(boolean multiChoose) {
        Configs.multiChoose = multiChoose;
    }

    public static boolean isSingleMedia() { // 小余最大MEDIA_TYPE的值，那么只有一种类型
        if (types.size() <= 1) {
            return true;
        }
        return false;
    }

    private static List<String> names = new ArrayList<>();

    public static List<String> getNames() {
        return names;
    }

    private static List<Integer> types = new ArrayList<>();

    public static List<Integer> getMedias() {
        return types;
    }

    public static void addMedia(int type) {

        if (type == MEDIA_PICTURE) {
            types.add(MEDIA_PICTURE);
            names.add(Gallery.applicationContext.getString(R.string.picture));

        } else if (type == MEDIA_MOVIE) {
            types.add(MEDIA_MOVIE);
            names.add(Gallery.applicationContext.getString(R.string.video));
        } else if (type == MEDIA_DOCUMENT) {
            types.add(MEDIA_DOCUMENT);
            names.add(Gallery.applicationContext.getString(R.string.document));
        } else if (type == MEDIA_MUSIC) {
            types.add(MEDIA_MUSIC);
            names.add(Gallery.applicationContext.getString(R.string.music));
        }
    }

    public static boolean isPreviewVideo() {
        return previewVideo;
    }

    public static void setPreviewVideo(boolean previewVideo) {
        Configs.previewVideo = previewVideo;
    }

    public static int getVideoSize() {
        return videoSize;
    }

    public static void setVideoSize(int videoSize) {
        Configs.videoSize = videoSize;
    }

    public static int getImageSize() {
        return imageSize;
    }

    public static void setImageSize(int imageSize) {
        Configs.imageSize = imageSize;
    }

    public static boolean isSimpleType() {
        return simpleType;
    }

    public static void setSimpleType(boolean simpleType) {
        Configs.simpleType = simpleType;
    }

    public static void reset() {
        editImage = true;
        previewVideo = true;
        Configs.types.clear();
        Configs.names.clear();
        multiChoose = false;
        videoSize = 1;
        imageSize = 1;
        simpleType = false;
    }
}
