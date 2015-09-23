package com.lordclockan.aicpwidgets;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import eu.chainfire.libsuperuser.Shell;

public class MainActivity extends Activity {

    // public boolean suAvailable = false;
    private Context context;
    SharedPreferences mSettings;
    Editor toEdit;
    private String selinuxPref;
    private String selinuxOnBootPref;

    Switch selinux;
    TextView selinuxSummary;
    CheckBox selinuxSetOnBoot;

    public boolean selinuxEnforcingState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        // suAvailable = Shell.SU.available();

        selinux = (Switch) findViewById(R.id.swSelinux);
        selinuxSummary = (TextView) findViewById(R.id.tvSelinuxSummary);
        selinuxSetOnBoot = (CheckBox) findViewById(R.id.chkSelinuxOnBoot);

        mSettings = getSharedPreferences("Selinux switch state", Context.MODE_PRIVATE);
        selinuxOnBootPref = mSettings.getString("onBoot", "");

        selinuxEnforcingState = Shell.SU.isSELinuxEnforcing();
        if (selinuxEnforcingState) {
            selinux.setChecked(true);
            selinuxSummary.setText(getString(R.string.selinux_switch_on));
        } else {
            selinux.setChecked(false);
            selinuxSummary.setText(getString(R.string.selinux_switch_off));
        }
        selinux.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    (new StartUp()).setContext(buttonView.getContext()).execute("selinuxOn");
                    selinuxSummary.setText(getString(R.string.selinux_switch_on));
                    selinuxPref = "true";
                    sharedPrefernces();
                } else {
                    (new StartUp()).setContext(buttonView.getContext()).execute("selinuxOff");
                    selinuxSummary.setText(getString(R.string.selinux_switch_off));
                    selinuxPref = "false";
                    sharedPrefernces();
                }
            }
        });

        updateSetOnBootCheckBox();
        addListenerOnChkBoot();

        this.context = this;
        Intent alarm = new Intent(this.context, BootCompletedIntentReceiver.class);
        boolean alarmRunning = (PendingIntent.getBroadcast(this.context, 0, alarm, PendingIntent.FLAG_NO_CREATE) != null);
        if(!alarmRunning) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, alarm, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 1800000, pendingIntent);
        }

    }

    public void sharedPrefernces() {
        toEdit = mSettings.edit();
        toEdit.putString("selinux", selinuxPref);
        toEdit.putString("onBoot", selinuxOnBootPref);
        toEdit.apply();
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
                case "selinuxOn":
                    Shell.SU.run("setenforce 1");
                    break;
                case "selinuxOff":
                    Shell.SU.run("setenforce 0");
                    break;
            }

            return null;
        }

    }

    public void addListenerOnChkBoot() {
        selinuxSetOnBoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selinuxSetOnBoot.isChecked()) {
                    selinuxOnBootPref = "true";
                    sharedPrefernces();
                    updateSetOnBootCheckBox();
                } else {
                    selinuxOnBootPref = "false";
                    sharedPrefernces();
                    updateSetOnBootCheckBox();
                }
            }
        });

    }

    private void updateSetOnBootCheckBox() {
        if (selinuxOnBootPref.equals("true")) {
            selinuxSetOnBoot.setChecked(true);
        } else if (selinuxOnBootPref.equals("false")) {
            selinuxSetOnBoot.setChecked(false);
        }
    }
}