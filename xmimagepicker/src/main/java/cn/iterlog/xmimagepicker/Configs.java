package cn.iterlog.xmimagepicker;

import java.util.ArrayList;
import java.util.List;

public class Configs {
    public static final String SINGLE_PHOTO = "SINGLE_PHOTO";
    public static final String LIMIT_PICK_PHOTO = "LIMIT_PICK_PHOTO";
    public static final String HAS_CAMERA = "HAS_CAMERA";
    public static final String FILTER_MIME_TYPES = "FILTER_MIME_TYPES";
    public static final String MEDIA_TYPE = "MEDIA_TYPE";

    public static final int MEDIA_PICTURE = 1;
    public static final int MEDIA_MOVIE = 2;
    public static final int MEDIA_DOCUMENT = 4;
    public static final int MEDIA_MUSIC = 8;

    public static final String IMAGE_ALBUM_SUFFIX = "张图片";
    public static final String VIDEO_ALBUM_SUFFIX = "个视频";

    public static int NOTIFY_TYPE_MEDIA  = 1;
    public static int NOTIFY_TYPE_DIRECTORY  = 21;

    private static int medias = 1;
    private static boolean editImage = true;
    private static boolean previewVideo = true;

    public static boolean isEditImage() {
        return editImage;
    }

    public static void setEditImage(boolean editImage) {
        Configs.editImage = editImage;
    }

    public static boolean isSingleMedia(){ // 小余最大MEDIA_TYPE的值，那么只有一种类型
        if(medias <= MEDIA_MUSIC){
            return true;
        }
        return false;
    }
    private static List<String> names = null;
    public static List<String> getNames(){
        if(names == null){
            List<String> list =new ArrayList<>();
            if((medias &MEDIA_PICTURE) > 0){
                list.add(Gallery.applicationContext.getString(R.string.picture));
            } else if((medias &MEDIA_MOVIE) > 0){
                list.add(Gallery.applicationContext.getString(R.string.video));
            } else if((medias &MEDIA_DOCUMENT) > 0){
                list.add(Gallery.applicationContext.getString(R.string.document));
            } else if((medias &MEDIA_MUSIC) > 0){
                list.add(Gallery.applicationContext.getString(R.string.music));
            }
            names = list;
        }
        return names;
    }
    private static List<Integer> types = null;
    public static List<Integer> getMedias(){
        if(types == null) {
            List<Integer> list = new ArrayList<>();
            if ((medias & MEDIA_PICTURE) > 0) {
                list.add(MEDIA_PICTURE);
            } else if ((medias & MEDIA_MOVIE) > 0) {
                list.add(MEDIA_MOVIE);
            } else if ((medias & MEDIA_DOCUMENT) > 0) {
                list.add(MEDIA_DOCUMENT);
            } else if ((medias & MEDIA_MUSIC) > 0) {
                list.add(MEDIA_MUSIC);
            }
            types = list;
        }
        return types;
    };

    public static void setMedias(int medias) {
        Configs.medias = medias;
    }

    public static boolean isPreviewVideo() {
        return previewVideo;
    }

    public static void setPreviewVideo(boolean previewVideo) {
        Configs.previewVideo = previewVideo;
    }

    public static void reset() {
        editImage = true;
        previewVideo = true;
        Configs.medias = MEDIA_PICTURE|MEDIA_MOVIE;
        names = null;
        types = null;
    }
}
