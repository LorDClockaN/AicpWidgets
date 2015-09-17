package com.lordclockan.aicpwidgets;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import eu.chainfire.libsuperuser.Shell;

public class MainActivity extends Activity {

    public boolean suAvailable = false;

   /* Button reboot, recv, shut, sysui;*/
    Switch selinux;
    TextView selinuxSummary;

    public boolean selinuxEnforcingState;
    public String selinuxPositive = "mount -o rw,remount /system"
            + " && echo '#!/system/bin/sh' > /system/etc/init.d/03setSelinux"
            + " && echo 'setenforce 1' >> /system/etc/init.d/03setSelinux"
            + " && mount -o ro,remount /system";
    public String selinuxNegative = "mount -o rw,remount /system"
            + " && echo '#!/system/bin/sh' > /system/etc/init.d/03setSelinux"
            + " && echo 'setenforce 0' >> /system/etc/init.d/03setSelinux"
            + " && mount -o ro,remount /system";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        suAvailable = Shell.SU.available();

      /*  reboot = (Button) findViewById(R.id.btn_reb);
        recv = (Button) findViewById(R.id.btn_rec);
        shut = (Button) findViewById(R.id.shut);
        sysui = (Button) findViewById(R.id.SysUi);*/

        selinux = (Switch) findViewById(R.id.swSelinux);
        selinuxSummary = (TextView) findViewById(R.id.tvSelinuxSummary);

    /*    reboot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                (new StartUp()).setContext(v.getContext()).execute("reboot");


            }
        });
        recv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                (new StartUp()).setContext(v.getContext()).execute("recov");

            }
        });
        shut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                (new StartUp()).setContext(v.getContext()).execute("shutdown");

            }
        });
        sysui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                (new StartUp()).setContext(v.getContext()).execute("sysui");


            }
        });*/

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
                    (new StartUp()).setContext(buttonView.getContext()).execute("selinuxPositive");
                    selinuxSummary.setText(getString(R.string.selinux_switch_on));
                } else {
                    (new StartUp()).setContext(buttonView.getContext()).execute("selinuxOff");
                    (new StartUp()).setContext(buttonView.getContext()).execute("selinuxNegative");
                    selinuxSummary.setText(getString(R.string.selinux_switch_off));
                }
            }
        });

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
                // suResult = Shell.SU.run(new String[] {"cd data; ls"}); Shell.SU.run("reboot");
                switch (params[0]){
                   /* case "reboot"  : Shell.SU.run("reboot");break;
                    case "recov"   : Shell.SU.run("reboot recovery");break;
                    case "shutdown": Shell.SU.run("reboot -p");break;
                    //case "sysui"   : Shell.SU.run("am startservice -n com.android.systemui/.SystemUIService");break;
                    case "sysui"   : Shell.SU.run("pkill com.android.systemui");break;*/
                    case "selinuxOn" : Shell.SU.run("setenforce 1");break;
                    case "selinuxOff" : Shell.SU.run("setenforce 0");break;
                    case "selinuxPositive" : Shell.SU.run(selinuxPositive); break;
                    case "selinuxNegative" : Shell.SU.run(selinuxNegative); break;
                }
            }
            else{
                Toast.makeText(getApplicationContext(),"Phone not Rooted",Toast.LENGTH_SHORT).show();
            }

            return null;
        }

    }

}