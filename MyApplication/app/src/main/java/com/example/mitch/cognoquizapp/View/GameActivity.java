package com.example.mitch.cognoquizapp.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer;

import com.example.mitch.cognoquizapp.Model.Question;
import com.example.mitch.cognoquizapp.R;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;



public class GameActivity extends Activity {

    public ArrayList<Question> QuestionSet = new ArrayList<Question>();

    private static final int SPEECH_REQUEST_CODE = 0;

    public final static String CORRECTNESS_MESSAGE = "com.example.mitch.cognoquizapp.MESSAGE";

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.newgame_loading_screen);

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

                            player = MediaPlayer.create(GameActivity.this,R.raw.cogno_game_score);
                            player.setLooping(true);
                            player.start();

                            setContentView(R.layout.activity_game);
                            questionBox = (Button) findViewById(R.id.questionbox);
                            optionABox = (Button) findViewById(R.id.optionA);
                            optionBBox = (Button) findViewById(R.id.optionB);
                            optionCBox = (Button) findViewById(R.id.optionC);
                            optionDBox = (Button) findViewById(R.id.optionD);
                            questionNumTxtView = (TextView) findViewById(R.id.questionNum);

                            currentQuestion = 1;
                            numbercorrect = 0;

                            setQuestion(QuestionSet.get(currentQuestion - 1));
                            questionNumTxtView.setText("Question " + currentQuestion);
                            resume = true;
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (resume && newquestion) {
            currentQuestion++;
            questionNumTxtView.setText("Question " + currentQuestion);
            setQuestion(QuestionSet.get(currentQuestion - 1));
            newquestion = false;
        }
        if (resume && !player.isPlaying()){
            player = MediaPlayer.create(GameActivity.this,R.raw.cogno_game_score);
            player.setLooping(true);
            player.start();

        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (pauseMusic) {
            player.stop();
        }
    }

    @Override
    public void onBackPressed() {
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

    public void speechButtonClick(View view){
        displaySpeechRecognizer();
    }

    public void optionAClick(View view){
        pauseMusic = false;
        Question q = QuestionSet.get(currentQuestion-1);
        Intent intent = new Intent(this, GameAnswerActivity.class);

        if (currentQuestion == QuestionSet.size()){
            intent.putExtra("isFinished", true);
        } else {
            intent.putExtra("isFinished", false);
        }

        if (q.getTF()){
            if (q.getAnswer().equals("True")){
                String message = "correct";
                numbercorrect++;
                intent.putExtra(CORRECTNESS_MESSAGE, message);
                intent.putExtra("question", q);
                intent.putExtra("numberCorrect", numbercorrect);
                startActivityForResult(intent, 1);
            } else {
                String message = "incorrect";
                intent.putExtra(CORRECTNESS_MESSAGE, message);
                intent.putExtra("question", q);
                intent.putExtra("numberCorrect", numbercorrect);
                startActivityForResult(intent, 1);
            }
        } else {
            if (q.getAnswer().equals(q.getOptionA())){
                String message = "correct";
                numbercorrect++;
                intent.putExtra(CORRECTNESS_MESSAGE, message);
                intent.putExtra("question", q);
                intent.putExtra("numberCorrect", numbercorrect);
                startActivityForResult(intent, 1);
            } else {
                String message = "incorrect";
                intent.putExtra(CORRECTNESS_MESSAGE, message);
                intent.putExtra("question", q);
                intent.putExtra("numberCorrect", numbercorrect);
                startActivityForResult(intent, 1);
            }
        }
    }

    public void optionBClick(View view){
        pauseMusic = false;
        Question q = QuestionSet.get(currentQuestion-1);
        Intent intent = new Intent(this, GameAnswerActivity.class);

        //check if its the last question
        if (currentQuestion == QuestionSet.size()){
            intent.putExtra("isFinished", true);
        } else {
            intent.putExtra("isFinished", false);
        }

        if (q.getTF()){
            if (q.getAnswer().equals("False")){
                String message = "correct";
                numbercorrect++;
                intent.putExtra(CORRECTNESS_MESSAGE, message);
                intent.putExtra("question", q);
                intent.putExtra("numberCorrect", numbercorrect);
                startActivityForResult(intent, 1);
            } else {
                String message = "incorrect";
                intent.putExtra(CORRECTNESS_MESSAGE, message);
                intent.putExtra("question", q);
                intent.putExtra("numberCorrect", numbercorrect);
                startActivityForResult(intent, 1);
            }
        } else {
            if (q.getAnswer().equals(q.getOptionB())){
                String message = "correct";
                numbercorrect++;
                intent.putExtra(CORRECTNESS_MESSAGE, message);
                intent.putExtra("question", q);
                intent.putExtra("numberCorrect", numbercorrect);
                startActivityForResult(intent, 1);
            } else {
                String message = "incorrect";
                intent.putExtra(CORRECTNESS_MESSAGE, message);
                intent.putExtra("question", q);
                intent.putExtra("numberCorrect", numbercorrect);
                startActivityForResult(intent, 1);
            }
        }
    }

    public void optionCClick(View view){
        pauseMusic = false;
        Question q = QuestionSet.get(currentQuestion-1);
        Intent intent = new Intent(this, GameAnswerActivity.class);

        //check if it's the last question
        if (currentQuestion == QuestionSet.size()){
            intent.putExtra("isFinished", true);
        } else {
            intent.putExtra("isFinished", false);
        }

        if (q.getAnswer().equals(q.getOptionC())){
            String message = "correct";
            numbercorrect++;
            intent.putExtra(CORRECTNESS_MESSAGE, message);
            intent.putExtra("question", q);
            intent.putExtra("numberCorrect", numbercorrect);
            startActivityForResult(intent, 1);
        } else {
            String message = "incorrect";
            intent.putExtra(CORRECTNESS_MESSAGE, message);
            intent.putExtra("question", q);
            intent.putExtra("numberCorrect", numbercorrect);
            startActivityForResult(intent, 1);
        }
    }

    public void optionDClick(View view){
        pauseMusic = false;
        Question q = QuestionSet.get(currentQuestion-1);
        Intent intent = new Intent(this, GameAnswerActivity.class);

        //check if it's the last question
        if (currentQuestion == QuestionSet.size()){
            intent.putExtra("isFinished", true);
        } else {
            intent.putExtra("isFinished", false);
        }

        if (q.getAnswer().equals(q.getOptionD())){
            String message = "correct";
            numbercorrect++;
            intent.putExtra(CORRECTNESS_MESSAGE, message);
            intent.putExtra("question", q);
            intent.putExtra("numberCorrect", numbercorrect);
            startActivityForResult(intent, 1);
        } else {
            String message = "incorrect";
            intent.putExtra(CORRECTNESS_MESSAGE, message);
            intent.putExtra("question", q);
            intent.putExtra("numberCorrect", numbercorrect);
            startActivityForResult(intent, 1);
        }
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
}
