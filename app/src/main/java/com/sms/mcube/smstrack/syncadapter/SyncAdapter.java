/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sms.mcube.smstrack.syncadapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jaredrummler.android.device.DeviceName;
import com.sms.mcube.smstrack.MyApplication;
import com.sms.mcube.smstrack.PowerConnectionReceiver;
import com.sms.mcube.smstrack.SmsData;
import com.sms.mcube.smstrack.SmsListener;
import com.sms.mcube.smstrack.TAG;
import com.sms.mcube.smstrack.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SyncAdapter extends AbstractThreadedSyncAdapter implements TAG, PowerConnectionReceiver.BatteryCallback {


    private static SmsListener mListener;
    private Context mContext;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext=context;


    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.d("Voley", "Beginning network synchronization");
        ArrayList<SmsData> dataList= MyApplication.getWritabledatabase().getData();
        for(SmsData smsData:dataList){
             sendSMS(smsData);
           // Log.d("Voley",smsData.getFrom()+"  "+smsData.getText()+"  "+dataList.size());



        }
        String deviceName = DeviceName.getDeviceName();
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceIMEI = telephonyManager.getDeviceId();
        String LandingNumber = Utils.getFromPrefs(mContext, LANDING_NUMBER, "0");

        PowerConnectionReceiver.bindBatteryCallback(this);



    }

    private  void sendSMS(final SmsData smsData) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest strRequest = new StringRequest(Request.Method.POST,SMS_SENT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Voley", response.toString());
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.has(MESSAGE) && obj.getString(MESSAGE).equals("Assigned")) {
                                int count = Integer.parseInt(Utils.getFromPrefs(mContext, SMS_COUNT, "0"));
                                Utils.saveToPrefs(mContext, SMS_COUNT, String.valueOf(count + 1));
                                Log.d("Voley", "Updated Successfully");
                                MyApplication.getWritabledatabase().delete(smsData.getId());



                            }
                            if(obj.has(MESSAGE) && obj.getString(MESSAGE).equals("Landing Number not found")){
                                MyApplication.getWritabledatabase().delete(smsData.getId());

                            }
                            if (mListener != null) {
                                Log.d("Voley count",Utils.getFromPrefs(mContext,SMS_COUNT,"0")+" "+ MyApplication.getWritabledatabase().getNoofRows()+"");
                                mListener.messageSent(Utils.getFromPrefs(mContext,SMS_COUNT,"0"), MyApplication.getWritabledatabase().getNoofRows()+"");
                            }

                        } catch (JSONException e) {
                            Log.d("Voley", "Invalid Response  " + response.toString());
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Voley", error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
//                String Number = Utils.getFromPrefs(mContext, LANDING_NUMBER, "000000");
//                String Time = new SimpleDateFormat(" dd-MM-yyyy hh:mm aa").format(System.currentTimeMillis());
                params.put(TO, smsData.getTo());
                params.put(FROM, smsData.getFrom());
                params.put(TEXT, smsData.getText());
                params.put(TIME, smsData.getTime());
                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(strRequest);

    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }

    @Override
    public void batteryInfo(boolean isCharging, boolean status, int percent) {

        Log.d("Voley", "Charging " + isCharging + " " + "Status " + status + "Percent " + percent);

    }
}




