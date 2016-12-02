package cn.iterlog.xmimagepicker.data;

import java.util.ArrayList;
import java.util.HashMap;

import cn.iterlog.xmimagepicker.Gallery;
import cn.iterlog.xmimagepicker.Utils.MediaController;

/**
 * Created by wizardholy on 2016/11/5.
 */
public class MediasLogic {
    private static MediasLogic ourInstance = new MediasLogic();
    private int selectAlbum;
    private int selectMedias;
    private String[] filterMimeTypes = null;
    private ArrayList<MediaController.AlbumEntry> pictureAlbums = new ArrayList<>();
    private ArrayList<MediaController.AlbumEntry> videoAlbums = new ArrayList<>();
    private HashMap<Integer, MediaController.PhotoEntry> selectedPhotos = new HashMap<>();
    private boolean loading = false;
    private HashMap<Object, MediaListener> listeners = new HashMap<>();
    public static MediasLogic getInstance() {
        return ourInstance;
    }

    private MediasLogic() {
    }

    public static MediasLogic getOurInstance() {
        return ourInstance;
    }

    public static void setOurInstance(MediasLogic ourInstance) {
        MediasLogic.ourInstance = ourInstance;
    }

    public String[] getFilterMimeTypes() {
        return filterMimeTypes;
    }

    public void setFilterMimeTypes(String[] filterMimeTypes) {
        this.filterMimeTypes = filterMimeTypes;
    }

    public ArrayList<MediaController.AlbumEntry> getPictureAlbums() {
        return pictureAlbums;
    }

    public void setPictureAlbums(ArrayList<MediaController.AlbumEntry> pictureAlbums) {
        this.pictureAlbums = pictureAlbums;
        notify(Gallery.TYPE_PICTURE);
    }

    public ArrayList<MediaController.AlbumEntry> getVideoAlbums() {
        return videoAlbums;
    }

    public void setVideoAlbums(ArrayList<MediaController.AlbumEntry> videoAlbums) {
        this.videoAlbums = videoAlbums;
        notify(Gallery.TYPE_VIDEO);
    }

    public HashMap<Integer, MediaController.PhotoEntry> getSelectedPhotos() {
        return selectedPhotos;
    }

    public void setSelectedPhotos(HashMap<Integer, MediaController.PhotoEntry> selectedPhotos) {
        this.selectedPhotos = selectedPhotos;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public void registerListener(Object obj, MediaListener listener){
        listeners.put(obj, listener);
    }
    public void unRegisterListener(Object obj){
        listeners.remove(obj);
    }

    public interface MediaListener {
        void onMediaLoaded(int type);
    }

    public interface DirectoryListener {
        void onDirectoryChange(int type);
    }

    private void notify(int type){
        for(MediaListener listener:listeners.values()){
           listener.onMediaLoaded(type);
        }
    }
}
