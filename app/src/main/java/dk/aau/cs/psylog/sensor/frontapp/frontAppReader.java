package dk.aau.cs.psylog.sensor.frontapp;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import dk.aau.cs.psylog.module_lib.DBAccessContract;
import dk.aau.cs.psylog.module_lib.ISensor;

public class frontAppReader implements ISensor {


    ActivityManager activityManager;
    Timer timer = new Timer();
    TimerTask timerTask;
    long delay = 0;
    long periode = 1000;
    static ComponentName lastComponentName;

    private ContentResolver resolver;

    public frontAppReader(final Context context) {
        resolver = context.getContentResolver();

        activityManager = (ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE);

        timerTask = new TimerTask() {
            @Override
            public void run() {
                ComponentName componentName = activityManager.getRunningTasks(Integer.MAX_VALUE).get(0).topActivity;
                if(lastComponentName == null || !componentName.equals(lastComponentName)){
                    Uri uri = Uri.parse(DBAccessContract.DBACCESS_CONTENTPROVIDER + "frontapp_frontapp");
                    ContentValues values = new ContentValues();
                    values.put("frontapp", componentName.flattenToString());
                    resolver.insert(uri, values);
                    lastComponentName = componentName;
                }
            }
        };
    }

    public void startSensor() {
        timer.schedule(timerTask,delay,periode);
    }

    public void stopSensor() {
        timer.cancel();
        timer.purge();
    }

    @Override
    public void sensorParameters(Intent intent) {
        Log.d("asdasd","3");
        delay = intent.getLongExtra("delay",0);
        periode = intent.getLongExtra("peirode",1000);
    }
}
