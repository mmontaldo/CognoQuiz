package com.example2.mitch.cognoquizapp.View;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer;

import com.example2.mitch.cognoquizapp.Model.Question;
import com.example2.mitch.cognoquizapp.R;
import com.example2.mitch.cognoquizapp.util.IabBroadcastReceiver;
import com.example2.mitch.cognoquizapp.util.IabHelper;
import com.example2.mitch.cognoquizapp.util.IabResult;
import com.example2.mitch.cognoquizapp.util.Inventory;
import com.example2.mitch.cognoquizapp.util.Purchase;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;



public class GameActivity extends Activity {

    public ArrayList<Question> QuestionSet = new ArrayList<Question>();

    private static final int SPEECH_REQUEST_CODE = 0;

    public final static String CORRECTNESS_MESSAGE = "com.example2.mitch.cognoquizapp.MESSAGE";

    public static MediaPlayer player;

    Button questionBox;
    Button optionABox;
    Button optionBBox;
    Button optionCBox;
    Button optionDBox;
    TextView questionNumTxtView;
    int currentQuestion;
    boolean resume = false;
    boolean newquestion = false;
    boolean pauseMusic = true;
    int numbercorrect;
    int userCurrentSet;

    //IN APP PURCHASES
    // Debug tag, for logging
    static final String TAG = "CognoQuiz";

    // The helper object
    IabHelper mHelper;

    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;

    static final String SKU_BONUS_PACK_1 = "bonus_pack_1";
    static final String SKU_BONUS_PACK_2 = "bonus_pack_2";

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;

    // Does the user have the bonus pack 1?
    boolean mIsBonus1 = false;

    // Does the user have the bonus pack 2?
    boolean mIsBonus2 = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Display loading screen while we make our data call
        setContentView(R.layout.newgame_loading_screen);

