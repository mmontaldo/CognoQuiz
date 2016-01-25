package com.example2.mitch.cognoquizapp.View;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example2.mitch.cognoquizapp.Model.HighScore;
import com.example2.mitch.cognoquizapp.R;
import com.example2.mitch.cognoquizapp.util.IabBroadcastReceiver;
import com.example2.mitch.cognoquizapp.util.IabHelper;
import com.example2.mitch.cognoquizapp.util.IabResult;
import com.example2.mitch.cognoquizapp.util.Inventory;
import com.example2.mitch.cognoquizapp.util.Purchase;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class WelcomeScreenActivity extends Activity {


    // Debug tag, for logging
    static final String TAG = "CognoQuiz";

    // The helper object
    IabHelper mHelper;

    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;

    ImageView characterImageView;
    TextView characterNameTxtView;
    TextView characterDescTxtView;

    //Purchase alert screen
    TextView loadingMessage;
    TextView titleMessage;
    TextView headingMessage;
    TextView pack1Message;
    TextView pack2Message;
    Button pack1Button;
    Button pack2Button;

    Animation animAlphaAppear = null;
    Animation animAlphaDisappear = null;
    int index;

    AlertDialog.Builder builder;
    LayoutInflater inflater;
    View dialogView;

    ParseUser currentUser;
    boolean set1complete;
    boolean set2complete;
    boolean set3complete;

    static final String SKU_BONUS_PACK_1 = "bonus_pack_1";
    static final String SKU_BONUS_PACK_2 = "bonus_pack_2";

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;

    // Does the user have the bonus pack 1?
    boolean mIsBonus1 = false;

    // Does the user have the bonus pack 2?
    boolean mIsBonus2 = false;

    //Use emulator
    boolean useEmulator = false;

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
        findViewsByID();
        loadAnimations();
        startRotatingCharacterDisplay();
    }

    public void checkPurchases(){

        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAynIkqbtb2T481PEKF+k3MTp4axrrpJlbDabYU3ZTpp++x9ZzpYLOxoKcpitspJUXJXtYjcNMe/ACKbq6qbAlAMlYpdh2bxf/ggEFWjZoP4w3KhSxMgmhlO7IuxPuPKjz8aJs4XMbKR8jRGZoAkIYNzFBb/cYKPUSZAlXYCw7B5QbfyF36vDYwb232Hhm9UZRXv52LmtC+0GWNTVFwq11M7gxbMmO/FzpyVhpa2bdiVeCzK8cUuGwRhyhxGOfQ9h6OKsd7M0wlbQCHRXjnDLnsuhL814s3jJCLQJHwE9BTfwQWpQ706nkXaKTz6FveOIj666eATjf4J2j+8IQfFH2ZQIDAQAB";


        // Create the helper, passing it our context and the public key to verify signatures with
        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(false);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;
//
//                // Important: Dynamically register for broadcast messages about updated purchases.
//                // We register the receiver here instead of as a <receiver> in the Manifest
//                // because we always call getPurchases() at startup, so therefore we can ignore
//                // any broadcasts sent while the app isn't running.
//                // Note: registering this listener in an Activity is a bad idea, but is done here
//                // because this is a SAMPLE. Regardless, the receiver must be registered after
//                // IabHelper is setup, but before first call to getPurchases().
//                mBroadcastReceiver = new IabBroadcastReceiver(WelcomeScreenActivity.this);
//                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
//                registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });
    }


    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");


