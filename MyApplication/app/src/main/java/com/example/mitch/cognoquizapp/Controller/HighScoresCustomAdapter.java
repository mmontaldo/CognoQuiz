package com.example.mitch.cognoquizapp.Controller;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mitch.cognoquizapp.Model.HighScore;
import com.example.mitch.cognoquizapp.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Mitch on 7/29/2015.
 */
public class HighScoresCustomAdapter extends BaseAdapter{
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater = null;
    public Resources res;
    HighScore tempValues = null;
    int i=0;
    Context c;

    public HighScoresCustomAdapter(Context c, Activity a, ArrayList d, Resources resLocal) {

        //take passed values
        activity = a;
        data=d;
        res = resLocal;
        this.c=c;

        //layout inflator to call external xml layout
        inflater = ( LayoutInflater )c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    //get passed arraylist size
    public int getCount() {

        if(data.size()<=0)
            return 1;
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    //holder Class to contain inflated xml file elements
    public static class ViewHolder{

        public TextView date;
        public TextView score;
        public ImageView medal;

    }

    //create each ListView row
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            //inflate tabitem.xml file for each row
            vi = inflater.inflate(R.layout.high_scores_list_row, null);


            holder = new ViewHolder();
            holder.date = (TextView) vi.findViewById(R.id.date);
            holder.score = (TextView) vi.findViewById(R.id.highscore);
            holder.medal=(ImageView)vi.findViewById(R.id.medal);


            //set holder with LayoutInflater
            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();

        if(data.size()<=0)
        {
            vi.setVisibility(View.INVISIBLE);

        }
        else
        {
            //get each model object from arraylist
            tempValues=null;
            tempValues = ( HighScore ) data.get( position );

            //set model values in holder elements

            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            String dateString = df.format(tempValues.getDate());

            holder.date.setText(dateString);
            holder.score.setText(Integer.toString(tempValues.getScore()));


            if (tempValues.getRank() == 1){
                holder.medal.setVisibility(View.VISIBLE);
                holder.medal.setImageResource(R.drawable.gold_medal);
            } else if (tempValues.getRank() == 2){
                holder.medal.setVisibility(View.VISIBLE);
                holder.medal.setImageResource(R.drawable.silver_medal);
            } else if (tempValues.getRank() == 3){
                holder.medal.setVisibility(View.VISIBLE);
                holder.medal.setImageResource(R.drawable.bronze_medal);
            }else {
                holder.medal.setVisibility(View.INVISIBLE);
            }

        }
        return vi;
    }
}
