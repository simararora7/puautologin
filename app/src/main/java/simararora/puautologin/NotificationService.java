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
 * This App is Licensed under GNU General Public License. A copy of this license can be found in the root of this project.
 *
 */
public class NotificationService extends IntentService{

    /**
     * Default Constructor
     * @param name
     */
    public NotificationService(String name) {
        super(name);
    }

    /**
     * Empty Required Constructor
     */
    public NotificationService(){
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if(intent == null)
            return;

        // message to show in the notification
        String message = intent.getStringExtra("message");

        //show logout button in notification or not
        boolean showAction = intent.getBooleanExtra("showAction", true);

        //Create a builder for notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle("PU Auto Login")
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_notification_large))
        .setDefaults(Notification.DEFAULT_VIBRATE);

        //Initialise Logout action in notification builder
        if(showAction){
            Intent logoutIntent = new Intent(this, LogoutService.class);
            PendingIntent logoutPendingIntent = PendingIntent.getService(this, 0, logoutIntent, 0);
            builder.addAction(R.drawable.action_logout, "Logout", logoutPendingIntent);
        }

        // set Notification intent
        Intent in = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(in);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        // Get an instance of notificaton manager and send notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = builder.build();
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        //First Argument 0 = FLAG_UPDATE
        notificationManager.notify(0, notification);

        //Stop Service
        this.stopSelf();
    }
}
