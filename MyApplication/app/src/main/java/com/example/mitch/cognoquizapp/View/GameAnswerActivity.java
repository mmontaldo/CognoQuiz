package com.example.mitch.cognoquizapp.View;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mitch.cognoquizapp.Model.Question;
import com.example.mitch.cognoquizapp.R;


public class GameAnswerActivity extends Activity {


    TextView correctnessTextView;
    TextView answerTextView;
    Button explanationBtn;
    Button nextQuestionBtn;
    Question question;
    int numberCorrect;
    MediaPlayer playerCorrect;
    MediaPlayer playerIncorrect;
    boolean activityPausedCheck = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String correctness = intent.getStringExtra(GameActivity.CORRECTNESS_MESSAGE);
        question = (Question)intent.getSerializableExtra("question");
        boolean isFinished = intent.getBooleanExtra("isFinished", false);
        numberCorrect = intent.getIntExtra("numberCorrect", 0);

        setContentView(R.layout.activity_game_answer);

        correctnessTextView = (TextView)findViewById(R.id.correctness);
        answerTextView = (TextView)findViewById(R.id.answer);
        explanationBtn = (Button)findViewById(R.id.explanation);
        nextQuestionBtn = (Button) findViewById(R.id.nextquestion);

        if (correctness.equals("correct")){
            correctnessTextView.setText("Correct!");
            playerCorrect = MediaPlayer.create(GameAnswerActivity.this,R.raw.correct);
            playerCorrect.start();
        } else {
            correctnessTextView.setText("Oops, not quite!");
        }

        if (isFinished){
            nextQuestionBtn.setText("Finish");
        } else {
            nextQuestionBtn.setText("Next Question");
        }

        answerTextView.setText("The answer is: " + question.getAnswer());

    }

    @Override
    protected void onResume(){
        super.onResume();
        if (GameActivity.player != null) {
            if (!GameActivity.player.isPlaying()) {
                GameActivity.player.start();
            }
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (activityPausedCheck) {
            if (GameActivity.player != null) {
                GameActivity.player.stop();
            }
        }
    }

    @Override
    public void onBackPressed() {
    }

    public void explanationClick(View view){
        new AlertDialog.Builder(this)
                .setMessage(question.getExplanation())
                .setTitle("Here's Why!")
                .setPositiveButton("close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    public void nextQuestionClick(View view){
        String buttonText = nextQuestionBtn.getText().toString();
        if (buttonText.equals("Next Question")) {
            activityPausedCheck = false;
            Intent intent = new Intent();
            intent.putExtra("returnCheck", true);
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else {
            Intent intent = new Intent(this, GameResultsActivity.class);
            intent.putExtra("numberCorrect", numberCorrect);
            GameActivity.player.stop();
            startActivity(intent);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_answer, menu);
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
