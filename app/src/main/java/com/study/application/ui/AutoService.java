package com.study.application.ui;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.study.application.R;
import com.study.application.leanCloud.DisplayData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.support.v4.app.NotificationCompat.*;


public class AutoService extends Service {
    private String date1,date2;
    private final Date date = new Date();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd ", Locale.TAIWAN);
    private ArrayList<DisplayData> conditionDataArrayList;
    public AutoService() {
    }

    @Override
    public void onCreate() {
        Log.i("Owen","onCreate - Thread ID = " + Thread.currentThread().getId());
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

                date2 = "2018-08-28";
                    if (DateUtils.isDate2Bigger(date2,dateFormat.format(date) )){
                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(AutoService.this)
                        .setContentTitle("歸還提醒")
                        .setSmallIcon(R.drawable.ic_home)
                        .setContentText("借用已到期,請及時歸還")
                        .setNumber(1)
                        .setAutoCancel(true);
                        Notification notification = builder.build();
                        manager.notify(1,notification);
                    Log.e("Owen","onStartCommand end - Thread ID = " + Thread.currentThread().getId());
                    }

                //dosomething

        AlarmManager manager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
        int mHour = 8*60*60*1000; //8hours
        long triggerAtTime = SystemClock.elapsedRealtime()+mHour;
        Log.e("Owen" + mHour,"onStartCommand - 2 = "+ triggerAtTime);
        Intent intent2 = new Intent(this,AutoReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this,0,intent2,0);
        manager1.set(AlarmManager.RTC_WAKEUP,triggerAtTime,pi);
        flags = START_STICKY;
        Log.e("Owen","onStartCommand - 3= ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("Owen","onBind - Thread ID = " + Thread.currentThread().getId());
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onDestroy() {
        Log.i("Owen","onDestroy - Thread ID = " + Thread.currentThread().getId());
        super.onDestroy();
    }
}
