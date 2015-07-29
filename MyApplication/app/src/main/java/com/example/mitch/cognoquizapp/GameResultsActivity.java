package com.example.mitch.cognoquizapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class GameResultsActivity extends Activity {

    TextView questionsCorrectTxtView;
    TextView xpGainedTxtView;
    TextView userNameTxtView;
    TextView userLevelTxtView;
    TextView userXpTxtView;
    TextView userXpLeftTxtView;
    TextView resultsTxtView;
    ProgressBar xpProgressBar;
    Animation animAlphaQuestionsCorrect = null;
    Animation animAlphaXpGained = null;
    int numberCorrect;
    String userName;
    String userLevel;
    int currentXP;
    int levelNumber;
    String[] levels = {"student","intern","professor","scientist","master"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        numberCorrect = intent.getIntExtra("numberCorrect", 0);

        //Query to get user information
        ParseUser currentUser = ParseUser.getCurrentUser();

        ParseQuery query = ParseUser.getQuery();
        query.whereEqualTo("objectId", currentUser.getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> userSet, com.parse.ParseException e) {

                ParseObject v = userSet.get(0);

                userName = userSet.get(0).getString("username");
                userLevel = userSet.get(0).getString("level");
                currentXP = userSet.get(0).getInt("current_xp");
                levelNumber = userSet.get(0).getInt("level_number");

                setContentView(R.layout.activity_game_results);
                setupResultsActivity();
            }
        });

    }

    public void setupResultsActivity(){
        questionsCorrectTxtView = (TextView) findViewById(R.id.numbercorrect);
        xpGainedTxtView = (TextView) findViewById(R.id.xpgained);
        userNameTxtView = (TextView) findViewById(R.id.username);
        userLevelTxtView = (TextView) findViewById(R.id.userlevel);
        userXpTxtView = (TextView) findViewById(R.id.userxp);
        userXpLeftTxtView = (TextView) findViewById(R.id.userxpleft);
        xpProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        resultsTxtView = (TextView) findViewById(R.id.results);

        userNameTxtView.setText(userName);
        userLevelTxtView.setText(userLevel);
        questionsCorrectTxtView.setText(numberCorrect + "/10");
        resultsTxtView.setPaintFlags(resultsTxtView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        //Calculate xp gained
        int xp = (numberCorrect * 10)/4;
        xpGainedTxtView.setText("+" + xp + " xp");

        //Update xp
        int newXP = currentXP + xp;
        userXpTxtView.setText(newXP + "/100 xp");

        //Set xp left to next level
        userXpLeftTxtView.setText(100-newXP + " xp to " + levels[levelNumber]);

        //Set Progress Bar
        xpProgressBar.setProgress(newXP);

        //initialize animations
        animAlphaQuestionsCorrect = AnimationUtils.loadAnimation(this,
                R.anim.anim_alpha_appear);

        animAlphaXpGained = AnimationUtils.loadAnimation(this,
                R.anim.anim_alpha_appear);

        questionsCorrectTxtView.setVisibility(View.INVISIBLE);
        xpGainedTxtView.setVisibility(View.INVISIBLE);

        questionsCorrectTxtView.startAnimation(animAlphaQuestionsCorrect);

        //animation listeners
        animAlphaQuestionsCorrect.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                questionsCorrectTxtView.setVisibility(View.VISIBLE);
                xpGainedTxtView.startAnimation(animAlphaXpGained);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animAlphaXpGained.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                xpGainedTxtView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void restartGameClick(View view){
        startActivity(new Intent(GameResultsActivity.this, GameActivity.class));
    }

    public void mainMenuClick(View view){
        startActivity(new Intent(GameResultsActivity.this, WelcomeScreenActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_feedback, menu);
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
