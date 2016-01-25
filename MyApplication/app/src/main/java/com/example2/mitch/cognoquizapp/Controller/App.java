package com.example2.mitch.cognoquizapp.Controller;

/**
 * Created by Mitch on 7/22/2015.
 */
import android.app.Application;
import com.parse.Parse;

public class App extends Application {

    @Override public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "YtpR6J37w9tmZwGRa04GggaOdSazg5LHfvr60MRs", "rsQq07muz6VnigFKnA2NRJtLqP8E8uWLj0nsenMP");
    }
}