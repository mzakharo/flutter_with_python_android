# Flutter with Python on Android

 Instructions on how to generate one APK, containing Flutter UI as a frontend, and Python Service as a backend.

## Install buildozer:
 https://buildozer.readthedocs.io/en/latest/installation.html

## Generate empty buildozer config

```
cd libapp
buildozer init
```

Edit a sample python file called srv.py

```
import time
while True:
    time.sleep(1)
    print('hello world')
```

Edit `buildozer.spec`

```
package.domain = org.domain

#select service bootstrap 
p4a.bootstrap = service_library 

# ask p4a to output aar instead of apk 
android.release_artifact = aar

#foreground so that OS does not kill us 
# sticky : so that OS restarts us on exit/crash 
services = Srv:srv.py:foreground:sticky  

#pick an ABI -> only one is supported  
android.archs = arm64-v8a 
```

## Compile the aar 

`buildozer  android release`

##  Setup flutter project
```
cd ..\
flutter create -i objc -a java testapp
```


## Edit testapp/android/app/build.gradle 

 ```

# change minSdkVersion to match p4a aar library. 

 minSdkVersion 21 

# add aar as a dependency 

dependencies { 
    implementation files('../../../libapp/bin/myapp-0.1-arm64-v8a-release.aar') 
} 

#filter out ABIs that won't work – add to defaultConfig {} 
        ndk { abiFilters "arm64-v8a" } 

 ```

## Edit `testapp/android/app/src/main/java/com/example/testapp/MainActivity.java` 

 ```
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
} 
```
 

## Edit testapp/android/app/src/main/AndroidManifest.xml add  to <manifest> 

    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" /> 

 


## Build and run the flutter app 

`flutter run`
  
At this point, you should see 'hello world' printed if you `adb locat` , while the flutter demo app running.  Communicating between the two is an excercse to the user,  you can use raw sockets, ZeroMQ, Nanomsg-NG libraries to name a few.

 
