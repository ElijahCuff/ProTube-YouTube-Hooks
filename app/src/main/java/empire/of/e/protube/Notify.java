package empire.of.e.protube;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class Notify extends Service { 

		int notifyID = 6272;
		static String title;
		static String descr;

		NotificationManager notiManager;
		@Override
		public void onDestroy() {
				notiManager.cancel(notifyID);
				super.onDestroy();
		}

		@Override
		public void onTrimMemory(int level) {
				// TODO: Implement this method
		  	//	super.onTrimMemory(level);
		}



    @Override
    public void onTaskRemoved(Intent rootIntent) {
        this.stopSelf();
				super.onTaskRemoved(rootIntent);
    }

		@Override
		public int onStartCommand(Intent intent, int flags, int startId) {
				NotificationCompat.Builder builder =
            new NotificationCompat.Builder(this)
						.setSmallIcon(R.drawable.ic_launcher)
						.setAutoCancel(false)
						.setColorized(true)
						.setOngoing(true)
						//		.setContentTitle(title)
						.setTicker(title)
						.setChannelId("zoopy")
						.setContentText(descr);
				Intent targetIntent = new Intent(this, Smasher.class);
				PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				builder.setContentIntent(contentIntent);
				notiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				notiManager.notify(notifyID, builder.build());
				return START_NOT_STICKY;	
		}

		@Override
		public IBinder onBind(Intent p1) {
				// TODO: Implement this method
				return null;
		}

}
