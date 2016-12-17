package cn.iterlog.xmimagepicker;

import android.app.Application;
import android.os.Handler;

import com.squareup.picasso.Picasso;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.iterlog.xmimagepicker.Utils.VideoRequestHandler;

public class Gallery {

    public volatile static Application applicationContext;
    public volatile static Handler applicationHandler;
    public volatile static Picasso picasso;
    private volatile static ThreadPoolExecutor executor;
    public static void init(Application application) {
        VideoRequestHandler requestHandler = new VideoRequestHandler();
        if (applicationContext == null) {
            applicationContext = application;
            applicationHandler = new Handler(application.getMainLooper());
            picasso = new Picasso.Builder(applicationContext)
                    .addRequestHandler(requestHandler)
                    .build();
            executor = new ThreadPoolExecutor(5, 15, 30, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
        }
    }
}
