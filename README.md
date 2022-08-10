# Flutter with Python on Android

 Instructions on how to generate one APK, containing Flutter UI as a frontend, and a Python Service as a backend.
 
 Using the [p4a](https://python-for-android.readthedocs.io/en/latest/) project for bundling python scripts and cross-compiling python libraries with native components (numpy/scipy/pandas/tflite-runtime, etc). 
 
NOTE: This tutorial requres at minimum [p4a version v2022.07.20](https://github.com/kivy/python-for-android/releases/tag/v2022.07.20)  and [buildozer version 1.4.0](https://github.com/kivy/buildozer/releases/tag/1.4.0)

## Example project

[Flutter + Python example](https://github.com/mzakharo/flutter_with_python_android/tree/main/example)

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

# select service bootstrap 
p4a.bootstrap = service_library 

# ask p4a to output aar instead of apk 
android.release_artifact = aar

# specify your runtime python package dependencies here (comma separated)
requirements = python3

# foreground : so that OS does not kill us (optional)
# sticky : so that OS restarts us on exit/crash  (optional)
# NOTE: sticky services are persistent and will not close when the main app closes
services = Srv:srv.py:foreground:sticky  

# pick ABI(s) - NOTE: listing more than needed here grows the final .apk size
android.archs = arm64-v8a, x86
```

## Compile the aar 

`buildozer  android release`

##  Setup a flutter project
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
    implementation files('../../../libapp/bin/myapp-0.1-arm64-v8a_x86-release.aar') 
} 

 ```

## Start services on app startup


 Here we assume you wish to start the service when your app starts and you are using a sticky foreground service.
 



edit `testapp/android/app/src/main/java/com/example/testapp/MainActivity.java` 

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
    
    //needed if service is not foreground/sticky
    @Override
    protected void onResume () {
        ServiceSrv.start(this, "");
        super.onResume();
    }
} 
```
Lastly, there is `ServiceSrv.stop(this)` API to stop the service manually

##  Add Foreground Service Permission

Optional if you selected `foreground` when specifying the service in buildozer.

Edit `testapp/android/app/src/main/AndroidManifest.xml`
write to `<manifest>`

   ` <uses-permission android:name="android.permission.FOREGROUND_SERVICE" /> `

 


## Build and run the flutter app 

`flutter run`
  
At this point, you should see 'hello world' printed if you `adb logcat` , while the flutter demo is running. Options for communicating between python and flutter include: 
 - flask server 
 - zeromq
 - nanomsg-ng
 - raw UDP/TCP sockets

Some useful projects:
 - Access Android Java API from Python: [pyjnius](https://pyjnius.readthedocs.io/en/stable/)
 - Cross platform API for common tasks: [plyer](https://github.com/kivy/plyer)

## Proguard rules for pyjnius

```
-keep public class org.kivy.android.** {
    *;
}
-keep public class org.jnius.** {
    *;
}
```

 
