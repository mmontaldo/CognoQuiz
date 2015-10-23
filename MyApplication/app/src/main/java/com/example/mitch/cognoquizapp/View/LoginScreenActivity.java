package com.example.mitch.cognoquizapp.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mitch.cognoquizapp.R;
import com.parse.LogInCallback;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class LoginScreenActivity extends Activity {

    Animation animTranslateLogin = null;
    Animation animTranslateSignUp = null;
    Animation animAlphaLogin = null;
    Animation animAlphaSignUp = null;
    Animation animAlphaDisappearLogin = null;
    Animation animAlphaDisappearSignUp = null;

    Animation animAlphaEmail = null;
    Animation animAlphaUser = null;
    Animation animAlphaPass = null;
    Animation animAlphaSignUp2 = null;
    Animation animAlphaLogIn2 = null;
    Animation animAlphaBack = null;

    Animation animAlphaDisappearEmail = null;
    Animation animAlphaDisappearUser = null;
    Animation animAlphaDisappearPass = null;
    Animation animAlphaDisappearSignUp2 = null;
    Animation animAlphaDisappearLogIn2 = null;
    Animation animAlphaDisappearBack = null;

    Button loginBtn;
    Button signupBtn;
    Button loginBtn2;
    Button signupBtn2;
    Button backBtn;
    EditText emailEdTxt;
    EditText userEdTxt;
    EditText passEdTxt;

    boolean isLogin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        //Get current user
        ParseUser currentUser = ParseUser.getCurrentUser();

        //If there is an active login session, automatically log in
        if (currentUser != null) {

            startActivity(new Intent(LoginScreenActivity.this,WelcomeScreenActivity.class ));

        } else {

            //Start login/sign up page
            findViewsByID();

            //Button animations initialization
            loadAnimations();
            initializeInvisible();

            animTranslateLogin.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    loginButtonClickAnimations();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            animTranslateSignUp.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    signUpButtonClickAnimations();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        }
    }

    @Override
    public void onBackPressed() {
    }

    //Signup Button click listener
    public void signUpButtonClick(View v) {
        isLogin = false;
        loginBtn.startAnimation(animAlphaDisappearLogin);
        v.startAnimation(animTranslateSignUp);
    }

    //Login Button click listener
    public void logInButtonClick(View v) {
        isLogin = true;
        signupBtn.startAnimation(animAlphaDisappearSignUp);
        v.startAnimation(animTranslateLogin);
    }

    public void backButtonClick(View view){

        userEdTxt.startAnimation(animAlphaDisappearUser);
        passEdTxt.startAnimation(animAlphaDisappearPass);
        backBtn.startAnimation(animAlphaDisappearBack);

        if (isLogin == false){
            emailEdTxt.startAnimation(animAlphaDisappearEmail);
            signupBtn2.startAnimation(animAlphaDisappearSignUp2);
        } else {
            loginBtn2.startAnimation(animAlphaDisappearLogIn2);
        }
        animAlphaDisappearBack.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //make buttons disappear
                emailEdTxt.setVisibility(View.INVISIBLE);
                userEdTxt.setVisibility(View.INVISIBLE);
                passEdTxt.setVisibility(View.INVISIBLE);
                backBtn.setVisibility(View.INVISIBLE);
                signupBtn2.setVisibility(View.INVISIBLE);
                loginBtn2.setVisibility(View.INVISIBLE);
                loginBtn.setVisibility(View.VISIBLE);
                signupBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void signUpButton2Click(View v){
        String username = userEdTxt.getText().toString();
        String password = passEdTxt.getText().toString();
        String email = emailEdTxt.getText().toString();

        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);

        // initialize fields
        user.put("firstTime", true);
        user.put("current_set", 1);
        user.put("level_number", 1);
        user.put("current_xp", 0);
        user.put("avatar_name", "R.drawable.cogno");
        user.put("total_questions_correct", 0);
        user.put("total_questions", 0);
        

        user.signUpInBackground(new SignUpCallback() {
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    setContentView(R.layout.signup_loading_screen);
                    Log.v("SIGN UP", "REACHED END");
                    startActivity(new Intent(LoginScreenActivity.this, WelcomeScreenActivity.class));
                    // Hooray! Let them use the app now.
                } else {
                    Toast.makeText(getApplicationContext(), e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    Log.v("SIGN UP", e.getMessage());
                    Log.v("SIGN UP", "REACHED ERROR");
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                }
            }
        });
    }

    public void logInButton2Click(View v){
        setContentView(R.layout.login_loading_screen);
        String username = userEdTxt.getText().toString();
        String password = passEdTxt.getText().toString();

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            public void done(ParseUser user, com.parse.ParseException e) {
                if (user != null) {
                    startActivity(new Intent(LoginScreenActivity.this,WelcomeScreenActivity.class ));
                    // Hooray! The user is logged in.
                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_screen, menu);
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

    public void findViewsByID(){
        loginBtn = (Button) findViewById(R.id.login);
        signupBtn = (Button) findViewById(R.id.signup);
        loginBtn2 = (Button) findViewById(R.id.login2);
        signupBtn2 = (Button) findViewById(R.id.signup2);
        backBtn = (Button) findViewById(R.id.back);
        emailEdTxt = (EditText) findViewById(R.id.etEmail);
        userEdTxt = (EditText) findViewById(R.id.etUserName);
        passEdTxt = (EditText) findViewById(R.id.etPass);
    }

    public void loginButtonClickAnimations(){
        loginBtn.setVisibility(View.INVISIBLE);
        signupBtn.setVisibility(View.INVISIBLE);
        loginBtn2.setVisibility(View.VISIBLE);
        userEdTxt.setVisibility(View.VISIBLE);
        passEdTxt.setVisibility(View.VISIBLE);
        backBtn.setVisibility(View.VISIBLE);
        loginBtn2.startAnimation(animAlphaSignUp2);
        userEdTxt.startAnimation(animAlphaUser);
        passEdTxt.startAnimation(animAlphaPass);
        backBtn.startAnimation(animAlphaBack);
    }

    public void signUpButtonClickAnimations() {
        loginBtn.setVisibility(View.INVISIBLE);
        signupBtn.setVisibility(View.INVISIBLE);
        signupBtn2.setVisibility(View.VISIBLE);
        emailEdTxt.setVisibility(View.VISIBLE);
        userEdTxt.setVisibility(View.VISIBLE);
        passEdTxt.setVisibility(View.VISIBLE);
        backBtn.setVisibility(View.VISIBLE);
        signupBtn2.startAnimation(animAlphaSignUp2);
        emailEdTxt.startAnimation(animAlphaEmail);
        userEdTxt.startAnimation(animAlphaUser);
        passEdTxt.startAnimation(animAlphaPass);
        backBtn.startAnimation(animAlphaBack);
    }

    public void initializeInvisible(){
        loginBtn2.setVisibility(View.INVISIBLE);
        signupBtn2.setVisibility(View.INVISIBLE);
        emailEdTxt.setVisibility(View.INVISIBLE);
        userEdTxt.setVisibility(View.INVISIBLE);
        passEdTxt.setVisibility(View.INVISIBLE);
        backBtn.setVisibility(View.INVISIBLE);
    }

    public void loadAnimations(){
        animTranslateLogin = AnimationUtils.loadAnimation(this,
                R.anim.anim_trans);

        animTranslateSignUp = AnimationUtils.loadAnimation(this,
                R.anim.anim_trans);

        animAlphaLogin = AnimationUtils.loadAnimation(this,
                R.anim.anim_alpha_appear);

        animAlphaSignUp = AnimationUtils.loadAnimation(this,
                R.anim.anim_alpha_appear);

        animAlphaDisappearLogin = AnimationUtils.loadAnimation(this,
                R.anim.anim_alpha_disappear);

        animAlphaDisappearSignUp = AnimationUtils.loadAnimation(this,
                R.anim.anim_alpha_disappear);

        animAlphaEmail = AnimationUtils.loadAnimation(this,
                R.anim.anim_alpha_appear);

        animAlphaUser = AnimationUtils.loadAnimation(this,
                R.anim.anim_alpha_appear);

        animAlphaPass = AnimationUtils.loadAnimation(this,
                R.anim.anim_alpha_appear);

        animAlphaSignUp2 = AnimationUtils.loadAnimation(this,
                R.anim.anim_alpha_appear);

        animAlphaLogIn2 = AnimationUtils.loadAnimation(this,
                R.anim.anim_alpha_appear);

        animAlphaBack = AnimationUtils.loadAnimation(this,
                R.anim.anim_alpha_appear);

        animAlphaDisappearEmail = AnimationUtils.loadAnimation(this,
                R.anim.anim_alpha_disappear);

        animAlphaDisappearUser = AnimationUtils.loadAnimation(this,
                R.anim.anim_alpha_disappear);

        animAlphaDisappearPass = AnimationUtils.loadAnimation(this,
                R.anim.anim_alpha_disappear);

        animAlphaDisappearSignUp2 = AnimationUtils.loadAnimation(this,
                R.anim.anim_alpha_disappear);

        animAlphaDisappearLogIn2 = AnimationUtils.loadAnimation(this,
                R.anim.anim_alpha_disappear);

        animAlphaDisappearBack = AnimationUtils.loadAnimation(this,
                R.anim.anim_alpha_disappear);
    }
}
