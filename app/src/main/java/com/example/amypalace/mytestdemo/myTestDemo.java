package com.example.amypalace.mytestdemo;

import android.app.Application;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;

/**
 * Created by AmyPalace on 1/30/2018.
 */

public class myTestDemo extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AVOSCloud.initialize(this,"NTQblNi5IJ7OtwFLe3g0BtgT-gzGzoHsz", "mQElN8ot3CM7oU3RXIwBNskj");
        AVOSCloud.setDebugLogEnabled(true);
        AVAnalytics.enableCrashReport(this, true);
    }
}