        checkPurchases();
    }

    public void checkPurchases(){

        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAynIkqbtb2T481PEKF+k3MTp4axrrpJlbDabYU3ZTpp++x9ZzpYLOxoKcpitspJUXJXtYjcNMe/ACKbq6qbAlAMlYpdh2bxf/ggEFWjZoP4w3KhSxMgmhlO7IuxPuPKjz8aJs4XMbKR8jRGZoAkIYNzFBb/cYKPUSZAlXYCw7B5QbfyF36vDYwb232Hhm9UZRXv52LmtC+0GWNTVFwq11M7gxbMmO/FzpyVhpa2bdiVeCzK8cUuGwRhyhxGOfQ9h6OKsd7M0wlbQCHRXjnDLnsuhL814s3jJCLQJHwE9BTfwQWpQ706nkXaKTz6FveOIj666eATjf4J2j+8IQfFH2ZQIDAQAB";


        // Create the helper, passing it our context and the public key to verify signatures with
        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

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

            makeDataCallForQuestionSet();
        }
    };



    @Override
    protected void onResume(){
        super.onResume();
        //resume makes sure the question is not incremented before the game starts
        //newquestion is set to true after the results page is closed
        if (resume && newquestion) {
            currentQuestion++;
            questionNumTxtView.setText("Question " + currentQuestion + " of 10");
            setQuestion(QuestionSet.get(currentQuestion - 1));
            newquestion = false;
        }
        if (player != null) {
            if (resume && !player.isPlaying()) {
                player = MediaPlayer.create(GameActivity.this, R.raw.cogno_game_score);
                player.setLooping(true);
                player.start();

            }
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (pauseMusic) {
            if (GameActivity.player != null) {
                player.stop();
            }
        }
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

    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    @Override
    public void onBackPressed() {
    }

    public void speechButtonClick(View view){
        displaySpeechRecognizer();
    }

    public void optionAClick(View view){
        pauseMusic = false;
        Question q = QuestionSet.get(currentQuestion-1);

        //Check answer, 0 corresponds with selecting A
        boolean answerCorrectness = q.checkAnswer(0);

        if (answerCorrectness){
            startIntentForCorrectAnswer(q);
        } else {
            startIntentForIncorrectAnswer(q);
        }
    }

    public void optionBClick(View view){
        pauseMusic = false;
        Question q = QuestionSet.get(currentQuestion-1);

        //Check answer, 1 corresponds with selecting B
        boolean answerCorrectness = q.checkAnswer(1);

        if (answerCorrectness){
            startIntentForCorrectAnswer(q);
        } else {
            startIntentForIncorrectAnswer(q);
        }
    }

    public void optionCClick(View view){
        pauseMusic = false;
        Question q = QuestionSet.get(currentQuestion-1);

        //Check answer, 2 corresponds with selecting C
        boolean answerCorrectness = q.checkAnswer(2);

        if (answerCorrectness){
            startIntentForCorrectAnswer(q);
        } else {
            startIntentForIncorrectAnswer(q);
        }
    }

    public void optionDClick(View view){
        pauseMusic = false;
        Question q = QuestionSet.get(currentQuestion-1);

        //Check answer, 3 corresponds with selecting D
        boolean answerCorrectness = q.checkAnswer(3);

        if (answerCorrectness){
            startIntentForCorrectAnswer(q);
        } else {
            startIntentForIncorrectAnswer(q);
        }
    }

    public void startIntentForCorrectAnswer(Question q){
        Intent intent = new Intent(this, GameAnswerActivity.class);

        //Check if it is the last question of the set
        if (currentQuestion == QuestionSet.size()){
            intent.putExtra("isFinished", true);
        } else {
            intent.putExtra("isFinished", false);
        }

        String message = "correct";
        numbercorrect++;
        intent.putExtra("mIsBonus1", mIsBonus1);
        intent.putExtra("mIsBonus2", mIsBonus2);
        intent.putExtra(CORRECTNESS_MESSAGE, message);
        intent.putExtra("question", q);
        intent.putExtra("numberCorrect", numbercorrect);
        startActivityForResult(intent, 1);
    }

    public void startIntentForIncorrectAnswer(Question q){

        Intent intent = new Intent(this, GameAnswerActivity.class);

        //Check if it is the last question of the set
        if (currentQuestion == QuestionSet.size()){
            intent.putExtra("isFinished", true);
        } else {
            intent.putExtra("isFinished", false);
        }

        String message = "incorrect";
        intent.putExtra("mIsBonus1", mIsBonus1);
        intent.putExtra("mIsBonus2", mIsBonus2);
        intent.putExtra(CORRECTNESS_MESSAGE, message);
        intent.putExtra("question", q);
        intent.putExtra("numberCorrect", numbercorrect);
        startActivityForResult(intent, 1);
    }

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        //Results from Speech Recognition
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            if (results != null){
                String spokenText = results.get(0).toLowerCase();
                if (QuestionSet.get(currentQuestion - 1).getTF()){
                    if (spokenText.equals("true")){
                        optionABox.performClick();
                    } else if (spokenText.equals("false")) {
                        optionBBox.performClick();
                    } else {
                        Toast.makeText(getApplicationContext(), "\"" + spokenText + "\" is not an answer. Please try again.",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (spokenText.equals(QuestionSet.get(currentQuestion - 1).getOptionA().toLowerCase())) {
                        optionABox.performClick();
                    } else if (spokenText.equals(QuestionSet.get(currentQuestion - 1).getOptionB().toLowerCase())) {
                        optionBBox.performClick();
                    } else if (spokenText.equals(QuestionSet.get(currentQuestion - 1).getOptionC().toLowerCase())) {
                        optionCBox.performClick();
                    } else if (spokenText.equals(QuestionSet.get(currentQuestion - 1).getOptionD().toLowerCase())) {
                        optionDBox.performClick();
                    } else {
                        Toast.makeText(getApplicationContext(), "\"" + spokenText + "\" is not an answer. Please try again.",
                                Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), "Please try again.",
                        Toast.LENGTH_LONG).show();
            }
        }
        // Result from game answer activity
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle b = data.getExtras();
                if (b != null) {
                    newquestion = (boolean) b.getSerializable("returnCheck");
                    pauseMusic = true;
                }
            } else if (resultCode == 0) {
                System.out.println("RESULT CANCELLED");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
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

    public void makeDataCallForQuestionSet(){
        //Query to get user's current question set
        ParseUser currentUser = ParseUser.getCurrentUser();

        ParseQuery query = ParseUser.getQuery();
        query.whereEqualTo("objectId", currentUser.getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> userSet, com.parse.ParseException e) {

                ParseObject v = userSet.get(0);
                userCurrentSet = v.getInt("current_set");

                //Query to get Question Set
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Question");
                query.whereEqualTo("set_number", userCurrentSet);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> questionSet, com.parse.ParseException e) {
                        if (e == null) {
                            for (ParseObject q : questionSet) {
                                Question new_q;
                                int question_number = q.getInt("question_number");
                                String question = q.getString("question");
                                String answer = q.getString("answer");
                                String explanation = q.getString("explanation");
                                boolean isTF = q.getBoolean("isTF");
                                String optionA, optionB, optionC, optionD;
                                if (isTF == false) {
                                    optionA = q.getString("optionA");
                                    optionB = q.getString("optionB");
                                    optionC = q.getString("optionC");
                                    optionD = q.getString("optionD");
                                    new_q = new Question(question_number, question, answer, explanation,
                                            isTF, optionA, optionB, optionC, optionD);
                                } else {
                                    new_q = new Question(question_number, question, answer, explanation, isTF);
                                }
                                QuestionSet.add(new_q);
                            }

                            //After Data set is returned, Set up the page
                            initialize();
                        }
                    }
                });
            }
        });
    }

    public void initialize(){

        setContentView(R.layout.activity_game);
        findViewsByID();

        currentQuestion = 1;
        numbercorrect = 0;

        setQuestion(QuestionSet.get(currentQuestion - 1));
        questionNumTxtView.setText("Question " + currentQuestion + " of 10");
        resume = true;

        //Music
        player = MediaPlayer.create(GameActivity.this,R.raw.cogno_game_score);
        player.setLooping(true);
        player.start();
    }

    public void setQuestion(Question q) {

        questionBox.setText(q.getQuestion());

        if (q.getTF()) {
            optionABox.setText("True");
            optionBBox.setText("False");
            optionCBox.setVisibility(View.INVISIBLE);
            optionDBox.setVisibility(View.INVISIBLE);
        } else {
            optionABox.setText(q.getOptionA());
            optionBBox.setText(q.getOptionB());
            optionCBox.setVisibility(View.VISIBLE);
            optionDBox.setVisibility(View.VISIBLE);
            optionCBox.setText(q.getOptionC());
            optionDBox.setText(q.getOptionD());
        }
    }

    public void findViewsByID(){
        questionBox = (Button) findViewById(R.id.questionbox);
        optionABox = (Button) findViewById(R.id.optionA);
        optionBBox = (Button) findViewById(R.id.optionB);
        optionCBox = (Button) findViewById(R.id.optionC);
        optionDBox = (Button) findViewById(R.id.optionD);
        questionNumTxtView = (TextView) findViewById(R.id.questionNum);
    }
}
