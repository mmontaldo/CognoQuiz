package com.example2.mitch.cognoquizapp.Model;

import java.util.Date;

/**
 * Created by Mitch on 7/29/2015.
 */
public class HighScore {
    Date date;
    String name;
    int score;
    int rank;

    public HighScore(){

    }

    public HighScore(Date date, String name, int score){
        this.date = date;
        this.name = name;
        this.score = score;
        rank = 0;
    }


    public Date getDate(){
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
