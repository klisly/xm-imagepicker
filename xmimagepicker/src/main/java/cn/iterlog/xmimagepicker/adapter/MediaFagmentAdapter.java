package cn.iterlog.xmimagepicker.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import cn.iterlog.xmimagepicker.Configs;
import cn.iterlog.xmimagepicker.MediaFragment;
public class MediaFagmentAdapter extends FragmentPagerAdapter {

    public MediaFagmentAdapter(FragmentManager fm, Context context) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return MediaFragment.newInstance(Configs.getMedias().get(position));
    }

    @Override
    public int getCount() {
        return Configs.getMedias().size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Configs.getNames().get(position);
    }
}