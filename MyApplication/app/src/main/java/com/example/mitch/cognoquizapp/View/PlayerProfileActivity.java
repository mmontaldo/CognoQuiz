package com.example.mitch.cognoquizapp.View;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mitch.cognoquizapp.R;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class PlayerProfileActivity extends Activity {

    String userName;
    int currentXP;
    int levelNumber;
    int correctQuestions;
    int totalQuestions;
    String avatarName;
    TextView userNameTxtView;
    TextView userLevelTxtView;
    TextView questionsTxtView;
    TextView questionsPercentTxtView;
    TextView userXpTxtView;
    TextView userXpLeftTxtView;
    ProgressBar xpProgressBar;
    ImageView userImgView;
    String[] levels = {"Student","Intern","Professor","Scientist","Master"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_profile_loading_screen);

        //Retrieve user information

        //Query to get user information
        ParseUser currentUser = ParseUser.getCurrentUser();

        ParseQuery query = ParseUser.getQuery();
        query.whereEqualTo("objectId", currentUser.getObjectId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject u, com.parse.ParseException e) {
                if (u == null) {
                    Log.d("score", "The getFirst request failed.");
                } else {
                    userName = u.getString("username");
                    currentXP = u.getInt("current_xp");
                    levelNumber = u.getInt("level_number");
                    correctQuestions = u.getInt("total_questions_correct");
                    totalQuestions = u.getInt("total_questions");
                    avatarName = u.getString("avatar_name");

                    setView();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    public void setView(){
        setContentView(R.layout.activity_player_profile);

        userNameTxtView = (TextView) findViewById(R.id.usernamePP);
        userLevelTxtView = (TextView) findViewById(R.id.userlevelPP);
        questionsTxtView = (TextView) findViewById(R.id.questionsPP);
        questionsPercentTxtView = (TextView) findViewById((R.id.percentagePP));
        userXpTxtView = (TextView) findViewById(R.id.userxpPP);
        userXpLeftTxtView = (TextView) findViewById(R.id.userxpleftPP);
        userImgView = (ImageView) findViewById(R.id.userAvatarPP);
        xpProgressBar = (ProgressBar) findViewById(R.id.progressbarPP);

        userNameTxtView.setText(userName);
        userLevelTxtView.setText(levels[levelNumber-1]);
        questionsTxtView.setText("Correct Answers: " + correctQuestions + " of " + totalQuestions);
        userXpTxtView.setText("Current XP: " + currentXP + "/100 xp");
        userXpLeftTxtView.setText(100-currentXP + " xp to " + levels[levelNumber]);

        if (totalQuestions == 0){
            questionsPercentTxtView.setText("Correct Percentage: 0%");
        } else {
            questionsPercentTxtView.setText("Correct Percentage: " + (int)((correctQuestions * 100.0f) / totalQuestions) + "%");
        }

        int imageResource = getResources().getIdentifier(avatarName.substring(11, avatarName.length()), "drawable", this.getPackageName());
        Drawable image = getResources().getDrawable(imageResource);
        userImgView.setImageDrawable(image);


        //Set Progress Bar
        xpProgressBar.setProgress(currentXP);

    }
    public void changeAvatarClick(View view){
        startActivity(new Intent(this, ChangeAvatarActivity.class));
    }

    public void mainMenuClick(View view){
        startActivity(new Intent(this, WelcomeScreenActivity.class));
    }


    @Override

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player_profile, menu);
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
