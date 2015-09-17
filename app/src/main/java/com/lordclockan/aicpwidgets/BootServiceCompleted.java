package com.lordclockan.aicpwidgets;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.Toast;

import eu.chainfire.libsuperuser.Shell;

public class BootServiceCompleted extends Service {

    SharedPreferences mSettings;
    private boolean suAvailable;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mSettings = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        suAvailable = Shell.SU.available();

        if (isEnforcingPref()) {
            Shell.SU.run("setenforce 1");
            Toast.makeText(this, "Selinux Enforcing", Toast.LENGTH_LONG).show();
        } else {
            Shell.SU.run("setenforce 0");
            Toast.makeText(this, "Selinux Permissive", Toast.LENGTH_LONG).show();
        }
    }

    private class StartUp extends AsyncTask<String,Void,Void> {

        private Context context = null;
        public StartUp setContext(Context context) {
            this.context = context;
            return this;
        }

        @Override
        protected Void doInBackground(String... params) {
            switch (params[0]){
                case "selinuxOn" : Shell.SU.run("setenforce 1");break;
                case "selinuxOff" : Shell.SU.run("setenforce 0");break;
            }

            return null;
        }

    }

    public boolean isEnforcingPref(){
        boolean state = false;
        if (mSettings.getBoolean("selinux", true)){
            state = true;
        }
        return state;
    }
}
