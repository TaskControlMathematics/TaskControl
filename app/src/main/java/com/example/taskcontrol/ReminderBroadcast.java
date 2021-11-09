package com.example.taskcontrol;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("taskName");
        String desc = intent.getStringExtra("taskDesc");
        String time = intent.getStringExtra("time");
        int id = intent.getIntExtra("id_notification",1);
        Intent resultIntent = new Intent(context,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,3,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"zxc")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Task name: "+title)
                .setContentText("Time: "+time)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setOngoing(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//        Log.d("INTENTQWEASD:",""+intent.getStringExtra("hello_123"));
        notificationManager.notify(id,builder.build());

    }
}
