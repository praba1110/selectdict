package dev.noob.pro.rb.selectdict;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by rb on 22/11/15.
 */
public class HeadService extends Service
{
    public String LOGGING="LOGGING";
    HeadLayer headLayer;
    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        initHeadLayer();
        return super.onStartCommand(intent, flags, startId);
    }
    public void initHeadLayer()
    {
        this.headLayer =new HeadLayer(this);
        Log.d(LOGGING,"Inside Head");
    }

    @Override
    public void onDestroy()
    {
        headLayer.destroy();
    }
}
