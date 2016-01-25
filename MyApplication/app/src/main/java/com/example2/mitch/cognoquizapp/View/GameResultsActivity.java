package com.example2.mitch.cognoquizapp.View;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example2.mitch.cognoquizapp.Model.HighScore;
import com.example2.mitch.cognoquizapp.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class GameResultsActivity extends Activity {

    static final String TAG = "CognoQuiz";

    TextView questionsCorrectTxtView;
    TextView xpGainedTxtView;
    TextView userNameTxtView;
    TextView userLevelTxtView;
    TextView userXpTxtView;
    TextView userXpLeftTxtView;
    TextView resultsTxtView;
    TextView highScoreTxtView;
    ProgressBar xpProgressBar;
    ImageView userImgView;
    Animation animAlphaQuestionsCorrect = null;
    Animation animAlphaXpGained = null;
    int numberCorrect;
    String userName;
    String avatarName;
    int totalQuestions;
    int correctQuestions;
    int gainedXP = 0;
    int currentXP;
    int newXP = 0;
    int levelNumber;
    int questionSet;
    boolean set1complete;
    boolean set2complete;
    boolean set3complete;
    ParseUser currentUser;
    boolean isHighScore = false;
    public ArrayList<HighScore> HighScoresRetrievedList = new ArrayList<HighScore>();
    String[] levels = {"Student","Intern","Professor","Scientist","Master", "He Who Knows All"};

    // Does the user have the bonus pack 1?
    boolean mIsBonus1 = false;

    // Does the user have the bonus pack 2?
    boolean mIsBonus2 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_finished_loading_screen);
        Intent intent = getIntent();
        mIsBonus1 = intent.getBooleanExtra("mIsBonus1", false);
        mIsBonus2 = intent.getBooleanExtra("mIsBonus2", false);
        numberCorrect = intent.getIntExtra("numberCorrect", 0);

        //Query to get user information
        currentUser = ParseUser.getCurrentUser();

        ParseQuery query = ParseUser.getQuery();
        query.whereEqualTo("objectId", currentUser.getObjectId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject u, com.parse.ParseException e) {
                if (u == null){
                    Log.d("score", "The getFirst request failed.");
                } else {
                    userName = u.getString("username");
                    currentXP = u.getInt("current_xp");
                    levelNumber = u.getInt("level_number");
                    questionSet = u.getInt("current_set");
                    avatarName = u.getString("avatar_name");
                    totalQuestions = u.getInt("total_questions");
                    correctQuestions = u.getInt("total_questions_correct");
                    set1complete = u.getBoolean("set1_complete");
                    set2complete = u.getBoolean("set2_complete");
                    set3complete = u.getBoolean("set3_complete");


                    ParseQuery<ParseObject> queryScores = ParseQuery.getQuery("HighScore");
                    queryScores.whereEqualTo("user", currentUser);
                    queryScores.orderByDescending("score");
                    queryScores.addDescendingOrder("date");
                    queryScores.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> highScoreList, ParseException e) {
                            if (e == null) {
                                Log.d("High Scores", "Retrieved " + highScoreList.size() + " scores");
                                for (ParseObject po : highScoreList) {

                                    String name = po.getString("name");
                                    Date date = po.getDate("date");
                                    int score = po.getInt("score");

                                    HighScore h = new HighScore(date, name, score);
                                    HighScoresRetrievedList.add(h);

                                }

                                if (HighScoresRetrievedList.size() < 5){
                                    isHighScore = true;

                                    //Enter new High Score
                                    ParseObject highScore = new ParseObject("HighScore");
                                    highScore.put("score", numberCorrect);
                                    highScore.put("date", Calendar.getInstance().getTime());
                                    highScore.put("name", userName);
                                    highScore.put("user", currentUser);
                                    highScore.saveInBackground();

                                } else if (numberCorrect >= HighScoresRetrievedList.get(HighScoresRetrievedList.size() - 1).getScore()) {

                                    isHighScore = true;

                                    //Enter new High Score
                                    ParseObject highScore = new ParseObject("HighScore");
                                    highScore.put("score", numberCorrect);
                                    highScore.put("date", Calendar.getInstance().getTime());
                                    highScore.put("name", userName);
                                    highScore.put("user", currentUser);
                                    highScore.saveInBackground();

                                    //Delete lowest old high score
                                    ParseQuery<ParseObject> queryScores2 = ParseQuery.getQuery("HighScore");
                                    queryScores2.whereEqualTo("user", currentUser);
                                    queryScores2.orderByAscending("score");
                                    queryScores2.addAscendingOrder("date");
                                    queryScores2.setLimit(1);
                                    queryScores2.findInBackground(new FindCallback<ParseObject>() {
                                        public void done(List<ParseObject> highScoreList, ParseException e) {
                                            if (e == null) {
                                                Log.d("High Scores", "Retrieved " + highScoreList.size() + " scores");
                                                for (ParseObject po : highScoreList) {

                                                    po.deleteInBackground();

                                                }
                                            } else {
                                                Log.d("Goals", "Error: " + e.getMessage());
                                            }
                                        }
                                    });
                                }

                                updateUserStats();

                            } else {
                                Log.d("Goals", "Error: " + e.getMessage());
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
    }

    public void updateUserStats(){

        if ((set1complete && questionSet < 5) || (set2complete && questionSet < 14) || (set3complete)){

            //dont add experience points for repeat questions
            newXP = currentXP;

        } else {
            //Calculate xp gained
            gainedXP = (numberCorrect * 10)/2;

            //Update xp
            newXP = currentXP + gainedXP;

            //Check for level up
            if (newXP > 99){
                newXP = newXP - 100;
                if (levelNumber != 6){
                    levelNumber++;

                    new AlertDialog.Builder(this)
                            .setMessage("You have been promoted to " + levels[levelNumber-1])
                            .setTitle("Congratulations!")
                            .setPositiveButton("close", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .show();
                }
            }
        }


        //next question set
        if (mIsBonus1 == false && mIsBonus2 == false){
            if (questionSet > 3){
                alert("You have completed all of the free rounds of Cogno's Quiz! " +
                        "Go to the Cogno Store ($) in the main menu to purchase more questions, or try the free rounds again!");
                questionSet = 1;
                set1complete = true;
            } else {
                questionSet++;
            }
        }
        if (mIsBonus1 == true && mIsBonus2 == false){

            if (questionSet == 4){
                set1complete = true;
            }

            if (questionSet > 12){
                alert("You have completed all of your purchased rounds of Cogno's Quiz! " +
                        "Go to the Cogno Store ($) in the main menu to purchase more questions, or try your rounds again!");
                questionSet = 1;
                set2complete = true;
            } else {
                questionSet++;
            }
        }

        if (mIsBonus1 == true && mIsBonus2 == true){

            if (questionSet == 4){
                set1complete = true;
            }

            if (questionSet == 13){
                set2complete = true;
            }

            if (questionSet > 21){
                alert("Congrats! You have completed all of the rounds of Cogno's Quiz! " +
                        "Try the rounds again and tell your friends about Cogno!");
                questionSet = 1;
                set3complete = true;
            } else {
                questionSet++;
            }
        }

        //Query to get user information
        currentUser = ParseUser.getCurrentUser();

        ParseQuery query = ParseUser.getQuery();
        query.whereEqualTo("objectId", currentUser.getObjectId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
           @Override
           public void done(ParseObject u, com.parse.ParseException e) {
               if (u == null) {
                   Log.d("score", "The getFirst request failed.");
               } else {
                   u.put("current_set", questionSet);
                   u.put("current_xp", newXP);
                   u.put("level_number", levelNumber);
                   u.put("total_questions", totalQuestions + 10);
                   u.put("total_questions_correct", correctQuestions + numberCorrect);
                   u.put("set1_complete", set1complete);
                   u.put("set2_complete", set2complete);
                   u.put("set3_complete", set3complete);
                   u.saveInBackground();
               }
           }
        });
        setContentView(R.layout.activity_game_results);
        setupResultsActivity();
    }

    public void setupResultsActivity(){
        questionsCorrectTxtView = (TextView) findViewById(R.id.numbercorrect);
        xpGainedTxtView = (TextView) findViewById(R.id.xpgained);
        userNameTxtView = (TextView) findViewById(R.id.username);
        userLevelTxtView = (TextView) findViewById(R.id.userlevel);
        userXpTxtView = (TextView) findViewById(R.id.userxp);
        userXpLeftTxtView = (TextView) findViewById(R.id.userxpleft);
        xpProgressBar = (ProgressBar) findViewById(R.id.progressbarPP);
        resultsTxtView = (TextView) findViewById(R.id.results);
        highScoreTxtView = (TextView) findViewById(R.id.highscoretxt);
        userImgView = (ImageView) findViewById(R.id.userAvatar);

        //set user avatar
        int imageResource = getResources().getIdentifier(avatarName.substring(11, avatarName.length()), "drawable", this.getPackageName());
        Drawable image = getResources().getDrawable(imageResource);
        userImgView.setImageDrawable(image);

        userNameTxtView.setText(userName);
        userLevelTxtView.setText(levels[levelNumber-1]);
        questionsCorrectTxtView.setText(numberCorrect + "/10");
        resultsTxtView.setPaintFlags(resultsTxtView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        if (isHighScore){
            highScoreTxtView.setVisibility(View.VISIBLE);
        } else {
            highScoreTxtView.setVisibility(View.INVISIBLE);
        }


        if (levelNumber == 6){
            xpGainedTxtView.setVisibility(View.INVISIBLE);
            userXpTxtView.setVisibility(View.INVISIBLE);
            userXpLeftTxtView.setVisibility(View.INVISIBLE);
            //Set Progress Bar
            xpProgressBar.setProgress(100);
        } else {
            xpGainedTxtView.setText("+" + gainedXP + " xp");
            userXpTxtView.setText(newXP + "/100 xp");

            //Set xp left to next level
            userXpLeftTxtView.setText(100-newXP + " xp to next level: " + levels[levelNumber]);

            //Set Progress Bar
            xpProgressBar.setProgress(newXP);
        }

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
                if (levelNumber != 6){
                    xpGainedTxtView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void nextRoundClick(View view){
        startActivity(new Intent(GameResultsActivity.this, GameActivity.class));
    }

    public void mainMenuClick(View view){
        startActivity(new Intent(GameResultsActivity.this, WelcomeScreenActivity.class));
    }


    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
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
