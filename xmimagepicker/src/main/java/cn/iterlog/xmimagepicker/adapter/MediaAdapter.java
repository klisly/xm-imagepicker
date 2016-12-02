package cn.iterlog.xmimagepicker.adapter;

import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import cn.iterlog.xmimagepicker.Gallery;
import cn.iterlog.xmimagepicker.R;
import cn.iterlog.xmimagepicker.Utils.MediaController;
import cn.iterlog.xmimagepicker.Utils.VideoRequestHandler;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaHolder> {

    private List<MediaController.PhotoEntry> mMedias;
    private OnItemChangeListener listener;

    public MediaAdapter(List<MediaController.PhotoEntry> medias) {
        this.mMedias = medias;
    }

    public void setListener(OnItemChangeListener listener) {
        this.listener = listener;
    }

    public List<MediaController.PhotoEntry> getmMedias() {
        return mMedias;
    }

    public void setmMedias(List<MediaController.PhotoEntry> mMedias) {
        this.mMedias = mMedias;
    }

    @Override
    public MediaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View childView = inflater.inflate(R.layout.item_media, parent, false);
        MediaHolder viewHolder = new MediaHolder(childView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MediaHolder holder, final int position) {
        MediaController.PhotoEntry photoEntry = mMedias.get(position);
        if (photoEntry.isVideo) {
            Gallery.picasso.load(VideoRequestHandler.SCHEME_VIDEO + ":" + photoEntry.path)
                    .resize(Gallery.THUMB_SIZE, Gallery.THUMB_SIZE)
                    .centerCrop()
                    .placeholder(R.drawable.nophotos)
                    .error(R.drawable.nophotos).into(holder.mPortraitView);
            holder.mVideoTag.setVisibility(View.VISIBLE);
        } else {
            Uri imageURI = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.toString(photoEntry.imageId));
            Gallery.picasso
                    .load(imageURI)
                    .resize(Gallery.THUMB_SIZE, Gallery.THUMB_SIZE)
                    .centerCrop()
                    .placeholder(R.drawable.nophotos)
                    .error(R.drawable.nophotos)
                    .into(holder.mPortraitView);
            holder.mVideoTag.setVisibility(View.GONE);
        }
        if (listener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onMediaView(holder, position, mMedias.get(position));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mMedias == null) {
            return 0;
        }
        return mMedias.size();
    }

    public interface OnItemChangeListener {
        void onMediaView(MediaHolder view, int position, MediaController.PhotoEntry photoEntry);
    }

    public class MediaHolder extends RecyclerView.ViewHolder {

        ImageView mPortraitView;
        ImageView mVideoTag;
        View itemView;

        public MediaHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            mPortraitView = (ImageView) itemView.findViewById(R.id.iv_portrait);
            mVideoTag = (ImageView) itemView.findViewById(R.id.iv_video_tag);
        }
    }
}
