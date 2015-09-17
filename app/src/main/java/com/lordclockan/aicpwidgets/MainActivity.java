package com.lordclockan.aicpwidgets;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import eu.chainfire.libsuperuser.Shell;

public class MainActivity extends Activity {

    public boolean suAvailable = false;
    SharedPreferences mSettings;
    Editor toEdit;
    private String selinuxPref;

    Switch selinux;
    TextView selinuxSummary;

    public boolean selinuxEnforcingState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        suAvailable = Shell.SU.available();

        selinux = (Switch) findViewById(R.id.swSelinux);
        selinuxSummary = (TextView) findViewById(R.id.tvSelinuxSummary);

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

    }

    public void sharedPrefernces() {
        mSettings = getSharedPreferences("Selinux switch state", Context.MODE_PRIVATE);
        toEdit = mSettings.edit();
        toEdit.putString("selinux", selinuxPref);
        toEdit.commit();
    }

    private class StartUp extends AsyncTask<String,Void,Void> {

        private Context context = null;
                public StartUp setContext(Context context) {
            this.context = context;
            return this;
        }

        @Override
        protected Void doInBackground(String... params) {
            if (suAvailable) {
                switch (params[0]){
                    case "selinuxOn":
                        Shell.SU.run("setenforce 1");
                        break;
                    case "selinuxOff":
                        Shell.SU.run("setenforce 0");
                        break;
                }
            }
            else{
                Toast.makeText(getApplicationContext(),"Phone not Rooted",Toast.LENGTH_SHORT).show();
            }

            return null;
        }

    }
}