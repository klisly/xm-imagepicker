package cn.iterlog.xmimagepicker;

import android.animation.Animator;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import cn.iterlog.xmimagepicker.Utils.AndroidUtilities;
import cn.iterlog.xmimagepicker.Utils.MediaController;
import cn.iterlog.xmimagepicker.data.MediasLogic;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PicturesPreviewActivity extends AppCompatActivity implements MediasLogic.MediaListener {
    private static int displayType = Configs.PREVIEW_TYPE_PICTURE;
    private int curPos = 0;
    private PreviewPagerAdapter pagerAdapter;
    private Toolbar toolbar;
    private List<MediaController.PhotoEntry> datas;
    private TextView mTvChoose;
    private RippleChoiceView mRcvNumber;
    private RippleChoiceView mRcvItemChoose;
    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures_pager);
        displayType = getIntent().getIntExtra(Configs.PREVIEW_TYPE, Configs.PREVIEW_TYPE_PICTURE);
        curPos = getIntent().getIntExtra(Configs.PREVIEW_POS, 0);
        mTvChoose = (TextView) findViewById(R.id.choose);
        mRcvNumber = (RippleChoiceView) findViewById(R.id.rcv_choice);
        mViewPager = (PreviewViewPager) findViewById(R.id.view_pager);
        mRcvItemChoose = (RippleChoiceView) findViewById(R.id.rcv_item_choice);

        if (displayType == Configs.PREVIEW_TYPE_VIDEO) {
            datas = MediasLogic.getInstance().getChooseVideos();
        } else if (displayType == Configs.PREVIEW_TYPE_PICTURE) {
            datas = MediasLogic.getInstance().getChoosePictures();
        } else {
            datas = MediasLogic.getInstance()
                    .getChooseAlbum().get(MediasLogic.getInstance().getChoosePosition())
                    .photos;
        }

        if (MediasLogic.getInstance().getChooseCount() > 0) {
            mRcvNumber.setVisibility(View.VISIBLE);
            updateChooseCount();
        } else {
            mTvChoose.setTextColor(getResources().getColor(R.color.white_50));
        }
        initToolBar();
        pagerAdapter = new PreviewPagerAdapter(datas);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(curPos);

        initListener();

        updateChooseStatus();
    }

    private void initListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                curPos = position;
                toolbar.setTitle(String.format(title, position + 1, datas.size()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        MediasLogic.getInstance().registerListener(this, this);

        mTvChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void updateChooseCount() {
        int count = 0;
        if (displayType == Configs.PREVIEW_TYPE_VIDEO || displayType == Configs.PREVIEW_TYPE_VIDEO_ALL) {
            count = MediasLogic.getInstance().getChooseVideos().size();
        } else {
            count = MediasLogic.getInstance().getChoosePictures().size();
        }

        if (count > 0) {
            if (mRcvNumber.getVisibility() != View.VISIBLE) {
                mRcvNumber.setVisibility(View.VISIBLE);
                mRcvNumber.setScaleX(0f);
                mRcvNumber.setScaleX(0f);
                mRcvNumber.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(300)
                        .setInterpolator(new OvershootInterpolator())
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mRcvNumber.setScaleX(1f);
                                mRcvNumber.setScaleX(1f);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                mRcvNumber.setScaleX(1f);
                                mRcvNumber.setScaleX(1f);
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        })
                        .start();
                mTvChoose.setTextColor(getResources().getColor(R.color.white));
            }
            mRcvNumber.setmNumber(count);

        } else {
            mRcvNumber.animate()
                    .scaleX(0.0f)
                    .scaleY(0.0f)
                    .setDuration(300)
                    .setInterpolator(new AccelerateInterpolator())
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mRcvNumber.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            mRcvNumber.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    })
                    .start();
            mTvChoose.setTextColor(getResources().getColor(R.color.white_50));
        }

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                curPos = position;
                updateChooseStatus();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mRcvItemChoose.setOnCheckedChangeListener(new RippleChoiceView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RippleChoiceView view, boolean isChecked) {
                if (isChecked) {
                    if (Configs.isSimpleType()) {
                        if ((datas.get(curPos).isVideo && MediasLogic.getInstance().getChoosePictures().size() > 0)
                                || datas.get(curPos).isVideo && MediasLogic.getInstance().getChooseVideos().size() > 0) {
                            StringBuilder builder = new StringBuilder();
                            builder.append("只能选择")
                                    .append(Configs.getImageSize())
                                    .append("张图片或者")
                                    .append(Configs.getVideoSize())
                                    .append("条视频");
                            AndroidUtilities.showToast(builder.toString());
                            mRcvItemChoose.setmChecked(false);
                            return;
                        }
                    }

                    if (datas.get(curPos).isVideo
                            && MediasLogic.getInstance().getChooseVideos().size() >= Configs.getVideoSize()) {
                        StringBuilder builder = new StringBuilder();
                        builder.append("只能选择")
                                .append(Configs.getVideoSize())
                                .append("条视频");
                        AndroidUtilities.showToast(builder.toString());
                        mRcvItemChoose.setmChecked(false);
                        return;
                    }
                    if (!datas.get(curPos).isVideo
                            && MediasLogic.getInstance().getChoosePictures().size() >= Configs.getImageSize()) {
                        StringBuilder builder = new StringBuilder();
                        builder.append("只能选择")
                                .append(Configs.getImageSize())
                                .append("张图片");
                        AndroidUtilities.showToast(builder.toString());
                        mRcvItemChoose.setmChecked(false);
                        return;
                    }
                    MediasLogic.getInstance().chooseMedia(datas.get(curPos));
                } else {
                    MediasLogic.getInstance().unChooseMedia(datas.get(curPos));
                }
            }
        });
    }

    private String title = "%s/%s";

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(String.format(title, curPos + 1, datas.size()));
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onMediaNotify(int type) {
        if (type == Configs.NOTIFY_TYPE_STATUS) {
            updateChooseStatus();
        }
    }


    static class PreviewPagerAdapter extends PagerAdapter {
        private List<MediaController.PhotoEntry> datas;

        public PreviewPagerAdapter(List<MediaController.PhotoEntry> datas) {
            this.datas = datas;
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, final int position) {
            View view = LayoutInflater.from(Gallery.applicationContext).inflate(R.layout.item_preview, null, false);
            SurfaceView surfaceView = (SurfaceView) view.findViewById(R.id.sv);
            ImageView preview = (ImageView) view.findViewById(R.id.preview);
            ImageView play = (ImageView) view.findViewById(R.id.iv_play);
            final PhotoViewAttacher photoView = new PhotoViewAttacher(preview);
            photoView.update();
            if (datas.get(position).isVideo) {
                play.setVisibility(View.VISIBLE);
            } else {
                play.setVisibility(View.INVISIBLE);
                Gallery.picasso
                        .load(Uri.fromFile(new File(datas.get(position).path)))
                        .placeholder(R.drawable.nophotos)
                        .error(R.drawable.nophotos)
                        .into(preview);
            }
            container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    private void updateChooseStatus() {
        if (MediasLogic.getInstance().isChoosed(datas.get(curPos))) {
            mRcvItemChoose.setmChecked(true);
        } else {
            mRcvItemChoose.setmChecked(false);
        }
        updateChooseCount();
    }
}
