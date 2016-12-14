package com.sms.mcube.smstrack;

/**
 * Created by gousebabjan on 28/11/16.
 */

public interface SmsListener {
    public void messageSent(String sentCount,String OfflineCount);
}