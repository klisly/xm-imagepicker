package cn.iterlog.xmimagepicker.adapter;

import android.net.Uri;
import android.provider.MediaStore;
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
import cn.iterlog.xmimagepicker.Utils.VideoRequestHandler;
import cn.iterlog.xmimagepicker.data.MediasLogic;

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
        if (albumEntry.isVideo) {
            Gallery.picasso.load(VideoRequestHandler.SCHEME_VIDEO + ":" + albumEntry.coverPhoto.path)
                    .resize(Configs.THUMB_SIZE, Configs.THUMB_SIZE)
                    .centerCrop()
                    .placeholder(R.drawable.nophotos)
                    .error(R.drawable.nophotos).into(holder.mThumb);
        } else {
            Uri imageURI = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.toString(albumEntry.coverPhoto.imageId));
            Gallery.picasso
                    .load(imageURI)
                    .resize(Configs.THUMB_SIZE, Configs.THUMB_SIZE)
                    .centerCrop()
                    .placeholder(R.drawable.nophotos)
                    .error(R.drawable.nophotos)
                    .into(holder.mThumb);
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

        ImageView mThumb;
        ImageView mIvCheck;
        View itemView;
        TextView mTvName;
        TextView mTvInfo;
        public MediaHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            mThumb = (ImageView) itemView.findViewById(R.id.iv_thumbnail);
            mIvCheck = (ImageView) itemView.findViewById(R.id.iv_check);
            mTvName = (TextView) itemView.findViewById(R.id.tv_album_name);
            mTvInfo = (TextView) itemView.findViewById(R.id.tv_album_info);
        }
    }
}
