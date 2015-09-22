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
    private String selinuxValuePref;
    private int selinuxOnBootPref;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mSettings = getSharedPreferences("Selinux switch state", Context.MODE_PRIVATE);
        selinuxValuePref = mSettings.getString("selinux", "");
        selinuxOnBootPref = mSettings.getInt("onBoot", -1);

        if (selinuxOnBootPref == 1) {
            if (selinuxValuePref.equals("true")) {
                Shell.SU.run("setenforce 1");
                Toast.makeText(this, "Selinux Enforcing", Toast.LENGTH_LONG).show();
            } else if (selinuxValuePref.equals("false")) {
                Shell.SU.run("setenforce 0");
                Toast.makeText(this, "Selinux Permissive", Toast.LENGTH_LONG).show();
            }
        }
    }
}
