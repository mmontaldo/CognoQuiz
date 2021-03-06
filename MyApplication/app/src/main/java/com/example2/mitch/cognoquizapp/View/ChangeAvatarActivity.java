package com.example2.mitch.cognoquizapp.View;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.example2.mitch.cognoquizapp.Controller.ChangeAvatarCustomAdapter;
import com.example2.mitch.cognoquizapp.R;

public class ChangeAvatarActivity extends Activity {

    GridView gv;

    public static String [] charNameList={
            "Cello","Chrona","Cogno", "Furtu", "Gemini Twins",
            "Komodo", "Marauder", "Nonus", "Phonica", "Undula", "Volo"
    };

    private int[] charImages = {
            R.drawable.cello, R.drawable.chrona,
            R.drawable.cogno, R.drawable.furtu,
            R.drawable.gemini, R.drawable.komodo,
            R.drawable.marauder, R.drawable.nonus,
            R.drawable.phonica, R.drawable.undula,
            R.drawable.volo
    };

    private int[] smallCharImages = {
            R.drawable.cellosmall, R.drawable.chronasmall,
            R.drawable.cognosmall, R.drawable.furtusmall,
            R.drawable.geminismall, R.drawable.komodosmall,
            R.drawable.maraudersmall, R.drawable.nonussmall,
            R.drawable.phonicasmall, R.drawable.undulasmall,
            R.drawable.volosmall
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_avatar);
        gv=(GridView) findViewById(R.id.gridview);
        gv.setAdapter(new ChangeAvatarCustomAdapter(this, charNameList, charImages, smallCharImages));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_change_avatar, menu);
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
