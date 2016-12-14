package com.sms.mcube.smstrack;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by mukesh on 6/7/15.
 */
public class Utils{

    public static boolean onlineStatus(Context activityContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activityContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        } else {
            if (connectivityManager != null) {
                //noinspection deprecation
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {

                            return true;
                        }
                    }
                }
            }
        }
        //  Toast.makeText(mContext,mContext.getString(R.string.please_connect_to_internet),Toast.LENGTH_SHORT).show();
        return false;
    }


    public  static void deleteSMS(Context context,String message,String number) {
        try {
            Uri uriSms = Uri.parse("content://sms/inbox");
            Cursor c = context.getContentResolver().query(
                    uriSms,
                    new String[] { "_id", "thread_id", "address", "person",
                            "date", "body" }, "read=0", null, null);

            if (c != null && c.moveToFirst()) {
                do {
                    long id = c.getLong(0);
                    long threadId = c.getLong(1);
                    String address = c.getString(2);
                    String body = c.getString(5);
                    String date = c.getString(3);
                    Log.d("Voley",
                            "0>" + c.getString(0) + "1>" + c.getString(1)
                                    + "2>" + c.getString(2) + "<-1>"
                                    + c.getString(3) + "4>" + c.getString(4)
                                    + "5>" + c.getString(5));
                    Log.d("Voley", "date" + c.getString(0));

                    if (message.equals(body) && address.equals(number)) {
                        // mLogger.logInfo("Deleting SMS with id: " + threadId);
                        context.getContentResolver().delete(
                                Uri.parse("content://sms/" + id), "date=?",
                                new String[] { c.getString(4) });
//                        context.getContentResolver().delete(
//                                Uri.parse("content://sms/" + id), null,
//                                null);

                        Log.d("Voley", " Inbox Delete success.........");
                    }
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            Log.d("log>>>", e.toString());
        }
    }


    public static void deleteSms(Context context, long messageId) {
        try {
            Uri uriSms = Uri.parse("content://sms/inbox");
            Cursor c = context.getContentResolver().query(uriSms, new String[]{"_id"}, null, null, null);

            if (c != null && c.moveToFirst()) {
                do {
                    long id = c.getLong(0);

                    if (id == messageId) {
                        context.getContentResolver().delete(
                                Uri.parse("content://sms/" + id), null, null);
                        Log.e("Message:", "Message is Deleted successfully");
                    }

                } while (c.moveToNext());
            }

            if (c != null) {
                c.close();
            }
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

    }





    public static void saveToPrefs(Context context, String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void saveToPrefs(Context context, String key, boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }


    public static String getFromPrefs(Context context, String key, String defaultValue) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            return sharedPrefs.getString(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }


    public static void sendSms(String number, Activity mActivity) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.putExtra("sms_body", " ");
        sendIntent.putExtra("address", number);
        sendIntent.setType("vnd.android-dir/mms-sms");
        mActivity.startActivity(sendIntent);

    }

    public static void sendAnEmail(String recipient, Activity mActivity) {
        try {
            final Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            if (recipient != null)
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient});
//	        if (subject != null) {   emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);}
//	        if (message != null)    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
            mActivity.startActivity(emailIntent);

        } catch (ActivityNotFoundException e) {
            // cannot send email for some reason
        }

    }



}
