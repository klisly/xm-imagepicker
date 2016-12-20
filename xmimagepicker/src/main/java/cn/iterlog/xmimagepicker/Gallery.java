package cn.iterlog.xmimagepicker;

import android.app.Application;
import android.os.Handler;

public class Gallery {
    public static int lastClassGuid = 1;
    public volatile static Application applicationContext;
    public volatile static Handler applicationHandler;

    public static void init(Application application) {
        if (applicationContext == null) {
            applicationContext = application;
            applicationHandler = new Handler(application.getMainLooper());
            CrashHandler.getInstance().init(applicationContext);
        }
    }

    public static void clean() {
        lastClassGuid = 1;
        applicationContext = null;
        applicationHandler = null;
    }
}
