package org.xxnan.blockinger.game.activities;

import android.app.Application;

import net.youmi.android.AdManager;
import net.youmi.android.nm.sp.SpotManager;
import net.youmi.android.nm.sp.SpotRequestListener;

/**
 * Created by vanke on 2017/8/31.
 */

public class MyApplication extends Application {
    public MyApplication()
    {

    }

    @Override
    public void onCreate() {
        super.onCreate();
       /* SpotRequestListener spotRequestListener=new SpotRequestListener() {
            @Override
            public void onRequestSuccess() {

            }

            @Override
            public void onRequestFailed(int i) {

            }
        };
        SpotManager.getInstance(this).requestSpot(spotRequestListener);*/
    }
}
