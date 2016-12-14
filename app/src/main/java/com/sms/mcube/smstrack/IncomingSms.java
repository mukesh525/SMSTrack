package com.sms.mcube.smstrack;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sms.mcube.smstrack.syncadapter.SyncAdapter;
import com.sms.mcube.smstrack.syncadapter.SyncUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.sms.mcube.smstrack.TAG.LANDING_NUMBER;


/**
 * Created by mukesh on 26/11/15.
 */
public class IncomingSms extends BroadcastReceiver implements TAG {

    private static SmsListener mListener;
    String loginURL = "http://mcube.vmc.in/api/getSMS";
    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();
    private String sender;

    //   private static SmsListener mListener;
    public void onReceive(Context context, Intent intent) {


        Bundle myBundle = intent.getExtras();
        SmsMessage[] messages = null;
        String strMessage = "";

        if (myBundle != null) {
            Object[] pdus = (Object[]) myBundle.get("pdus");

            messages = new SmsMessage[pdus.length];

            for (int i = 0; i < messages.length; i++) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    String format = myBundle.getString("format");
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                } else {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                sender = messages[i].getOriginatingAddress();
                long id = messages[i].getIndexOnIcc();
                strMessage = messages[i].getMessageBody();
                String LandingNumber = Utils.getFromPrefs(context, LANDING_NUMBER, "0");
                SyncUtils.TriggerRefresh();
                String Time = new SimpleDateFormat(" dd-MM-yyyy hh:mm aa").format(System.currentTimeMillis());

                if (!LandingNumber.equals("0")) {
                    MyApplication.getWritabledatabase().insertData(new SmsData(LandingNumber, sender, strMessage, Time));
                    if (mListener != null) {
                        mListener.messageSent(Utils.getFromPrefs(context, SMS_COUNT, "0"), MyApplication.getWritabledatabase().getNoofRows() + "");


                    }
                } else
                    Log.d("Voley", "Number not Assigend.");


                //abortBroadcast();
                // Utils.deleteSMS(context,strMessage,sender);
                //Utils.deleteSms(context,id);

                break;

            }


        }
    }


    private void NotifyMeNotification(Context ctx, String sender, String message) {

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(sender)
                .setAutoCancel(false)
                .setSound(defaultSoundUri)
                .setContentText(message);
        // .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, HomeActivity.class), 0));
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, mBuilder.build());
    }


    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }
}

