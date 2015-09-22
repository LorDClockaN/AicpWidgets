package com.lordclockan.aicpwidgets;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.Toast;

import eu.chainfire.libsuperuser.Shell;

public class BootServiceCompleted extends Service {

    SharedPreferences mSettings;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mSettings = getSharedPreferences("Selinux switch state", Context.MODE_PRIVATE);
        String value = mSettings.getString("selinux", "");
        if (value.equals("true")) {
            Shell.SU.run("setenforce 1");
            Toast.makeText(this, "Selinux Enforcing", Toast.LENGTH_LONG).show();
        } else if (value.equals("false")) {
            Shell.SU.run("setenforce 0");
            Toast.makeText(this, "Selinux Permissive", Toast.LENGTH_LONG).show();
        }
    }
}
