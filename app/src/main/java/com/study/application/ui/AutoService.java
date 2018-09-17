package com.study.application.ui;

import android.annotation.SuppressLint;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.support.v4.app.NotificationCompat.*;

public class AutoService extends Service{
    private String date1,date2;

    String[] getDateList,getItemList;

    private final Date date = new Date();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd ", Locale.TAIWAN);
    public AutoService() {
    }

    @Override
    public void onCreate() {

        Log.i("Owen","onCreate - Thread ID = " + Thread.currentThread().getId());
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getDateList = intent.getStringArrayExtra("date");
        getItemList = intent.getStringArrayExtra("item");
        int mNotificationNum = 0;
        for (int i = 0; i < getDateList.length; i++) {
            //date2 = "2018-09-20";
            if (DateUtils.isDate2Bigger(getDateList[i], dateFormat.format(date))) {
                mNotificationNum++;

                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                Builder builder = new Builder(AutoService.this)
                        .setContentTitle("歸還提醒")
                        .setSmallIcon(R.drawable.ic_home)
                        .setContentText(getItemList[i] + "借用已到期,請及時歸還")
                        .setNumber(mNotificationNum)
                        .setAutoCancel(true);
                Notification notification = builder.build();
                manager.notify(mNotificationNum, notification);

                //Log.e("Owen","onStartCommand end - Thread ID = " + Thread.currentThread().getId());
            }
        }
            //dosomething

            AlarmManager manager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
            int mHour = 8 * 60 * 60 * 1000; //8hours
            //long triggerAtTime = SystemClock.elapsedRealtime()+mHour;
            long triggerAtTime = System.currentTimeMillis() + mHour;

            Log.e("Owen" + mHour, "onStartCommand - 2 = " + triggerAtTime);
            Intent intent2 = new Intent(this, AutoReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent2, 0);
            manager1.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pi);
            flags = START_STICKY;
            //Log.e("Owen","onStartCommand - 3= ");
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
