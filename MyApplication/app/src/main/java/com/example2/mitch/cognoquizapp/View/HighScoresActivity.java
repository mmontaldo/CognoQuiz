package com.example2.mitch.cognoquizapp.View;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example2.mitch.cognoquizapp.Controller.HighScoresCustomAdapter;
import com.example2.mitch.cognoquizapp.Model.HighScore;
import com.example2.mitch.cognoquizapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HighScoresActivity extends Activity {

    ListView list;
    HighScoresCustomAdapter adapter;
    public HighScoresActivity CustomListView = null;
    public ArrayList<HighScore> CustomListViewValuesArr = new ArrayList<HighScore>();
    public ArrayList<String> highScoreList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.high_scores_loading_screen);

        CustomListView = this;

        setListData();
    }


    //set data in arraylist
    public void setListData()
    {

        ParseUser currentUser = ParseUser.getCurrentUser();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("HighScore");
        query.whereEqualTo("user", currentUser);
        query.orderByDescending("score");
        query.addDescendingOrder("date");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> highScoreList, ParseException e) {
                if (e == null) {
                    setContentView(R.layout.activity_high_scores);
                    Log.d("High Scores", "Retrieved " + highScoreList.size() + " scores");
                    for(ParseObject po: highScoreList) {

                        String name = po.getString("name");
                        Date date = po.getDate("date");
                        int score = po.getInt("score");

                        HighScore h = new HighScore(date, name, score);
                        CustomListViewValuesArr.add(h);

                    }

                    if (CustomListViewValuesArr.size() > 0) {
                        CustomListViewValuesArr.get(0).setRank(1);
                    }
                    if (CustomListViewValuesArr.size() > 1) {
                        CustomListViewValuesArr.get(1).setRank(2);
                    }
                    if (CustomListViewValuesArr.size() > 2) {
                        CustomListViewValuesArr.get(2).setRank(3);
                    }
                    Resources res = getResources();
                    list = ( ListView )findViewById( R.id.list );  // List defined in XML ( See Below )

                    //create customer adapter
                    adapter = new HighScoresCustomAdapter( HighScoresActivity.this, CustomListView, CustomListViewValuesArr, res);

                    list.setAdapter( adapter );
                } else {
                    Log.d("Goals", "Error: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    public void mainMenuClick(View view){
        startActivity(new Intent(this, WelcomeScreenActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_high_scores, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
