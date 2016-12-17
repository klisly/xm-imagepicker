package cn.iterlog.xmimagepicker.adapter;

import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import java.util.List;

import cn.iterlog.xmimagepicker.Configs;
import cn.iterlog.xmimagepicker.Gallery;
import cn.iterlog.xmimagepicker.R;
import cn.iterlog.xmimagepicker.RippleChoiceView;
import cn.iterlog.xmimagepicker.Utils.AndroidUtilities;
import cn.iterlog.xmimagepicker.Utils.MediaController;
import cn.iterlog.xmimagepicker.Utils.VideoRequestHandler;
import cn.iterlog.xmimagepicker.data.MediasLogic;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaHolder> {

    private List<MediaController.PhotoEntry> mMedias;
    private OnItemChooseListener listener;

    public MediaAdapter(List<MediaController.PhotoEntry> medias) {
        this.mMedias = medias;
    }

    public void setListener(OnItemChooseListener listener) {
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
        Log.i("MediaAdapter", "position:" + position);
        MediaController.PhotoEntry photoEntry = mMedias.get(position);
        if (photoEntry.isVideo) {
            Gallery.picasso.load(VideoRequestHandler.SCHEME_VIDEO + ":" + photoEntry.path)
                    .resize(Configs.THUMB_SIZE, Configs.THUMB_SIZE)
                    .centerCrop()
                    .placeholder(R.drawable.nophotos)
                    .error(R.drawable.nophotos).into(holder.mPortraitView);
            holder.mVideoTag.setVisibility(View.VISIBLE);
        } else {
            Uri imageURI = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.toString(photoEntry.imageId));
            Gallery.picasso
                    .load(imageURI)
                    .resize(Configs.THUMB_SIZE, Configs.THUMB_SIZE)
                    .centerCrop()
                    .placeholder(R.drawable.nophotos)
                    .error(R.drawable.nophotos)
                    .into(holder.mPortraitView);
            holder.mVideoTag.setVisibility(View.GONE);
        }
        if (MediasLogic.getInstance().isChoosed(photoEntry)) {
            holder.mRcv.setmChecked(true);
            holder.mPortraitView.setScaleX(0.9f);
            holder.mPortraitView.setScaleY(0.9f);
        } else {
            holder.mRcv.setmChecked(false);
            holder.mPortraitView.setScaleX(1.0f);
            holder.mPortraitView.setScaleY(1.0f);
        }
        if (listener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onMediaView(position, mMedias.get(position));
                }
            });
            holder.mRcv.setOnCheckedChangeListener(new RippleChoiceView.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RippleChoiceView view, boolean isChecked) {

                    if (isChecked) {
                        if (Configs.isSimpleType()) {
                            if ((mMedias.get(position).isVideo && MediasLogic.getInstance().getChoosePictures().size() > 0)
                                    || (!mMedias.get(position).isVideo && MediasLogic.getInstance().getChooseVideos().size() > 0)) {
                                StringBuilder builder = new StringBuilder();
                                builder.append("只能选择")
                                        .append(Configs.getImageSize())
                                        .append("张图片或者")
                                        .append(Configs.getVideoSize())
                                        .append("条视频");
                                AndroidUtilities.showToast(builder.toString());
                                holder.mRcv.setmChecked(false);
                                return;
                            }
                        }

                        if (mMedias.get(position).isVideo
                                && MediasLogic.getInstance().getChooseVideos().size() >= Configs.getVideoSize()) {
                            StringBuilder builder = new StringBuilder();
                            builder.append("只能选择")
                                    .append(Configs.getVideoSize())
                                    .append("条视频");
                            AndroidUtilities.showToast(builder.toString());
                            holder.mRcv.setmChecked(false);
                            return;
                        }
                        if (!mMedias.get(position).isVideo
                                && MediasLogic.getInstance().getChoosePictures().size() >= Configs.getImageSize()) {
                            StringBuilder builder = new StringBuilder();
                            builder.append("只能选择")
                                    .append(Configs.getImageSize())
                                    .append("张图片");
                            AndroidUtilities.showToast(builder.toString());
                            holder.mRcv.setmChecked(false);
                            return;
                        }
                    }


                    listener.onMediaChoose(position, mMedias.get(position), isChecked);
                    if (isChecked) {
                        holder.mPortraitView
                                .animate()
                                .scaleX(0.9f)
                                .scaleY(0.9f)
                                .setDuration(300)
                                .setInterpolator(new OvershootInterpolator())
                                .start();
                    } else {
                        holder.mPortraitView
                                .animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(300)
                                .setInterpolator(new OvershootInterpolator())
                                .start();
                    }
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

    public interface OnItemChooseListener {
        void onMediaView(int position, MediaController.PhotoEntry photoEntry);

        void onMediaChoose(int position, MediaController.PhotoEntry photoEntry, boolean isChecked);
    }

    public class MediaHolder extends RecyclerView.ViewHolder {

        ImageView mPortraitView;
        ImageView mVideoTag;
        View itemView;
        RippleChoiceView mRcv;

        public MediaHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            mPortraitView = (ImageView) itemView.findViewById(R.id.iv_portrait);
            mVideoTag = (ImageView) itemView.findViewById(R.id.iv_video_tag);
            mRcv = (RippleChoiceView) itemView.findViewById(R.id.rcv_choice);
            if (Configs.isMultiChoose()) {
                mRcv.setVisibility(View.VISIBLE);
            } else {
                mRcv.setVisibility(View.GONE);
            }
        }
    }
}
