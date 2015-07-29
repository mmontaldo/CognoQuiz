package com.example.mitch.cognoquizapp;

import java.io.Serializable;
/**
 * Created by Mitch on 7/27/2015.
 */
public class Question implements Serializable{
    int question_number;
    String question;
    String answer;
    String explanation;
    boolean isTF;
    String optionA;
    String optionB;
    String optionC;
    String optionD;

    public Question(){

    }

    public Question(int question_number,
                    String question,
                    String answer,
                    String explanation,
                    boolean isTF){
        this.question_number = question_number;
        this.question = question;
        this.answer = answer;
        this.explanation = explanation;
        this.isTF = isTF;
    }

    public Question(int question_number,
            String question,
            String answer,
            String explanation,
            boolean isTF,
            String optionA,
            String optionB,
            String optionC,
            String optionD){
        this.question_number = question_number;
        this.question = question;
        this.answer = answer;
        this.explanation = explanation;
        this.isTF = isTF;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
    }
}
