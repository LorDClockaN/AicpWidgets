package com.lordclockan.aicpwidgets;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Looper;
import android.os.MessageQueue;
import android.widget.Toast;

import eu.chainfire.libsuperuser.Shell;

public class BootServiceCompleted extends Service {

    private boolean isRunning;
    private Context context;
    private Thread backgroundThread;

    SharedPreferences mSettings;
    private String selinuxValuePref;
    private int selinuxOnBootPref;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        this.context = this;
        this.isRunning = false;
        this.backgroundThread = new Thread(myBoot);
    }

    private Runnable myBoot = new Runnable() {
        @Override
        public void run() {
            mSettings = getSharedPreferences("Selinux switch state", Context.MODE_PRIVATE);
            selinuxValuePref = mSettings.getString("selinux", "");
            selinuxOnBootPref = mSettings.getInt("onBoot", -1);
            if (selinuxOnBootPref == 1) {
                if (selinuxValuePref.equals("true")) {
                    Shell.SU.run("setenforce 1");
                    // System.out.println("SERVICE IS RUNNING AND ENFORCING");
                    // Toast.makeText(this, "Selinux Enforcing", Toast.LENGTH_LONG).show();
                    showToastInThread(context, "Selinux Enforcing");
                } else if (selinuxValuePref.equals("false")) {
                    Shell.SU.run("setenforce 0");
                    // System.out.println("SERVICE IS RUNNING AND ENFORCING");
                    showToastInThread(context, "Selinux Permissive");
                    // Toast.makeText(this, "Selinux Permissive", Toast.LENGTH_LONG).show();
                }
            }
            stopSelf();
        }
    };

    @Override
    public void onDestroy() {
        this.isRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!this.isRunning) {
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return START_STICKY;
    }

    public void showToastInThread(final Context context,final String str){
        Looper.prepare();
        MessageQueue queue = Looper.myQueue();
        queue.addIdleHandler(new MessageQueue.IdleHandler() {
            int mReqCount = 0;

            @Override
            public boolean queueIdle() {
                if (++mReqCount == 2) {
                    Looper.myLooper().quit();
                    return false;
                } else
                    return true;
            }
        });
        Toast.makeText(context, str,Toast.LENGTH_LONG).show();
        Looper.loop();
    }
}
