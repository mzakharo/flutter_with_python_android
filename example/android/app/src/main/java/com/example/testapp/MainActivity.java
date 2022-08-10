package com.example.testapp;

import io.flutter.embedding.android.FlutterActivity;

import android.os.Bundle; 
import  org.domain.myapp.ServiceSrv; 

public class MainActivity extends FlutterActivity {
    @Override 
    protected void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        ServiceSrv.prepare(this); 
        ServiceSrv.start(this, ""); 
    } 

    //needed if service is not sticky
    @Override
    protected void onResume () {
        ServiceSrv.start(this, "");
        super.onResume();
    }
}
