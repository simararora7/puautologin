package simararora.puautologin;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import simararora.puautologin.widget.LogoutService;

/**
 * Created by Simar Arora on 2/14/2015.
 *
 */
public class NotificationService extends IntentService{

    public NotificationService(String name) {
        super(name);
    }

    public NotificationService(){
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String message = intent.getStringExtra("message");
        boolean showAction = intent.getBooleanExtra("showAction", true);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setContentTitle("PU Auto Login");
        builder.setContentText(message);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_notification_large));
        if(showAction){
            Intent logoutIntent = new Intent(this, LogoutService.class);
            PendingIntent logoutPendingIntent = PendingIntent.getService(this, 0, logoutIntent, 0);
            builder.addAction(R.drawable.action_logout, "Logout", logoutPendingIntent);
        }

        Intent in = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(in);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = builder.build();
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);
    }
}
