package com.example.mitch.cognoquizapp.Model;

/**
 * Created by Mitch on 7/29/2015.
 */
public class HighScore {
    String date;
    String name;
    int score;
    int rank;

    public HighScore(){

    }

    public HighScore(String date, String name, int score){
        this.date = date;
        this.name = name;
        this.score = score;
        rank = 0;
    }


    public String getDate(){
        return date;
    }

    public String getName(){
        return name;
    }

    public int getScore(){
        return score;
    }

    public void setRank(int r){
        this.rank = r;
    }

    public int getRank(){
        return rank;
    }

}
