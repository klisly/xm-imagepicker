package cn.iterlog.imgaepicker.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import cn.iterlog.xmimagepicker.BaseActivity;

import cn.iterlog.imgaepicker.R;
import cn.iterlog.imgaepicker.util.ActivityUtils;
import cn.iterlog.xmimagepicker.PickerActivity;
import cn.iterlog.xmimagepicker.corp.Crop;

public class MainActivity extends BaseActivity {

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

        // Create the presenter
        new MainPresenter(fragment);
//        GridView gv = (GridView) findViewById(R.id.gv);
//        gv.setAdapter(adapter = new BaseAdapter() {
//            @Override
//            public int getCount() {
//                return photos == null ? 0 : photos.size();
//            }
//
//            @Override
//            public Object getItem(int position) {
//                if (photos == null) {
//                    return null;
//                }
//                return photos.get(position);
//            }
//
//            @Override
//            public long getItemId(int position) {
//                return position;
//            }
//
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                ImageView view = new ImageView(MainActivity.this);
//                view.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                view.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                        256));
//                String path = (String) getItem(position);
//                BitmapFactory.Options opts = new BitmapFactory.Options();
//                opts.inPreferredConfig = Bitmap.Config.ARGB_4444;
//                opts.inSampleSize = 4;
//                Bitmap bitmap = BitmapFactory.decodeFile(path, opts);
//                view.setImageBitmap(bitmap);
//                return view;
//            }
//        });
    }

    @SuppressWarnings("all")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (12 == requestCode && resultCode == Activity.RESULT_OK) {
            Log.i(PickerActivity.class.getCanonicalName(), Crop.getOutput(data).toString());
        }
    }
}