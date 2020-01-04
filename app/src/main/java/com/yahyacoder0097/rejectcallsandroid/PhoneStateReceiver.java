package com.yahyacoder0097.rejectcallsandroid;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import java.lang.reflect.Method;
import java.time.LocalTime;
import java.util.Calendar;


public class PhoneStateReceiver extends BroadcastReceiver {

    private Calendar calendar;
    private SharedPreferences sharedPreferences;
    private String currentTime;
    private boolean targetInZone;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(final Context context, final Intent intent) {


        calendar = Calendar.getInstance();


        sharedPreferences=context.getSharedPreferences("MyFile",Context.MODE_PRIVATE);
        boolean StateSwitch = sharedPreferences.getBoolean("StateSwitch",false);
        boolean StateCheckBox = sharedPreferences.getBoolean("StateCheckBox",false);


        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMin =calendar.get(Calendar.MINUTE);

        currentTime=String.format("%02d:%02d", currentHour, currentMin);
        Log.d("currentTime",currentTime);

        LocalTime target = LocalTime.parse(currentTime);
        targetInZone = (
                target.isAfter(LocalTime.parse( sharedPreferences.getString("startTime","") ))
                        &&
                        target.isBefore(LocalTime.parse( sharedPreferences.getString("endTime","")))
        );


        if (intent.getAction().equals("android.intent.action.PHONE_STATE")){
            final String numberCall = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            final String endCall =intent.getStringExtra(TelephonyManager.EXTRA_STATE);

           // Log.d("switchState",silent+"");
//reject call if number is matched to our blocking number
            if(numberCall != null  && StateSwitch==true && targetInZone == true) {
                Log.d("callState", endCall);
                disconnectPhoneItelephony(context);
                if (endCall.equals("IDLE") && StateCheckBox== true) {
                String messageToSend = sharedPreferences.getString("editMessage","") +"\n"+
                        "You can call at this time ->" + sharedPreferences.getString("endTime","") ;
                SmsManager.getDefault().sendTextMessage(numberCall, null, messageToSend, null,null);
                    //Toast.makeText(context, ""+numberCall, Toast.LENGTH_SHORT).show();
                }
            }
        }


    }

    // Keep this method as it is
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void disconnectPhoneItelephony(Context context) {
        try {

            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";
            Class<?> telephonyClass;
            Class<?> telephonyStubClass;
            Class<?> serviceManagerClass;
            Class<?> serviceManagerNativeClass;
            Method telephonyEndCall;
            Object telephonyObject;
            Object serviceManagerObject;
            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
            Method getService = // getDefaults[29];
                    serviceManagerClass.getMethod("getService", String.class);
            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");
            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
            telephonyObject = serviceMethod.invoke(null, retbinder);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            telephonyEndCall.invoke(telephonyObject);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}