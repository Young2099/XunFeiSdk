package com.lanfeng.young.xunfeisdk.Chat;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.iflytek.location.PosLocator;
import com.iflytek.location.result.GPSLocResult;
import com.iflytek.location.result.NetLocResult;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * 位置Repo
 */

@Singleton
public class LocationRepo {
    private Context mContext;
    private SingleLiveEvent<NetLocResult> mNetLocData = new SingleLiveEvent<>();
    private SingleLiveEvent<GPSLocResult> mGPSLocData = new SingleLiveEvent<>();

    @Inject
    public LocationRepo(Context context) {
        mContext = context;
    }

    public LiveData<NetLocResult> getNetLoc() {
        PosLocator.getInstance(mContext).asyncGetLocation(PosLocator.TYPE_NET_LOCATION, locResult -> mNetLocData.postValue((NetLocResult) locResult));

        return mNetLocData;
    }

    public LiveData<GPSLocResult> getGPSLoc() {
        PosLocator.getInstance(mContext).asyncGetLocation(PosLocator.TYPE_GPS_LOCATION, locResult -> mGPSLocData.postValue((GPSLocResult) locResult));

        return mGPSLocData;
    }

    public void stopLocate() {
        PosLocator.getInstance(mContext).asyncDestroy();
    }

}
