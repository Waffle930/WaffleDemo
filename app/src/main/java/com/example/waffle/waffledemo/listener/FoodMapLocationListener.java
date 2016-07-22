package com.example.waffle.waffledemo.listener;

import android.os.Handler;
import android.os.Message;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;

/**
 * Created by Waffle on 2016/7/21.
 */

public class FoodMapLocationListener implements AMapLocationListener {

    public static final int FOOD_MAP_LOCATION_COMPLETE = 0;


    private Handler mHandler;

    public FoodMapLocationListener(Handler handler){
        mHandler = handler;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if(aMapLocation != null){
            Message msg = new Message();
            msg.obj = aMapLocation;
            msg.what = FOOD_MAP_LOCATION_COMPLETE;
            mHandler.sendMessage(msg);
        }
    }

}
