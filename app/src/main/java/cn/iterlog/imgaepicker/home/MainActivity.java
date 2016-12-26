package cn.iterlog.imgaepicker.home;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import cn.iterlog.imgaepicker.R;
import cn.iterlog.imgaepicker.util.ActivityUtils;
import cn.iterlog.xmimagepicker.BaseActivity;
import cn.iterlog.xmimagepicker.Configs;

public class MainActivity extends BaseActivity {
    private MainPresenter mainPresenter;
//    private List<String> photos;
//    private BaseAdapter adapter;

    @SuppressWarnings("all")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainFragment fragment = (MainFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);
        if(fragment == null){
            fragment = MainFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    fragment, R.id.contentFrame);
        }
        mainPresenter = new MainPresenter(fragment);
    }

    @SuppressWarnings("all")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (12 == requestCode && resultCode == Activity.RESULT_OK) {
            int type = data.getIntExtra(Configs.MEDIA_TYPE, -1);
            if(type == Configs.MEDIA_PICTURE){
                Uri res = data.getParcelableExtra(Configs.OUT_PUT);
                mainPresenter.setImage(false, res);
                Toast.makeText(this, "result: picture:"+res, Toast.LENGTH_LONG).show();
            } else if(type == Configs.MEDIA_MOVIE){
                Uri res = data.getParcelableExtra(Configs.OUT_PUT);
                mainPresenter.setImage(true, res);
                Toast.makeText(this, "result: movie:"+res, Toast.LENGTH_LONG).show();
            } else if(type == Configs.MEDIA_MULTI) {
                List<Uri> videos = data.getParcelableArrayListExtra(Configs.OUT_PUT_VIDEOS);
                List<Uri> images = data.getParcelableArrayListExtra(Configs.OUT_PUT_IMAGES);
                Log.i(TAG, "videos:"+videos+" images:"+images);
                Toast.makeText(this, "result: images:"+images.toString()+" videos:"+videos.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }
}