package com.example.mitch.cognoquizapp.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mitch.cognoquizapp.Model.Question;
import com.example.mitch.cognoquizapp.R;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;



public class GameActivity extends Activity {

    public ArrayList<Question> QuestionSet = new ArrayList<Question>();

    public final static String CORRECTNESS_MESSAGE = "com.example.mitch.cognoquizapp.MESSAGE";

    Button questionBox;
    Button optionABox;
    Button optionBBox;
    Button optionCBox;
    Button optionDBox;
    TextView questionNumTxtView;
    int currentQuestion;
    boolean resume = false;
    int numbercorrect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Query to get Question Set
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Question");
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
                        if (isTF == false){
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

    @Override
    protected void onResume(){
        super.onResume();
        if (resume) {
            currentQuestion++;
            questionNumTxtView.setText("Question " + currentQuestion);
            setQuestion(QuestionSet.get(currentQuestion - 1));
        }
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

    public void optionAClick(View view){
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
                startActivity(intent);
            } else {
                String message = "incorrect";
                intent.putExtra(CORRECTNESS_MESSAGE, message);
                intent.putExtra("question", q);
                intent.putExtra("numberCorrect", numbercorrect);
                startActivity(intent);
            }
        } else {
            if (q.getAnswer().equals(q.getOptionA())){
                String message = "correct";
                numbercorrect++;
                intent.putExtra(CORRECTNESS_MESSAGE, message);
                intent.putExtra("question", q);
                intent.putExtra("numberCorrect", numbercorrect);
                startActivity(intent);
            } else {
                String message = "incorrect";
                intent.putExtra(CORRECTNESS_MESSAGE, message);
                intent.putExtra("question", q);
                intent.putExtra("numberCorrect", numbercorrect);
                startActivity(intent);
            }
        }
    }

    public void optionBClick(View view){
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
                startActivity(intent);
            } else {
                String message = "incorrect";
                intent.putExtra(CORRECTNESS_MESSAGE, message);
                intent.putExtra("question", q);
                intent.putExtra("numberCorrect", numbercorrect);
                startActivity(intent);
            }
        } else {
            if (q.getAnswer().equals(q.getOptionB())){
                String message = "correct";
                numbercorrect++;
                intent.putExtra(CORRECTNESS_MESSAGE, message);
                intent.putExtra("question", q);
                intent.putExtra("numberCorrect", numbercorrect);
                startActivity(intent);
            } else {
                String message = "incorrect";
                intent.putExtra(CORRECTNESS_MESSAGE, message);
                intent.putExtra("question", q);
                intent.putExtra("numberCorrect", numbercorrect);
                startActivity(intent);
            }
        }
    }

    public void optionCClick(View view){
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
            startActivity(intent);
        } else {
            String message = "incorrect";
            intent.putExtra(CORRECTNESS_MESSAGE, message);
            intent.putExtra("question", q);
            intent.putExtra("numberCorrect", numbercorrect);
            startActivity(intent);
        }
    }

    public void optionDClick(View view){
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
            startActivity(intent);
        } else {
            String message = "incorrect";
            intent.putExtra(CORRECTNESS_MESSAGE, message);
            intent.putExtra("question", q);
            intent.putExtra("numberCorrect", numbercorrect);
            startActivity(intent);
        }
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
