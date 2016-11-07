package cn.iterlog.xmimagepicker;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class MediaFagmentAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public MediaFagmentAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        int mediaType = 0;
        if(position == 0){
            mediaType = Constants.TYPE_PICTURE;
        } else if(position == 1){
            mediaType = Constants.TYPE_VIDEO;
        }
        return MediaFragment.newInstance(mediaType);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.picture);
            case 1:
                return mContext.getString(R.string.video);
        }
        return null;
    }
}