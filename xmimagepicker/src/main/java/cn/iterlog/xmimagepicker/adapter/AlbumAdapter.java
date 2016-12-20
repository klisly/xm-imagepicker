package cn.iterlog.xmimagepicker.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.iterlog.xmimagepicker.Configs;
import cn.iterlog.xmimagepicker.Gallery;
import cn.iterlog.xmimagepicker.R;
import cn.iterlog.xmimagepicker.Utils.MediaController;
import cn.iterlog.xmimagepicker.data.MediasLogic;
import cn.iterlog.xmimagepicker.widget.BackupImageView;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MediaHolder> {

    private List<MediaController.AlbumEntry> albums;
    private OnItemChangeListener listener;

    public AlbumAdapter(List<MediaController.AlbumEntry> medias) {
        this.albums = medias;
    }

    public void setListener(OnItemChangeListener listener) {
        this.listener = listener;
    }

    public List<MediaController.AlbumEntry> getAlbums() {
        return albums;
    }

    public void setAlbums(List<MediaController.AlbumEntry> albums) {
        this.albums = albums;
    }

    @Override
    public MediaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View childView = inflater.inflate(R.layout.item_album, parent, false);
        MediaHolder viewHolder = new MediaHolder(childView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MediaHolder holder, final int position) {
        MediaController.AlbumEntry albumEntry = albums.get(position);
        holder.mTvName.setText(albumEntry.bucketName);
        String infoSuffix = albumEntry.isVideo? Configs.VIDEO_ALBUM_SUFFIX: Configs.IMAGE_ALBUM_SUFFIX;
        holder.mTvInfo.setText(albumEntry.photos.size() + infoSuffix);

        if (albumEntry.coverPhoto.thumbPath != null) {
            holder.mThumb.setImage(albumEntry.coverPhoto.thumbPath, null,
                    Gallery.applicationContext.getResources().getDrawable(R.drawable.nophotos));
        } else if (albumEntry.coverPhoto.path != null) {
            holder.mThumb.setOrientation(albumEntry.coverPhoto.orientation, true);
            if (albumEntry.coverPhoto.isVideo) {
                holder.mThumb.setImage(
                        "vthumb://" + albumEntry.coverPhoto.imageId + ":" + albumEntry.coverPhoto.path, null,
                        Gallery.applicationContext.getResources().getDrawable(R.drawable.nophotos));
            } else {
                holder.mThumb.setImage(
                        "thumb://" + albumEntry.coverPhoto.imageId + ":" + albumEntry.coverPhoto.path, null,
                        Gallery.applicationContext.getResources().getDrawable(R.drawable.nophotos));
            }
        } else {
            holder.mThumb.setImageResource(R.drawable.nophotos);
        }
        if(MediasLogic.getInstance().isChooseAlbum(albumEntry.isVideo, position)){
            holder.mIvCheck.setVisibility(View.VISIBLE);
        } else {
            holder.mIvCheck.setVisibility(View.INVISIBLE);
        }
        if (listener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAlbumChoose(holder, position, albums.get(position));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (albums == null) {
            return 0;
        }
        return albums.size();
    }

    public interface OnItemChangeListener {
        void onAlbumChoose(MediaHolder view, int position, MediaController.AlbumEntry entry);
    }

    public class MediaHolder extends RecyclerView.ViewHolder {

        BackupImageView mThumb;
        ImageView mIvCheck;
        View itemView;
        TextView mTvName;
        TextView mTvInfo;
        public MediaHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            mThumb = (BackupImageView) itemView.findViewById(R.id.iv_thumbnail);
            mIvCheck = (ImageView) itemView.findViewById(R.id.iv_check);
            mTvName = (TextView) itemView.findViewById(R.id.tv_album_name);
            mTvInfo = (TextView) itemView.findViewById(R.id.tv_album_info);
        }
    }
}
