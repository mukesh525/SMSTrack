package com.sms.mcube.smstrack;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Telephony;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sms.mcube.smstrack.syncadapter.SyncAdapter;
import com.sms.mcube.smstrack.syncadapter.SyncUtils;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements TAG, View.OnClickListener, SmsListener, PowerConnectionReceiver.BatteryCallback {

    Button saveNumber, changNumber;
    EditText et_number;
    TextView showNumber, tv_SMScount, tv_offline;
    LinearLayout saveLayout, changeLayout;
    String phoneNumber;
    private MainActivity mActivity;
    BatteryManager bm;
    int battery_level;
    private CircleImageView imageLogo;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        saveNumber = (Button) findViewById(R.id.btnSave);
        changNumber = (Button) findViewById(R.id.btnchange);
        et_number = (EditText) findViewById(R.id.etnumber);
        showNumber = (TextView) findViewById(R.id.tvnumber);
        tv_SMScount = (TextView) findViewById(R.id.tv_Sent_count);
        tv_offline = (TextView) findViewById(R.id.tv_offline_Count);
        saveLayout = (LinearLayout) findViewById(R.id.llsaveNumber);
        changeLayout = (LinearLayout) findViewById(R.id.llchangeNumber);
        imageLogo = (CircleImageView) findViewById(R.id.logo);
        imageLogo.setOnClickListener(this);
        saveNumber.setOnClickListener(this);
        tv_SMScount.setOnClickListener(this);
        changNumber.setOnClickListener(this);
        phoneNumber = Utils.getFromPrefs(this, LANDING_NUMBER, "0");

        TranslateAnimation animation = new TranslateAnimation(-150.0f, 150.0f,
                0.0f, 0.0f);          //  new TranslateAnimation(xFrom,xTo, yFrom,yTo)
        animation.setDuration(5000);  // animation duration
        animation.setRepeatCount(Animation.INFINITE);  // animation repeat count
        animation.setRepeatMode(2);   // repeat animation (left to right, right to left )
        //animation.setFillAfter(true);
        findViewById(R.id.tv_mcube).startAnimation(animation);

        Animation top = AnimationUtils.loadAnimation(this, R.anim.enter_top);
        Animation left = AnimationUtils.loadAnimation(this, R.anim.enter_left);
        Animation right = AnimationUtils.loadAnimation(this, R.anim.enter_right);
        findViewById(R.id.logo).startAnimation(top);
        findViewById(R.id.tv_offline_Count).startAnimation(right);
        findViewById(R.id.tv_Sent_count).startAnimation(right);
        Animation bottomAnim = AnimationUtils.loadAnimation(this, R.anim.enter_bottom);
        findViewById(R.id.offline_relative_layout).startAnimation(bottomAnim);
        findViewById(R.id.rl_sentLayout).startAnimation(bottomAnim);
        bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
        battery_level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        if (!phoneNumber.equals("0")) {
            if (changeLayout.getVisibility() == View.GONE) {
                changeLayout.setVisibility(View.VISIBLE);
            }
            if (saveLayout.getVisibility() == View.VISIBLE) {
                saveLayout.setVisibility(View.GONE);
            }
            showNumber.setText(phoneNumber);
        } else {
            et_number.setText("");
            if (changeLayout.getVisibility() == View.VISIBLE) {
                changeLayout.setVisibility(View.GONE);
            }
            if (saveLayout.getVisibility() == View.GONE) {
                saveLayout.setVisibility(View.VISIBLE);
            }
        }

        IncomingSms.bindListener(this);
        IncomingSms.bindListener(this);
        SyncAdapter.bindListener(this);
        PowerConnectionReceiver.bindBatteryCallback(this);
    }


    public void UpdateUi(String sentCount, String OfflineCount) {
        tv_SMScount.setText(sentCount);
        tv_offline.setText(OfflineCount);

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        tv_SMScount.setText(Utils.getFromPrefs(this, SMS_COUNT, "00"));
        tv_offline.setText(MyApplication.getWritabledatabase().getNoofRows() + "");


        final String myPackageName = getPackageName();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            if (!Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("This app is not set as your default messaging app. Do you want to set it as default?")
                        .setCancelable(false)
                        .setTitle("SMS Track")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @TargetApi(19)
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
                                startActivity(intent);
                            }
                        });
                builder.show();
            }
        }

    }







    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnchange:
                if (changeLayout.getVisibility() == View.VISIBLE) {
                    changeLayout.setVisibility(View.GONE);
                }
                if (saveLayout.getVisibility() == View.GONE) {
                    saveLayout.setVisibility(View.VISIBLE);
                }
                if (!phoneNumber.equals("0"))
                    et_number.setText(phoneNumber);

                break;
            case R.id.btnSave:
                hideKeyboard();
                if (validateNumber()) {
                    Utils.saveToPrefs(this, LANDING_NUMBER, et_number.getText().toString() != null ? et_number.getText().toString() : "");
                    phoneNumber = Utils.getFromPrefs(this, LANDING_NUMBER, "0");
                    showNumber.setText(phoneNumber);
                    if (saveLayout.getVisibility() == View.VISIBLE) {
                        saveLayout.setVisibility(View.GONE);
                    }
                    if (changeLayout.getVisibility() == View.GONE) {
                        changeLayout.setVisibility(View.VISIBLE);
                    }

                }

                break;

            case R.id.tv_Sent_count:
                tv_SMScount.setText(Utils.getFromPrefs(this, SMS_COUNT, "00"));
                break;

            case R.id.logo:
                SyncUtils.TriggerRefresh();
                break;
        }


    }



    public boolean validateNumber() {
        boolean valid = true;
        String phoneNumber = et_number.getText().toString().trim();
        Drawable drawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.error);
        drawable.setBounds(new Rect(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()));
        if (phoneNumber.isEmpty() || (phoneNumber.length() != 10)) {
            et_number.setError("Enter Phone Number", drawable);
            et_number.requestFocus();
            valid = false;
        } else {
            et_number.setError(null);
        }

        return valid;
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) {
            View view = this.getCurrentFocus();
            if (view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } else {
            // writeToLog("Software Keyboard was not shown");
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void messageSent(final String sentCount, final String OfflineCount) {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                battery_level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                UpdateUi(sentCount, OfflineCount);
                //   Log.d("Voley", "Sent from syn adapter Battery " +battery_level);
            }
        });


    }

    @Override
    public void batteryInfo(boolean isCharging, boolean status, int percent) {

        String chargge = "isCharging: " + isCharging + "\n" + "\n" +
                "isCharged:" + status + "\n" + "\n" +
                "Percent: " + percent + "\n";

        // Toast.makeText(MainActivity.this,"isCharging " +isCharging+" "+"isCharged "+ status+" Percent "+percent,Toast.LENGTH_LONG).show();
        Toast.makeText(MainActivity.this, chargge, Toast.LENGTH_LONG).show();


    }


}
