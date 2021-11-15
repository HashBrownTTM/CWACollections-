package com.example.cwacollections;

import android.app.Application;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;

//app class runs before your launcher activity
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    //static method to convert timestamp to a date format, ensuring that it does not have to be rewritten
    public static final String formatTimestamp(long timestamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp);
        //formats timestamp to dd/MM/yyyy
        String date = DateFormat.format("dd/MM/yyyy", cal).toString();

        return date;
    }
}

/* References
Book App Firebase | 06 Show Books Admin | Android Studio | Java. 2021. YouTube video, added by Atif Pervaiz.
[ONLINE]. Available at: https://youtu.be/vgWihyzAv-U. [Accessed 12 July 2021].

*/
