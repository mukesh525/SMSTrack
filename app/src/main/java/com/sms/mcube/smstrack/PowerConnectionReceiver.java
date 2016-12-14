package com.sms.mcube.smstrack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

/**
 * Created by gousebabjan on 7/12/16.
 */

public class PowerConnectionReceiver extends BroadcastReceiver {
    private static BatteryCallback batteryCallback;

    @Override
    public void onReceive(Context context, Intent intent) {

        final Intent mIntent = context.getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        int status = mIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING;
        boolean isCharged=status == BatteryManager.BATTERY_STATUS_CHARGING &&
                status == BatteryManager.BATTERY_STATUS_FULL;

        int chargePlug = mIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        int level = mIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = mIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int batteryPct = (int) (level / (float)scale *100);

       if (batteryCallback != null) {
            batteryCallback.batteryInfo(isCharging, isCharged, batteryPct);
        }
    }


    public static void bindBatteryCallback(BatteryCallback batteryCallback1) {
        batteryCallback = batteryCallback1;
    }



public interface BatteryCallback {

    public void batteryInfo(boolean isCharging, boolean status,int percent);

}
}