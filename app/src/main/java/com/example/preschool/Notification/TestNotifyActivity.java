package com.example.preschool.Notification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.preschool.R;

import static com.example.preschool.Notification.App.CHANNEL_1_ID;
//import static com.example.preschool.Notification.App.CHANNEL_2_ID;

public class TestNotifyActivity extends AppCompatActivity {

    private NotificationManagerCompat notificationManager;
    private EditText notify_content;
    private Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_notify);

        notificationManager = NotificationManagerCompat.from(this);
        notify_content = findViewById(R.id.notify_content);


    }

    public void sendOnChannel1(View v) {
        String message = notify_content.getText().toString();

        Intent activityIntent = new Intent(this, TestNotifyActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, activityIntent, 0);

        Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
        broadcastIntent.putExtra("toastMessage", message);
//        uri=Uri.parse("android.resource://"+getPackageName()+"/raw/sound_notify.mp3");
        uri=Uri.parse(String.format("android.resource://%s/%s/%s",this.getPackageName(),"raw","sound_notify.mp3"));

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Preschool")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.YELLOW)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .build();

        notificationManager.notify(1, notification);
    }

//    public void sendOnChannel2(View v) {
//        String message = notify_content.getText().toString();
//
//        Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
//                .setSmallIcon(R.drawable.ic_child)
//                .setContentTitle("Preschool")
//                .setContentText(message)
//                .setPriority(NotificationCompat.PRIORITY_LOW)
//                .build();
//
//        notificationManager.notify(2, notification);
//    }
}
