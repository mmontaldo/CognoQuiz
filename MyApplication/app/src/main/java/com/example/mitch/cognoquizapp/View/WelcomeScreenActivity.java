package com.example.mitch.cognoquizapp.View;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mitch.cognoquizapp.R;
import com.example.mitch.cognoquizapp.View.GameActivity;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class WelcomeScreenActivity extends Activity {


    ImageView characterImageView;
    TextView characterNameTxtView;
    TextView characterDescTxtView;
    Animation animAlphaAppear = null;
    Animation animAlphaDisappear = null;
    int index;

    String[] CharacterNames = {"Cello","Chrona","Cogno", "Furtu", "Gemini Twins",
                                "Komodo", "Marauder", "Nonus", "Phonica", "Undula", "Volo"};

    String[] CharacterDescriptions = {"Once Komodo's assistant, Cello was\nconcealed for years under a tarp\non this rickety old cart.",
                                        "From a race of time travelers,\nChrona spends most of his time\nin another time.",
                                        "Cogno combines telepathy and brains\nto be the most intelligent being in the\nknown universe.",
                                        "Furtu, a computer criminal, is\nspending time in prison for\nattempting to blackmail the galaxy.",
                                        "The Gemini Twins are telepaths,\ncapable of manipulating light and\nsound waves.",
                                        "Komodo is a black market trader who\nhas the ability to make himself\nresemble almost any other species.",
                                        "Marauder robots are military\nfighting machines that have not\nalways served for the greater good.",
                                        "Nonus is Cogno's father and is\na leading artificial intelligence expert\nin the universe.",
                                        "Phonica, the team's communications\nofficer, speaks 544 alien languages\nfluently.",
                                        "Undula is the last of a race of braided\nspace serpents, the fastest creatures\nin the universe.",
                                        "Volo is a bit immature and a daredevil,\nbut is the best pilot in the\nknown universe."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        characterImageView = (ImageView) findViewById(R.id.character_image);
        characterNameTxtView = (TextView) findViewById(R.id.character_name);
        characterDescTxtView = (TextView) findViewById(R.id.character_desc);

        //initialize animations
        animAlphaAppear = AnimationUtils.loadAnimation(this,
                R.anim.anim_alpha_appear);

        animAlphaDisappear = AnimationUtils.loadAnimation(this,
                R.anim.anim_alpha_disappear);

        Random rand = new Random();

        index = rand.nextInt(12);

        //set initial character
        characterNameTxtView.setText(CharacterNames[index]);
        characterDescTxtView.setText(CharacterDescriptions[index]);

        if (CharacterNames[index].equals("Gemini Twins")){
            characterImageView.setImageResource(R.drawable.gemini);
        } else {
            String imageStr = CharacterNames[index].toLowerCase();
            int resourceId = this.getResources().getIdentifier(CharacterNames[index].toLowerCase(), "drawable", "com.example.mitch.cognoquizapp");
            characterImageView.setImageResource(resourceId);
        }


        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                index++;
                if (index == CharacterNames.length){
                    index = 0;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //start disappear animations
                        characterDescTxtView.startAnimation(animAlphaDisappear);
                        characterImageView.startAnimation(animAlphaDisappear);
                        characterNameTxtView.startAnimation(animAlphaDisappear);

                        //animation listeners
                        animAlphaDisappear.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                characterImageView.setVisibility(View.INVISIBLE);
                                characterDescTxtView.setVisibility(View.INVISIBLE);
                                characterNameTxtView.setVisibility(View.INVISIBLE);

                                characterNameTxtView.setText(CharacterNames[index]);
                                characterDescTxtView.setText(CharacterDescriptions[index]);

                                if (CharacterNames[index].equals("Gemini Twins")){
                                    characterImageView.setImageResource(R.drawable.gemini);
                                } else {
                                    String imageStr = CharacterNames[index].toLowerCase();
                                    int resourceId = getResources().getIdentifier(CharacterNames[index].toLowerCase(), "drawable", "com.example.mitch.cognoquizapp");
                                    characterImageView.setImageResource(resourceId);
                                }

                                //start appearing animations
                                characterDescTxtView.startAnimation(animAlphaAppear);
                                characterImageView.startAnimation(animAlphaAppear);
                                characterNameTxtView.startAnimation(animAlphaAppear);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                        //animation listeners
                        animAlphaAppear.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                characterImageView.setVisibility(View.VISIBLE);
                                characterDescTxtView.setVisibility(View.VISIBLE);
                                characterNameTxtView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                    }
                });

            }
        }, 7*1000, 7*1000);

    }

    public void newGameClick(View view){
        startActivity(new Intent(this, GameActivity.class));
    }

    public void highScoresClick(View view){
        startActivity(new Intent(this, HighScoresActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome_screen, menu);
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
