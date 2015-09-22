package com.lordclockan.aicpwidgets;

import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;

public class BootCompletedIntentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, BootServiceCompleted.class);
        context.startService(serviceIntent);
    }
}