//             * Check for items we own. Notice that for each purchase, we check
//             * the developer payload to see if it's correct! See
//             * verifyDeveloperPayload().


            // Do we have the bonus pack 1?
            Purchase bonusPack1Purchase = inventory.getPurchase(SKU_BONUS_PACK_1);
            mIsBonus1 = (bonusPack1Purchase != null);
            Log.d(TAG, "User has " + (mIsBonus1 ? "Bonus Pack 1" : "Not Bonus Pack 1"));


            // Do we have the bonus pack 2?
            Purchase bonusPack2Purchase = inventory.getPurchase(SKU_BONUS_PACK_2);
            mIsBonus2 = (bonusPack2Purchase != null);
            Log.d(TAG, "User has " + (mIsBonus2 ? "Bonus Pack 2" : "Not Bonus Pack 2"));


            Log.d(TAG, "Initial inventory query finished; enabling main UI.");

            setupPurchaseAlert();
        }
    };


    @Override
    public void onBackPressed() {
    }

    public void newGameClick(View view){
        startActivity(new Intent(this, GameActivity.class));
    }

    public void highScoresClick(View view){
        startActivity(new Intent(this, HighScoresActivity.class));
    }

    public void playerProfileClick(View view){
        startActivity(new Intent(this, PlayerProfileActivity.class));
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }

    public void logoutClick(View view){
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ParseUser.logOut();
                        Intent intent = new Intent(WelcomeScreenActivity.this, LoginScreenActivity.class);
                        startActivity(intent);
                        WelcomeScreenActivity.this.finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    public void infoClick(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.info_dialog_window, null))
                // Add action buttons
                .setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });


        builder.create();
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void purchaseMenuClick(View view){
        builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        inflater = this.getLayoutInflater();

        dialogView = inflater.inflate(R.layout.purchase_dialog_window, null);

        findViewByIDPurchaseScreen();
        setLoadingScreen(true);

        builder.setView(dialogView);
        builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // do nothing
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        if (useEmulator){
            setupPurchaseAlert();
        } else {
            makeDataCallForFinishedSets();
        }


    }

    public void makeDataCallForFinishedSets(){
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
                    set1complete = u.getBoolean("set1_complete");
                    set2complete = u.getBoolean("set2_complete");
                    set3complete = u.getBoolean("set3_complete");

                    checkPurchases();

                }
            }
        });
    }

    public void findViewByIDPurchaseScreen(){
        loadingMessage = (TextView) dialogView.findViewById(R.id.loading);
        titleMessage = (TextView) dialogView.findViewById(R.id.purchaseTitle);
        headingMessage = (TextView) dialogView.findViewById(R.id.heading);
        pack1Message = (TextView) dialogView.findViewById(R.id.pack1Text);
        pack2Message = (TextView) dialogView.findViewById(R.id.pack2Text);
        pack1Button = (Button) dialogView.findViewById(R.id.pack1button);
        pack2Button = (Button) dialogView.findViewById(R.id.pack2button);
    }

    public void setLoadingScreen(boolean isLoading){
        if (isLoading){
            loadingMessage.setVisibility(View.VISIBLE);
            titleMessage.setVisibility(View.INVISIBLE);
            headingMessage.setVisibility(View.INVISIBLE);
            pack1Message.setVisibility(View.INVISIBLE);
            pack2Message.setVisibility(View.INVISIBLE);
            pack1Button.setVisibility(View.INVISIBLE);
            pack2Button.setVisibility(View.INVISIBLE);
        } else {
            loadingMessage.setVisibility(View.INVISIBLE);
            titleMessage.setVisibility(View.VISIBLE);
            headingMessage.setVisibility(View.VISIBLE);
            pack1Message.setVisibility(View.VISIBLE);
            pack2Message.setVisibility(View.VISIBLE);
            pack1Button.setVisibility(View.VISIBLE);
            pack2Button.setVisibility(View.VISIBLE);
        }

    }

    public void setupPurchaseAlert(){

        setLoadingScreen(false);
        //Set Title
        titleMessage.setText("Cogno's Store");

        //Set buttons depending on user's purchase history
        if (mIsBonus1 == false && mIsBonus2 == false){
            Drawable d = ContextCompat.getDrawable(this, R.drawable.unavailable_button);
            pack2Button.setBackground(d);
            pack2Button.setEnabled(false);
        }

        if (mIsBonus1 == true && mIsBonus2 == false){
            Drawable d = ContextCompat.getDrawable(this, R.drawable.owned_button);
            pack1Button.setBackground(d);
            pack1Button.setText("Owned");
            pack1Button.setEnabled(false);
        }

        if (mIsBonus1 && mIsBonus2){
            headingMessage.setText("You already own both bonus packs. More questions are coming soon!");

            Drawable a = ContextCompat.getDrawable(this, R.drawable.owned_button);
            pack2Button.setBackground(a);
            pack2Button.setText("Owned");
            pack2Button.setEnabled(false);

            Drawable b = ContextCompat.getDrawable(this, R.drawable.owned_button);
            pack1Button.setBackground(b);
            pack1Button.setText("Owned");
            pack1Button.setEnabled(false);
        }

        //User clicks to purchase pack 1
        pack1Button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.d(TAG, "Upgrade button clicked; launching purchase flow for upgrade.");

                String payload = "";

                mHelper.launchPurchaseFlow(WelcomeScreenActivity.this, SKU_BONUS_PACK_1, RC_REQUEST,
                        mPurchaseFinishedListener, payload);
            }
        });

        //User clicks to purchase pack 2
        pack2Button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.d(TAG, "Upgrade button clicked; launching purchase flow for upgrade.");

                String payload = "";

                mHelper.launchPurchaseFlow(WelcomeScreenActivity.this, SKU_BONUS_PACK_2, RC_REQUEST,
                        mPurchaseFinishedListener, payload);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }


    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
       //         setWaitScreen(false);
                return;
            }

            Log.d(TAG, "Purchase successful.");

        if (purchase.getSku().equals(SKU_BONUS_PACK_1)) {
            Log.d(TAG, "Purchase is bonus pack 1. Congratulating user.");
            alert("Thank you for purchasing bonus pack 1!");

            Drawable a = ContextCompat.getDrawable(WelcomeScreenActivity.this, R.drawable.owned_button);
            pack1Button.setBackground(a);
            pack1Button.setText("Owned");
            pack1Button.setEnabled(false);

            Drawable d = ContextCompat.getDrawable(WelcomeScreenActivity.this, R.drawable.purchase_background);
            pack2Button.setBackground(d);
            pack2Button.setEnabled(true);

            if (set1complete){
                setNewQuestionSetAfterPurchase(5);
            }
     //       setWaitScreen(false);
        }
        else if (purchase.getSku().equals(SKU_BONUS_PACK_2)) {
            // bought the premium upgrade!
            Log.d(TAG, "Purchase is bonus pack 2. Congratulating user.");
            alert("Thank you for purchasing bonus pack 2!");

            Drawable a = ContextCompat.getDrawable(WelcomeScreenActivity.this, R.drawable.owned_button);
            pack2Button.setBackground(a);
            pack2Button.setText("Owned");
            pack2Button.setEnabled(false);

            Drawable b = ContextCompat.getDrawable(WelcomeScreenActivity.this, R.drawable.owned_button);
            pack1Button.setBackground(b);
            pack1Button.setText("Owned");
            pack1Button.setEnabled(false);

            if (set2complete){
                setNewQuestionSetAfterPurchase(14);
            }

    //        setWaitScreen(false);
        }
        }
    };

    public void setNewQuestionSetAfterPurchase(int newSet){
        //Query to get user information
        currentUser = ParseUser.getCurrentUser();

        final int newSET = newSet;

        ParseQuery query = ParseUser.getQuery();
        query.whereEqualTo("objectId", currentUser.getObjectId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject u, com.parse.ParseException e) {
                if (u == null) {
                    Log.d("score", "The getFirst request failed.");
                } else {
                    u.put("current_set", newSET);
                    u.saveInBackground();
                }
            }
        });
    }


    public void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    public void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
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



    public void findViewsByID(){
        characterImageView = (ImageView) findViewById(R.id.character_image);
        characterNameTxtView = (TextView) findViewById(R.id.character_name);
        characterDescTxtView = (TextView) findViewById(R.id.character_desc);
    }

    public void loadAnimations(){
        //initialize animations
        animAlphaAppear = AnimationUtils.loadAnimation(this,
                R.anim.anim_alpha_appear);

        animAlphaDisappear = AnimationUtils.loadAnimation(this,
                R.anim.anim_alpha_disappear);
    }

    public void startRotatingCharacterDisplay(){

        //Choose random character to display first
        Random rand = new Random();
        index = rand.nextInt(11);
        index = 2;

        //set initial character
        characterNameTxtView.setText(CharacterNames[index]);
        characterDescTxtView.setText(CharacterDescriptions[index]);

        if (CharacterNames[index].equals("Gemini Twins")){
            characterImageView.setImageResource(R.drawable.gemini);
        } else {
            String imageStr = CharacterNames[index].toLowerCase();
            int resourceId = this.getResources().getIdentifier(CharacterNames[index].toLowerCase(), "drawable", "com.example2.mitch.cognoquizapp");
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
                                    int resourceId = getResources().getIdentifier(CharacterNames[index].toLowerCase(), "drawable", "com.example2.mitch.cognoquizapp");
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
}
