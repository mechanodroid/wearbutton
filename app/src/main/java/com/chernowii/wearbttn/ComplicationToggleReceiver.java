package com.chernowii.wearbttn;

/**
 * Created by konrad on 6/23/17.
 */

import android.app.Instrumentation;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.wearable.complications.ComplicationData;
import android.support.wearable.complications.ProviderUpdateRequester;
import android.util.Log;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/** Receives intents on tap and causes complication states to be toggled and updated. */
public class ComplicationToggleReceiver extends BroadcastReceiver {
    private static final String EXTRA_PROVIDER_COMPONENT = "providerComponent";
    private static final String EXTRA_COMPLICATION_ID = "complicationId";

    static final String PREFERENCES_NAME = "ComplicationTestSuite";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        ComponentName provider = extras.getParcelable(EXTRA_PROVIDER_COMPONENT);
        int complicationId = extras.getInt(EXTRA_COMPLICATION_ID);
        String preferenceKey = getPreferenceKey(provider, complicationId);
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, 0);
        int value = pref.getInt(preferenceKey, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(preferenceKey, value + 1); // Increase value by 1
        editor.apply();
        String name = pref.getString("COMP_NAME", "none");

        // Request an update for the complication that has just been toggled.
        ProviderUpdateRequester requester = new ProviderUpdateRequester(context, provider);
        requester.requestUpdate(complicationId);
        Log.d("COMPID", provider.flattenToString());
        Log.d("TEST2", name);
        //Toast.makeText(context, "Test", Toast.LENGTH_SHORT).show();
        /*
        This should open the drawer because 3 is HOME keyevent, tried using adb shell and it opens the wear 2.0 drawer.
        Using adb shell no root needed.
        Can also try using start launcher activity or emulate power button press
         */
        /*
        //Root methods:
        try{
            Process su = Runtime.getRuntime().exec("/system/bin/sh -c ");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

            outputStream.writeBytes("input keyevent 3\n");
            outputStream.flush();

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            su.waitFor();
        }catch(IOException | InterruptedException e){
            try {
                throw new Exception(e);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        //App drawer id:
        06-24 15:58:11.829   480   542 I ActivityManager: START u0 {act=android.intent.action.MAIN cat=[android.intent.category.HOME] flg=0x10200000 cmp=com.google.android.wearable.app/com.google.android.clockwork.home2.activity.HomeActivity2 (has extras)} from uid 1000 on display 0

        */
            //com.google.android.googlequicksearchbox/com.google.android.apps.gsa.binaries.clockwork.search.VoicePlateActivity
        Intent startGA = new Intent();
        startGA.setComponent(new ComponentName("uk.org.openseizuredetector", "uk.org.openseizuredetector.StartUpActivity"));
        context.startActivity(startGA);


    }

    /**
     * Returns a pending intent, suitable for use as a tap intent, that causes a complication to be
     * toggled and updated.
     */
    static PendingIntent getToggleIntent(
            Context context, ComponentName provider, int complicationId) {
        Intent intent = new Intent(context, ComplicationToggleReceiver.class);
        intent.putExtra(EXTRA_PROVIDER_COMPONENT, provider);
        intent.putExtra(EXTRA_COMPLICATION_ID, complicationId);

        // Pass complicationId as the requestCode to ensure that different complications get
        // different intents.
        return PendingIntent.getBroadcast(
                context, complicationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Returns the key for the shared preference used to hold the current state of a given
     * complication.
     */
    static String getPreferenceKey(ComponentName provider, int complicationId) {
        return provider.getClassName() + complicationId;
    }
}