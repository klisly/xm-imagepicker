package cn.iterlog.xmimagepicker.adapter;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.iterlog.xmimagepicker.Constants;
import cn.iterlog.xmimagepicker.Gallery;
import cn.iterlog.xmimagepicker.R;
import cn.iterlog.xmimagepicker.Utils.MediaController;
import cn.iterlog.xmimagepicker.VideoRequestHandler;

public class MediaRecyclerAdapter extends RecyclerView.Adapter<MediaRecyclerAdapter.MediaHolder> {

    private List<MediaController.PhotoEntry> mMedias;
    private Context mContext;
    public MediaRecyclerAdapter(Context ctx, List<MediaController.PhotoEntry> medias) {
        this.mMedias = medias;
        mContext = ctx;
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
    public void onBindViewHolder(MediaHolder holder, int position) {
        MediaController.PhotoEntry photoEntry = mMedias.get(position);
        if (photoEntry.isVideo) {
            Gallery.picasso.load(VideoRequestHandler.SCHEME_VIDEO+":"+photoEntry.path)
                    .resize(Constants.THUMB_SIZE, Constants.THUMB_SIZE)
                    .centerCrop()
                    .placeholder(R.drawable.nophotos)
                    .error(R.drawable.nophotos).into(holder.mPortraitView);
        } else {
            Uri imageURI = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.toString(photoEntry.imageId));
            Gallery.picasso
                    .load(imageURI)
                    .resize(Constants.THUMB_SIZE, Constants.THUMB_SIZE)
                    .centerCrop()
                    .placeholder(R.drawable.nophotos)
                    .error(R.drawable.nophotos)
                    .into(holder.mPortraitView);
        }

    }

    @Override
    public int getItemCount() {
        if (mMedias == null) {
            return 0;
        }
        return mMedias.size();
    }

    class MediaHolder extends RecyclerView.ViewHolder {

        ImageView mPortraitView;
        TextView mNickNameView;
        TextView mMottoView;

        public MediaHolder(View itemView) {
            super(itemView);

            mPortraitView = (ImageView) itemView.findViewById(R.id.iv_portrait);

        }
    }
}
