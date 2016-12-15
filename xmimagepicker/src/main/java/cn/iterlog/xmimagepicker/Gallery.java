package cn.iterlog.xmimagepicker;

import android.app.Application;
import android.os.Handler;

import com.squareup.picasso.Picasso;

import cn.iterlog.xmimagepicker.Utils.VideoRequestHandler;

public class Gallery {

    public volatile static Application applicationContext;
    public volatile static Handler applicationHandler;
    public volatile static Picasso picasso;
    public static void init(Application application) {
        VideoRequestHandler requestHandler = new VideoRequestHandler();
        if (applicationContext == null) {
            applicationContext = application;
            applicationHandler = new Handler(application.getMainLooper());
            picasso = new Picasso.Builder(applicationContext)
                    .addRequestHandler(requestHandler)
                    .build();
        }
    }
}
