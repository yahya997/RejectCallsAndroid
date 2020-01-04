package com.yahyacoder0097.rejectcallsandroid;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_READ_PHONE_STATE = 0;
    private static final int PERMISSION_REQUEST_CODE = 1;

    private TextView txtStartTime,txtEndTime;
    private TimePickerDialog timePickerDialog;
    private Switch switch1;
    private SharedPreferences sharedPreferences;
    private Button btnSave;
    private EditText editMessage;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init
        txtStartTime=(TextView)findViewById(R.id.txtStartTime);
        txtEndTime=(TextView)findViewById(R.id.txtEndTime);
        switch1 = (Switch)findViewById(R.id.switch1);
        btnSave=(Button)findViewById(R.id.btnSave);
        editMessage=(EditText)findViewById(R.id.editMessage);
        checkBox=(CheckBox)findViewById(R.id.checkbox);

        //sharedPreferences
        sharedPreferences=getSharedPreferences("MyFile", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor=sharedPreferences.edit();


        //Switch
        switch1.setChecked(sharedPreferences.getBoolean("StateSwitch", false));
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    /*Common.switchState = "true";*/
                    editor.putBoolean("StateSwitch", true);
                    editor.commit();
                } else {
                    //Common.switchState = "false";
                    editor.putBoolean("StateSwitch", false);
                    editor.commit();
                }
            }
        });



        //choose start time
        txtStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        /*startHour = hourOfDay;
                        startMin=minute;*/
                        editor.putString("startTime", String.format("%02d:%02d", hourOfDay, minute));
                        //editor.putString("startMin", String.valueOf(minute));
                        editor.commit();
                        txtStartTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, 0, 0, true);
                timePickerDialog.show();
            }//on click
        });//button

        //choose end time
        txtEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                       /* endHour=hourOfDay;
                        endMin=minute;*/
                        editor.putString("endTime", String.format("%02d:%02d", hourOfDay, minute));
                       // editor.putString("endMin", String.valueOf(minute));
                        editor.commit();
                        txtEndTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, 0, 0, true);
                timePickerDialog.show();
            }//end on click
        });//end button


        //Check Box
        checkBox.setChecked(sharedPreferences.getBoolean("StateCheckBox", false));
       checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if (isChecked) {
                   editor.putBoolean("StateCheckBox", true);
                   editor.commit();
               } else {
                   editor.putBoolean("StateCheckBox", false);
                   editor.commit();
               }
           }
       });

        //Button Save
        btnSave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                editor.putString("editMessage",editMessage.getText().toString());
                editor.commit();
                Toast.makeText(MainActivity.this, "Saved !!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //display endTime in TextView
        String sH=sharedPreferences.getString("startTime","");
        if (sH !=null) {
            txtStartTime.setText(sharedPreferences.getString("startTime", ""));
            txtEndTime.setText(sharedPreferences.getString("endTime", "") );
            editMessage.setText(sharedPreferences.getString("editMessage",""));
        }
        requestPermissionCall();

    }

    public void requestPermissionCall(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED ){
                String[] permissions = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE};
                requestPermissions(permissions, PERMISSION_REQUEST_READ_PHONE_STATE);
            }
        }
    }

    public void requestPermissionSms(){

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.SEND_SMS};
                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_READ_PHONE_STATE : {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted: " + PERMISSION_REQUEST_READ_PHONE_STATE, Toast.LENGTH_SHORT).show();
                    requestPermissionSms();
                } else {
                    Toast.makeText(this, "Permission NOT granted: " + PERMISSION_REQUEST_READ_PHONE_STATE, Toast.LENGTH_SHORT).show();
                }

                return;
            }
        }
    }
}
 /*LocalTime target = LocalTime.parse( "02:15" );
                Boolean targetInZone = (
                        target.isAfter(LocalTime.parse( sharedPreferences.getString("startTime","") ))
                                &&
                                target.isBefore(LocalTime.parse( sharedPreferences.getString("endTime","")))
                );
                Toast.makeText(MainActivity.this, targetInZone+"", Toast.LENGTH_SHORT).show();*/