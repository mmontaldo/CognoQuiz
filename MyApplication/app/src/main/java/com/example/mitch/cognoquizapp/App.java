package com.example.mitch.cognoquizapp;

/**
 * Created by Mitch on 7/22/2015.
 */
import android.app.Application;
import com.parse.Parse;
import com.parse.ParseObject;

public class App extends Application {

    @Override public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "YtpR6J37w9tmZwGRa04GggaOdSazg5LHfvr60MRs", "rsQq07muz6VnigFKnA2NRJtLqP8E8uWLj0nsenMP");
    }
}